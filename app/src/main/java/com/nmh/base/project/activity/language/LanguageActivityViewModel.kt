package com.nmh.base.project.activity.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmh.base.project.db.model.LanguageModel
import com.nmh.base.project.activity.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageActivityViewModel @Inject constructor() : ViewModel() {

    private val _uiStateLanguage = MutableStateFlow<UiState<MutableList<LanguageModel>>>(UiState.Loading)
    val uiStateLanguage: StateFlow<UiState<MutableList<LanguageModel>>> = _uiStateLanguage

    fun getAllLanguage() {
        viewModelScope.launch(Dispatchers.IO) {
            DataLanguage.getListLanguage().catch {
                _uiStateLanguage.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateLanguage.value = UiState.Success(it)
            }
        }
    }
}