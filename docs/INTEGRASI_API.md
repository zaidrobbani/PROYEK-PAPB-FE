# Integrasi Android ↔ BE TEMUKARYA

## Menjalankan

1. BE jalan di Docker (`~/temukarya-BE`, `docker compose up -d`), cek `http://localhost:8000/api/v1/health`.
2. HP via USB: `adb reverse tcp:8000 tcp:8000` (wajib diulang setiap HP dicabut/dipasang lagi).
3. Base URL default `http://localhost:8000/api/v1/`. Untuk emulator atau HP via Wi-Fi, tambahkan di `local.properties`:
   `api.base.url=http://10.0.2.2:8000/api/v1/` (emulator) atau `http://<IP-laptop>:8000/api/v1/`.
   Build debug mengizinkan http ke host mana pun; build release hanya localhost/10.0.2.2.
4. Akun baru wajib verifikasi email (link dikirim BE). Login memakai email + kata sandi.

## Arsitektur (pola modul praktikum 8)

```
core/data/remote        Retrofit ApiService per modul, ApiResponse (envelope), ApiException, AuthInterceptor
core/data/remote/dto    DTO snake_case otomatis (Gson LOWER_CASE_WITH_UNDERSCORES) + mapper toDomain()
core/data/source        RemoteDataSource tipis (apiCall membuka envelope & error BE)
core/data/repository    interface + Default*Repository (UI tidak pernah melihat DTO)
core/domain/model       model domain hasil BE
core/di/AppContainer    DI manual, diinisialisasi TemuKaryaApplication
core/session            SessionManager (token di SharedPreferences, 401 → kembali ke Login)
feature/*/viewmodel     ViewModel + UiState (StateFlow), factory(repository)
feature/*/container     Composable yang collectAsStateWithLifecycle() lalu memanggil komponen UI
```

Store lama yang masih dipakai banyak layar (`UmkmAccount`, `UmkmProjectStore`, `UmkmConversationStore`,
`UmkmNegotiationStore`, `UmkmNotificationStore`, `FreelancerProfileStore`) sekarang hanya **cache tampilan** yang diisi
ViewModel dari BE dan dikosongkan saat logout. Store/data dummy lain dihapus.

## Pemetaan layar → endpoint

| Area | Layar | Endpoint |
|---|---|---|
| Auth | Splash, Login, Registrasi, Lupa Password, Cek Email | `/auth/login`, `/auth/register`, `/auth/me`, `/auth/logout`, `/auth/email/resend`, `/auth/password/forgot` |
| UMKM | Profil Usaha, Profil | `/profiles/umkm`, `/profiles/umkm/me`, `/workflow/projects`, `/payments/history`, `/notifications/unread-count` |
| UMKM | Beranda | `/workflow/projects`, `/freelancers` |
| UMKM | Buat/Edit Project | `/project-categories`, `/skills`, `POST/PUT/DELETE /projects`, `/projects/{id}/attachments` |
| UMKM | Project Saya | `/workflow/projects`, `DELETE /projects/{id}` |
| UMKM | Pengajuan, Profil Freelancer | `/projects/{id}/applications`, `/projects/{id}/recommendations/freelancers`, `/freelancers/{id}`, `/freelancers/{id}/reviews`, `POST /projects/{id}/agreement` |
| UMKM | Tinjau Rencana, Negosiasi | `/projects/{id}/workflow`, `/milestones`, `/agreement/approve`, `/agreement/request-changes`, `/cancel` |
| UMKM | Pembayaran, Bukti Bayar | `/projects/{id}/payment`, `/payment/qris`, `/payment/simulate` (+ `Idempotency-Key`) |
| UMKM | Pemantauan Progres | `/workflow`, `/milestones`, `/milestones/delay-risk` |
| UMKM | Review Hasil, Selesai, Ulasan | `/submissions`, `/request-revision`, `/approve`, `/review`, unduh file hasil |
| UMKM | Riwayat Transaksi, Notifikasi, Cari Freelancer, Chat | `/payments/history`, `/notifications`, `/freelancers`, `/conversations` |
| Freelancer | Beranda, Cari Proyek, Detail, Ajukan Penawaran | `/wallet`, `/workflow/projects`, `/freelancers/me/recommendations/projects`, `/projects`, `POST /projects/{id}/applications` |
| Freelancer | Proyek (Status Pengajuan), Negosiasi, Rencana Pengerjaan | `/applications/mine`, `/workflow`, `PUT /milestones`, `/agreement/approve`, `/start`, `/milestones/{id}/complete`, `/cancel` |
| Freelancer | Chat (+ Unggah Draf), Permintaan Revisi | `/conversations`, `POST /projects/{id}/submissions` (multipart), `/submissions` |
| Freelancer | Dompet, Notifikasi, Profil, Portofolio, Pengalaman | `/wallet`, `/wallet/transactions`, `/notifications`, `/profiles/freelancer/*` |

