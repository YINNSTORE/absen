# Absensi MTs-Al Basroh

Aplikasi absensi sekolah Android dengan desain biru modern, rounded UI, mode terang/gelap, QR attendance, pengajuan ketidakhadiran, chatroom kelas, dan laporan.

## Changelog 1.1.0 — UI & School System Update

### Tampilan & UX
- Redesain login dengan visual biru modern dan kartu rounded.
- Seluruh kolom input menggunakan sudut membulat.
- Dashboard Admin, Guru, dan Murid dibuat lebih visual dengan header gradient, statistik, quick actions, dan kartu modern.
- Tombol, kartu, chip, dan area chat memakai bentuk rounded untuk tampilan yang konsisten.
- Dark/Light/System theme tetap didukung.
- Menghilangkan teks dan label yang bernuansa "demo" dari aplikasi.

### Absensi
- QR student tetap terintegrasi dengan alur scan guru.
- Statistik kehadiran ditampilkan lebih jelas pada dashboard.
- Data absensi tetap disimpan menggunakan Room secara offline-first.

### Pengajuan Izin / Sakit
- Murid dapat mengajukan SAKIT, IZIN, atau KEPERLUAN.
- Periode mulai dan selesai.
- Keterangan/alasan.
- Nama lampiran opsional.
- Murid dapat melihat riwayat pengajuan.
- Guru/Admin dapat menyetujui atau menolak pengajuan dengan catatan reviewer.

### Chatroom
- Chatroom grup berbasis Room lokal.
- Pesan masuk/keluar dengan bubble chat.
- Nama pengirim dan waktu pesan.
- Input pesan rounded.
- Room otomatis berdasarkan role/kelas pada data lokal.

> Catatan: versi ini menyediakan fondasi chatroom lokal/offline. Sinkronisasi realtime antar perangkat memerlukan backend/cloud pada tahap berikutnya.

### Laporan & Export
- Export laporan absensi ke **Excel-compatible `.xls`**.
- Export laporan absensi ke **PDF**.
- File disimpan pada folder `Download/Absensi-MTs-Al-Basroh` pada Android modern.
- Laporan memuat nama murid, NIS, kelas, tanggal, waktu, status, dan metode absensi.

### Database
- Room database dinaikkan ke version 2.
- Ditambahkan tabel `leave_requests`.
- Ditambahkan tabel `chat_messages`.
- Ditambahkan migrasi Room 1 → 2 yang mempertahankan data absensi lama saat update.

## Akun awal

- Admin: `admin` / `admin123`
- Guru: `guru` / `guru123`
- Murid: `murid` / `murid123`

Akun awal hanya digunakan untuk instalasi lokal/development dan dapat diganti melalui pengembangan manajemen akun berikutnya.

## Build

- Java 17
- Android Gradle Plugin 8.5.2
- Kotlin 2.0.21
- Gradle 8.7
- Compile SDK 35
- Min SDK 26

## Versi 1.2.0

Update ini memfokuskan aplikasi pada UI biru modern bergaya rounded seperti reference, branding logo resmi MTs-Al Basroh, performa/ukuran APK, serta manajemen murid + pembuatan akun login dari Admin maupun Guru.

### Build APK
Gunakan GitHub Actions workflow **Android Build**. Workflow menjalankan unit test, build Debug teroptimasi, ABI split, dan menampilkan ukuran setiap APK. Untuk perangkat Android 64-bit pilih `arm64-v8a`.
