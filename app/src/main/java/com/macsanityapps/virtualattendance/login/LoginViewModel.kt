package com.macsanityapps.virtualattendance.login

import androidx.lifecycle.MutableLiveData
import com.macsanityapps.virtualattendance.common.*
import com.macsanityapps.virtualattendance.common.LOGIN_ERROR
import com.macsanityapps.virtualattendance.common.SIGN_IN
import com.macsanityapps.virtualattendance.data.User
import com.macsanityapps.virtualattendance.data.repository.IUserRepository
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


/**
 * This approach to ViewModels reduces the complexity of the View by containing specific details about widgets and
 * controls present in the View. The benefit of doing so is to make the View in to a Humble Object; reducing or
 * eliminating the need to test the View.
 *
 * The downside of this approach, is that the ViewModel is no longer re-usable across a variety of Views. In this case,
 * since this ViewModel is only used by a single View, and the application architecture will not change any time soon,
 * losing re-usability in exchange for a simpler View is not a problem.
 */
class LoginViewModel ( val repo : IUserRepository,
                       uiContext: CoroutineContext) : BaseViewModel<LoginEvent<LoginResult>>(uiContext) {


    //The actual data model is kept private to avoid unwanted tampering
    private val userState = MutableLiveData<User>()

    //Control Logic
    internal val authAttempt = MutableLiveData<Unit>()
    internal val startAnimation = MutableLiveData<Unit>()

    //UI Binding
    internal val signInStatusText = MutableLiveData<String>()
    internal val authButtonText = MutableLiveData<String>()
    internal val isAuthComplete = MutableLiveData<User>()



    private fun showErrorState() {
        signInStatusText.value = LOGIN_ERROR
        authButtonText.value = SIGN_IN

    }

    private fun showLoadingState() {
        signInStatusText.value = LOADING

        startAnimation.value = Unit
    }

    private fun showSignedInState() {
        signInStatusText.value = SIGNED_IN
        authButtonText.value = SIGN_OUT

    }

    private fun showSignedOutState() {
        signInStatusText.value = SIGNED_OUT
        authButtonText.value = SIGN_IN

    }


    override fun handleEvent(event: LoginEvent<LoginResult>) {
        //Trigger loading screen first
        showLoadingState()
        when (event) {
            is LoginEvent.OnStart -> getUser()
            is LoginEvent.OnSignInClick -> onAuthButtonClick()
            is LoginEvent.OnLoginSignInResult -> onSignInResult(event.result)
        }
    }

    private fun getUser() = launch {
        val result = repo.getCurrentUser()
        when (result) {
            is ResultWrapper.Value -> {
                userState.value = result.value
                if (result.value == null) showSignedOutState()
                else isAuthComplete.value = result.value
            }
            is ResultWrapper.Error -> showErrorState()
        }
    }

    /**
     * If user is null, tell the View to begin the authAttempt. Else, attempt to sign the user out
     */
    private fun onAuthButtonClick() {
        if (userState.value == null) authAttempt.value = Unit
        else signOutUser()
    }

    private fun onSignInResult(result: LoginResult) = launch {
        if (result.requestCode == RC_SIGN_IN && result.userToken != null) {

            val createGoogleUserResult = repo.signInGoogleUser(result.userToken)

            //ResultWrapper.Value means it was successful
            if (createGoogleUserResult is ResultWrapper.Value) getUser()
            else showErrorState()
        } else {
            showErrorState()
        }
    }

    private fun signOutUser() = launch {
        val result = repo.signOutCurrentUser()

        when (result) {
            is ResultWrapper.Value -> {
                userState.value = null
                showSignedOutState()
            }
            is ResultWrapper.Error -> showErrorState()
        }
    }


}