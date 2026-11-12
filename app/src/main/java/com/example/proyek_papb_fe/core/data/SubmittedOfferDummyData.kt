package com.example.proyek_papb_fe.core.data

import com.example.proyek_papb_fe.core.data.model.CancellationReason
import com.example.proyek_papb_fe.core.data.model.ProjectCancellation
import com.example.proyek_papb_fe.core.data.model.SubmittedOffer
import java.util.concurrent.TimeUnit

/**
 * Contoh penawaran terkirim agar tab "Project" bisa langsung dicoba (sementara, sebelum
 * ada backend). Satu per tahap: Menunggu Kesepakatan (102), Negosiasi (103, 109),
 * Project Berjalan (108), Selesai (107), dan Dibatalkan (105). Project lain bisa masuk
 * Project Berjalan lewat alur Negosiasi → Rencana Pengerjaan → "Ajukan ke UMKM".
 *
 * Project 101 sengaja tidak diisi: alurnya (Cari Project → Detail → Ajukan Penawaran)
 * tetap dimulai dari kosong seperti di Figma. Nilai penawaran = pagu klien project,
 * biaya platform 5%, durasi memakai pilihan yang ada di form.
 */
internal object SubmittedOfferDummyData {

    fun seed(now: Long = System.currentTimeMillis()): List<SubmittedOffer> = listOf(
        // Menunggu Kesepakatan: disetujui otomatis ±3 detik setelah Status Pengajuan-nya dibuka.
        SubmittedOffer(
            projectId = 102,
            offerAmount = 1_200_000,
            platformFee = 60_000,
            netAmount = 1_140_000,
            durationDays = 7,
            message = "Halo Bu Rina, saya siap membuat desain box dan label toples yang menonjol di rak oleh-oleh.",
            submittedAtMillis = now - TimeUnit.HOURS.toMillis(2)
        ),
        // Sudah disetujui UMKM, sedang tahap negosiasi.
        SubmittedOffer(
            projectId = 103,
            offerAmount = 2_500_000,
            platformFee = 125_000,
            netAmount = 2_375_000,
            durationDays = 21,
            message = "Halo Bu Siti, saya berpengalaman membuat toko online katering dengan pemesanan via WhatsApp.",
            submittedAtMillis = now - TimeUnit.DAYS.toMillis(1),
            isApproved = true
        ),
        // Dibatalkan freelancer dari ruang negosiasi.
        SubmittedOffer(
            projectId = 105,
            offerAmount = 1_100_000,
            platformFee = 55_000,
            netAmount = 1_045_000,
            durationDays = 14,
            message = "Halo Bu Lia, saya tertarik membantu identitas visual cabang baru laundry Anda.",
            submittedAtMillis = now - TimeUnit.DAYS.toMillis(3),
            isApproved = true,
            cancellation = ProjectCancellation(
                reason = CancellationReason.SCHEDULE_MISMATCH,
                cancelledAtMillis = now - TimeUnit.DAYS.toMillis(2)
            )
        ),
        // Disetujui UMKM, sedang negosiasi lewat percakapan "Toko Kue Sari Manis" di tab Chat.
        SubmittedOffer(
            projectId = 109,
            offerAmount = 1_000_000,
            platformFee = 50_000,
            netAmount = 950_000,
            durationDays = 14,
            message = "Halo Bu Sari, saya bisa siapkan 2 konsep kemasan kue kering Lebaran yang siap dijadikan hampers.",
            submittedAtMillis = now - TimeUnit.DAYS.toMillis(2),
            isApproved = true
        ),
        // Project Berjalan: rencana pengerjaan disetujui UMKM, sedang dikerjakan (21 hari).
        SubmittedOffer(
            projectId = 108,
            offerAmount = 3_000_000,
            platformFee = 150_000,
            netAmount = 2_850_000,
            durationDays = 21,
            message = "Halo Bu Sekar, saya siap membangun landing page batik tulis lengkap dengan galeri motif dan formulir pemesanan.",
            submittedAtMillis = now - TimeUnit.DAYS.toMillis(10),
            isApproved = true,
            agreedAtMillis = now - TimeUnit.DAYS.toMillis(8)
        ),
        // Selesai: disepakati, dikerjakan 7 hari, lalu diselesaikan.
        SubmittedOffer(
            projectId = 107,
            offerAmount = 650_000,
            platformFee = 32_500,
            netAmount = 617_500,
            durationDays = 7,
            message = "Halo Bu Darmi, saya bisa membuat label jamu yang modern tapi tetap terasa tradisional.",
            submittedAtMillis = now - TimeUnit.DAYS.toMillis(15),
            isApproved = true,
            agreedAtMillis = now - TimeUnit.DAYS.toMillis(13),
            completedAtMillis = now - TimeUnit.DAYS.toMillis(6)
        )
    )
}
