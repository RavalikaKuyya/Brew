package io.github.koss.brew.data.remote.image.imgur.model.response

import com.squareup.moshi.JsonClass

/**
 * The base of most of Imgur's responses. All data classes must use
 * the [com.squareup.moshi.JsonClass] annotation
 */
@JsonClass(generateAdapter = true)
data class BasicResponse<T>(

        /**
         * The response data
         */
        val data: T,

        /**
         * Whether or not the response was successful
         */
        val success: Boolean,

        /**
         * The HTTP status
         */
        val status: Int
)