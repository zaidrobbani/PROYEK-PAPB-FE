package com.example.proyek_papb_fe.feature.auth.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun LoginFieldIssueLabel() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_login_error_label),
            contentDescription = null
        )
        Text(
            text = "Perlu diperbaiki",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                fontWeight = FontWeight.Medium,
                fontSize = 11.621.sp,
                lineHeight = 14.79.sp
            )
        )
    }
}

@Composable
fun BoxScope.LoginFieldErrorIcon() {
    Image(
        painter = painterResource(R.drawable.ic_login_error_input),
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-12.677).dp, y = 14.79.dp)
    )
}

@Composable
fun LoginErrorCallout(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 2.113.dp),
        horizontalArrangement = Arrangement.spacedBy(6.338.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(R.drawable.ic_login_error_callout),
            contentDescription = null
        )
        Text(
            text = message,
            modifier = Modifier
                .weight(1f)
                .padding(end = 11.504.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall.withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 12.677.sp,
                lineHeight = 16.903.sp
            )
        )
    }
}

@Composable
fun BoxScope.LoginPasswordToggle(
    visible: Boolean,
    onToggle: () -> Unit
) {
    val tint = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)

    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-8.451).dp, y = 6.338.dp)
            .size(38.031.dp)
            .clip(RoundedCornerShape(8.451.dp))
            .clickable(role = Role.Button, onClick = onToggle)
            .semantics {
                contentDescription =
                    if (visible) "Sembunyikan kata sandi" else "Tampilkan kata sandi"
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_register_eye),
            contentDescription = null,
            colorFilter = tint
        )
        if (!visible) {
            Image(
                painter = painterResource(R.drawable.ic_register_eye_slash),
                contentDescription = null,
                colorFilter = tint
            )
        }
    }
}
