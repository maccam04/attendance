package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.R

import com.macsanityapps.virtualattendance.common.ValidationResult
import com.macsanityapps.virtualattendance.common.ValidationRule
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.AuthUser
import com.macsanityapps.virtualattendance.data.User
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.fragment_registration.btn_register
import kotlinx.android.synthetic.main.fragment_registration.tie_contact_no
import kotlinx.android.synthetic.main.fragment_registration.tie_email
import kotlinx.android.synthetic.main.fragment_registration.tie_name
import kotlinx.android.synthetic.main.fragment_registration.til_contact
import kotlinx.android.synthetic.main.fragment_registration.til_email
import kotlinx.android.synthetic.main.fragment_registration.til_name

class RegistrationFragment : Fragment() {

    private var userData: AuthUser? = null
    private var role: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_registration,
            container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let { userData = RegistrationFragmentArgs.fromBundle(it).userData }

        tie_name.setText(userData?.name)
        tie_email.setText(userData?.email)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rg_role.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId){
                R.id.rb_student -> {
                    role = 0
                }
                R.id.rb_prof -> {
                    role = 1
                }
            }
        }


        btn_register.setOnClickListener {

            val resultName = isInputValid(tie_name.text.toString())
            val resultContactNo = validatePhone(tie_contact_no.text.toString())
            val resultEmail = isInputValid(tie_email.text.toString())
            val resultStundentId = isInputValid(tie_id_number.text.toString())
            val resultCourse = isInputValid(tie_course.text.toString())

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
                til_username.error = resultStundentId.reason
            } else til_username.error = ""

            if (!resultCourse.isValid) {
                til_course.error = resultCourse.reason
            } else til_course.error = ""

            if (resultName.isValid && resultEmail.isValid && resultContactNo.isValid && resultStundentId.isValid && resultCourse.isValid) {

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
                editor?.putString("course", tie_course.text.toString())
                editor?.apply()


                val user = User(
                    tie_id_number.text.toString(),
                    tie_name.text.toString(),
                    tie_email.text.toString(),
                    tie_contact_no.text.toString(),
                    tie_course.text.toString(),
                    role!!,
                    true
                )

                FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(tie_id_number.text.toString())
                    .set(user)
                    .addOnSuccessListener {

                        if(role!! == 0){
                            val direction = RegistrationFragmentDirections.actionRegistrationFragmentToDashboardFragment()
                            findNavController().navigate(direction)
                        } else {
                            val direction = RegistrationFragmentDirections.actionRegistrationFragmentToTeacherDashboardFragment()
                            findNavController().navigate(direction)
                        }

                    }
                    .addOnFailureListener {
                        Log.d("OnFailure", it.localizedMessage!!)

                    }
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
