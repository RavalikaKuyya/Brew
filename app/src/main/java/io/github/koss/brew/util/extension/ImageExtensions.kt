package io.github.koss.brew.util.extension

import android.net.Uri
import android.support.annotation.WorkerThread
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@WorkerThread
fun Uri.toMultipartImage(): MultipartBody.Part {
    val imageFile = File(path)
    val reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile)
    return MultipartBody.Part.createFormData("image", imageFile.name, reqFile)
}