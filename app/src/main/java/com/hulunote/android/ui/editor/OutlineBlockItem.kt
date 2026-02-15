package com.hulunote.android.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.hulunote.android.ui.theme.*

@Composable
fun OutlineBlockItem(
    node: OutlineNode,
    focusRequester: FocusRequester,
    onContentChange: (String) -> Unit,
    onEnterKey: () -> Unit,
    onBackspaceEmpty: () -> Unit,
    onTab: () -> Unit,
    onShiftTab: () -> Unit,
    onToggleCollapse: () -> Unit,
    onFocused: () -> Unit,
) {
    var textFieldValue by remember(node.id, node.content) {
        mutableStateOf(TextFieldValue(node.content, TextRange(node.content.length)))
    }
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (node.depth * 24).dp, end = 8.dp)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Collapse/expand toggle
        if (node.hasChildren) {
            Icon(
                imageVector = if (node.isCollapsed) Icons.Default.KeyboardArrowRight
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (node.isCollapsed) "Expand" else "Collapse",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onToggleCollapse)
                    .padding(2.dp),
                tint = TextSecondary,
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }

        // Bullet point
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isFocused) PurpleStart else TextSecondary),
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Editable text
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                onContentChange(newValue.text)
            },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    if (focusState.isFocused) onFocused()
                }
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        when {
                            event.key == Key.Enter -> {
                                onEnterKey()
                                true
                            }
                            event.key == Key.Backspace && textFieldValue.text.isEmpty() -> {
                                onBackspaceEmpty()
                                true
                            }
                            event.key == Key.Tab && event.isShiftPressed -> {
                                onShiftTab()
                                true
                            }
                            event.key == Key.Tab -> {
                                onTab()
                                true
                            }
                            else -> false
                        }
                    } else false
                },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = TextPrimary,
            ),
            cursorBrush = SolidColor(PurpleStart),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = "Type something...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextMuted,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}
