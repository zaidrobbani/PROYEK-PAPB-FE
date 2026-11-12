package com.example.proyek_papb_fe.feature.freelancer.wallet.container

import com.example.proyek_papb_fe.core.data.model.PayoutAccount

/** 2850000 → "Rp 2.850.000". */
internal fun formatRupiah(amount: Long): String =
    "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()

/** "BCA akhiran 4821". */
internal val PayoutAccount.displayTitle: String get() = "$bankName akhiran $suffix"

/** "a.n. Andi Ramadhan". */
internal val PayoutAccount.displayHolder: String get() = "a.n. $holderName"
