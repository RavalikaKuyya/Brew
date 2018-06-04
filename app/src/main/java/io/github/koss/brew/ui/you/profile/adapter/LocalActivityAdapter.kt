package io.github.koss.brew.ui.you.profile.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.koss.brew.R
import io.github.koss.brew.data.model.DrinkAdded
import io.github.koss.brew.data.model.ProfileActivity

class LocalActivityAdapter(private val onItemClicked: (ProfileActivity) -> Unit): PagedListAdapter<ProfileActivity, ActivityViewHolder>(DIFF_CALLBACK) {

    /**
     * For now we're only handling local drinks, so always inflate the Drink ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder =
            DrinkActivityViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.drink_activity_view_holder, parent, false))

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = getItem(position)
        if (activity != null) {
            holder.bind(activity = activity, onClick = { onItemClicked(activity) })
        } else {
            holder.clear()
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProfileActivity>() {
            override fun areItemsTheSame(old: ProfileActivity,
                                         new: ProfileActivity): Boolean {
                if (old is DrinkAdded && new is DrinkAdded) {
                    return old.drinkId == new.drinkId
                }

                return false
            }


            override fun areContentsTheSame(old: ProfileActivity,
                                            new: ProfileActivity): Boolean =
                    old == new
        }
    }
}