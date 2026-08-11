package com.fang.arrangement.di

import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.storage.AttendanceImageRepository
import com.fang.cosmos.definition.externalcoroutinescope.createExternalCoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal object CoreModule {
    operator fun invoke() =
        module {
            single {
                SheetRepository(
                    coroutineScope = createExternalCoroutineScope(),
                )
            }
            single { AttendanceImageRepository(context = androidContext()) }
        }
}
