package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
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
import com.macsanityapps.virtualattendance.data.User
import com.macsanityapps.virtualattendance.util.BottomNavigationViewPager
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment(), Callback<String>,
    BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

    //    setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Dashboard"

        return inflater.inflate(
            R.layout.fragment_dashboard,
            container,
            false
        )
    }

    override fun onStart() {
        super.onStart()

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
                val dir =
                    DashboardFragmentDirections.actionDashboardFragmentToUpdateProfileFragment()
                findNavController().navigate(dir)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_add_room_section.setOnClickListener {
            showAddRoomDialog()
        }

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




    /*private val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false

        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val notesHelper = viewHolder as RoomsAdapter.NoteViewHolder
            notesHelper.deleteItem()
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                .addBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.colorPrimaryDark
                    )
                )
                .addActionIcon(R.mipmap.ic_delete)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }*/

    private fun showAddRoomDialog() {

        val builder = AlertDialog.Builder(activity!!)
        val inflater = layoutInflater
        builder.setTitle("Add Room")
        val dialogLayout = inflater.inflate(R.layout.layout_add_room, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.et_code)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Submit") { _, i ->


            val resultRoom = isInputValid(editText.text.toString())

            if (!resultRoom.isValid) {
                editText.error = resultRoom.reason
            } else editText.error = ""


            if (resultRoom.isValid) {
                FirebaseFirestore.getInstance()
                    .collection("Rooms")
                    .whereEqualTo("id", editText.text.toString())
                    .get()
                    .addOnSuccessListener {

                        val pref = activity?.getSharedPreferences("Account", 0)
                        val bol = pref?.getBoolean("registered", false)
                        val email = pref?.getString("email", "")
                        val studentId = pref?.getString("studentId", "")
                        val mobileNo = pref?.getString("phoneNumber", "")
                        val name = pref?.getString("name", "")
                        val cousre = pref?.getString("course", "")

                        val user = User(studentId, name, email, mobileNo, cousre, 0, true, "Present",
                                (activity?.application as VirtualAttendanceApplication).giveToken())


                        FirebaseFirestore.getInstance()
                            .collection("Rooms")
                            .document("${editText.text}")
                            .get()
                            .addOnSuccessListener {

                                val rooms = it.toObject(Rooms::class.java)
                                FirebaseFirestore.getInstance()
                                    .collection("Rooms")
                                    .document("${editText.text}")
                                    .collection("students")
                                    .document("$studentId")
                                    .get()
                                    .addOnSuccessListener {
                                        if (it.exists()) {
                                            makeToast("You're already sent a request to this room!. ")
                                        } else {
                                            FirebaseFirestore.getInstance()
                                                .collection("Rooms")
                                                .document("${editText.text}")
                                                .collection("students")
                                                .document("$studentId")
                                                .set(user)
                                                .addOnSuccessListener {

                                                    try {

                                                        val message = "{ \"data\": \n" +
                                                                "   { \"title\": \"Status Update! \", \n" +
                                                                "    \"content\" : \"Hi professor, ${name} a new student has applied on your section.\",\n" +
                                                                "    \"imageUrl\": \"http://h5.4j.com/thumb/Ninja-Run.jpg\", \n" +
                                                                "    \"gameUrl\": \"https://h5.4j.com/Ninja-Run/index.php?pubid=noad\" \n" +
                                                                "   }, \n" +
                                                                "\n" +
                                                                " \"to\": \"${rooms?.token}\"\n" +
                                                                "}"

                                                        val userCall = (activity?.application as VirtualAttendanceApplication).getApi().sendData(message)
                                                        userCall.enqueue(this)

                                                    } catch (e: JSONException) {
                                                        e.printStackTrace()
                                                    }


                                                    val builders = android.app.AlertDialog.Builder(activity)

                                                    // Set the alert dialog title
                                                    builders.setTitle("Request Sent!")

                                                    // Display a message on alert dialog
                                                    builders.setMessage("Please wait for your professor to accept your request!")

                                                    // Set a positive button and its click listener on alert dialog
                                                    builders.setPositiveButton("OK") { _, _ -> }

                                                    // Finally, make the alert dialog using builder
                                                    val dialog: android.app.AlertDialog = builders.create()

                                                    // Display the alert dialog on app interface
                                                    dialog.show()
                                                }
                                                .addOnFailureListener {

                                                }

                                        }
                                    }


                            }



                        /*FirebaseFirestore.getInstance()
                            .collection("room${editText.text}")
                            .document("${editText.text}")
                            .set(user)
                            .addOnSuccessListener {
                                val builders = android.app.AlertDialog.Builder(activity)

                                // Set the alert dialog title
                                builders.setTitle("Request Sent!")

                                // Display a message on alert dialog
                                builders.setMessage("Please wait for your professor to accept your request!")

                                // Set a positive button and its click listener on alert dialog
                                builders.setPositiveButton("OK"){ _, _ ->

                                }

                                // Finally, make the alert dialog using builder
                                val dialog: android.app.AlertDialog = builders.create()

                                // Display the alert dialog on app interface
                                dialog.show()
                            }
                            .addOnFailureListener {
                                Log.d("OnFailure", it.localizedMessage!!)

                            }*/
                    }.addOnFailureListener {
                        makeToast("Sorry, No room found.")
                    }
            }
        }
        builder.show()
    }


    private fun isInputValid(@NonNull text: String): ValidationResult<String> {
        return ValidationRule.isNotEmpty(text)
    }

    override fun onFailure(call: Call<String>, t: Throwable) {}

    override fun onResponse(call: Call<String>, response: Response<String>) {}

    private fun setupViewPager(viewPager: ViewPager) {

        viewPager.offscreenPageLimit = 1

        val adapter = BottomNavigationViewPager(childFragmentManager).apply {
            addFragment(StudentHomeFragment())
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




}
