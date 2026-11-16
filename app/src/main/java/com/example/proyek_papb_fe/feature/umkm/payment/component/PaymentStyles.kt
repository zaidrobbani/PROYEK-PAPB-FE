package com.example.proyek_papb_fe.feature.umkm.payment.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans

/** Tipografi Figma 17:1267 / 17:3177 (ukuran & line-height px persis). */
internal object PaymentTypography {
    private fun style(weight: FontWeight, size: Float, line: Float, tracking: Float = 0f) =
        TextStyle(fontFamily = PlusJakartaSans, fontWeight = weight, fontSize = size.sp, lineHeight = line.sp, letterSpacing = tracking.sp)

    val Caption = style(FontWeight.Medium, 11.621f, 14.79f)
    val CaptionSemi = style(FontWeight.SemiBold, 11.621f, 14.79f)
    val CaptionRegular = style(FontWeight.Normal, 11.621f, 21.128f)
    val Body = style(FontWeight.Normal, 12.677f, 16.903f)
    val BodySemi = style(FontWeight.SemiBold, 12.677f, 16.903f)
    val BodyBold = style(FontWeight.Bold, 12.677f, 16.903f)
    val Note = style(FontWeight.Bold, 12.677f, 20.6f)
    val Row = style(FontWeight.Normal, 14.79f, 21.128f)
    val RowSemi = style(FontWeight.SemiBold, 14.79f, 21.128f)
    val ProjectInReceipt = style(FontWeight.SemiBold, 14.79f, 20.336f)
    val Subtitle = style(FontWeight.Normal, 14.79f, 24.033f)
    val Button = style(FontWeight.SemiBold, 14.79f, 19.015f)
    val Title = style(FontWeight.SemiBold, 16.903f, 23.241f)
    val TotalBold = style(FontWeight.Bold, 16.903f, 23.241f)
    val Qris = style(FontWeight.Bold, 19.015f, 25.354f, tracking = -0.9508f)
    val Hero = style(FontWeight.Bold, 23.241f, 29.579f, tracking = -0.581f)
    val Amount = style(FontWeight.Bold, 31.692f, 40.144f, tracking = -0.7923f)
}

internal object PaymentColors {
    val Heading = Color(0xFF4E3123)
    val StatusCard = Color(0xFFF1E4C6)
    val Chip = Color(0xFFEDE0C2)
    val Green = Color(0xFFDBE3C4)
    val OnGreen = Color(0xFF5E664D)
    val MethodChip = Color(0xFFDEE6C7)
    val OnMethodChip = Color(0xFF171E0A)
    val Olive = Color(0xFF5A6149)
    val EscrowNote = Color(0x99F1E4C6) // rgba(241,228,198,0.6)
    val NetText = Color(0xCC50443F) // rgba(80,68,63,0.8)
    val UmkmAvatar = Color(0xFFEADBC8)
    val Divider = Color(0xFFE2D5BA)
    val Shadow = Color(0x0D000000) // rgba(0,0,0,0.05)
    val ButtonShadow = Color(0x1A000000) // rgba(0,0,0,0.1)
}

internal val PaymentCardShape = RoundedCornerShape(16.dp)
internal val PaymentInnerShape = RoundedCornerShape(8.451.dp)
internal val PaymentButtonShape = RoundedCornerShape(12.dp)
internal val P17 = 16.903.dp
internal val P13 = 12.677.dp
internal val P8 = 8.451.dp

/** drop-shadow 0 1.056 1.056 rgba(0,0,0,0.05) (kartu Figma). */
internal fun Modifier.paymentCardShadow(shape: Shape): Modifier =
    dropShadow(shape = shape, shadow = Shadow(radius = 1.056.dp, color = PaymentColors.Shadow, offset = DpOffset(0.dp, 1.056.dp)))

/** 2850000 → "Rp 2.850.000". */
internal fun formatRupiah(amount: Long): String =
    "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()
