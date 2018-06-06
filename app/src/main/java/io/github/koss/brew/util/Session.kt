package io.github.koss.brew.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Completable
import io.reactivex.Maybe

object Session {

    val isLoggedIn: Boolean
        get() = user != null

    val user: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser
}

fun FirebaseUser.getAlbumDeleteHash(): Maybe<String> {
    // TODO - Make this work by creating an album is not present
    return Maybe.just("")
}

fun FirebaseUser.setAlbumDetails(albumId: String, albumDeleteHash: String): Completable {
    TODO()
}