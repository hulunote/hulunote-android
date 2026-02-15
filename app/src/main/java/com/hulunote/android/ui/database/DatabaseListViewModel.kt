package com.hulunote.android.ui.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulunote.android.data.model.DatabaseInfo
import com.hulunote.android.data.repository.AuthRepository
import com.hulunote.android.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DatabaseListUiState(
    val databases: List<DatabaseInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class DatabaseListViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DatabaseListUiState())
    val uiState: StateFlow<DatabaseListUiState> = _uiState.asStateFlow()

    init {
        loadDatabases()
    }

    fun loadDatabases() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = databaseRepository.getDatabaseList()
            result.fold(
                onSuccess = { databases ->
                    _uiState.value = _uiState.value.copy(
                        databases = databases.filter { !it.isDelete },
                        isLoading = false,
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load databases",
                    )
                }
            )
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
