package io.github.koss.brew.ui.you.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import io.github.koss.brew.R
import io.github.koss.brew.data.model.DrinkAdded
import io.github.koss.brew.data.model.ProfileActivity

class FirestoreActivityAdapter(
        options: FirestorePagingOptions<ProfileActivity>,
        private val onItemClicked: (ProfileActivity) -> Unit
): FirestorePagingAdapter<ProfileActivity, ActivityViewHolder>(options) {

    /**
     * For now we're only handling local drinks, so always inflate the Drink ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder =
            DrinkActivityViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.drink_activity_view_holder, parent, false))

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int, model: ProfileActivity) {
        val activity = model as DrinkAdded
        holder.bind(activity = activity, onClick = { onItemClicked(activity) })
    }
}