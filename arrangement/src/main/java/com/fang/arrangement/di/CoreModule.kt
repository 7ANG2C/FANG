package com.fang.arrangement.di

import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.cosmos.definition.CosmosDef
import com.fang.cosmos.definition.externalcoroutinescope.createExternalCoroutineScope
import org.koin.dsl.module

internal object CoreModule {
    operator fun invoke() =
        module {
            single { CosmosDef.Gson }
            single {
                SheetRepository(
                    context = get(),
                    coroutineScope = createExternalCoroutineScope(),
                    gson = get(),
                )
            }
        }
}
