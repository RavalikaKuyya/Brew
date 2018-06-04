package io.github.koss.brew.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import io.github.koss.brew.data.remote.worker.util.*

sealed class ProfileActivity

data class DrinkAdded(
        @PropertyName(KEY_ACTIVITY_TYPE)
        var activityType: String = "drink",

        @PropertyName(KEY_IMAGE_LINK)
        var imageLink: String = "",

        @PropertyName(KEY_NAME)
        var drinkName: String = "",

        @PropertyName(KEY_DESCRIPTION)
        var drinkDescription: String = "",

        @PropertyName(KEY_DRINK_ID)
        var drinkId: String = "",

        @ServerTimestamp
        var timestamp: Timestamp? = null
): ProfileActivity()