package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.common.SelectionDialogListener

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


class RegistrationFragment : Fragment(), SelectionDialogListener,
    AdapterView.OnItemSelectedListener {


    private var userData: AuthUser? = null
    private var role: Int? = null
    var token: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                token = task.result?.token
                Log.e("TAG", token)
            })


    }

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

        (activity as AppCompatActivity).supportActionBar?.title = "Registration"
        (activity as AppCompatActivity).supportActionBar?.show()

        val dialogFragment = SelectionDialogFragment()
        dialogFragment.newInstance()
        dialogFragment.isCancelable = false
        activity?.supportFragmentManager?.let { dialogFragment.show(it, "dialog") }
        dialogFragment.setSelectionDialogListener(this)
 
        btn_register.setOnClickListener {


            val resultName = isInputValid(tie_name.text.toString())
            val resultContactNo = validatePhone(tie_contact_no.text.toString())
            val resultEmail = isInputValid(tie_email.text.toString())
            val resultStundentId = isInputValid(tie_id_number.text.toString())

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
                til_id.error = resultStundentId.reason
            } else til_id.error = ""

          /*  if (!resultCourse.isValid) {
                til_course.error = resultCourse.reason
            } else til_course.error = ""
*/

            val pref = activity?.getSharedPreferences(
                "Account",
                0
            )

            if(role == 0){
                if (resultName.isValid && resultEmail.isValid && resultContactNo.isValid && resultStundentId.isValid) {

                    val editor = pref?.edit()
                    editor?.putBoolean("registered", true)
                    editor?.putString("studentId", tie_id_number.text.toString())
                    editor?.putString("name", tie_name.text.toString())
                    editor?.putString("email", tie_email.text.toString())
                    editor?.putString("phoneNumber", tie_contact_no.text.toString())
                    editor?.putString("course", if(role == 0) til_course.selectedItem.toString() else "")
                    editor?.putString("token", token)
                    editor?.apply()

                    val user = User(
                        tie_id_number.text.toString(),
                        tie_name.text.toString(),
                        tie_email.text.toString(),
                        tie_contact_no.text.toString(),
                        til_course.selectedItem.toString() ,
                        role!!,
                        true,
                        "Present",
                        token!!
                    )

                    FirebaseFirestore.getInstance().collection("Users")
                        .document(tie_email.text.toString())
                        .get()
                        .addOnCompleteListener {
                            if(it.isSuccessful){

                                val doc = it.result!!

                                if(doc.exists()){

                                    makeToast("Your email is already registered. Please try another email address.")

                                } else {

                                    FirebaseFirestore.getInstance()
                                        .collection("Users")
                                        .document(tie_email.text.toString())
                                        .set(user)
                                        .addOnSuccessListener {

                                            if (role!! == 0) {
                                                //Students
                                                val direction =
                                                    RegistrationFragmentDirections.actionRegistrationFragmentToDashboardFragment()
                                                findNavController().navigate(direction)
                                            } else {
                                                //Professor
                                                val direction =
                                                    RegistrationFragmentDirections.actionRegistrationFragmentToTeacherDashboardFragment()
                                                findNavController().navigate(direction)
                                            }

                                        }
                                        .addOnFailureListener {
                                            Log.d("OnFailure", it.localizedMessage!!)

                                        }
                                }
                            }
                        }


                }

            } else {
                if (resultName.isValid && resultEmail.isValid && resultContactNo.isValid) {

                    val editor = pref?.edit()
                    editor?.putBoolean("registered", true)
                    editor?.putString("studentId", "")
                    editor?.putString("name", tie_name.text.toString())
                    editor?.putString("email", tie_email.text.toString())
                    editor?.putString("phoneNumber", tie_contact_no.text.toString())
                    editor?.putString("course", "")
                    editor?.putString("token", token)
                    editor?.apply()

                    val user = User(
                        tie_id_number.text.toString(),
                        tie_name.text.toString(),
                        tie_email.text.toString(),
                        tie_contact_no.text.toString(),
                        if(role == 0) til_course.selectedItem.toString() else "",
                        role!!,
                        true,
                        "Present",
                        token!!
                    )

                    FirebaseFirestore.getInstance().collection("Users")
                        .document(tie_email.text.toString())
                        .get()
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                val doc = it.result!!

                                if(doc.exists()){
                                    makeToast("Your email is already registered. Please try another email address.")
                                } else {

                                    FirebaseFirestore.getInstance()
                                        .collection("Users")
                                        .document(tie_email.text.toString())
                                        .set(user)
                                        .addOnSuccessListener {

                                            if (role!! == 0) {
                                                //Students
                                                val direction =
                                                    RegistrationFragmentDirections.actionRegistrationFragmentToDashboardFragment()
                                                findNavController().navigate(direction)
                                            } else {
                                                //Professor
                                                val direction =
                                                    RegistrationFragmentDirections.actionRegistrationFragmentToTeacherDashboardFragment()
                                                findNavController().navigate(direction)
                                            }

                                        }
                                        .addOnFailureListener {
                                            Log.d("OnFailure", it.localizedMessage!!)

                                        }
                                }
                            }
                        }


                }
            }

        }

        btn_change.setOnClickListener {
            activity?.supportFragmentManager?.let { dialogFragment.show(it, "dialog") }
        }


        til_course.onItemSelectedListener = this
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

    override fun onSelectStudent() {
        role = 0
        til_course.visibility = View.VISIBLE
        til_id.visibility = View.VISIBLE

        tv_user.text = "You're registering as a Student"
    }

    override fun onSelectTeacher() {
        role = 1
        til_course.visibility = View.GONE
        til_id.visibility = View.GONE

        tv_user.text = "You're registering as a Teacher"
        tie_id_number.setText("")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) { }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val course = parent?.getItemAtPosition(position).toString()
    }

}
