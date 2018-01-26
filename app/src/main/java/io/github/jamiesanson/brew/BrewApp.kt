package io.github.jamiesanson.brew

import android.app.Application
import io.github.jamiesanson.brew.di.component.ApplicationComponent
import io.github.jamiesanson.brew.di.component.DaggerApplicationComponent
import io.github.jamiesanson.brew.di.module.ApplicationModule
import io.github.jamiesanson.brew.util.QuicksandRegular
import io.github.jamiesanson.brew.util.RobotoMonoRegular
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class BrewApp: Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath(QuicksandRegular().path)
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }

}