package com.fang.arrangement

import android.app.Application
import com.fang.arrangement.di.CoreModule
import com.fang.arrangement.di.ViewModelModule
import com.fang.cosmos.definition.CosmosModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

internal class ArrangementApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ArrangementApplication)
            modules(
                CosmosModule(),
                CoreModule(),
                ViewModelModule(),
            )
        }
    }

}