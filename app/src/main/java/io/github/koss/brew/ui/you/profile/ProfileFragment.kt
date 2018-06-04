package io.github.koss.brew.ui.you.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.Query
import io.github.koss.brew.R
import io.github.koss.brew.data.model.*
import io.github.koss.brew.ui.main.MainActivity
import io.github.koss.brew.util.arch.BrewViewModelFactory
import io.github.koss.brew.util.extension.component
import javax.inject.Inject
import io.github.koss.brew.ui.you.TitleProvider
import io.github.koss.brew.util.extension.observe
import kotlinx.android.synthetic.main.fragment_profile.*
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.DocumentSnapshot
import io.github.koss.brew.ui.drink.DrinkActivity
import io.github.koss.brew.ui.drink.DrinkActivity.Companion.ARG_DRINK_ID
import io.github.koss.brew.ui.drink.DrinkActivity.Companion.ARG_DRINK_REFERENCE
import io.github.koss.brew.ui.you.profile.adapter.FirestoreActivityAdapter
import io.github.koss.brew.ui.you.profile.adapter.LocalActivityAdapter
import org.jetbrains.anko.support.v4.startActivity


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

        viewModel.loadActivity()

        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        recyclerView.addItemDecoration(
                DividerItemDecorator(ContextCompat.getDrawable(context!!, R.drawable.list_divider)!!)
        )

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadActivity()
        }

        viewModel.activityType.observe(this) {
            it?.let(::onActivitySourceLoaded)
        }

        showLoading()
    }

    override fun getTitle(): String = "Recent activity"

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
    }

    private fun showEmptyState() {
        TransitionManager.beginDelayedTransition(parentLayout)
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
    }

    private fun showLocalActivity(list: LiveData<PagedList<ProfileActivity>>) {
        TransitionManager.beginDelayedTransition(parentLayout)
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        emptyStateLayout.visibility = View.GONE

        val adapter = LocalActivityAdapter(::onActivityClicked)
        list.observe(this) { adapter.submitList(it) }

        recyclerView.adapter = adapter
    }

    private fun showRemoteActivity(query: Query, config: PagedList.Config) {
        TransitionManager.beginDelayedTransition(parentLayout)
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        emptyStateLayout.visibility = View.GONE

        val options = FirestorePagingOptions.Builder<ProfileActivity>()
                .setLifecycleOwner(this)
                .setQuery(query, config, { snapshot: DocumentSnapshot ->
                    val activity = snapshot.toObject(DrinkAdded::class.java)
                    return@setQuery activity!!
                })
                .build()

        val adapter = FirestoreActivityAdapter(options, ::onActivityClicked)
        recyclerView.adapter = adapter
    }

    private fun onActivityClicked(profileActivity: ProfileActivity) {
        when (profileActivity) {
            is DrinkAdded -> {
                if (profileActivity.drinkId.toIntOrNull() != null) {
                    startActivity<DrinkActivity>(ARG_DRINK_ID to profileActivity.drinkId)
                } else {
                    startActivity<DrinkActivity>(ARG_DRINK_REFERENCE to profileActivity.drinkId)
                }
            }
        }
    }

    private fun onActivitySourceLoaded(activityType: ActivitySource) {
        swipeRefreshLayout.isRefreshing = false

        when (activityType) {
            is LocalOnly -> showLocalActivity(activityType.source)
            is FirestoreOnly -> showRemoteActivity(activityType.query, activityType.config)
            is NoActivity -> showEmptyState()
        }
    }
}