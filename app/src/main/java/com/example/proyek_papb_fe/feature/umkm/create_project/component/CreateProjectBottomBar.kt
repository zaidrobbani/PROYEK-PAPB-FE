package com.example.proyek_papb_fe.feature.umkm.create_project.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthPrimaryButton

@Composable
fun CreateProjectBottomBar(
    onSaveDraft: () -> Unit,
    onPublish: () -> Unit,
    modifier: Modifier = Modifier,
    publishLabel: String = "Publikasikan Project Sekarang",
    showSaveDraft: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val buttonShape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 16.903.dp,
                    color = colors.primary.copy(alpha = 0.08f),
                    offset = DpOffset(x = 0.dp, y = (-4.226).dp)
                )
            )
            .background(colors.background.copy(alpha = 0.95f))
            .padding(horizontal = 16.903.dp, vertical = 8.451.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 473.272.dp),
            horizontalArrangement = Arrangement.spacedBy(8.451.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showSaveDraft) Box(
                modifier = Modifier
                    .height(50.708.dp)
                    .clip(buttonShape)
                    .background(colors.tertiaryContainer)
                    .clickable(role = Role.Button, onClick = onSaveDraft)
                    .semantics { contentDescription = "Simpan draf" }
                    .padding(horizontal = 16.903.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_create_project_bookmark),
                    contentDescription = null
                )
            }
            AuthPrimaryButton(
                text = publishLabel,
                onClick = onPublish,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
