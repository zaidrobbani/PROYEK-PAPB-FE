package com.example.proyek_papb_fe.feature.freelancer.workplan.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

private val FieldHeight = 48.dp
private val WeightFieldWidth = 92.dp
private val DeleteSlotWidth = 23.dp

/**
 * Figma: "Milestone n" (88:31) — label + "Bobot (%)", lalu input nama (fleksibel),
 * input bobot 92dp, dan ikon hapus.
 */
@Composable
internal fun MilestoneRow(
    index: Int,
    milestone: MilestoneInput,
    showErrors: Boolean,
    isLocked: Boolean,
    canDelete: Boolean,
    requestFocus: Boolean,
    onNameChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onDelete: () -> Unit,
    onFocusConsumed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(requestFocus) {
        if (requestFocus) {
            focusRequester.requestFocus()
            onFocusConsumed()
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Milestone ${index + 1}",
                style = WorkPlanTypography.FieldLabel,
                color = TemuKaryaColors.TextBody,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Bobot (%)",
                style = WorkPlanTypography.FieldLabel,
                color = TemuKaryaColors.TextBody,
                modifier = Modifier.width(WeightFieldWidth)
            )
            // Figma 88:35: kolom kosong selebar area ikon hapus.
            Spacer(modifier = Modifier.width(28.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = milestone.name,
                onValueChange = { onNameChange(it.take(MaxMilestoneNameLength)) },
                readOnly = isLocked,
                singleLine = true,
                textStyle = WorkPlanTypography.NameInput.copy(color = TemuKaryaColors.TextPrimary),
                cursorBrush = SolidColor(TemuKaryaColors.ActionPrimary),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                decorationBox = { inner ->
                    FieldFrame(isError = showErrors && !milestone.isNameValid) {
                        if (milestone.name.isEmpty()) {
                            Text(
                                text = "Nama tahap",
                                style = WorkPlanTypography.NameInput,
                                color = TemuKaryaColors.TextPlaceholder,
                                maxLines = 1
                            )
                        }
                        inner()
                    }
                }
            )

            BasicTextField(
                value = milestone.weight,
                onValueChange = { raw -> sanitizeWeightInput(raw)?.let(onWeightChange) },
                readOnly = isLocked,
                singleLine = true,
                textStyle = WorkPlanTypography.WeightInput.copy(color = TemuKaryaColors.TextPrimary),
                cursorBrush = SolidColor(TemuKaryaColors.ActionPrimary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.width(WeightFieldWidth),
                decorationBox = { inner ->
                    FieldFrame(isError = showErrors && !milestone.isWeightValid) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (milestone.weight.isEmpty()) {
                                    Text(
                                        text = "0",
                                        style = WorkPlanTypography.WeightInput,
                                        color = TemuKaryaColors.TextPlaceholder
                                    )
                                }
                                inner()
                            }
                            Text(
                                text = "%",
                                style = WorkPlanTypography.PercentSuffix,
                                color = TemuKaryaColors.TextPlaceholder
                            )
                        }
                    }
                }
            )

            val deleteEnabled = canDelete && !isLocked
            Box(
                modifier = Modifier
                    .width(DeleteSlotWidth)
                    .height(FieldHeight)
                    .clip(CircleShape)
                    .clickable(enabled = deleteEnabled, onClick = onDelete)
                    .alpha(if (deleteEnabled) 1f else 0.38f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Hapus milestone ${index + 1}",
                    tint = TemuKaryaColors.TextPlaceholder,
                    modifier = Modifier.size(23.dp)
                )
            }
        }
    }
}

/** Figma: "Input Nama"/"Input Bobot" — putih, border #E2D5BA, radius 12, tinggi 48, px 14. */
@Composable
private fun FieldFrame(isError: Boolean, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(FieldHeight)
            .background(TemuKaryaColors.SurfaceWhite, WorkPlanFieldShape)
            .border(
                width = 1.dp,
                color = if (isError) WorkPlanColors.Error else WorkPlanColors.Outline,
                shape = WorkPlanFieldShape
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        content()
    }
}
