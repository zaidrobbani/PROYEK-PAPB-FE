package com.example.proyek_papb_fe.feature.umkm.find_freelancer.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.core.ui.theme.SandContainer
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.registerInputShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.BudgetSteps
import com.example.proyek_papb_fe.feature.umkm.data.RatingOption
import com.example.proyek_papb_fe.feature.umkm.data.TalentFilter
import com.example.proyek_papb_fe.feature.umkm.data.TalentLocation
import com.example.proyek_papb_fe.feature.umkm.data.formatBudgetStep
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
private fun text(weight: FontWeight, size: TextUnit, lineHeight: TextUnit): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(
        fontWeight = weight,
        fontSize = size,
        lineHeight = lineHeight
    )

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TalentFilterOverlay(
    visible: Boolean,
    applied: TalentFilter,
    resultCount: (TalentFilter) -> Int,
    onApply: (TalentFilter) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = visible, onBack = onDismiss)

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        var draft by remember { mutableStateOf(applied) }
        val colors = MaterialTheme.colorScheme

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.inverseSurface.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClickLabel = "Tutup filter",
                        onClick = onDismiss
                    )
            )
            val sheetShape = RoundedCornerShape(topStart = 29.579.dp, topEnd = 29.579.dp)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .animateEnterExit(
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                    )
                    .fillMaxWidth()
                    .heightIn(max = min(764.841.dp, maxHeight))
                    .dropShadow(
                        sheetShape,
                        Shadow(
                            radius = 25.354.dp,
                            color = colors.primary.copy(alpha = 0.12f),
                            offset = DpOffset(0.dp, (-4.226).dp)
                        )
                    )
                    .clip(sheetShape)
                    .background(colors.surfaceContainerLowest)
                    .pointerInput(Unit) { detectTapGestures { } }
            ) {
                SheetHeader(onClose = onDismiss)
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.903.dp, end = 16.903.dp, top = 16.903.dp, bottom = 101.415.dp),
                    verticalArrangement = Arrangement.spacedBy(21.128.dp)
                ) {
                    RatingSection(
                        selected = RatingOption.entries.firstOrNull { it.min == draft.minRating },
                        onSelect = { option ->
                            draft = draft.copy(minRating = if (draft.minRating == option.min) null else option.min)
                        }
                    )
                    BudgetSection(
                        start = draft.budgetMinIndex,
                        end = draft.budgetMaxIndex,
                        onChange = { start, end -> draft = draft.copy(budgetMinIndex = start, budgetMaxIndex = end) }
                    )
                    LocationSection(
                        selected = draft.location,
                        onSelect = { draft = draft.copy(location = it) }
                    )
                    FastResponseToggle(
                        checked = draft.fastResponse,
                        onCheckedChange = { draft = draft.copy(fastResponse = it) }
                    )
                }
            }
            FilterFooter(
                resultCount = resultCount(draft),
                onReset = { draft = TalentFilter() },
                onApply = { onApply(draft) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .animateEnterExit(
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                    )
            )
        }
    }
}

@Composable
private fun SheetHeader(onClose: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceContainerLowest)
            .padding(start = 16.903.dp, end = 16.903.dp, top = 12.677.dp, bottom = 8.451.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 10.564.dp)
                .size(width = 42.256.dp, height = 6.338.dp)
                .background(colors.surfaceVariant, CircleShape)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.451.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(R.drawable.ic_talent_filter_title), contentDescription = null)
                Text(
                    text = "Filter Pencarian Talenta",
                    color = colors.onSurface,
                    style = text(FontWeight.Bold, 16.903.sp, 23.241.sp)
                )
            }
            Box(
                modifier = Modifier
                    .size(33.805.dp)
                    .clip(CircleShape)
                    .background(colors.tertiaryContainer)
                    .clickable(role = Role.Button, onClick = onClose)
                    .semantics { contentDescription = "Tutup filter" },
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(R.drawable.ic_talent_filter_close), contentDescription = null)
            }
        }
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(
        text = label,
        color = MaterialTheme.colorScheme.onSurface,
        style = text(FontWeight.SemiBold, 14.79.sp, 19.015.sp)
    )
}

