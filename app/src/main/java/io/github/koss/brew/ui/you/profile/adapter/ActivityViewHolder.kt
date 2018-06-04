package io.github.koss.brew.ui.you.profile.adapter

import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import io.github.koss.brew.data.model.DrinkAdded
import io.github.koss.brew.data.model.ProfileActivity
import kotlinx.android.synthetic.main.drink_activity_view_holder.view.*
import org.jetbrains.anko.dip

sealed class ActivityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun bind(activity: ProfileActivity, onClick: () -> Unit)

    abstract fun clear()
}

class DrinkActivityViewHolder(itemView: View): ActivityViewHolder(itemView) {

    override fun bind(activity: ProfileActivity, onClick: () -> Unit) {
        activity as? DrinkAdded ?: throw IllegalArgumentException("DrinkActivityViewHolder only accepts binding to Drink activity")

        val transform = MultiTransformation(
                CenterCrop(),
                RoundedCorners(itemView.context.dip(4))
        )

        with (itemView) {
            Glide.with(this)
                    .asBitmap()
                    .load(activity.imageLink)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .apply(RequestOptions.bitmapTransform(transform))
                    .into(drinkImageView)

            titleTextView.text = activity.drinkName
            descriptionTextView.text = activity.drinkDescription
            timestampTextView.text = activity.timestamp?.format()
            setOnClickListener { _ -> onClick() }
        }

    }

    override fun clear() {
        with (itemView) {
            Glide.with(this).clear(drinkImageView)
            titleTextView.text = ""
            descriptionTextView.text = ""
            timestampTextView.text = ""
        }
    }

    private fun Timestamp.format(): String {
        val formatString = "ha - d MMM"
        return DateFormat.format(formatString, toDate()).toString()
    }

}