package io.github.jamiesanson.brew.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.jamiesanson.brew.di.scope.ApplicationScope
import io.github.jamiesanson.brew.util.nav.LocalCiceroneCache

@Module(includes = [ViewModelModule::class])
class ApplicationModule(private val application: Application) {

    @Provides
    @ApplicationScope
    fun provideApplicationContext(): Context = application

    @Provides
    @ApplicationScope
    fun provideApplication(): Application = application

    @Provides
    @ApplicationScope
    fun provideLocalCiceroneCache(): LocalCiceroneCache = LocalCiceroneCache()

}