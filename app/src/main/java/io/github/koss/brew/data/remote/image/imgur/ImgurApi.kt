package io.github.koss.brew.data.remote.image.imgur

import io.github.koss.brew.data.remote.image.imgur.model.request.CreateAlbumRequest
import io.github.koss.brew.data.remote.image.imgur.model.response.BasicResponse
import io.github.koss.brew.data.remote.image.imgur.model.response.CreateAlbumResponseData
import io.github.koss.brew.data.remote.image.imgur.model.response.CreditData
import io.github.koss.brew.data.remote.image.imgur.model.response.UploadImageResponseData
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * REST API interface for accessing the Imgur service.
 */
interface ImgurApi {

    /**
     * Creates an album given the request
     */
    @POST("/3/album")
    fun createAlbum(@Body createAlbumRequest: CreateAlbumRequest): Single<BasicResponse<CreateAlbumResponseData>>

    /**
     * Deletes an album given the delete hash
     */
    @DELETE("/3/album/{albumDeleteHash}")
    fun deleteAlbum(@Path("albumDeleteHash") albumDeleteHash: String): Single<BasicResponse<Boolean>>

    /**
     * Uploads an image to Imgur. This service method should be preferred over the querymap counterpart
     */
    @Multipart
    @POST("/3/image")
    fun uploadImage(@Part file: MultipartBody.Part, @Query("album") albumDeleteHash: String): Single<BasicResponse<UploadImageResponseData>>

    /**
     * Uploads an image to Imgur
     */
    @Multipart
    @POST("/3/image")
    fun uploadImage(@Part file: MultipartBody.Part, @QueryMap queryMap: Map<String, String>): Single<BasicResponse<UploadImageResponseData>>

    /**
     * Deletes an image given the delete hash
     */
    @DELETE("/3/image/{imageDeleteHash}")
    fun deleteImage(@Path("imageDeleteHash") imageDeleteHash: String): Single<BasicResponse<Boolean>>

    /**
     * Gets remaining credits. Does not deduct from Imgur API quota
     */
    @GET("/3/credits")
    fun getRemainingCredits(): Single<BasicResponse<CreditData>>

}