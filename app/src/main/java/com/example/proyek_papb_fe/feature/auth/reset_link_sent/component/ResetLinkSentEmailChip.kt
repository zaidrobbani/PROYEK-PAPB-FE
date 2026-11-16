package com.example.proyek_papb_fe.feature.auth.reset_link_sent.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun ResetLinkSentEmailChip(
    email: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .background(colors.tertiaryContainer, CircleShape)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_symbol_mail),
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(19.dp)
        )
        Text(
            text = email,
            color = colors.onSurface,
            style = MaterialTheme.typography.labelLarge.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.7.sp,
                lineHeight = 1.4.em
            )
        )
    }
}
