package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R

@Composable
fun RegisterTermsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val boxShape = RoundedCornerShape(6.338.dp)
    val termsText = buildAnnotatedString {
        append("Saya menyetujui ")
        withStyle(
            SpanStyle(
                color = colors.onTertiaryContainer,
                fontSize = 11.621.sp,
                fontWeight = FontWeight.Medium
            )
        ) {
            append("Syarat & Ketentuan Layanan")
        }
        append(" serta sistem keamanan perlindungan dana escrow TEMUKARYA.")
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.226.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange)
                .padding(8.451.dp),
            horizontalArrangement = Arrangement.spacedBy(12.677.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 2.113.dp)
                    .size(21.128.dp)
                    .registerInputShadow(boxShape)
                    .background(if (checked) colors.primary else colors.surfaceVariant, boxShape),
                contentAlignment = Alignment.Center
            ) {
                if (checked) {
                    Image(
                        painter = painterResource(R.drawable.ic_register_checkbox_check),
                        contentDescription = null
                    )
                }
            }
            Text(
                text = termsText,
                modifier = Modifier.weight(1f),
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall.withFrameMetrics(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.677.sp,
                    lineHeight = 20.6.sp
                )
            )
        }
        if (error != null) {
            RegisterErrorText(
                message = error,
                modifier = Modifier.padding(horizontal = 8.451.dp)
            )
        }
    }
}
