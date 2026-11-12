package com.example.proyek_papb_fe.feature.freelancer.wallet.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Chip nominal cepat: "Rp 500rb", "Rp 1 jt", "Semua saldo". */
data class QuickAmountUi(
    val label: String,
    val amount: Long,
    val isSelected: Boolean
)

/** Satu pilihan Rekening tujuan (diambil dari rekening Dompet & Rekening). */
data class PayoutAccountOptionUi(
    /** Nomor rekening, sebagai kunci pilihan. */
    val key: String,
    /** "BCA akhiran 4821". */
    val title: String,
    /** "a.n. Andi Ramadhan". */
    val holder: String,
    val isSelected: Boolean
)

/** Data tampilan Pengajuan Pencairan Dana (sudah diformat). */
data class WithdrawUiState(
    /** "Rp 2.850.000". */
    val availableBalance: String,
    /** Nominal mentah tanpa pemisah ("2850000"); kosong = belum diisi. */
    val amountDigits: String,
    val quickAmounts: List<QuickAmountUi>,
    val amountError: String?,
    val accounts: List<PayoutAccountOptionUi>,
    val accountError: String?,
    val nominal: String,
    val fee: String,
    val received: String,
    val estimate: String
)

private val MainPadding = 17.dp

/**
 * Figma: Ajukan Pencairan Dana (27:161), dibuka dari "Cairkan Dana" di Dompet & Rekening.
 * App bar Detail di atas, isi di-scroll (pt 84 − app bar 64 = 20, px 17, gap 16), dan
 * Sticky Bottom Bar "Ajukan Pencairan" menumpang di bawah (ikut naik bersama keyboard).
 */
@Composable
fun WithdrawScreen(
    state: WithdrawUiState,
    onBack: () -> Unit,
    onAmountChange: (String) -> Unit,
    onQuickAmountClick: (Long) -> Unit,
    onAccountSelect: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(TemuKaryaColors.BgCanvas)) {
        DetailTopBar(title = "Cairkan Dana", onBack = onBack)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    // Figma: Main pb 120 (ruang untuk Sticky Bottom Bar ±80).
                    .padding(start = MainPadding, end = MainPadding, top = 20.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AvailableBalanceCard(state.availableBalance)
                AmountField(
                    digits = state.amountDigits,
                    error = state.amountError,
                    quickAmounts = state.quickAmounts,
                    onAmountChange = onAmountChange,
                    onQuickAmountClick = onQuickAmountClick
                )
                Text("Rekening tujuan", style = WithdrawTypography.SectionTitle, color = TemuKaryaColors.TextHeading)
                AccountOptions(accounts = state.accounts, error = state.accountError, onSelect = onAccountSelect)
                DetailsCard(state)
            }
            SubmitBar(onSubmitClick = onSubmitClick, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

/** Figma 27:174: kartu "Saldo tersedia". */
@Composable
private fun AvailableBalanceCard(balance: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, WithdrawCardShape)
            .border(1.dp, WithdrawColors.CardBorder, WithdrawCardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("Saldo tersedia", style = WithdrawTypography.BalanceLabel, color = TemuKaryaColors.TextBody)
        Text(balance, style = WithdrawTypography.Headline, color = TemuKaryaColors.TextHeading)
    }
}

/** Figma 27:177: "Nominal pencairan" + input Rp + chip nominal cepat. */
@Composable
private fun AmountField(
    digits: String,
    error: String?,
    quickAmounts: List<QuickAmountUi>,
    onAmountChange: (String) -> Unit,
    onQuickAmountClick: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Nominal pencairan", style = WithdrawTypography.FieldLabel, color = TemuKaryaColors.TextBody)
        BasicTextField(
            value = digits,
            onValueChange = onAmountChange,
            singleLine = true,
            textStyle = WithdrawTypography.InputValue.copy(color = TemuKaryaColors.TextPrimary),
            cursorBrush = SolidColor(TemuKaryaColors.ActionPrimary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            visualTransformation = ThousandsSeparatorTransformation,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Nominal pencairan" },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(TemuKaryaColors.SurfaceWhite, WithdrawButtonShape)
                        .border(
                            1.5.dp,
                            if (error != null) WithdrawColors.Error else TemuKaryaColors.ActionPrimary,
                            WithdrawButtonShape
                        )
                        .padding(horizontal = 17.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Rp", style = WithdrawTypography.InputPrefix, color = TemuKaryaColors.TextPlaceholder)
                    Box(modifier = Modifier.weight(1f)) {
                        if (digits.isEmpty()) {
                            Text("0", style = WithdrawTypography.InputValue, color = TemuKaryaColors.TextPlaceholder)
                        }
                        innerTextField()
                    }
                }
            }
        )
        if (error != null) {
            Text(error, style = WithdrawTypography.Error, color = WithdrawColors.Error)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            quickAmounts.forEach { chip ->
                QuickAmountChip(chip, onClick = { onQuickAmountClick(chip.amount) })
            }
        }
    }
}

@Composable
private fun QuickAmountChip(chip: QuickAmountUi, onClick: () -> Unit) {
    val contentColor = if (chip.isSelected) TemuKaryaColors.OnAction else TemuKaryaColors.TextBody
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (chip.isSelected) TemuKaryaColors.ActionPrimary else WithdrawColors.Chip)
            .selectable(selected = chip.isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (chip.isSelected) {
            Box(modifier = Modifier.size(15.dp), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_check_small), contentDescription = null, tint = contentColor)
            }
        }
        Text(chip.label, style = WithdrawTypography.Chip, color = contentColor, maxLines = 1)
    }
}

