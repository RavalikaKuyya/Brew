package io.github.koss.brew.di.module

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import io.github.koss.brew.BuildConfig
import io.github.koss.brew.data.remote.DrinkService
import io.github.koss.brew.data.remote.image.imgur.ImgurApi
import io.github.koss.brew.data.remote.image.imgur.ImgurImageService
import io.github.koss.brew.di.scope.ApplicationScope
import io.github.koss.brew.repository.config.PreferencesManager
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
class NetworkModule {

    @Provides
    @ApplicationScope
    fun provideDrinkService(): DrinkService = DrinkService()

    @Provides
    @ApplicationScope
    fun provideImageService(imgurApi: ImgurApi): ImgurImageService = ImgurImageService(imgurApi)

    @Provides
    @ApplicationScope
    fun provideImgurApi(
            @Named("imgur_okhttp_client") okHttpClient: OkHttpClient,
            moshi: Moshi): ImgurApi {

        return Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://api.imgur.com/")
                .build()
                .create(ImgurApi::class.java)
    }


    @Provides
    @Named("imgur_okhttp_client_builder")
    @ApplicationScope
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder {
        val okHttpClient = OkHttpClient.Builder()

        // Add the auth header for all Imgur requests
        okHttpClient.addInterceptor {
            val ongoing = it.request().newBuilder()
            ongoing.addHeader("Authorization", "Client-ID ${BuildConfig.IMGUR_CLIENT_ID}")

            it.proceed(ongoing.build())
        }

        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
        }

        okHttpClient.connectTimeout(15, TimeUnit.SECONDS)
        okHttpClient.readTimeout(15, TimeUnit.SECONDS)

        return okHttpClient
    }

    @Provides
    @Named("imgur_okhttp_client")
    @ApplicationScope
    fun provideOkHttpClient(
            @Named("imgur_okhttp_client_builder") okHttpClientBuilder: OkHttpClient.Builder,
            @Named("imgur_cache") cache: Cache): OkHttpClient {
        return okHttpClientBuilder
                .followRedirects(true)
                .cache(cache)
                .build()
    }

    @Provides
    @Named("imgur_cache")
    @ApplicationScope
    fun provideCache(application: Application): Cache {
        return Cache(
                application.cacheDir,
                10L * 1024L * 1024L) // 10 MB
    }

    @Provides
    @ApplicationScope
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }
}