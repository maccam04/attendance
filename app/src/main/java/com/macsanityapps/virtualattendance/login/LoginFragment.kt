package com.macsanityapps.virtualattendance.login


import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.macsanityapps.virtualattendance.common.RC_SIGN_IN
import com.macsanityapps.virtualattendance.login.buildLogic.LoginInjector
import kotlinx.android.synthetic.main.fragment_login.*
import android.content.SharedPreferences
import android.R
import androidx.appcompat.app.AppCompatActivity


/**
 * A simple [Fragment] subclass.
 */

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.macsanityapps.virtualattendance.R.layout.fragment_login, container, false)
    }


    override fun onStart() {
        super.onStart()

        (activity as AppCompatActivity).supportActionBar!!.hide()


        viewModel = ViewModelProvider(
            this, LoginInjector(requireActivity().application).provideUserViewModelFactory()
        )
            .get(LoginViewModel::class.java)

        observeViewModel()
        setUpClickListeners()

        viewModel.handleEvent(LoginEvent.OnStart)

    }

    private fun setUpClickListeners() {
        btn_signin.setOnClickListener { viewModel.handleEvent(LoginEvent.OnSignInClick) }
    }

    private fun observeViewModel() {
        val pref = activity?.getSharedPreferences("Account", 0)
        val bol = pref?.getBoolean("registered", false)

        viewModel.isAuthComplete.observe(
            viewLifecycleOwner,
            Observer {

                if (it.isLogin) {

                    if (bol!!) {
                        val direction = LoginFragmentDirections.actionLoginFragmentToDashboardFragment()
                        findNavController().navigate(direction)

                    } else {
                        val direction =
                            LoginFragmentDirections.actionLoginFragmentToRegistrationFragment(it)
                        findNavController().navigate(direction)
                    }
                }
            }

        )

        viewModel.authAttempt.observe(
            viewLifecycleOwner,
            Observer { startSignInFlow() }
        )


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

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        var userToken: String? = null

        print(task.exception)

        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

            if (account != null) userToken = account.idToken

            print(account)
        } catch (exception: Exception) {
            Log.d("LOGIN", exception.toString())
        }

        viewModel.handleEvent(
            LoginEvent.OnLoginSignInResult(
                LoginResult(
                    requestCode,
                    userToken
                )
            )
        )

    }


}
