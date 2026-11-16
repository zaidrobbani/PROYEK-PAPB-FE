package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R

@Composable
fun AuthStepHeader(
    stepLabel: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.677.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AuthBrand()
        StepPill(label = stepLabel)
    }
}

@Composable
private fun AuthBrand() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tileShape = RoundedCornerShape(8.451.dp)

        Box(
            modifier = Modifier
                .size(33.805.dp)
                .authDropShadow(tileShape, AuthShadowStyle.Subtle)
                .background(MaterialTheme.colorScheme.primary, tileShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_temukarya_logo_small),
                contentDescription = "Logo TEMUKARYA",
                modifier = Modifier.size(width = 16.114.dp, height = 14.703.dp)
            )
        }
        Text(
            text = "TEMUKARYA",
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            style = MaterialTheme.typography.titleLarge.withFrameMetrics(
                fontWeight = FontWeight.Bold,
                fontSize = 19.015.sp,
                lineHeight = 25.354.sp,
                letterSpacing = (-0.4754).sp
            )
        )
    }
}

@Composable
private fun StepPill(label: String) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
            .padding(horizontal = 12.677.dp, vertical = 4.226.dp),
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.451.dp)
                .background(MaterialTheme.colorScheme.secondary, CircleShape)
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                fontWeight = FontWeight.Medium,
                fontSize = 11.621.sp,
                lineHeight = 14.79.sp
            )
        )
    }
}