/** Figma 27:192: kartu rekening dengan radio; ditampilkan untuk setiap rekening Dompet. */
@Composable
private fun AccountOptions(accounts: List<PayoutAccountOptionUi>, error: String?, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (accounts.isEmpty()) {
            Text(
                text = "Belum ada rekening pencairan. Tambahkan rekening di Dompet & Rekening.",
                style = WithdrawTypography.Caption,
                color = TemuKaryaColors.TextBody
            )
        }
        accounts.forEach { account -> AccountOption(account, onClick = { onSelect(account.key) }) }
        if (error != null) {
            Text(error, style = WithdrawTypography.Error, color = WithdrawColors.Error)
        }
    }
}

@Composable
private fun AccountOption(account: PayoutAccountOptionUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WithdrawCardShape)
            .background(TemuKaryaColors.SurfaceContainer)
            .border(
                if (account.isSelected) 1.5.dp else 1.dp,
                if (account.isSelected) TemuKaryaColors.ActionPrimary else WithdrawColors.CardBorder,
                WithdrawCardShape
            )
            .selectable(selected = account.isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(TemuKaryaColors.SurfaceWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(R.drawable.ic_wallet_bank), contentDescription = null, tint = TemuKaryaColors.ActionPrimary)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(account.title, style = WithdrawTypography.AccountTitle, color = TemuKaryaColors.TextPrimary, maxLines = 1)
            Text(account.holder, style = WithdrawTypography.Caption, color = TemuKaryaColors.TextBody, maxLines = 1)
        }
        Icon(
            painter = painterResource(
                if (account.isSelected) R.drawable.ic_radio_button_checked else R.drawable.ic_radio_button_unchecked
            ),
            contentDescription = null,
            tint = TemuKaryaColors.ActionPrimary,
            modifier = Modifier.size(23.dp)
        )
    }
}

/** Figma 27:199: kartu "Rincian". */
@Composable
private fun DetailsCard(state: WithdrawUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceContainer, WithdrawCardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Rincian", style = WithdrawTypography.CardTitle, color = TemuKaryaColors.TextHeading)
        WithdrawDetailRow("Nominal", state.nominal)
        WithdrawDetailRow("Biaya transfer", state.fee, valueColor = TemuKaryaColors.Olive)
        Box(Modifier.fillMaxWidth().height(1.dp).background(WithdrawColors.CardBorder))
        WithdrawDetailRow("Diterima di rekening", state.received, valueStyle = WithdrawTypography.RowValueStrong)
        WithdrawDetailRow("Estimasi masuk", state.estimate)
    }
}

/** Figma 27:217: Sticky Bottom Bar dengan tombol "Ajukan Pencairan". */
@Composable
private fun SubmitBar(onSubmitClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(WithdrawColors.BottomBar)
            .drawBehind {
                drawLine(WithdrawColors.CardBorder, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
            }
            .padding(horizontal = MainPadding, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(51.dp)
                .clip(WithdrawButtonShape)
                .background(TemuKaryaColors.ActionPrimary)
                .clickable(role = Role.Button, onClick = onSubmitClick)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.ic_send),
                contentDescription = null,
                tint = TemuKaryaColors.OnAction,
                modifier = Modifier.size(19.dp)
            )
            Text("Ajukan Pencairan", style = WithdrawTypography.Button, color = TemuKaryaColors.OnAction)
        }
    }
}

/** Tampilkan "2850000" sebagai "2.850.000" tanpa mengubah nilai mentah di state. */
private object ThousandsSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val out = StringBuilder()
        // originalToTransformed[i] = posisi kursor hasil untuk kursor asli i.
        val originalToTransformed = IntArray(raw.length + 1)
        raw.forEachIndexed { i, c ->
            if (i > 0 && (raw.length - i) % 3 == 0) out.append('.')
            originalToTransformed[i] = out.length
            out.append(c)
        }
        originalToTransformed[raw.length] = out.length
        val formatted = out.toString()
        return TransformedText(AnnotatedString(formatted), object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                originalToTransformed[offset.coerceIn(0, raw.length)]

            override fun transformedToOriginal(offset: Int): Int =
                formatted.take(offset.coerceIn(0, formatted.length)).count { it != '.' }
        })
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun WithdrawScreenPreview() {
    WithdrawScreen(
        state = WithdrawUiState(
            availableBalance = "Rp 2.850.000",
            amountDigits = "2850000",
            quickAmounts = listOf(
                QuickAmountUi("Rp 500rb", 500_000, false),
                QuickAmountUi("Rp 1 jt", 1_000_000, false),
                QuickAmountUi("Semua saldo", 2_850_000, true)
            ),
            amountError = null,
            accounts = listOf(PayoutAccountOptionUi("0372154821", "BCA akhiran 4821", "a.n. Andi Ramadhan", true)),
            accountError = null,
            nominal = "Rp 2.850.000",
            fee = "Rp 0",
            received = "Rp 2.850.000",
            estimate = "1x24 jam kerja"
        ),
        onBack = {}, onAmountChange = {}, onQuickAmountClick = {}, onAccountSelect = {}, onSubmitClick = {}
    )
}
