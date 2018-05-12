package io.github.koss.brew.ui.you.loggedout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import io.github.koss.brew.R
import io.github.koss.brew.ui.you.TitleProvider
import kotlinx.android.synthetic.main.layout_logged_out.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class LoggedOutFragment: Fragment(), TitleProvider {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.fragment_logged_out, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logInButton.onClick {
            launchFirebaseAuth()
        }

        signUpButton.onClick {
            launchFirebaseAuth()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                (parentFragment as? LoggedInCallback)?.onLoggedIn()
                Log.d("LoggedOutFragment", FirebaseAuth.getInstance().currentUser.toString())
                return
            }

            val response = IdpResponse.fromResultIntent(data)

            // TODO - Handle erroneous responses here
        }
    }

    override fun getTitle(): String = "Profile"

    private fun launchFirebaseAuth() {
        val providers = listOf(
                AuthUI.IdpConfig.EmailBuilder(),
                AuthUI.IdpConfig.GoogleBuilder()).map { it.build() }

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)

    }

    companion object {
        const val RC_SIGN_IN = 123
    }
}

/**
 * Interface to be implemented by parent fragment
 */
interface LoggedInCallback {
    fun onLoggedIn()
}