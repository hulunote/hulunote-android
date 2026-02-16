package com.hulunote.android.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulunote.android.data.model.NavInfo
import com.hulunote.android.data.repository.NavRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorUiState(
    val navList: List<NavInfo> = emptyList(),
    val displayList: List<OutlineNode> = emptyList(),
    val rootNavId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val focusNodeId: String? = null,
    val collapsedIds: Set<String> = emptySet(),
)

@HiltViewModel
class OutlineEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val navRepository: NavRepository,
) : ViewModel() {

    val noteId: String = savedStateHandle.get<String>("noteId") ?: ""

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val saveJobs = mutableMapOf<String, Job>()

    init {
        loadNavs()
    }

    fun loadNavs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = navRepository.getNavList(noteId)
            result.fold(
                onSuccess = { navList ->
                    // Find root nav: parid is null, blank, self-referencing, or nil UUID
                    val nilUuid = "00000000-0000-0000-0000-000000000000"
                    val rootNav = navList.find {
                        it.parid == null || it.parid == it.id || it.parid.isBlank() || it.parid == nilUuid
                    }
                    val rootNavId = rootNav?.id

                    _uiState.value = _uiState.value.copy(
                        navList = navList,
                        rootNavId = rootNavId,
                        isLoading = false,
                    )
                    rebuildDisplayList()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load outline",
                    )
                }
            )
        }
    }

    private fun rebuildDisplayList() {
        val state = _uiState.value
        val displayList = OutlineTree.buildDisplayList(
            state.navList,
            state.rootNavId,
            state.collapsedIds,
        )
        _uiState.value = state.copy(displayList = displayList)
    }

    fun onContentChange(navId: String, content: String) {
        // Update local state immediately
        val updatedNavList = _uiState.value.navList.map { nav ->
            if (nav.id == navId) nav.copy(content = content) else nav
        }
        _uiState.value = _uiState.value.copy(navList = updatedNavList)
        rebuildDisplayList()

        // Debounced save to server
        saveJobs[navId]?.cancel()
        saveJobs[navId] = viewModelScope.launch {
            delay(500)
            navRepository.updateNav(noteId = noteId, navId = navId, content = content)
        }
    }

    fun createNewBlock(afterNodeId: String) {
        val state = _uiState.value
        val displayList = state.displayList
        val currentIndex = displayList.indexOfFirst { it.id == afterNodeId }
        if (currentIndex < 0) return

        val currentNode = displayList[currentIndex]
        val nextSibling = OutlineTree.findNextSibling(displayList, currentIndex)
        val newOrder = OutlineTree.orderBetween(currentNode.order, nextSibling?.order)

        viewModelScope.launch {
            val result = navRepository.createNav(
                noteId = noteId,
                parid = currentNode.parid,
                content = "",
                order = newOrder,
            )
            result.fold(
                onSuccess = { response ->
                    val newNavId = response.id ?: return@fold
                    // Add to local list and refresh
                    val newNav = NavInfo(
                        id = newNavId,
                        parid = currentNode.parid,
                        sameDeepOrder = newOrder,
                        content = "",
                        noteId = noteId,
                    )
                    _uiState.value = _uiState.value.copy(
                        navList = _uiState.value.navList + newNav,
                        focusNodeId = newNavId,
                    )
                    rebuildDisplayList()
                },
                onFailure = { /* silently fail */ }
            )
        }
    }

    fun deleteBlock(navId: String) {
        val state = _uiState.value
        val displayList = state.displayList
        val currentIndex = displayList.indexOfFirst { it.id == navId }
        if (currentIndex < 0) return

        // Focus previous block
        val focusId = if (currentIndex > 0) displayList[currentIndex - 1].id else null

        viewModelScope.launch {
            navRepository.updateNav(noteId = noteId, navId = navId, isDelete = true)
            _uiState.value = _uiState.value.copy(
                navList = _uiState.value.navList.map { nav ->
                    if (nav.id == navId) nav.copy(isDelete = true) else nav
                },
                focusNodeId = focusId,
            )
            rebuildDisplayList()
        }
    }

    fun indentBlock(navId: String) {
        val state = _uiState.value
        val displayList = state.displayList
        val currentIndex = displayList.indexOfFirst { it.id == navId }
        if (currentIndex <= 0) return

        // Find previous sibling to become new parent
        val prevSibling = OutlineTree.findPreviousSibling(displayList, currentIndex) ?: return

        viewModelScope.launch {
            val result = navRepository.updateNav(
                noteId = noteId,
                navId = navId,
                parid = prevSibling.id,
                order = 1f,
            )
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    navList = _uiState.value.navList.map { nav ->
                        if (nav.id == navId) nav.copy(parid = prevSibling.id, sameDeepOrder = 1f) else nav
                    },
                    focusNodeId = navId,
                )
                rebuildDisplayList()
            }
        }
    }

    fun outdentBlock(navId: String) {
        val state = _uiState.value
        val currentNav = state.navList.find { it.id == navId } ?: return
        val parentNav = state.navList.find { it.id == currentNav.parid } ?: return
        val grandParentId = parentNav.parid ?: return

        viewModelScope.launch {
            val result = navRepository.updateNav(
                noteId = noteId,
                navId = navId,
                parid = grandParentId,
                order = parentNav.sameDeepOrder + 0.5f,
            )
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    navList = _uiState.value.navList.map { nav ->
                        if (nav.id == navId) nav.copy(
                            parid = grandParentId,
                            sameDeepOrder = parentNav.sameDeepOrder + 0.5f,
                        ) else nav
                    },
                    focusNodeId = navId,
                )
                rebuildDisplayList()
            }
        }
    }

    fun toggleCollapse(navId: String) {
        val state = _uiState.value
        val newCollapsed = if (navId in state.collapsedIds) {
            state.collapsedIds - navId
        } else {
            state.collapsedIds + navId
        }
        _uiState.value = state.copy(collapsedIds = newCollapsed)
        rebuildDisplayList()
    }

    fun clearFocusRequest() {
        _uiState.value = _uiState.value.copy(focusNodeId = null)
    }

    fun createFirstBlock() {
        val rootNavId = _uiState.value.rootNavId ?: return
        viewModelScope.launch {
            val result = navRepository.createNav(
                noteId = noteId,
                parid = rootNavId,
                content = "",
                order = 1f,
            )
            result.onSuccess { response ->
                val newNavId = response.id ?: return@onSuccess
                val newNav = NavInfo(
                    id = newNavId,
                    parid = rootNavId,
                    sameDeepOrder = 1f,
                    content = "",
                    noteId = noteId,
                )
                _uiState.value = _uiState.value.copy(
                    navList = _uiState.value.navList + newNav,
                    focusNodeId = newNavId,
                )
                rebuildDisplayList()
            }
        }
    }
}
