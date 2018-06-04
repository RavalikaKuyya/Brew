package io.github.koss.brew.ui.you

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.koss.brew.R
import io.github.koss.brew.util.Session
import kotlinx.android.synthetic.main.fragment_you.*
import android.graphics.Typeface
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import io.github.koss.brew.util.QuicksandMedium
import org.jetbrains.anko.childrenRecursiveSequence
import android.content.ContentResolver
import android.net.Uri
import io.github.koss.brew.BrewApp
import io.github.koss.brew.repository.config.PreferencesManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.support.v4.alert
import javax.inject.Inject


class YouFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var preferencesManager: PreferencesManager

    private lateinit var viewModel: YouViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as BrewApp).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(YouViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_you, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = YouFragmentPagerAdapter(childFragmentManager) {
            Session.isLoggedIn
        }

        tabLayout.setupWithViewPager(viewPager)
        refresh(refreshAdapter = false)

        viewModel.reloadTrigger.observe(this, Observer { refresh() })
    }

    private fun refresh(refreshAdapter: Boolean = true) {
        if (refreshAdapter) {
            viewPager.adapter?.notifyDataSetChanged()

            if (Session.isLoggedIn) {

                if (!preferencesManager.hasSeenSyncPrompt) {

                    viewModel.hasLocalDrinks()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { hasLocal ->
                                if (hasLocal) {
                                    alert {
                                        title = getString(R.string.sync_drinks)
                                        message = getString(R.string.sync_drinks_question)
                                        positiveButton(getString(R.string.sync_now)) {
                                            viewModel.syncAllDrinks()
                                            preferencesManager.hasSeenSyncPrompt = true
                                        }

                                        negativeButton(getString(R.string.later)) {
                                            it.dismiss()
                                            preferencesManager.hasSeenSyncPrompt = true
                                        }

                                        isCancelable = false

                                        show()
                                    }
                                }
                            }

                }
            }
        }

        updateTabFont()
        updateProfileImage()
    }

    private fun updateTabFont() {
        val viewGroup = tabLayout.getChildAt(0) as ViewGroup
        val typeface = Typeface.createFromAsset(context?.assets, QuicksandMedium.path)

        viewGroup.childrenRecursiveSequence()
                .filter { it is TextView }
                .forEach { (it as TextView).typeface = typeface }
    }

    private fun updateProfileImage() {
        val photoUrl = if (Session.isLoggedIn) {
            FirebaseAuth.getInstance().currentUser?.photoUrl
                    ?: R.drawable.ic_no_profile_picture.toUri()
        } else {
            null
        }

        Glide.with(this)
                .load(photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImageView)
    }

    private fun Int.toUri(): Uri {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context!!.resources.getResourcePackageName(this)
                + '/'.toString() + context!!.resources.getResourceTypeName(this)
                + '/'.toString() + context!!.resources.getResourceEntryName(this))
    }
}