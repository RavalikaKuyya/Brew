package io.github.koss.brew.ui.you.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.koss.brew.data.local.dao.DrinkDao
import io.github.koss.brew.data.model.ActivitySource
import io.github.koss.brew.data.model.FirestoreOnly
import io.github.koss.brew.data.model.LocalOnly
import io.github.koss.brew.data.model.NoActivity
import io.github.koss.brew.data.remote.error.NotLoggedInException
import io.github.koss.brew.data.remote.worker.util.await
import io.github.koss.brew.repository.activity.ActivityRepository
import io.github.koss.brew.repository.config.ConfigurationWrapper
import io.github.koss.brew.util.SingleLiveEvent
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val drinkDao: DrinkDao
): ViewModel() {

    val activityType: LiveData<ActivitySource> = MutableLiveData()

    fun loadActivity() {
        launch {
            val configWrapper = ConfigurationWrapper.blockingFetch()

            if (!configWrapper.shouldSyncDrinks) {
                if (drinkDao.getAllDrinks().isEmpty()) {
                    (activityType as MutableLiveData).postValue(NoActivity())
                } else {
                    (activityType as MutableLiveData).postValue(
                            LocalOnly(source = activityRepository.getLocalActivity())
                    )
                }
            } else {
                val user = FirebaseAuth.getInstance().currentUser ?: throw NotLoggedInException()

                val query = FirebaseFirestore.getInstance()
                        .collection("users/${user.uid}/activity")
                        .orderBy("timestamp", Query.Direction.DESCENDING)

                val config = PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPrefetchDistance(10)
                        .setPageSize(20)
                        .build()

                if (query.limit(1).get().await().isEmpty) {
                    (activityType as MutableLiveData).postValue(NoActivity())
                } else {
                    (activityType as MutableLiveData).postValue(
                            FirestoreOnly(
                                    query = query,
                                    config = config
                            )
                    )
                }
            }
        }
    }
}