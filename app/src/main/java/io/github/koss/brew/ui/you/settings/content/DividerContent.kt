package io.github.koss.brew.ui.you.settings.content

import android.content.Context
import io.github.koss.brew.keylineDivider
import io.github.koss.brew.util.epoxy.BuildCallback
import io.github.koss.brew.util.epoxy.EpoxyContent

class DividerContent(private val id: String): EpoxyContent<Nothing>() {
    override fun generateBuildCallback(context: Context): BuildCallback = {
        keylineDivider {
            id(id)
        }
    }
}