package io.github.koss.brew.ui.you.profile

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.koss.brew.R
import kotlinx.android.synthetic.main.layout_logged_out.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import android.content.Intent
import io.github.koss.brew.ui.main.MainActivity
import io.github.koss.brew.util.arch.BrewViewModelFactory
import io.github.koss.brew.util.extension.component
import javax.inject.Inject
import com.firebase.ui.auth.AuthUI
import io.github.koss.brew.ui.you.TitleProvider

class ProfileFragment: Fragment(), TitleProvider {

    @Inject
    lateinit var viewModelFactory: BrewViewModelFactory

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).component.inject(this)
        viewModel = ViewModelProviders
                .of(activity as MainActivity, viewModelFactory)
                .get(ProfileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_profile, container, false)

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
           viewModel.handleLogin(data, resultCode)
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