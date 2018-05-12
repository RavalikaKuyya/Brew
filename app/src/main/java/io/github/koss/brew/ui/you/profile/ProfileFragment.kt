package io.github.koss.brew.ui.you.profile

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.koss.brew.R
import io.github.koss.brew.profileEmptyState
import io.github.koss.brew.sectionHeader
import io.github.koss.brew.ui.main.MainActivity
import io.github.koss.brew.util.arch.BrewViewModelFactory
import io.github.koss.brew.util.extension.component
import javax.inject.Inject
import io.github.koss.brew.ui.you.TitleProvider
import io.github.koss.brew.util.extension.withModels
import kotlinx.android.synthetic.main.fragment_profile.*

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
        super.onViewCreated(view, savedInstanceState)
        recyclerView.withModels {
            if (viewModel.activity.value?.isNotEmpty() == true) {
                sectionHeader {
                    id("recent_activity")
                    title(getString(R.string.recent_activity))
                }
            } else {
                profileEmptyState {
                    id("profile_empty_state")
                }
            }
        }
    }

    override fun getTitle(): String = "Profile"

}