package io.github.jamiesanson.brew.ui.create.drink

import android.Manifest
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tylersuehr.chips.ChipsInputLayout
import com.tylersuehr.chips.data.Chip
import com.tylersuehr.chips.data.ChipSelectionObserver
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.addPhotoHeader
import io.github.jamiesanson.brew.drinkTagInput
import io.github.jamiesanson.brew.drinkTitleInput
import io.github.jamiesanson.brew.util.PermissionDelegate
import io.github.jamiesanson.brew.util.anim.CircularRevealUtil
import io.github.jamiesanson.brew.util.anim.RevealAnimationSettings
import io.github.jamiesanson.brew.util.anim.StatusBarAnimationSettings
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory
import io.github.jamiesanson.brew.util.event.ExitDrinkScreen
import io.github.jamiesanson.brew.util.event.UiEventBus
import io.github.jamiesanson.brew.util.extension.OnTextChanged
import io.github.jamiesanson.brew.util.extension.component
import io.github.jamiesanson.brew.util.extension.withModels
import io.github.jamiesanson.brew.util.nav.BackButtonListener
import kotlinx.android.synthetic.main.fragment_drink.*
import kotlinx.android.synthetic.main.fragment_drink.view.*
import kotlinx.android.synthetic.main.view_holder_drink_tag_input.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import javax.inject.Inject
import com.zhihu.matisse.engine.impl.GlideEngine
import android.content.pm.ActivityInfo
import io.github.jamiesanson.brew.ui.main.MainActivity
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import io.github.jamiesanson.brew.ui.create.drink.photo.Camera
import io.github.jamiesanson.brew.ui.create.drink.photo.Gallery
import io.github.jamiesanson.brew.ui.create.drink.photo.PhotoSourceChooser
import io.github.jamiesanson.brew.util.GlideImageEngine


class DrinkFragment : BackButtonListener, Fragment() {

    @Inject lateinit var eventBus: UiEventBus
    @Inject lateinit var viewModelFactory: BrewViewModelFactory
    private lateinit var viewModel: DrinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).component.inject(this)
        viewModel = ViewModelProviders
                .of(activity as MainActivity, viewModelFactory)
                .get(DrinkViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_drink, container, false)

        if (!viewModel.isViewRevealed && arguments?.containsKey(ARG_REVEAL_SETTINGS) == true) {
            showCircularReveal(view)
        } else {
            activity?.window?.statusBarColor = ContextCompat.getColor(context!!, R.color.colorAccentDark)
            view.addImageView.visibility = View.GONE
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        drinkToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        drinkToolbar.inflateMenu(R.menu.add_drink_item)
        drinkToolbar.onMenuItemClick {
            // There's only one menu item, so assume that means the user's done
            onDonePressed()
        }

        recyclerView.withModels {
            addPhotoHeader {
                id("photo header")
                clickListener { _: View ->
                    showImageChooser()
                }
            }

            drinkTitleInput {
                id("title input")
                textWatcher( OnTextChanged {
                    Log.d("DrinkFragment", "Title: $it")
                })
            }

            drinkTagInput {
                id("tag input")
                filterableTags(emptyList())

                onBind { _, view, _ ->
                    setupChipSelectionObserver(view.dataBinding.root.chipsInputLayout)
                }
            }

        }
    }

    override fun onBackPressed(): Boolean {
        showCircularExit {
            eventBus.postEvent(ExitDrinkScreen())
            viewModel.isViewRevealed = false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            val selected = Matisse.obtainResult(data)
            Log.d("Matisse", "selected: $selected")
        }
    }

    private fun onDonePressed() {
        // TODO - Implement saving logic
        onBackPressed()
    }

    private fun showImageChooser() {
        PhotoSourceChooser()
                .sources(Camera(), Gallery())
                .onSourceChosen {
                    when (it) {
                        is Camera -> TODO()
                        is Gallery -> startMatisse()
                    }
                }
                .show(fragmentManager, TAG_PHOTO_SOURCE_CHOOSER)

    }

    private fun startMatisse() {
        async(UI) {
            val permissionsGranted = PermissionDelegate().checkPermissions(
                    activity!!,
                    "We need to ask you for some permissions before adding photos",
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permissionsGranted) {
                Matisse.from(this@DrinkFragment)
                        .choose(MimeType.of(MimeType.PNG, MimeType.JPEG))
                        .maxSelectable(1)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(GlideImageEngine())
                        .forResult(REQUEST_CODE_CHOOSE)
            }
        }
    }

    private fun setupChipSelectionObserver(layout: ChipsInputLayout) {
        layout.addChipSelectionObserver( object: ChipSelectionObserver {
            override fun onChipSelected(p0: Chip?) {
                Log.d("DrinkFragment", "Selected: ${p0?.title}")

            }

            override fun onChipDeselected(p0: Chip?) {
                Log.d("DrinkFragment", "Deselected: ${p0?.title}")
            }

        })
    }

    private fun showCircularReveal(view: View) {
        var revealSettings = arguments?.getParcelable(ARG_REVEAL_SETTINGS) as RevealAnimationSettings

        // Update RevealSettings to reflect required behavior
        revealSettings = revealSettings.copy(
                duration = 500L,
                targetView = view,
                backgroundView = view.revealScrim,
                statusBarAnimationSettings = StatusBarAnimationSettings(
                        startColor = activity?.window?.statusBarColor ?: 0,
                        endColor = ContextCompat.getColor(context!!, R.color.colorAccentDark),
                        window = activity?.window
                )
        )

        // Move add icon to appropriate location
        val drawableWidth = ContextCompat.getDrawable(context!!, R.drawable.ic_add_black_24dp)?.intrinsicWidth ?: 0
        view.addImageView.x = (revealSettings.centerX - drawableWidth / 2).toFloat()
        view.addImageView.y = (revealSettings.centerY - drawableWidth / 2).toFloat()
        view.addImageView.visibility = View.VISIBLE

        // Start fade-out of ImageView
        view.addImageView.animate()
                .alpha(0f)
                .setInterpolator(FastOutSlowInInterpolator())
                .setDuration(500L)
                .start()

        CircularRevealUtil.startCircularRevealEnterAnimation(revealSettings) {
            viewModel.isViewRevealed = true
        }
    }

    private fun showCircularExit(onFinish: () -> Unit) {
        var revealSettings = arguments?.getParcelable(ARG_REVEAL_SETTINGS) as RevealAnimationSettings

        // Update RevealSettings to reflect required behavior
        revealSettings = revealSettings.copy(
                duration = 300L,
                targetView = view,
                backgroundView = revealScrim,
                statusBarAnimationSettings = StatusBarAnimationSettings(
                        startColor = activity?.window?.statusBarColor ?: 0,
                        endColor = ContextCompat.getColor(context!!, R.color.colorPrimaryDark),
                        window = activity?.window
                )
        )

        // Start fade-in of icon ImageView
        with(view) {
            addImageView.animate()
                    .alpha(1f)
                    .setDuration(300L)
                    .start()
        }

        CircularRevealUtil.startCircularRevealExitAnimation(revealSettings, onFinish)
    }

    companion object {
        const val ARG_REVEAL_SETTINGS = "reveal_settings"
        const val REQUEST_CODE_CHOOSE = 110
        const val TAG_PHOTO_SOURCE_CHOOSER = "tag_photo_chooser"
    }
}