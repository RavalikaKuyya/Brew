package io.github.jamiesanson.brew.ui.home

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SnapHelper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.*
import io.github.jamiesanson.brew.*
import io.github.jamiesanson.brew.ui.home.content.HomeContent
import io.github.jamiesanson.brew.ui.main.MainActivity
import io.github.jamiesanson.brew.util.RalewayRegular
import io.github.jamiesanson.brew.util.anim.GravitySnapHelper
import io.github.jamiesanson.brew.util.anim.RevealAnimationSettings
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory
import io.github.jamiesanson.brew.util.event.MoveToAddDrinkScreen
import io.github.jamiesanson.brew.util.event.UiEventBus
import io.github.jamiesanson.brew.util.extension.component
import io.github.jamiesanson.brew.util.extension.observe
import io.github.jamiesanson.brew.util.extension.withContent
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import javax.inject.Inject
import kotlin.math.abs

class HomeFragment : Fragment() {

    @Inject lateinit var eventBus: UiEventBus
    @Inject lateinit var viewModelFactory: BrewViewModelFactory
    @Inject lateinit var content: HomeContent
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).component.inject(this)
        viewModel = ViewModelProviders
                .of(activity as MainActivity, viewModelFactory)
                .get(HomeViewModel::class.java)

        Carousel.setDefaultGlobalSnapHelperFactory(object: Carousel.SnapHelperFactory() {
            override fun buildSnapHelper(context: Context?): SnapHelper = GravitySnapHelper(Gravity.START)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialiseToolbar()
        applyTypeface()
        floatingActionButton.onClick { onAddClicked(true) }
        setupRecyclerView()

        viewModel.rebuildTrigger.observe(this) {
            if (recyclerView != null) {
                recyclerView.requestModelBuild()
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rebuildRecyclerView()
    }

    private fun rebuildRecyclerView() {
        recyclerView?.withContent(content)
    }

    private fun onAddClicked(fromFab: Boolean = false) {
        val revealSettings = getRevealSettings(
                ContextCompat.getColor(context!!, if (fromFab) R.color.colorAccent else R.color.colorPrimary)
        )

        eventBus.postEvent(MoveToAddDrinkScreen(revealSettings))
    }

    private fun getRevealSettings(startColor: Int): RevealAnimationSettings {
        return RevealAnimationSettings(
                (floatingActionButton.x + floatingActionButton.width / 2).toInt(),
                (floatingActionButton.y + floatingActionButton.height / 2).toInt(),
                parentLayout.width,
                parentLayout.height,
                startColor)
    }

    private fun applyTypeface() {
        val typeface = Typeface.createFromAsset(context?.assets, RalewayRegular.path)
        with (collapsingLayout) {
            setCollapsedTitleTypeface(typeface)
            setExpandedTitleTypeface(typeface)
        }
    }

    private fun initialiseToolbar() {
        toolbar.inflateMenu(R.menu.home_toolbar_items)
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.add_drink_item) {
                // Only fire the listener if the toolbar is actually visible
                if (toolbar.alpha > 0f) {
                    onAddClicked()
                }
            }

            false
        }

        toolbar.alpha = 0f

        // This is such that the toolbar is animated in when the AppBarLayout is collapsed
        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                toolbar.animate().alpha(1f)
                        .withStartAction { toolbar.alpha = 0f }
                        .setDuration(200L)
                        .start()
            } else {
                toolbar.animate().alpha(0f)
                        .withStartAction { toolbar.alpha = 1f }
                        .setDuration(10L)
                        .start()
            }
        }
    }
}