package io.github.koss.brew.ui.create.drink.simple

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.tylersuehr.chips.ChipsInputLayout
import com.tylersuehr.chips.data.Chip
import com.tylersuehr.chips.data.ChipSelectionObserver
import io.github.koss.brew.BrewApp
import io.github.koss.brew.R
import kotlinx.android.synthetic.main.fragment_simple_add_drink.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import javax.inject.Inject
import io.github.koss.brew.ui.camera.CameraActivity
import io.github.koss.brew.ui.create.drink.AddDrinkFragment
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivityForResult


class SimpleAddDrinkBottomSheetDialogFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SimpleAddDrinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_BottomSheetDialog)
        (activity!!.application as BrewApp).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SimpleAddDrinkViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_simple_add_drink, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        okButton.onClick { _ ->
            viewModel.addDrink(
                    title = (titleTextInputLayout.editText?.text ?: "").toString(),
                    description = (descriptionTextInputLayout.editText?.text ?: "").toString()
            )
            dismiss()
        }

        addPictureButton.onClick {
            startActivityForResult<CameraActivity>(AddDrinkFragment.REQUEST_CODE_TAKE_PHOTO)
        }

        setupChipSelectionObserver(chipsInputLayout)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddDrinkFragment.REQUEST_CODE_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val photoUri = data?.getParcelableExtra<Uri>(CameraActivity.RESULT_PHOTO_URI) ?: return
            onPictureTaken(photoUri)
        }
    }

    private fun onPictureTaken(photoUri: Uri) {
        viewModel.onPhotoTaken(photoUri)

        // Hide Take picture views and remove click listener
        photoIcon.visibility = View.GONE
        takePictureTextView.visibility = View.GONE
        addPictureButton.setOnClickListener(null)
        addPictureButton.isClickable = false

        val transform = MultiTransformation(
                CenterCrop(),
                RoundedCorners(dip(4))
        )

        // Load photo into imageView
        Glide.with(this)
                .asBitmap()
                .load(photoUri)
                .apply(RequestOptions.bitmapTransform(transform))
                .into(pictureImageView)
    }

    private fun setupChipSelectionObserver(layout: ChipsInputLayout) {
        layout.addChipSelectionObserver(object : ChipSelectionObserver {
            override fun onChipSelected(chip: Chip?) {
                chip ?: return
                viewModel.tagAdded(chip.title)
            }

            override fun onChipDeselected(chip: Chip?) {
                chip ?: return
                viewModel.tagRemoved(chip.title)
            }
        })
    }
}