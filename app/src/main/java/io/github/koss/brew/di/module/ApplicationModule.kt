package io.github.koss.brew.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.koss.brew.di.scope.ApplicationScope
import io.github.koss.brew.util.arch.EventBus
import io.github.koss.brew.util.event.UiEventBus
import io.github.koss.brew.util.nav.LocalCiceroneCache

@Module(includes = [
    ViewModelModule::class,
    ContentModule::class
])
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

    @Provides
    @ApplicationScope
    fun provideUiEventBus(): UiEventBus = UiEventBus()

    @Provides
    @ApplicationScope
    fun provideEventBus(): EventBus = EventBus()
}