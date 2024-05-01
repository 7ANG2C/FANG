package com.fang.arrangement

import android.app.Application
import com.fang.arrangement.di.CoreModule
import com.fang.arrangement.di.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ArrangementApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ArrangementApplication)
            modules(
                CoreModule(),
                ViewModelModule(),
            )
        }
    }

}