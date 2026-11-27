package com.example.proyek_papb_fe.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MinRefreshMillis = 500L

@Composable
fun RefreshableContent(
    onRefresh: (() -> Job)?,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    if (onRefresh == null) {
        Box(modifier = modifier, content = content)
        return
    }
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    RefreshableContent(
        isRefreshing = isRefreshing,
        onRefresh = {
            if (!isRefreshing) {
                isRefreshing = true
                scope.launch {
                    try {
                        val started = System.currentTimeMillis()
                        onRefresh().join()
                        delay((MinRefreshMillis - (System.currentTimeMillis() - started)).coerceAtLeast(0))
                    } finally {
                        isRefreshing = false
                    }
                }
            }
        },
        modifier = modifier,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshableContent(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = state,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = TemuKaryaColors.SurfaceWhite,
                color = TemuKaryaColors.ActionPrimary
            )
        },
        content = content
    )
}
