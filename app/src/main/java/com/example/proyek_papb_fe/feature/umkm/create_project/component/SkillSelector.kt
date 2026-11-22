package com.example.proyek_papb_fe.feature.umkm.create_project.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillSelector(
    options: List<String>,
    selected: List<String>,
    onToggle: (String) -> Unit,
    onAddCustomSkill: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(13.5.dp),
        verticalArrangement = Arrangement.spacedBy(8.354.dp)
    ) {
        options.forEach { skill ->
            SkillPill(
                label = skill,
                selected = skill in selected,
                onToggle = { onToggle(skill) }
            )
        }
        AddSkillPill(onClick = { showAddDialog = true })
    }

    if (showAddDialog) {
        AddSkillDialog(
            onDismiss = { showAddDialog = false },
            onAdd = {
                onAddCustomSkill(it)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun SkillPill(
    label: String,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) colors.primary else colors.tertiaryContainer)
            .toggleable(value = selected, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(horizontal = 12.677.dp, vertical = 6.338.dp),
        horizontalArrangement = Arrangement.spacedBy(6.338.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (selected) colors.onPrimary else colors.onSurfaceVariant,
            style = pillTextStyle()
        )
        Image(
            painter = painterResource(
                if (selected) R.drawable.ic_create_project_skill_check
                else R.drawable.ic_create_project_skill_add
            ),
            contentDescription = null
        )
    }
}

@Composable
private fun AddSkillPill(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(SageBadge)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.677.dp, vertical = 6.338.dp),
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_create_project_custom_skill),
            contentDescription = null
        )
        Text(text = "Tambah Skill Lain", color = OnSageBadge, style = pillTextStyle())
    }
}

@Composable
private fun AddSkillDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var skill by rememberSaveable { mutableStateOf("") }
    val colors = MaterialTheme.colorScheme
    val canAdd = skill.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceContainerLow,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Tambah Skill Lain",
                color = colors.onTertiaryContainer,
                style = MaterialTheme.typography.titleMedium.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.903.sp,
                    lineHeight = 23.241.sp
                )
            )
        },
        text = {
            ProjectTextInput(
                value = skill,
                onValueChange = { skill = it },
                contentDescription = "Nama keahlian",
                error = null,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onAdd(skill) }, enabled = canAdd) {
                Text("Tambah", color = if (canAdd) colors.primary else colors.outline)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = colors.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun pillTextStyle() = MaterialTheme.typography.labelMedium.withFrameMetrics(
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.677.sp,
    lineHeight = 16.903.sp
)
