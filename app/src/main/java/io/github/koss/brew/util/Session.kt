package io.github.koss.brew.util

import com.google.firebase.auth.FirebaseAuth

class Session {

    val isLoggedIn: Boolean
        get() = FirebaseAuth.getInstance().currentUser != null
}