package com.healthai.app.ui.screens.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.repository.DietRepository
import com.healthai.app.domain.model.DietPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DietPlannerViewModel : ViewModel() {

    private val dietRepository = DietRepository()

    private val _dietPlan = MutableStateFlow<DietPlan?>(null)
    val dietPlan = _dietPlan.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchDietPlan()
    }

    private fun fetchDietPlan() {
        viewModelScope.launch {
            _isLoading.value = true
            _dietPlan.value = dietRepository.getDietPlan()
            _isLoading.value = false
        }
    }
}