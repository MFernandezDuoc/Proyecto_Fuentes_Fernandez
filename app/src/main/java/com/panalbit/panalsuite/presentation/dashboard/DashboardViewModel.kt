package com.panalbit.panalsuite.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.domain.model.DashboardStats
import com.panalbit.panalsuite.domain.usecase.GetDashboardStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DashboardUiState(
    val stats: DashboardStats? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getDashboardStatsUseCase: GetDashboardStatsUseCase
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = getDashboardStatsUseCase()
        .map { stats -> DashboardUiState(stats = stats, isLoading = false) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DashboardUiState()
        )
}
