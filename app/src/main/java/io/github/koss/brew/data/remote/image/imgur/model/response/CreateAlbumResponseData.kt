package io.github.koss.brew.data.remote.image.imgur.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response from album creation
 */
@JsonClass(generateAdapter = true)
data class CreateAlbumResponseData(

        val id: String,

        @Json(name = "deletehash")
        val deleteHash: String
)