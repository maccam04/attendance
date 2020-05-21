package com.macsanityapps.virtualattendance.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.R
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val pref = activity?.getSharedPreferences("Account", 0)
        val bol = pref?.getBoolean("registered", false)
        val email = pref?.getString("email", "")
        val studentId = pref?.getString("studentId", "")
        val mobileNo = pref?.getString("phoneNumber", "")
        val name = pref?.getString("name", "")
        val course = pref?.getString("course", "")
        val role = pref?.getString("type", "")

        tv_name.text = name
        tv_email.text = email
        tv_contact.text = mobileNo


        if(role == "0"){
            tv_course.visibility = View.VISIBLE
            tv_student_id.visibility = View.VISIBLE

            tv_student_id.text = studentId
            tv_type.text = "STUDENT"
            tv_course.text = course

        } else {
            tv_type.text = "PROFESSOR"
            tv_course.visibility = View.GONE
            tv_student_id.visibility = View.GONE

        }

        tv_edit_profile.setOnClickListener {

            if(role == "0"){
                val dir = DashboardFragmentDirections.actionDashboardFragmentToUpdateProfileFragment()
                findNavController().navigate(dir)
            } else {
                val dir = TeacherDashboardFragmentDirections.actionTeacherDashboardFragmentToUpdateProfileFragment()
                findNavController().navigate(dir)
            }


        }

        btn_logout.setOnClickListener {

            val builder = AlertDialog.Builder(activity)

            // Set the alert dialog title
            builder.setTitle("Warning")

            // Display a message on alert dialog
            builder.setMessage("Are you sure you want to logout? ")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("Yes") { dialog, which ->

                val map = HashMap<String, Boolean>()
                map["register"] = false

                FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document("$email")
                    .update(map as Map<String, Boolean>)
                    .addOnSuccessListener {


                        val gsoBuilder =
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                        activity?.let {
                            GoogleSignIn.getClient(it, gsoBuilder.build())?.signOut()
                        }


                        if(role == "0"){
                            val dir = DashboardFragmentDirections.actionDashboardFragmentToLoginFragment()
                            findNavController().navigate(dir)
                        } else {
                            val dir = TeacherDashboardFragmentDirections.actionTeacherDashboardFragmentToLoginFragment()
                            findNavController().navigate(dir)
                        }

                    }


            }.setNegativeButton("No") { dialog, which ->

            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()
        }



    }


}
