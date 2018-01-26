package io.github.jamiesanson.brew.util.anim
/*
 * Copyright (C) 2016 The Android Open Source Project
 * Copyright (C) 2016 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific languag`e governing permissions and
 * limitations under the License.
 */


import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View


class GravitySnapHelper @JvmOverloads constructor(
        gravity: Int,
        enableSnapLastItem: Boolean = false,
        snapListener: SnapListener? = null) : LinearSnapHelper() {

    private val delegate: GravityDelegate = GravityDelegate(gravity, enableSnapLastItem, snapListener)

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        delegate.attachToRecyclerView(recyclerView)
        super.attachToRecyclerView(recyclerView)
    }

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager,
                                              targetView: View): IntArray? {
        return delegate.calculateDistanceToFinalSnap(layoutManager, targetView)
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return delegate.findSnapView(layoutManager)
    }

    /**
     * Enable snapping of the last item that's snappable.
     * The default value is false, because you can't see the last item completely
     * if this is enabled.
     *
     * @param snap true if you want to enable snapping of the last snappable item
     */
    fun enableLastItemSnap(snap: Boolean) {
        delegate.enableLastItemSnap(snap)
    }

    interface SnapListener {
        fun onSnap(position: Int)
    }

}
