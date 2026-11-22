package com.example.proyek_papb_fe.feature.umkm.create_project.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.feature.auth.component.shared.registerInputShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

private const val BudgetMaxDigits = 12

@Composable
fun BudgetField(
    digits: String,
    onDigitsChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)

    BasicTextField(
        value = digits,
        onValueChange = { input ->
            onDigitsChange(input.filter(Char::isDigit).trimStart('0').take(BudgetMaxDigits))
        },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Estimasi Budget (Rp)"
                if (error != null) error(error)
            },
        singleLine = true,
        textStyle = MaterialTheme.typography.titleLarge
            .withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.015.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.4754.sp
            )
            .copy(color = colors.onTertiaryContainer),
        cursorBrush = SolidColor(colors.primary),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = ThousandSeparatorTransformation,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.708.dp)
                    .registerInputShadow(shape)
                    .background(colors.surfaceContainerLowest, shape)
            ) {
                Text(
                    text = "Rp",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 14.79.dp),
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.titleSmall.withFrameMetrics(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.79.sp,
                        lineHeight = 21.128.sp
                    )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 42.256.dp, end = 16.903.dp)
                ) {
                    innerTextField()
                }
            }
        }
    )
}

private object ThousandSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val formatted = buildString {
            digits.forEachIndexed { index, char ->
                if (index > 0 && (digits.length - index) % 3 == 0) append('.')
                append(char)
            }
        }
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val separatorsBefore = (0 until offset).count { index ->
                    index > 0 && (digits.length - index) % 3 == 0
                }
                return offset + separatorsBefore
            }

            override fun transformedToOriginal(offset: Int): Int =
                formatted.take(offset).count { it != '.' }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}
