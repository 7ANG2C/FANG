package com.fang.arrangement

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.fang.arrangement.di.CoreModule
import com.fang.arrangement.di.RepositoryModule
import com.fang.arrangement.di.ViewModelModule
import com.fang.cosmos.definition.CosmosModule
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

internal class ArrangementApplication :
    Application(),
    SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ArrangementApplication)
            modules(
                CosmosModule(),
                CoreModule(),
                RepositoryModule(),
                ViewModelModule(),
            )
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader
            .Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = {
                            OkHttpClient
                                .Builder()
                                .connectTimeout(30, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .writeTimeout(30, TimeUnit.SECONDS)
                                .build()
                        },
                    ),
                )
            }.build()
}
