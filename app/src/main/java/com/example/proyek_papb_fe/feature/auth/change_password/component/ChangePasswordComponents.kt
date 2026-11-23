package com.example.proyek_papb_fe.feature.auth.change_password.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.RegisterFieldIcon
import com.example.proyek_papb_fe.feature.auth.component.shared.RegisterPasswordMatchIcon
import com.example.proyek_papb_fe.feature.auth.component.shared.RegisterPasswordToggle
import com.example.proyek_papb_fe.feature.auth.component.shared.RegisterTextField
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun SecurityAccountCard(email: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .background(colors.surfaceContainerLow, shape)
            .padding(16.903.dp),
        horizontalArrangement = Arrangement.spacedBy(12.677.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(42.dp).background(SageBadge, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_symbol_shield),
                contentDescription = null,
                tint = colors.secondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Akun yang sedang masuk",
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.621.sp,
                    lineHeight = 14.79.sp
                )
            )
            Text(
                text = email,
                color = colors.onSurface,
                style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.79.sp,
                    lineHeight = 19.015.sp
                )
            )
        }
    }
}

@Composable
fun PasswordInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    visible: Boolean,
    onToggleVisible: () -> Unit,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    showMatch: Boolean = false,
    labelTrailing: (@Composable () -> Unit)? = null,
    onDone: () -> Unit = {}
) {
    RegisterTextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        error = error,
        leadingContent = { LockIcon() },
        modifier = modifier,
        labelTrailing = labelTrailing,
        trailingContent = {
            if (showMatch) RegisterPasswordMatchIcon()
            else RegisterPasswordToggle(visible = visible, onToggle = onToggleVisible)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
private fun BoxScope.LockIcon() {
    RegisterFieldIcon(R.drawable.ic_register_lock, 17.96.dp, 16.64.dp, 12.dp, 15.75.dp)
}

@Composable
fun ForgotCurrentPasswordLink(onClick: () -> Unit) {
    Text(
        text = "Lupa kata sandi?",
        modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelMedium.withFrameMetrics(
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.677.sp,
            lineHeight = 16.903.sp
        )
    )
}

@Composable
fun SecurityInfoCard(text: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SageBadge.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_symbol_info),
            contentDescription = null,
            tint = colors.secondary,
            modifier = Modifier.size(19.dp)
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = colors.onSecondaryContainer,
            style = MaterialTheme.typography.bodySmall.withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 12.7.sp,
                lineHeight = 1.4.em
            )
        )
    }
}
