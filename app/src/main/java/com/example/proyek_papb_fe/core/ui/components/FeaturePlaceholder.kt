package com.example.proyek_papb_fe.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans

private val PlaceholderBackground = Color(0xFFFEF7E6)
private val PlaceholderTitle = Color(0xFF4E3123)
private val PlaceholderBody = Color(0xFF50443F)

/** Halaman sementara untuk tab yang fiturnya belum dikembangkan. */
@Composable
fun FeaturePlaceholder(
    title: String,
    modifier: Modifier = Modifier,
    description: String = "Fitur ini sedang dalam pengembangan."
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PlaceholderBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 19.015.sp,
                lineHeight = 25.354.sp
            ),
            color = PlaceholderTitle,
            textAlign = TextAlign.Center
        )
        Text(
            text = description,
            style = TextStyle(
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.677.sp,
                lineHeight = 16.903.sp
            ),
            color = PlaceholderBody,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FeaturePlaceholderPreview() {
    FeaturePlaceholder(title = "Cari Project")
}
