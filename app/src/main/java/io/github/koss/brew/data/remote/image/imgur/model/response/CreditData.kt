package io.github.koss.brew.data.remote.image.imgur.model.response

import com.squareup.moshi.Json
/**
 * Credit data detailing remaining quota
 */
data class CreditData(
        @Json(name = "UserLimit")
        val userLimit: Int,

        @Json(name = "UserRemaining")
        val userRemaining: Int,

        @Json(name = "UserReset")
        val userReset: Long,

        @Json(name = "ClientLimit")
        val clientLimit: Int,

        @Json(name = "ClientRemaining")
        val clientRemaining: Int
)