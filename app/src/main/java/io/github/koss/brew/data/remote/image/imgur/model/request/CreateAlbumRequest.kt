package io.github.koss.brew.data.remote.image.imgur.model.request

import com.squareup.moshi.JsonClass

/**
 *  Create a new album. Optional parameter of ids[] is an array of image ids to add to the album.
 *  If uploading anonymous images to an anonymous album please use the optional parameter of deletehashes[]
 *  rather than ids[]. Note: including the optional deletehashes[] parameter will also work for authenticated
 *  user albums. There is no need to duplicate image ids with their corresponding deletehash.
 */
@JsonClass(generateAdapter = true)
data class CreateAlbumRequest(
        /**
         * The image ids that you want to be included in the album.
         */
        val ids: List<String> = emptyList(),

        /**
         * The deletehashes of the images that you want to be included in the album.
         */
        val deleteHashes: List<String> = emptyList(),

        /**
         * The title of the album
         */
        val title: String = "",

        /**
         * The description of the album
         */
        val description: String = "",

        /**
         * Sets the privacy level of the album. Values are :
         *          public | hidden | secret.
         *
         * Defaults to hidden for all users and should probably never be changed.
         */
        val privacy: String = "hidden"
)