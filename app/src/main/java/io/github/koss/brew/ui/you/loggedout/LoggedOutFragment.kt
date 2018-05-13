package io.github.koss.brew.ui.you.loggedout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import io.github.koss.brew.BrewApp
import io.github.koss.brew.R
import io.github.koss.brew.ui.you.OnRefreshYouFragmentRequested
import io.github.koss.brew.ui.you.TitleProvider
import io.github.koss.brew.util.arch.EventBus
import kotlinx.android.synthetic.main.layout_logged_out.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import javax.inject.Inject

class LoggedOutFragment: Fragment(), TitleProvider {

    @Inject lateinit var eventBus: EventBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as BrewApp).applicationComponent.inject(this)
    }

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
                eventBus.send(OnRefreshYouFragmentRequested)
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
