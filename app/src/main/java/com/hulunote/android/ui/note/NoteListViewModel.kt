package com.hulunote.android.ui.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulunote.android.data.model.NoteInfo
import com.hulunote.android.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteListUiState(
    val notes: List<NoteInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val newNoteTitle: String = "",
)

@HiltViewModel
class NoteListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
) : ViewModel() {

    val databaseId: String = savedStateHandle.get<String>("databaseId") ?: ""

    private val _uiState = MutableStateFlow(NoteListUiState())
    val uiState: StateFlow<NoteListUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = noteRepository.getNoteList(databaseId)
            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        notes = response.noteList.filter { !it.isDelete }
                            .sortedByDescending { it.updatedAt },
                        isLoading = false,
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load notes",
                    )
                }
            )
        }
    }

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true, newNoteTitle = "")
    }

    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false, newNoteTitle = "")
    }

    fun updateNewNoteTitle(title: String) {
        _uiState.value = _uiState.value.copy(newNoteTitle = title)
    }

    fun createNote() {
        val title = _uiState.value.newNoteTitle.trim()
        if (title.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = noteRepository.createNote(databaseId, title)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(showCreateDialog = false, newNoteTitle = "")
                    loadNotes()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to create note",
                    )
                }
            )
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            noteRepository.updateNote(noteId, isDelete = true)
            loadNotes()
        }
    }
}
