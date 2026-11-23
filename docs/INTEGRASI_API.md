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
| Auth | Splash, Login, Registrasi, Lupa Password, Cek Email | `/auth/login`, `/auth/register`, `/auth/me` (GET/PATCH), `/auth/logout`, `/auth/email/resend`, `/auth/password/forgot` |
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
| 3 | Nama pemilik/nama lengkap | Bisa diubah; disimpan ke `PATCH /auth/me` (nama akun) sebelum profil disimpan | Endpoint baru BE branch `22-feat/update-account-name` |
| 4 | Foto profil | Diunggah langsung saat dipilih (`PUT /users/me/photo`). Android mengompres ke WebP maks 512 px ≤ 150 KB, BE mengompres ulang dengan aturan sama dan menolak unggahan > 2 MB. Foto asli boleh sampai 15 MB karena yang dikirim hasil kompresi. Ditampilkan di profil sendiri, pembayaran, review hasil, serta (sisi UMKM) Profil Freelancer, Cari Freelancer, Rekomendasi Beranda, dan Pengajuan (BE kini mengirim `profile_image_url` di discovery); chat masih avatar inisial | Permintaan Zaid: server ringan |
| 5 | Ubah kata sandi | Tombol menampilkan pesan arahkan ke Lupa Password | BE belum punya endpoint (auth.md #26) |
| 6 | Login Google | Menampilkan pesan belum tersedia | Butuh Google Sign-In + `GOOGLE_CLIENT_ID` |
| 7 | Skill project | Saat edit, skill diambil dari hasil matching (bisa kosong bila belum ada freelancer) | `ProjectResponse` BE tidak mengirim `skill_ids` |
| 8 | Identitas UMKM di project publik | Ditampilkan "UMKM TEMUKARYA" sampai freelancer terpilih | Project publik BE tidak memuat nama/kota UMKM |
| 9 | Keahlian custom | Hanya skill dari master BE (26) yang bisa dipilih | BE menolak skill tak terdaftar |
| 10 | Simpan draf project | Menampilkan pesan belum tersedia | BE tidak punya status draf |
| 11 | Minta Ubah rencana | Ditambah dialog catatan | BE mewajibkan `note` |
| 12 | Batal dengan alasan "Lainnya" | Catatan otomatis "Alasan lainnya" | BE mewajibkan `note` untuk LAINNYA, sheet Figma tak punya kolom catatan |
| 13 | Lampiran di chat | Galeri/Kamera/Dokumen → `POST /conversations/{id}/attachments`, satu lampiran per pesan (seperti WhatsApp). Foto: pratinjau + caption, dikompres di HP ke WebP 1600 px ≤ 400 KB (server mengompres ulang + thumbnail 320 px); kamera resolusi penuh lewat FileProvider. Dokumen PDF/DOCX/XLSX/PPTX/TXT/CSV/ZIP ≤ 10 MB dengan konfirmasi. Bubble langsung tampil dengan status mengirim; gagal → kirim ulang/hapus. Ketuk foto → layar penuh + simpan; ketuk dokumen → unduh & buka dengan aplikasi lain. File hasil kerja tetap lewat "Unggah Draf" | Kontrak BE `docs/api/chat-attachments.md` |
| 13a | Centang pesan | Jam = mengirim, satu centang = terkirim, dua centang hijau = dibaca (event `read` WebSocket). Sebelumnya bubble freelancer selalu menampilkan centang dibaca | Tidak ada status "diterima perangkat" di BE |
| 13b | Chat UMKM per freelancer | BE membuat satu percakapan per project. Di sisi UMKM, daftar chat menampilkan satu baris per freelancer, dan ruang chatnya menggabungkan pesan semua project (penanda "Project · judul" saat berpindah project, satu WebSocket per percakapan). Pesan baru dikirim ke project yang dipilih lewat "Kirim ke: …" (bawaan: project aktif dengan pesan terbaru). Daftar chat selalu membuka ruang chat; kesepakatan yang menunggu dibuka dari banner. Strip project di ruang chat UMKM dan tombol pesan suara dihapus. Sisi freelancer tetap per project | Permintaan Zaid: chat tidak dobel dan bisa langsung membalas |
| 14 | Chat real-time | WebSocket `WS /conversations/{id}/ws` (OkHttp) di ruang chat UMKM & freelancer: pesan masuk & tanda dibaca langsung tampil, kirim lewat socket. Reconnect otomatis (1→30 detik); polling 5 detik hanya jalan saat socket terputus; REST jadi cadangan kirim | Kontrak BE `docs/api/chat-realtime.md` |
| 15 | Pencairan dana / rekening | Tombol menampilkan pesan di luar scope MVP | Keputusan PRD |
| 16 | Tab status project UMKM | "Menunggu Kesepakatan" mencakup TERBUKA, MENUNGGU_KESEPAKATAN, MENUNGGU_PEMBAYARAN; "Sedang Dikerjakan" mencakup SIAP_DIKERJAKAN, SEDANG_DIKERJAKAN, REVISI | Tab Figma lebih sedikit dari status BE |
| 17 | Highlight ulasan | Opsi ke-2 Figma "Hasil Siap Cetak Sempurna" dikirim sebagai `HASIL_BERKUALITAS` (label BE "Hasil Sesuai & Berkualitas") | Label Figma & enum BE berbeda |
| 18 | Metrik "Amanah" freelancer | Rasio project selesai / (selesai + dibatalkan) | BE belum punya metrik ini |
| 19 | Kategori/skill portofolio, jenis pekerjaan pengalaman | Disimpan lokal di perangkat | Kolom tidak ada di BE |
| 20 | Simulasi sisi UMKM dari akun freelancer | Dihapus; persetujuan & pembayaran dilakukan akun UMKM | Data kini nyata dari BE |
| 21 | Mulai Pengerjaan, tandai milestone, kirim hasil (freelancer) | Rencana Pengerjaan berubah jadi daftar progres setelah dibayar: bilah progres + tombol "Tandai Selesai" per milestone (urutan bebas, dengan konfirmasi karena tidak bisa dibatalkan). Kartu "Status Proyek" (dulu berlabel "Simulasi sisi UMKM") memandu: Mulai Pengerjaan → tandai milestone → Kirim Hasil Akhir (chat proyek, + → Unggah Draf) → Menunggu Review / Revisi → Selesai (Lihat Dompet). Jalan masuk: banner "Proyek sudah disepakati" di Status Pengajuan (dibuka dari Proyek Aktif di Beranda) dan banner di chat proyek | Figma freelancer tidak punya layar khusus; sebelumnya tidak ada jalan masuk setelah kesepakatan |
| 22 | Detail project untuk freelancer terpilih | `GET /projects/{id}` hanya untuk project TERBUKA, jadi bila gagal layar Detail Proyek memakai data `GET /projects/{id}/workflow` | Keputusan BE W11: modul projects tidak diubah |
| 23 | Izin perangkat | Kamera & lokasi: dialog penjelasan TEMUKARYA → dialog izin sistem; bila ditolak permanen tawarkan Buka Pengaturan. Galeri & dokumen: dialog persetujuan aplikasi (diingat), tanpa izin sistem karena memakai pemilih bawaan Android. Lokasi (perkiraan) hanya untuk tombol ikon lokasi di kolom kota profil freelancer & profil usaha | Pilihan Zaid; izin akses semua foto dibatasi kebijakan Play Store |
| 24 | Portofolio di Profil Freelancer (sisi UMKM) | Kartu menampilkan gambar cover dari `cover_image_url` (baru di `GET /freelancers/{id}`; diunduh lewat `GET /profiles/portfolios/{portfolio_id}/media/{media_id}/file`, boleh semua user login, cache 1 hari). Ketuk kartu → `project_url` dibuka di browser default HP; tanpa tautan → pesan. Tautan disimpan lengkap dengan `https://` (ditambahkan otomatis bila tidak diketik) | Permintaan Zaid; endpoint media lama hanya untuk pemilik |
| 25 | Notifikasi HP (push) | Firebase Cloud Messaging. Token perangkat didaftarkan ke `PUT /notifications/devices` setiap login/buka aplikasi dan dilepas (`DELETE`) sebelum logout. BE mengirim data message; aplikasi menampilkan lewat channel "Pesan chat" dan "Aktivitas project", pesan dari percakapan yang sedang dibuka tidak dimunculkan. Ketuk notifikasi → halaman yang sama dengan item di daftar notifikasi, lalu ditandai dibaca. Izin notifikasi (Android 13+) diminta sekali setelah login dengan dialog penjelasan. Konfigurasi Firebase dibaca dari `app/google-services.json` (tidak di-commit); tanpa file itu build tetap jalan dan push nonaktif. WebSocket `/notifications/ws` di BE belum dipakai aplikasi | Permintaan Zaid |
