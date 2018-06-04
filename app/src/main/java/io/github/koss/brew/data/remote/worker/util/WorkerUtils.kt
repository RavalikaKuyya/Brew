package io.github.koss.brew.data.remote.worker.util

import android.app.Application
import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import io.github.koss.brew.data.remote.image.imgur.ImgurImageService
import io.github.koss.brew.di.component.ApplicationComponent
import io.github.koss.brew.di.component.DaggerApplicationComponent
import io.github.koss.brew.di.module.ApplicationModule
import io.github.koss.brew.repository.drinks.DrinkRepository

// Stash the component statically - The Application Context is fine to be static as when it's cleared,
// as is the Java Heap
private var applicationComponent: ApplicationComponent? = null

/**
 * Function for getting an Imgur Image Service implementation from the application context
 */
fun Context.getImgurApi(): ImgurImageService {
    if (applicationComponent == null) {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this as Application))
                .build()
    }

    return applicationComponent!!.imageService()
}

/**
 * Function for getting a DrinkTable repository implementation from the application context
 */
fun Context.getDrinkRepository(): DrinkRepository {
    if (applicationComponent == null) {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this as Application))
                .build()
    }

    return applicationComponent!!.drinkRepository()
}

/**
 * Runs a task synchronously
 */
fun <T> Task<T>.await(): T = Tasks.await(this)