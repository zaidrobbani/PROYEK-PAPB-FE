package com.example.proyek_papb_fe.feature.auth.splash.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow

@Composable
fun TemuKaryaLogoBadge(modifier: Modifier = Modifier) {
    val outerShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .size(101.415.dp)
            .authDropShadow(outerShape)
            .background(color = Color.White, shape = outerShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 75.799.dp, height = 74.43.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(21.128.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_temukarya_logo),
                contentDescription = "Logo TEMUKARYA",
                modifier = Modifier.size(width = 53.837.dp, height = 52.43.dp)
            )
        }
    }
}
