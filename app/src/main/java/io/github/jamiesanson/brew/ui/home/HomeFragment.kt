package io.github.jamiesanson.brew.ui.home

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.DimenRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SnapHelper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.airbnb.epoxy.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions.*
import com.bumptech.glide.request.target.Target
import io.github.jamiesanson.brew.*
import io.github.jamiesanson.brew.data.model.Drink
import io.github.jamiesanson.brew.ui.main.MainActivity
import io.github.jamiesanson.brew.ui.main.navigator.ForwardToDrinkScreen
import io.github.jamiesanson.brew.util.RalewayRegular
import io.github.jamiesanson.brew.util.anim.GravitySnapHelper
import io.github.jamiesanson.brew.util.anim.RevealAnimationSettings
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory
import io.github.jamiesanson.brew.util.event.MoveToAddDrinkScreen
import io.github.jamiesanson.brew.util.event.MoveToDrinkScreen
import io.github.jamiesanson.brew.util.event.UiEventBus
import io.github.jamiesanson.brew.util.extension.component
import io.github.jamiesanson.brew.util.extension.observe
import io.github.jamiesanson.brew.util.extension.withModels
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropTransformation
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.view_holder_drink_item.view.*
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
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
        viewModel.recentDrinks.observe(this) {
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
        recyclerView?.withModels {
            val drinks = viewModel.recentDrinks.value ?: emptyList()

            if (drinks.isNotEmpty()) {
                carouselTitle {
                    id("drinks title")
                    title(getString(R.string.your_recent_drinks))
                }

                carousel {
                    id("carousel")
                    models(
                            ArrayList<DataBindingEpoxyModel>(drinks.map { drink -> DrinkItemBindingModel_().apply {
                                id(drink.id)
                                title(drink.name)
                                tagsDisplay(drink.tags.take(3).joinToString(", ") { it.capitalize() })
                                photo(drink)
                                onLongClick { _ ->
                                    alert {
                                        message = "Delete ${drink.name}?"
                                        okButton {
                                            viewModel.removeDrink(drink)
                                        }
                                        cancelButton {  }
                                        show()
                                    }
                                    true
                                }
                            }}).apply {
                                val added = add(ViewAllCarouselButtonBindingModel_().apply {
                                    id("view all")
                                    onClick { _ ->
                                        Log.d("HomeFragment", "View all clicked")
                                    }
                                })
                            }
                    )
                }
            }
        }
    }

    private fun onAddClicked(fromFab: Boolean = false) {
        val revealSettings = getRevealSettings(
                ContextCompat.getColor(context!!, if (fromFab) R.color.colorAccent else R.color.colorPrimary)
        )

        eventBus.postEvent(MoveToAddDrinkScreen(revealSettings))
    }

    private fun onDrinkClicked(sharedImageView: ImageView, drink: Drink) {
        eventBus.postEvent(MoveToDrinkScreen(
                command = ForwardToDrinkScreen(sharedImageView, drink)
        ))
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

    private fun DrinkItemBindingModelBuilder.photo(drink: Drink) {
        this.onBind { _, view, _ ->
            view.dataBinding.root.onClick {
                it ?: return@onClick
                onDrinkClicked(it.backgroundImageView, drink)
            }

            ViewCompat.setTransitionName(view.dataBinding.root.backgroundImageView, drink.id.toString())

            Glide.with(context!!)
                    .load(drink.photoUri)
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .transition(withCrossFade())
                    .apply(bitmapTransform(MultiTransformation(
                            BlurTransformation(),
                            CropTransformation(R.dimen.drink_item_width.resolve(), R.dimen.drink_item_height.resolve(), CropTransformation.CropType.CENTER))))
                    .into(view.dataBinding.root.backgroundImageView)
        }
    }

    @DimenRes
    private fun Int.resolve(): Int = resources.getDimensionPixelSize(this)

    /**
     * Custom epoxy carousel DSL
     */
    private inline fun EpoxyController.carousel(modelInitializer: CarouselModelBuilder.() -> Unit) {
        CarouselModel_().apply {
            modelInitializer()
        }.addTo(this)
    }
}