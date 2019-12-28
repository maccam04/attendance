package com.macsanityapps.virtualattendance


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.macsanityapps.virtualattendance.common.ValidationResult
import com.macsanityapps.virtualattendance.common.ValidationRule
import com.macsanityapps.virtualattendance.data.AuthUser
import kotlinx.android.synthetic.main.fragment_registration.btn_register
import kotlinx.android.synthetic.main.fragment_registration.tie_contact_no
import kotlinx.android.synthetic.main.fragment_registration.tie_email
import kotlinx.android.synthetic.main.fragment_registration.tie_name
import kotlinx.android.synthetic.main.fragment_registration.til_contact
import kotlinx.android.synthetic.main.fragment_registration.til_email
import kotlinx.android.synthetic.main.fragment_registration.til_name
import kotlinx.android.synthetic.main.fragment_update_profile.*
import kotlinx.android.synthetic.main.fragment_update_profile.til_user_name

/**
 * A simple [Fragment] subclass.
 */
class UpdateProfileFragment : Fragment() {

    private var userData : AuthUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_profile, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Profile"

        val pref = activity?.getSharedPreferences("Account", 0)
        val bol = pref?.getBoolean("registered", false)
        val email = pref?.getString("emai", "")
        val studentId = pref?.getString("studentId", "")
        val mobileNo = pref?.getString("phoneNumber", "")
        val name = pref?.getString("name", "")
        val cousre = pref?.getString("course", "")

        tie_email.setText(email)
        tie_contact_no.setText(mobileNo)
        tie_name.setText(name)
        tie_update_course.setText(cousre)
        tie_id_number.setText(studentId)

        btn_register.setOnClickListener {


            val resultName = isInputValid(tie_name.text.toString())
            val resultContactNo = validatePhone(tie_contact_no.text.toString())
            val resultEmail = isInputValid(tie_email.text.toString())
            val resultStundentId = isInputValid(tie_id_number.text.toString())
            val resultCourse = isInputValid(tie_update_course.text.toString())

            if (!resultName.isValid) {
                til_name.error = resultName.reason
            } else til_name.error = ""

            if (!resultEmail.isValid) {
                til_email.error = resultEmail.reason
            } else til_email.error = ""

            if (!resultContactNo.isValid) {
                til_contact.error = resultContactNo.reason
            } else til_contact.error = ""

            if (!resultStundentId.isValid) {
               til_user_name.error = resultStundentId.reason
            } else til_user_name.error = ""

            if (!resultCourse.isValid) {
                til_course_update.error = resultCourse.reason
            } else til_course_update.error = ""

            if(resultName.isValid && resultEmail.isValid && resultContactNo.isValid && resultStundentId.isValid && resultCourse.isValid) {

                val pref = activity?.getSharedPreferences(
                    "Account",
                    0
                )
                val editor = pref?.edit()
                editor?.putBoolean("registered", true)
                editor?.putString("studentId", tie_id_number.text.toString())
                editor?.putString("name", tie_name.text.toString())
                editor?.putString("emai", tie_email.text.toString())
                editor?.putString("phoneNumber", tie_contact_no.text.toString())
                editor?.putString("course", tie_update_course.text.toString())
                editor?.apply()


                val builder = AlertDialog.Builder(activity)

                // Set the alert dialog title
                builder.setTitle("Update")

                // Display a message on alert dialog
                builder.setMessage("Your profile has been updated!")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("OK"){dialog, which ->

                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }

        }
    }


    private fun isInputValid(@NonNull text: String): ValidationResult<String> {
        return ValidationRule.isNotEmpty(text)
    }

    private fun validatePhone(@NonNull phone: String): ValidationResult<String> {
        if (phone.isEmpty()) {
            return ValidationResult.failure("This field is required.", phone)
        }

        val isValid = ValidationRule.isValidMobileNumber(phone)
        return if (isValid) {
            ValidationResult.success(phone)
        } else ValidationResult.failure("Mobile number should be exactly 11 numbers.", phone)
    }




}
