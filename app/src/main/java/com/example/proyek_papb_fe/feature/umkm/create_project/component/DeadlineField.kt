package com.example.proyek_papb_fe.feature.umkm.create_project.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.registerInputShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private const val DayMillis = 24L * 60 * 60 * 1000

/** Tengah malam UTC untuk tanggal hari ini menurut zona waktu perangkat. */
internal fun todayUtcMillis(): Long {
    val local = Calendar.getInstance()
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

internal fun formatDeadline(utcMillis: Long): String =
    SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("id-ID"))
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(utcMillis)

/** "3 minggu", "2 minggu 3 hari", atau "5 hari" dari hari ini sampai batas waktu. */
internal fun formatDuration(deadlineUtcMillis: Long, todayUtcMillis: Long = todayUtcMillis()): String {
    val days = ((deadlineUtcMillis - todayUtcMillis) / DayMillis).toInt()
    val weeks = days / 7
    val rest = days % 7
    return when {
        weeks == 0 -> "$days hari"
        rest == 0 -> "$weeks minggu"
        else -> "$weeks minggu $rest hari"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlineField(
    deadlineUtcMillis: Long?,
    onDeadlineChange: (Long) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val formatted = deadlineUtcMillis?.let(::formatDeadline)

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.708.dp)
                .registerInputShadow(shape)
                .clip(shape)
                .background(colors.surfaceContainerLowest)
                .clickable(role = Role.Button) { showPicker = true }
                .semantics {
                    contentDescription = "Batas Waktu Pengerjaan"
                    stateDescription = formatted ?: "Belum dipilih"
                    if (error != null) error(error)
                }
                .padding(start = 16.903.dp, end = 46.482.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                // Figma tidak punya keadaan kosong; teks pengarah ini tambahan.
                text = formatted ?: "Pilih tanggal",
                color = if (formatted != null) colors.onSurface else colors.outline,
                style = MaterialTheme.typography.bodyLarge.withFrameMetrics(
                    fontWeight = if (formatted != null) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.79.sp,
                    lineHeight = 19.015.sp
                )
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_create_project_calendar),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-12.677).dp, y = 14.79.dp)
        )
    }

    if (showPicker) {
        val tomorrow = remember { todayUtcMillis() + DayMillis }
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = deadlineUtcMillis,
            selectableDates = object : SelectableDates {
                // Batas waktu harus setelah hari ini.
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= tomorrow
            }
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let(onDeadlineChange)
                        showPicker = false
                    },
                    enabled = pickerState.selectedDateMillis != null
                ) { Text("Pilih", color = colors.primary) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Batal", color = colors.onSurfaceVariant)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = colors.surfaceContainerLow)
        ) {
            DatePicker(
                state = pickerState,
                title = {
                    Text(
                        text = "Batas Waktu Pengerjaan",
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                headline = {
                    Text(
                        text = pickerState.selectedDateMillis?.let(::formatDeadline) ?: "Pilih tanggal",
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp),
                        style = MaterialTheme.typography.headlineSmall.withFrameMetrics(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 23.241.sp,
                            lineHeight = 29.579.sp
                        )
                    )
                },
                showModeToggle = false,
                colors = DatePickerDefaults.colors(containerColor = colors.surfaceContainerLow)
            )
        }
    }
}

/** "Durasi perkiraan 3 minggu pengerjaan" dengan bagian durasi ditebalkan seperti Figma. */
@Composable
fun DeadlineDurationNote(deadlineUtcMillis: Long, modifier: Modifier = Modifier) {
    val text = buildAnnotatedString {
        append("Durasi perkiraan ")
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            append(formatDuration(deadlineUtcMillis))
        }
        append(" pengerjaan")
    }
    ProjectFieldNote(
        iconRes = R.drawable.ic_create_project_clock,
        text = text,
        modifier = modifier
    )
}