@Composable
private fun RatingSection(selected: RatingOption?, onSelect: (RatingOption) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(8.451.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel("Rating Minimal")
                Image(painter = painterResource(R.drawable.ic_talent_filter_star), contentDescription = null)
            }
            if (selected != null) {
                Text(
                    text = "${selected.min} ke atas",
                    modifier = Modifier
                        .background(SandContainer, CircleShape)
                        .padding(horizontal = 10.564.dp, vertical = 2.113.dp),
                    color = colors.onTertiaryContainer,
                    style = text(FontWeight.Bold, 14.79.sp, 21.128.sp)
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 4.226.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.451.dp)
        ) {
            RatingOption.entries.forEach { option ->
                val isSelected = option == selected
                val shape = RoundedCornerShape(8.451.dp)
                Text(
                    text = option.pill,
                    modifier = Modifier
                        .then(if (isSelected) Modifier.authDropShadow(shape, AuthShadowStyle.Subtle) else Modifier)
                        .clip(shape)
                        .background(if (isSelected) colors.primary else colors.tertiaryContainer)
                        .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(option) })
                        .padding(horizontal = 22.dp, vertical = 8.451.dp),
                    color = if (isSelected) colors.onPrimary else colors.onSurfaceVariant,
                    style = text(FontWeight.SemiBold, 12.677.sp, 16.903.sp)
                )
            }
        }
    }
}

@Composable
private fun BudgetSection(start: Int, end: Int, onChange: (Int, Int) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(10.564.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionLabel("Rentang Estimasi Budget")
            Text(
                text = "${formatBudgetStep(start)} - ${formatBudgetStep(end)}",
                color = colors.secondary,
                style = text(FontWeight.Bold, 12.677.sp, 16.903.sp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surfaceContainerLow, RoundedCornerShape(16.dp))
                .padding(12.677.dp),
            verticalArrangement = Arrangement.spacedBy(12.677.dp)
        ) {
            StepRangeSlider(
                stepCount = BudgetSteps.size,
                start = start,
                end = end,
                onChange = onChange,
                description = "Rentang budget ${formatBudgetStep(start)} sampai ${formatBudgetStep(end)}"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Rp 100 rb", "Rp 2.5 jt", "Rp 10 jt+").forEach {
                    Text(text = it, color = colors.onSurfaceVariant, style = text(FontWeight.Medium, 11.621.sp, 14.79.sp))
                }
            }
        }
    }
}

@Composable
private fun StepRangeSlider(
    stepCount: Int,
    start: Int,
    end: Int,
    onChange: (Int, Int) -> Unit,
    description: String
) {
    val colors = MaterialTheme.colorScheme
    val thumb = 21.128.dp
    val currentStart by rememberUpdatedState(start)
    val currentEnd by rememberUpdatedState(end)
    val currentOnChange by rememberUpdatedState(onChange)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(25.354.dp)
            .semantics { contentDescription = description }
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val thumbPx = with(density) { thumb.toPx() }
        val usable = widthPx - thumbPx
        fun centerOf(index: Int): Float = thumbPx / 2 + usable * index / (stepCount - 1)
        fun indexAt(x: Float): Int =
            (((x - thumbPx / 2) / usable) * (stepCount - 1)).roundToInt().coerceIn(0, stepCount - 1)

        var draggingStart by remember { mutableStateOf(true) }
        fun move(x: Float) {
            val index = indexAt(x)
            if (draggingStart) {
                currentOnChange(index.coerceAtMost(currentEnd), currentEnd)
            } else {
                currentOnChange(currentStart, index.coerceAtLeast(currentStart))
            }
        }
        fun pick(x: Float) {
            draggingStart = abs(x - centerOf(currentStart)) <= abs(x - centerOf(currentEnd)) &&
                !(currentStart == currentEnd && x > centerOf(currentEnd))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(stepCount, widthPx) {
                    detectTapGestures { offset ->
                        pick(offset.x)
                        move(offset.x)
                    }
                }
                .pointerInput(stepCount, widthPx) {
                    detectDragGestures(
                        onDragStart = { offset -> pick(offset.x) },
                        onDrag = { change, _ -> move(change.position.x) }
                    )
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.451.dp)
                    .background(colors.surfaceVariant, CircleShape)
            )
            val startX = with(density) { centerOf(start).toDp() }
            val endX = with(density) { centerOf(end).toDp() }
            Box(
                modifier = Modifier
                    .offset(x = startX)
                    .width(endX - startX)
                    .height(8.451.dp)
                    .background(colors.primary, CircleShape)
            )
            SliderThumb(offsetX = startX - thumb / 2)
            SliderThumb(offsetX = endX - thumb / 2)
        }
    }
}

