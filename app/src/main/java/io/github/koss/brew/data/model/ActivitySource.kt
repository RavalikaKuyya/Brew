package io.github.koss.brew.data.model

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import com.google.firebase.firestore.Query

sealed class ActivitySource

data class LocalOnly(
        val source: LiveData<PagedList<ProfileActivity>>
): ActivitySource()

data class FirestoreOnly(
        val query: Query,
        val config: PagedList.Config
): ActivitySource()

class NoActivity: ActivitySource()