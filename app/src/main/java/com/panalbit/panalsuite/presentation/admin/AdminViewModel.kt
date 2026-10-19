package com.panalbit.panalsuite.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.domain.model.User
import com.panalbit.panalsuite.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class AdminUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    val uiState: StateFlow<AdminUiState> = getUsersUseCase()
        .map { list -> AdminUiState(users = list, isLoading = false) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AdminUiState()
        )
}
