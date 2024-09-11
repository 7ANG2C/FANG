package com.fang.arrangement.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.sheet.SheetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * https://docs.google.com/spreadsheets/d/1hYhuc7IYnVkjx6qK7WePQiTF7Jw9ZUwC-pU8DMVcNdI/pubhtml
 */
internal class ArrangementViewModel(
    private val repository: SheetRepository,
) : ViewModel() {
    private val _initialized = MutableStateFlow(false)
    val initialized = _initialized.asStateFlow()

    init {
        viewModelScope.launch {
            repository
                .workSheet
                .distinctUntilChangedBy { it != null }
                .flowOn(Dispatchers.Default)
                .collectLatest {
                    if (it != null) _initialized.value = true
                }
        }
    }
}
