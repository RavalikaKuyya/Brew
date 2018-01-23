package io.github.jamiesanson.brew.ui.home

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.annotation.Px
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.BitmapImageViewTarget
import io.github.jamiesanson.brew.*
import io.github.jamiesanson.brew.ui.main.MainActivity
import io.github.jamiesanson.brew.ui.main.fragment.NestedScrollListener
import io.github.jamiesanson.brew.util.RobotoMonoRegular
import io.github.jamiesanson.brew.util.anim.RevealAnimationSettings
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory
import io.github.jamiesanson.brew.util.event.MoveToDrinkScreen
import io.github.jamiesanson.brew.util.event.UiEventBus
import io.github.jamiesanson.brew.util.extension.component
import io.github.jamiesanson.brew.util.extension.observe
import io.github.jamiesanson.brew.util.extension.withModels
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropTransformation
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.view_holder_drink_item.view.*
import kotlinx.android.synthetic.main.view_holder_photo_header.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import javax.inject.Inject
import kotlin.math.abs

class HomeFragment : Fragment() {

    @Inject lateinit var eventBus: UiEventBus
    @Inject lateinit var viewModelFactory: BrewViewModelFactory
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).component.inject(this)
        viewModel = ViewModelProviders
                .of(activity as MainActivity, viewModelFactory)
                .get(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        interceptScrollAndDispatchToParent()
        initialiseToolbar()
        applyTypeface()
        floatingActionButton.onClick { onAddClicked(true) }
        setupRecyclerView()
        viewModel.drinkList.observe(this) {
            if (recyclerView != null) {
                recyclerView.requestModelBuild()
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.withModels {
            val drinks = viewModel.drinkList.value ?: emptyList()
            if (drinks.isNotEmpty()) {
                carouselTitle {
                    id("drinks title")
                    title("Your drinks")
                }

                for (drink in drinks) {
                    drinkItem {
                        id(drink.id)
                        title(drink.name)
                        tagsDisplay(drink.tags.take(3).joinToString(","))
                        if (drink.photoUri != null) {
                            photo(drink.photoUri!!)
                        }
                    }
                }
            }
        }
    }

    private fun onAddClicked(fromFab: Boolean = false) {
        val revealSettings = getRevealSettings(
                ContextCompat.getColor(context!!, if (fromFab) R.color.colorAccent else R.color.colorPrimary)
        )

        eventBus.postEvent(MoveToDrinkScreen(revealSettings))
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
        val typeface = Typeface.createFromAsset(context?.assets, RobotoMonoRegular().path)
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

    private fun DrinkItemBindingModelBuilder.photo(uri: Uri) {
        this.onBind { _, view, _ ->
            Glide.with(context!!)
                    .asBitmap()
                    .load(uri)
                    .apply(bitmapTransform(MultiTransformation(
                            BlurTransformation(),
                            CropTransformation(196.toPx(), 124.toPx(), CropTransformation.CropType.CENTER))))
                    .into(BitmapImageViewTarget(view.dataBinding.root.backgroundImageView))
        }
    }

    @Px
    private fun Int.toPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    /**
     * This method is a bit of a hack. For the BottomNavigationView in the MainFragment to hide on
     * scroll it needs to have access to callbacks fired by scrolling view layout behaviors. This
     * doesn't work in the current setup, as it isn't a direct child of the CoordinatorLayout.
     *
     * Note: NestedScroll events can be dispatched from *any* nested child or parent, however a bug with
     * CollapsingToolbarLayout causes events to not be dispatched to nested or parent behaviors.
     */
    private fun interceptScrollAndDispatchToParent() {
        val params = scrollInterceptorView.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = ScrollInterceptionBehavior()
        behavior.scrollCallback {
            if (parentFragment is NestedScrollListener) {
                (parentFragment as NestedScrollListener).onScroll(it)
            }
        }
        params.behavior = behavior
        scrollInterceptorView.requestLayout()
    }
}