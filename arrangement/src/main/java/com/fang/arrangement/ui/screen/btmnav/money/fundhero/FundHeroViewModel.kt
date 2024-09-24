package com.fang.arrangement.ui.screen.btmnav.money.fundhero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetFundHero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class FundHeroViewModel(sheetRepository: SheetRepository) : ViewModel() {
    init {
        viewModelScope.launch {
            sheetRepository.workSheets
                .mapLatest { workSheets ->
                    workSheets?.sheetFundHero()?.values?.sortedByDescending { it.millis }
                }
                .filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest {
                }
        }
    }
}
