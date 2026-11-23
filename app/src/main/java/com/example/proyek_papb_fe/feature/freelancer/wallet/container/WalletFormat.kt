package com.example.proyek_papb_fe.feature.freelancer.wallet.container

import com.example.proyek_papb_fe.core.data.model.PayoutAccount

internal fun formatRupiah(amount: Long): String =
    "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()

internal val PayoutAccount.displayTitle: String get() = "$bankName akhiran $suffix"

internal val PayoutAccount.displayHolder: String get() = "a.n. $holderName"
