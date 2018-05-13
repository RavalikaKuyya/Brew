package io.github.koss.brew.ui.thanks.contributors

import android.support.v4.app.Fragment
import com.airbnb.epoxy.CarouselModelBuilder
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.EpoxyController
import io.github.koss.brew.IconThanksCarouselItemBindingModel_
import io.github.koss.brew.R
import io.github.koss.brew.clickableCell
import io.github.koss.brew.util.epoxy.BuildCallback
import kotlinx.android.synthetic.main.view_holder_icon_thanks_carousel_item.view.*
import org.jetbrains.anko.support.v4.browse

/**
 * Definition of all icon contributors. Ignore all the hardcoded strings
 */
object DesignContributors {
    val contributors: List<DesignContributor> = listOf(
            DesignContributor("Zoë Austin from the Noun Project (Adapted)", listOf(R.mipmap.ic_launcher), "https://thenounproject.com/zoeaustin/"),
            DesignContributor("icon 54 from the Noun Project", listOf(R.drawable.ic_anonymous), "https://thenounproject.com/icon54app/"),
            DesignContributor("Lucas fhñe from the Noun Project", listOf(R.drawable.ic_empty_mug), "https://thenounproject.com/lucas124/"),
            DesignContributor("Emma Mitchell from the Noun Project", listOf(R.drawable.ic_no_profile_picture), "https://thenounproject.com/emmamitchell/")
    )
}

/**
 * Class for a single contributor, which also defines how to build an epoxy model for itself
 */
class DesignContributor(
        private val displayName: String,
        private val icons: List<Int>,
        private val link: String? = null
) {

    fun generateBuildCallback(fragment: Fragment): BuildCallback = {
        clickableCell {
            id(displayName)
            name(displayName)
            onClick { _ ->
                link?.let { fragment.browse(link, newTask = true) }
            }
        }

        carousel {
            id("$displayName carousel")
            hasFixedSize(true)
            models(icons.map { id ->
                IconThanksCarouselItemBindingModel_().apply {
                    id(id)
                    photo(id)
                }
            })
        }
    }

    private fun IconThanksCarouselItemBindingModel_.photo(resource: Int) {
        this.onBind { _, view, _ ->
            view.dataBinding.root.iconImageView.setImageResource(resource)
        }
    }

    /**
     * Custom epoxy carousel DSL
     */
    private inline fun EpoxyController.carousel(modelInitializer: CarouselModelBuilder.() -> Unit) {
        CarouselModel_().apply {
            modelInitializer()
        }.addTo(this)
    }
}