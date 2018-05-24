package io.github.koss.brew.data.remote.image.imgur.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response data for uploading images to the Imgur API endpoint. This
 * model lacks a lot of unnecessary detail provided by the API
 */
@JsonClass(generateAdapter = true)
data class UploadImageResponseData(

        val id: String,

        @Json(name = "deletehash")
        val deleteHash: String,

        val link: String
)