@Composable
private fun SliderThumb(offsetX: Dp) {
    val shadow = Color.Black.copy(alpha = 0.1f)
    Box(
        modifier = Modifier
            .offset(x = offsetX)
            .size(21.128.dp)
            .dropShadow(CircleShape, Shadow(radius = 6.338.dp, color = shadow, spread = (-1.056).dp, offset = DpOffset(0.dp, 4.226.dp)))
            .dropShadow(CircleShape, Shadow(radius = 4.226.dp, color = shadow, spread = (-2.113).dp, offset = DpOffset(0.dp, 2.113.dp)))
            .background(Color.White, CircleShape)
            .border(2.113.dp, MaterialTheme.colorScheme.onTertiaryContainer, CircleShape)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LocationSection(selected: TalentLocation, onSelect: (TalentLocation) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(8.451.dp)) {
        SectionLabel("Lokasi Talenta")
        FlowRow(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(12.677.dp),
            verticalArrangement = Arrangement.spacedBy(8.451.dp)
        ) {
            TalentLocation.entries.forEach { location ->
                val isSelected = location == selected
                val shape = RoundedCornerShape(12.dp)
                Row(
                    modifier = Modifier
                        .then(if (isSelected) Modifier.authDropShadow(shape, AuthShadowStyle.Subtle) else Modifier)
                        .clip(shape)
                        .background(if (isSelected) SageBadge else colors.tertiaryContainer)
                        .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(location) })
                        .padding(horizontal = 14.79.dp, vertical = 8.451.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Image(painter = painterResource(R.drawable.ic_talent_filter_location), contentDescription = null)
                    }
                    Text(
                        text = location.label,
                        color = if (isSelected) OnSageBadge else colors.onSurface,
                        style = text(FontWeight.SemiBold, 12.677.sp, 16.903.sp)
                    )
                    if (isSelected) {
                        Image(painter = painterResource(R.drawable.ic_register_role_check), contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun FastResponseToggle(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.tertiaryContainer)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(12.677.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.564.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.031.dp)
                    .background(SageBadge, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(R.drawable.ic_talent_filter_bolt), contentDescription = null)
            }
            Column {
                Text(
                    text = "Siap Kerja Kilat (< 3 Hari)",
                    color = colors.onSurface,
                    style = text(FontWeight.SemiBold, 12.677.sp, 16.903.sp)
                )
                Text(
                    text = "Freelancer respon di bawah 1 jam",
                    color = colors.onSurfaceVariant,
                    style = text(FontWeight.Normal, 11.621.sp, 21.128.sp)
                )
            }
        }
        Box(
            modifier = Modifier
                .size(width = 46.482.dp, height = 25.354.dp)
                .background(if (checked) colors.primary else colors.surfaceVariant, CircleShape)
                .padding(2.113.dp),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(21.128.dp)
                    .registerInputShadow(CircleShape)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

@Composable
private fun FilterFooter(
    resultCount: Int,
    onReset: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                RectangleShape,
                Shadow(radius = 16.903.dp, color = colors.primary.copy(alpha = 0.06f), offset = DpOffset(0.dp, (-4.226).dp))
            )
            .background(colors.surfaceContainerLowest.copy(alpha = 0.95f))
            .pointerInput(Unit) { detectTapGestures { } }
            .padding(horizontal = 16.903.dp, vertical = 12.677.dp),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(50.708.dp)
                .clip(shape)
                .background(colors.surfaceContainerHigh)
                .clickable(role = Role.Button, onClick = onReset)
                .padding(horizontal = 16.903.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Reset", color = colors.onSurface, style = text(FontWeight.SemiBold, 14.79.sp, 19.015.sp))
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .height(50.708.dp)
                .authDropShadow(shape, AuthShadowStyle.Prominent)
                .clip(shape)
                .background(colors.primary)
                .clickable(role = Role.Button, onClick = onApply)
                .semantics { stateDescription = "$resultCount talenta" },
            horizontalArrangement = Arrangement.spacedBy(8.451.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(R.drawable.ic_talent_filter_apply), contentDescription = null)
            Text(
                text = "Terapkan Filter ($resultCount Talenta)",
                color = colors.onPrimary,
                style = text(FontWeight.Bold, 14.79.sp, 19.015.sp)
            )
        }
    }
}
