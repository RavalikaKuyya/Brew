package io.github.koss.brew.ui.profile

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.koss.brew.R
import kotlinx.android.synthetic.main.layout_logged_out.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.IdpResponse
import android.content.Intent
import io.github.koss.brew.ui.main.MainActivity
import io.github.koss.brew.util.arch.BrewViewModelFactory
import io.github.koss.brew.util.event.UiEventBus
import io.github.koss.brew.util.extension.component
import javax.inject.Inject

class ProfileFragment: Fragment() {

    @Inject
    lateinit var eventBus: UiEventBus

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
        }

        signUpButton.onClick {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                // ...
            } else {
                // Sign in failed, check response for error code
                // ...
            }
        }
    }

    companion object {
        const val RC_SIGN_IN = 123
    }
}