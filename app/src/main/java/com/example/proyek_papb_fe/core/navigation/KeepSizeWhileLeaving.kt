package com.example.proyek_papb_fe.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.Layout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

@Composable
fun KeepSizeWhileLeaving(
    entry: LifecycleOwner,
    content: @Composable () -> Unit
) {
    val lastHeight = remember { intArrayOf(NO_HEIGHT) }

    Layout(content = content) { measurables, constraints ->
        val isLeaving = !entry.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        val frozenHeight = lastHeight[0]
        val freeze = isLeaving && frozenHeight != NO_HEIGHT && constraints.hasBoundedHeight

        val childConstraints = if (freeze) {
            val height = frozenHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
            constraints.copy(minHeight = height, maxHeight = height)
        } else {
            constraints
        }

        val placeables = measurables.map { it.measure(childConstraints) }
        val width = placeables.maxOfOrNull { it.width } ?: constraints.minWidth
        val height = placeables.maxOfOrNull { it.height } ?: constraints.minHeight
        if (!freeze) lastHeight[0] = height

        layout(
            width.coerceIn(constraints.minWidth, constraints.maxWidth),
            height.coerceIn(constraints.minHeight, constraints.maxHeight)
        ) {
            placeables.forEach { it.place(0, 0) }
        }
    }
}

private const val NO_HEIGHT = -1
