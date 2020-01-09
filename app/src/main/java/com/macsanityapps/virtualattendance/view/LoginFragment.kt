package com.macsanityapps.virtualattendance.view


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.common.RC_SIGN_IN
import com.macsanityapps.virtualattendance.data.AuthUser
import kotlinx.android.synthetic.main.fragment_login.*
import com.macsanityapps.virtualattendance.data.User
import com.macsanityapps.virtualattendance.login.LoginFragmentDirections


/**
 * A simple [Fragment] subclass.
 */

class LoginFragment : Fragment() {

    private var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /*FirebaseFirestore.getInstance().collection("Users")
            .whereEqualTo("id", FirebaseAuth.getInstance().uid).query().addOnCompleteListener {

            if (it.isSuccessful) {

                val document = it.getResult()

                document.documents

                val direction = LoginFragmentDirections.actionLoginFragmentToDashboardFragment()
                findNavController().navigate(direction)
            }
        }*/

        val pref = activity?.getSharedPreferences("Account", 0)
        val studentId = pref?.getString("studentId", "")

        FirebaseFirestore.getInstance()
            .collection("Users")
            .whereEqualTo("id", if(studentId != "") studentId else "")
            .get()
            .addOnSuccessListener {

                val taskList: List<User> = it.toObjects(User::class.java)
                if(taskList.isNotEmpty()){
                    when(taskList[0].type){

                        0 -> {
                            val direction =
                                LoginFragmentDirections.actionLoginFragmentToDashboardFragment()
                            findNavController().navigate(direction)
                        }

                        1 -> {
                            val direction =
                                LoginFragmentDirections.actionLoginFragmentToTeacherDashboardFragment()
                            findNavController().navigate(direction)
                        }
                    }
                } else {
                    showView()
                }

            }
            .addOnFailureListener {

            }

    }

    private fun showView(){
        btn_signin.visibility = View.VISIBLE
        tv_title.visibility = View.VISIBLE
        iv_logo.visibility = View.VISIBLE
    }

    private fun hideView(){
        btn_signin.visibility = View.INVISIBLE
        tv_title.visibility = View.INVISIBLE
        iv_logo.visibility = View.INVISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            com.macsanityapps.virtualattendance.R.layout.fragment_login,
            container,
            false
        )
    }


    override fun onStart() {
        super.onStart()

        (activity as AppCompatActivity).supportActionBar!!.hide()


        btn_signin.setOnClickListener {
            startSignInFlow()
        }

    }

    private fun observeViewModel() {
        val pref = activity?.getSharedPreferences("Account", 0)
        val bol = pref?.getBoolean("registered", false)
    }


    private fun startSignInFlow() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("793352634157-8r45m1pjpra5830inm7tj36sefv76fpb.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)


                val direction =
                    LoginFragmentDirections.actionLoginFragmentToRegistrationFragment(
                        AuthUser(account?.id!!, account.displayName!!, account.email!!)
                    )

                findNavController().navigate(direction)

            } catch (exception: Exception) {
                Log.d("LOGIN", exception.toString())
            }
        }
    }


}