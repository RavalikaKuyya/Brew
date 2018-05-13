package io.github.koss.brew.ui.create.drink

import android.Manifest
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tylersuehr.chips.ChipsInputLayout
import com.tylersuehr.chips.data.Chip
import com.tylersuehr.chips.data.ChipSelectionObserver
import io.github.koss.brew.util.PermissionDelegate
import io.github.koss.brew.util.anim.CircularRevealUtil
import io.github.koss.brew.util.anim.RevealAnimationSettings
import io.github.koss.brew.util.anim.StatusBarAnimationSettings
import io.github.koss.brew.util.arch.BrewViewModelFactory
import io.github.koss.brew.util.event.ExitAddDrinkScreen
import io.github.koss.brew.util.event.UiEventBus
import io.github.koss.brew.util.extension.OnTextChanged
import io.github.koss.brew.util.extension.component
import io.github.koss.brew.util.extension.withModels
import io.github.koss.brew.util.nav.BackButtonListener
import kotlinx.android.synthetic.main.fragment_create_drink.*
import kotlinx.android.synthetic.main.fragment_create_drink.view.*
import kotlinx.android.synthetic.main.view_holder_drink_tag_input.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import javax.inject.Inject
import android.content.pm.ActivityInfo
import android.net.Uri
import io.github.koss.brew.ui.main.MainActivity
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import io.github.koss.brew.ui.create.drink.photo.Camera
import io.github.koss.brew.ui.create.drink.photo.Gallery
import io.github.koss.brew.ui.create.drink.photo.PhotoSourceChooser
import io.github.koss.brew.util.GlideImageEngine
import android.provider.Settings
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.*
import com.bumptech.glide.request.target.BitmapImageViewTarget
import io.github.koss.brew.*
import io.github.koss.brew.ui.camera.CameraActivity
import io.github.koss.brew.ui.camera.CameraActivity.Companion.RESULT_PHOTO_URI
import io.github.koss.brew.util.extension.observe
import kotlinx.android.synthetic.main.view_holder_photo_header.view.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.support.v4.startActivityForResult

class AddDrinkFragment : BackButtonListener, Fragment() {

    @Inject lateinit var eventBus: UiEventBus
    @Inject lateinit var viewModelFactory: BrewViewModelFactory
    private lateinit var viewModel: AddDrinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).component.inject(this)
        viewModel = ViewModelProviders
                .of(activity as MainActivity, viewModelFactory)
                .get(AddDrinkViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_drink, container, false)

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

        drinkToolbar.inflateMenu(R.menu.menu_done_button)
        drinkToolbar.onMenuItemClick {
            // There's only one menu item, so assume that means the user's done
            onDonePressed()
        }

        recyclerView.withModels {
            if (viewModel.hasPhoto) {
                photoHeader {
                    id("photo header")
                    photo(viewModel.photo!!)
                    clickListener { _: View ->
                        // No-op: Add another photo when supported
                    }
                }
            } else {
                addPhotoHeader {
                    id("add photo header")
                    clickListener { _: View ->
                        showImageChooser()
                    }
                }
            }

            drinkTitleInput {
                id("title input")
                textWatcher( OnTextChanged {
                    viewModel.postAction(TitleChanged(it.toString()))
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

        // Ensure RecyclerView is built before observing changes
        viewModel.state.observe(this, this::onStateChanged)
    }

    override fun onBackPressed(): Boolean {
        showCircularExit {
            eventBus.postEvent(ExitAddDrinkScreen())
            viewModel.clear()
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            val selected = Matisse.obtainResult(data)
            viewModel.postAction(PhotoChosen(selected.first()))
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            val photoUri = data?.getParcelableExtra<Uri>(RESULT_PHOTO_URI) ?: return
            viewModel.postAction(PhotoChosen(photoUri))
        }
    }

    private fun onStateChanged(state: DrinkState?) {
        state ?: return
        if (recyclerView != null) {
            recyclerView.requestModelBuild()
        }
    }

    private fun onDonePressed() {
        viewModel.postAction(DrinkSubmitted())

        if (viewModel.modelNotEmpty) {
            showCircularExit {
                eventBus.postEvent(ExitAddDrinkScreen())
                viewModel.isViewRevealed = false
                viewModel.clear()
            }
        } else {
            longSnackbar(coordinator, getString(R.string.drink_form_empty_error))
        }
    }

    private fun showImageChooser() {
        PhotoSourceChooser()
                .sources(Camera(), Gallery())
                .onSourceChosen {
                    when (it) {
                        is Camera -> startCamera()
                        is Gallery -> startMatisse()
                    }
                }
                .show(fragmentManager, TAG_PHOTO_SOURCE_CHOOSER)
    }

    private fun startCamera() {
        startActivityForResult<CameraActivity>(REQUEST_CODE_TAKE_PHOTO)
    }

    private fun startMatisse() {
        async(UI) {
            val permissionsGranted = PermissionDelegate().checkPermissions(
                    activity!!,
                    "We need to ask you for some permissions before adding photos",
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permissionsGranted) {
                Matisse.from(this@AddDrinkFragment)
                        .choose(MimeType.of(MimeType.PNG, MimeType.JPEG))
                        .maxSelectable(1)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(GlideImageEngine())
                        .forResult(REQUEST_CODE_CHOOSE)
            } else {
                showPermissionsSnackbar()
            }
        }
    }

    private fun showPermissionsSnackbar() {
        longSnackbar(view!!, "Permissions needed to use gallery", "Settings", {
                    with (Intent()) {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", activity?.packageName, null)
                        startActivity(this)
                    }
                }).show()
    }

    private fun setupChipSelectionObserver(layout: ChipsInputLayout) {
        layout.addChipSelectionObserver( object: ChipSelectionObserver {
            override fun onChipSelected(chip: Chip?) {
                chip ?: return
                viewModel.postAction(TagAdded(chip.title))
            }

            override fun onChipDeselected(chip: Chip?) {
                chip ?: return
                viewModel.postAction(TagRemoved(chip.title))
            }
        })
    }

    private fun PhotoHeaderBindingModelBuilder.photo(uri: Uri) {
        this.onBind { _, view, _ ->
            Glide.with(context!!)
                    .asBitmap()
                    .load(uri)
                    .apply(centerCropTransform())
                    .into(BitmapImageViewTarget(view.dataBinding.root.photoImageView))
        }
    }

    private fun showCircularReveal(view: View) {
        var revealSettings = arguments?.getParcelable(ARG_REVEAL_SETTINGS) as? RevealAnimationSettings ?: return

        // Update RevealSettings to reflect required behavior
        revealSettings = revealSettings.copy(
                duration = 500L,
                targetView = view,
                statusBarAnimationSettings = StatusBarAnimationSettings(
                        startColor = activity?.window?.statusBarColor ?: 0,
                        endColor = ContextCompat.getColor(context!!, R.color.material_white),
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
        var revealSettings = arguments?.getParcelable(ARG_REVEAL_SETTINGS)  as? RevealAnimationSettings ?: return

        // Update RevealSettings to reflect required behavior
        revealSettings = revealSettings.copy(
                duration = 300L,
                targetView = view,
                statusBarAnimationSettings = StatusBarAnimationSettings(
                        startColor = activity?.window?.statusBarColor ?: 0,
                        endColor = ContextCompat.getColor(context!!, R.color.material_white),
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
        const val REQUEST_CODE_TAKE_PHOTO = 111
        const val TAG_PHOTO_SOURCE_CHOOSER = "tag_photo_chooser"
    }
}