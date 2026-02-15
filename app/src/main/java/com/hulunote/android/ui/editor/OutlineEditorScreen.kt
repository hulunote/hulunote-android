package com.hulunote.android.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hulunote.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlineEditorScreen(
    noteId: String,
    noteTitle: String,
    onBack: () -> Unit,
    viewModel: OutlineEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequesters = remember { mutableMapOf<String, FocusRequester>() }

    // Handle focus requests
    LaunchedEffect(uiState.focusNodeId) {
        val focusId = uiState.focusNodeId ?: return@LaunchedEffect
        // Small delay to let composition complete
        kotlinx.coroutines.delay(100)
        focusRequesters[focusId]?.requestFocus()
        viewModel.clearFocusRequest()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        noteTitle,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::loadNavs) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurpleStart,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val lastNode = uiState.displayList.lastOrNull()
                    if (lastNode != null) {
                        viewModel.createNewBlock(lastNode.id)
                    } else {
                        viewModel.createFirstBlock()
                    }
                },
                containerColor = PurpleStart,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add block")
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PurpleStart,
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = viewModel::loadNavs) {
                            Text("Retry", color = PurpleStart)
                        }
                    }
                }
                uiState.displayList.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable { viewModel.createFirstBlock() },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Empty outline", color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap + to add a block", color = TextMuted)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            top = 16.dp,
                            bottom = 80.dp,
                        ),
                    ) {
                        itemsIndexed(
                            items = uiState.displayList,
                            key = { _, node -> node.id },
                        ) { index, node ->
                            val focusRequester = remember { FocusRequester() }
                            focusRequesters[node.id] = focusRequester

                            OutlineBlockItem(
                                node = node,
                                focusRequester = focusRequester,
                                onContentChange = { content ->
                                    viewModel.onContentChange(node.id, content)
                                },
                                onEnterKey = {
                                    viewModel.createNewBlock(node.id)
                                },
                                onBackspaceEmpty = {
                                    viewModel.deleteBlock(node.id)
                                },
                                onTab = {
                                    viewModel.indentBlock(node.id)
                                },
                                onShiftTab = {
                                    viewModel.outdentBlock(node.id)
                                },
                                onToggleCollapse = {
                                    viewModel.toggleCollapse(node.id)
                                },
                                onFocused = {},
                            )
                        }
                    }
                }
            }
        }
    }
}
