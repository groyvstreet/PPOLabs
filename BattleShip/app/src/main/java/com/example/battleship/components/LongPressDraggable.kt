package com.example.battleship.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.example.battleship.viewModels.BattleShipViewModel

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun LongPressDraggable(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember { DragTargetInfo() }
    CompositionLocalProvider(LocalDragTargetInfo provides state) {
        Box(modifier = modifier.fillMaxSize()) {
            content()
            if (state.isDragging) {
                var targetSize by remember { mutableStateOf(IntSize.Zero) }
                Box(modifier = Modifier
                    .graphicsLayer {
                        val offset = (state.dragPosition + state.dragOffset)
                        scaleX = 1.0f
                        scaleY = 1.0f
                        alpha = if (targetSize == IntSize.Zero) 0f else 1.0f
                        translationX = offset.x
                        translationY = if (targetSize.width >= targetSize.height) {
                            offset.y.minus(3 * targetSize.height)
                        } else {
                            offset.y.minus(targetSize.height)
                        }
                    }
                    .onGloballyPositioned {
                        targetSize = it.size
                    }
                ) {
                    state.draggableComposable?.invoke()
                }
            }
        }
    }
}

@Composable
fun <T> DragTarget(
    modifier: Modifier,
    dataToDrop: T,
    index: Int,
    content: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    currentState.dataToDrop = dataToDrop
                    currentState.isDragging = true
                    currentState.dragPosition = currentPosition + it
                    currentState.draggableComposable = content
                    currentState.index = index
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                    BattleShipViewModel.ships[index].value = true
                },
                onDragEnd = {
                    currentState.isDragging = false
                    currentState.dragOffset = Offset.Zero
                    BattleShipViewModel.ships[index].value = false
                },
                onDragCancel = {
                    currentState.dragOffset = Offset.Zero
                    currentState.isDragging = false
                }
            )
        }
    ) {
        if (!BattleShipViewModel.ships[index].value) {
            content()
        }
    }
}

@Composable
fun <T> DropTarget(
    modifier: Modifier,
    content: @Composable (BoxScope.(isInBound: Boolean, data: T?, index: Int) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    var isCurrentDropTarget by remember { mutableStateOf(false) }

    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            isCurrentDropTarget = rect.contains(dragPosition + dragOffset)
        }
    }) {
        val data =
            if (isCurrentDropTarget && !dragInfo.isDragging) {
                dragInfo.dataToDrop as T?
            } else {
                null
            }
        content(isCurrentDropTarget, data, dragInfo.index)
    }
}

internal class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
    var index by mutableStateOf(0)
}
