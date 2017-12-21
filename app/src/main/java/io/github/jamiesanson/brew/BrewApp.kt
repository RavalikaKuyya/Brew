package io.github.jamiesanson.brew

import android.app.Application
import io.github.jamiesanson.brew.di.component.ApplicationComponent
import io.github.jamiesanson.brew.di.component.DaggerApplicationComponent

class BrewApp: Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent
                .builder()
                .build()
    }

}