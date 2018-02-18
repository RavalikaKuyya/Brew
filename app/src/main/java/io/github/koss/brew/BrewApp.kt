package io.github.koss.brew

import android.app.Application
import io.github.koss.brew.di.component.ApplicationComponent
import io.github.koss.brew.di.component.DaggerApplicationComponent
import io.github.koss.brew.di.module.ApplicationModule
import io.github.koss.brew.util.QuicksandRegular
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class BrewApp: Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath(QuicksandRegular.path)
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }

    companion object {
        lateinit var INSTANCE: BrewApp
    }
}