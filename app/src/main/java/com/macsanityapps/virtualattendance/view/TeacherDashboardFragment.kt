package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.VirtualAttendanceApplication
import com.macsanityapps.virtualattendance.common.ValidationResult
import com.macsanityapps.virtualattendance.common.ValidationRule
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.Rooms
import com.macsanityapps.virtualattendance.util.BottomNavigationViewPager
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_teacher_dashboard.*
import kotlinx.android.synthetic.main.fragment_teacher_dashboard.bottom_nav
import kotlinx.android.synthetic.main.fragment_teacher_dashboard.viewpager

/**
 * A simple [Fragment] subclass.
 */
class TeacherDashboardFragment : Fragment(), ViewPager.OnPageChangeListener,
    BottomNavigationView.OnNavigationItemSelectedListener {


    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

    //    setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_teacher_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Dashboard"

    }
    override fun onStart() {
        super.onStart()
        //(activity as AppCompatActivity).supportActionBar!!.show()

        fab_add_room.setOnClickListener {

            val builder = AlertDialog.Builder(activity!!)
            val inflater = layoutInflater
            builder.setTitle("Create Room")
            val dialogLayout = inflater.inflate(R.layout.view_generate_room, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.et_room_name)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Submit") { dialogInterface, i ->

                val resultRoom = isInputValid(editText.text.toString())

                if (!resultRoom.isValid) {
                    editText.error = resultRoom.reason
                } else editText.error = ""


                if(resultRoom.isValid){
                    createRoom(editText.text.toString())
                }
            }

            builder.show()


        }

        setupViewPager(viewpager)

        bottom_nav.setOnNavigationItemSelectedListener(this)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_add -> {

                val dir = TeacherDashboardFragmentDirections.actionTeacherDashboardFragmentToUpdateProfileFragment()
                findNavController().navigate(dir)
                true

            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createRoom(text: String) {

        val randomString = (1..6)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

        val pref = activity?.getSharedPreferences("Account", 0)
        val email = pref?.getString("email", "")

        val rooms = Rooms(randomString, "", email!!, text, (activity?.application as VirtualAttendanceApplication).giveToken(), mutableListOf())

        FirebaseFirestore.getInstance()
            .collection("Rooms")
            .document(randomString)
            .set(rooms)
            .addOnSuccessListener {
                makeToast("Room created successfully.")
            }
            .addOnFailureListener {
                Log.d("OnFailure", it.localizedMessage!!)
            }

    }

    private fun isInputValid(@NonNull text: String): ValidationResult<String> {
        return ValidationRule.isNotEmpty(text)
    }

    private fun setupViewPager(viewPager: ViewPager) {

        viewPager.offscreenPageLimit = 1

        val adapter = BottomNavigationViewPager(childFragmentManager).apply {
            addFragment(TeacherHomeFragment())
            addFragment(SettingsFragment())
        }

        viewPager.adapter = adapter
        viewPager.currentItem = 0
        viewPager.addOnPageChangeListener(this)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.home -> {
                viewpager.currentItem = 0
                (activity as AppCompatActivity).supportActionBar?.title = "Dashboard"
                return true
            }

            R.id.settings -> {
                viewpager.currentItem = 1
                (activity as AppCompatActivity).supportActionBar?.title = "Settings"
                return true
            }

        }

        return false
    }

}
