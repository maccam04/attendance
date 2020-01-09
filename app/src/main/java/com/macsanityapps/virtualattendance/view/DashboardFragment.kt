package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.Rooms
import com.macsanityapps.virtualattendance.data.User

import kotlinx.android.synthetic.main.fragment_dashboard.*


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true)

        return inflater.inflate(
            R.layout.fragment_dashboard,
            container,
            false
        )
    }

    override fun onStart() {
        super.onStart()

        (activity as AppCompatActivity).supportActionBar!!.show()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
          R.id.action_add -> {
                val dir =
                    DashboardFragmentDirections.actionDashboardFragmentToUpdateProfileFragment()
                findNavController().navigate(dir)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var i = 1

        iv_add_section.setOnClickListener {
            withEditText()
        }

        iv_leave_section.setOnClickListener {

            val dir = DashboardFragmentDirections.actionDashboardFragmentToSectionListFragment()
            findNavController().navigate(dir)

        }
        iv_list_absence.setOnClickListener {

            val dir = DashboardFragmentDirections.actionDashboardFragmentToAddSectionFragment()
            findNavController().navigate(dir)
        }

        //absent list
        iv_list_section.setOnClickListener {
            val dir = DashboardFragmentDirections.actionDashboardFragmentToAbsenceListFragment()
            findNavController().navigate(dir)
        }

    }

    private fun withEditText() {

        val builder = AlertDialog.Builder(activity!!)
        val inflater = layoutInflater
        builder.setTitle("Add Room")
        val dialogLayout = inflater.inflate(R.layout.layout_add_room, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.et_code)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Submit") { _, i ->

            FirebaseFirestore.getInstance()
                .collection("Rooms")
                .whereEqualTo("id", editText.text.toString())
                .get()
                .addOnSuccessListener {

                    val pref = activity?.getSharedPreferences("Account", 0)
                    val bol = pref?.getBoolean("registered", false)
                    val email = pref?.getString("emai", "")
                    val studentId = pref?.getString("studentId", "")
                    val mobileNo = pref?.getString("phoneNumber", "")
                    val name = pref?.getString("name", "")
                    val cousre = pref?.getString("course", "")

                    val user = User(studentId, name, email, mobileNo, cousre, 0, bol)

                    FirebaseFirestore.getInstance()
                        .collection("Rooms")
                        .document("${editText.text}")
                        .collection("students")
                        .document("$studentId")
                        .get()
                        .addOnSuccessListener {
                            if(it.exists()){
                                makeToast("You're already sent a request to this room!. ")
                            } else {
                                FirebaseFirestore.getInstance()
                                    .collection("Rooms")
                                    .document("${editText.text}")
                                    .collection("students")
                                    .document("$studentId")
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
        builder.show()
    }

}