Diuji end-to-end ke BE lokal (2026-11-23) dengan payload yang sama seperti aplikasi: semua langkah dari registrasi sampai
dana RELEASED, dompet, riwayat, notifikasi, ulasan, dan logout lolos. Uji UI di perangkat belum dilakukan.

## Keputusan yang perlu dicek

| # | Hal | Keputusan sekarang | Alasan |
|---|---|---|---|
| 1 | Profil wajib | UMKM harus mengisi Profil Usaha sebelum membuat project, freelancer harus mengisi Profil Profesional sebelum melamar. Beranda UMKM memberi petunjuk bila belum ada | Aturan BE (`Profil UMKM tidak ditemukan`) |
| 2 | Alamat usaha | `business_address` diisi sama dengan kota | Figma hanya punya field kota, BE mewajibkan alamat |
| 3 | Nama pemilik/nama lengkap | Tidak bisa diubah (mengikuti nama akun) | BE belum punya endpoint ubah nama |
| 4 | Foto profil | Hanya tersimpan lokal; avatar memakai inisial | BE belum punya unggah foto profil |
| 5 | Ubah kata sandi | Tombol menampilkan pesan arahkan ke Lupa Password | BE belum punya endpoint (auth.md #26) |
| 6 | Login Google | Menampilkan pesan belum tersedia | Butuh Google Sign-In + `GOOGLE_CLIENT_ID` |
| 7 | Skill project | Saat edit, skill diambil dari hasil matching (bisa kosong bila belum ada freelancer) | `ProjectResponse` BE tidak mengirim `skill_ids` |
| 8 | Identitas UMKM di project publik | Ditampilkan "UMKM TEMUKARYA" sampai freelancer terpilih | Project publik BE tidak memuat nama/kota UMKM |
| 9 | Keahlian custom | Hanya skill dari master BE (26) yang bisa dipilih | BE menolak skill tak terdaftar |
| 10 | Simpan draf project | Menampilkan pesan belum tersedia | BE tidak punya status draf |
| 11 | Minta Ubah rencana | Ditambah dialog catatan | BE mewajibkan `note` |
| 12 | Batal dengan alasan "Lainnya" | Catatan otomatis "Alasan lainnya" | BE mewajibkan `note` untuk LAINNYA, sheet Figma tak punya kolom catatan |
| 13 | Lampiran di chat | Ditolak dengan pesan; file hasil dikirim lewat "Unggah Draf" | BE chat baru mendukung teks |
| 14 | Chat real-time | Polling pesan tiap 5 detik saat ruang chat terbuka | BE belum punya websocket/push |
| 15 | Pencairan dana / rekening | Tombol menampilkan pesan di luar scope MVP | Keputusan PRD |
| 16 | Tab status project UMKM | "Menunggu Kesepakatan" mencakup TERBUKA, MENUNGGU_KESEPAKATAN, MENUNGGU_PEMBAYARAN; "Sedang Dikerjakan" mencakup SIAP_DIKERJAKAN, SEDANG_DIKERJAKAN, REVISI | Tab Figma lebih sedikit dari status BE |
| 17 | Highlight ulasan | Opsi ke-2 Figma "Hasil Siap Cetak Sempurna" dikirim sebagai `HASIL_BERKUALITAS` (label BE "Hasil Sesuai & Berkualitas") | Label Figma & enum BE berbeda |
| 18 | Metrik "Amanah" freelancer | Rasio project selesai / (selesai + dibatalkan) | BE belum punya metrik ini |
| 19 | Kategori/skill portofolio, jenis pekerjaan pengalaman | Disimpan lokal di perangkat | Kolom tidak ada di BE |
| 20 | Simulasi sisi UMKM dari akun freelancer | Dihapus; persetujuan & pembayaran dilakukan akun UMKM | Data kini nyata dari BE |
| 21 | Tombol Mulai Pengerjaan & Tandai Milestone Selesai | Memakai kartu status di layar Rencana Pengerjaan | Figma freelancer tidak punya layar khusus |
