# Changelog — Absensi MTs-Al Basroh

## 1.2.0 — Full UI, Branding & Performance Update

### 🎨 UI / UX
- Redesain visual mengikuti reference changelog: biru modern, putih bersih, gradient, rounded card, soft elevation, dan spacing konsisten.
- Branding resmi **logo MTs-Al Basroh** digunakan di layar login dan dashboard.
- Semua kolom input utama memakai bentuk rounded/pill, termasuk password.
- Password sekarang memiliki kontrol tampil/sembunyikan.
- Button, card, chip, dialog, dan chat bubble menggunakan bentuk rounded.
- Light / Dark / System theme dipertahankan dengan palet biru yang konsisten.
- Dashboard Admin, Guru, dan Murid memakai pola visual yang sama agar pengalaman aplikasi konsisten.
- Tidak ada label atau halaman “Demo”.

### 👨‍🏫👨‍💼 Manajemen Murid & Akun
- Admin dapat menambah murid sekaligus membuat akun login murid.
- Guru juga dapat menambah murid dan akun login dari menu Manajemen Murid.
- Username/password dapat ditentukan manual.
- Jika kosong, sistem membuat kredensial otomatis.
- Sistem mengecek username agar tidak bentrok.
- Setelah akun dibuat, kredensial ditampilkan agar dapat diberikan kepada murid.
- QR token murid dibuat otomatis saat akun murid dibuat.

### 📷 Absensi
- QR scanner tetap menggunakan CameraX + ML Kit.
- Validasi sesi, murid aktif, kelas, dan duplikasi absensi tetap dipertahankan.
- Riwayat dan statistik absensi tetap tersedia.

### 📝 Pengajuan Ketidakhadiran
- Murid dapat mengajukan Sakit, Izin, atau Keperluan.
- Periode tanggal, keterangan, dan lampiran opsional.
- Guru/Admin dapat menyetujui atau menolak.
- Catatan reviewer tersimpan.

### 💬 Chatroom
- Chatroom kelas/sekolah berbasis Room.
- Pesan tersimpan lokal dan diperbarui melalui Flow.
- Bubble chat membedakan pengirim sendiri dan pengguna lain.
- Nama pengirim dan waktu pesan ditampilkan.

### 📊 Laporan
- Export laporan Excel-compatible `.xls`.
- Export PDF.
- Rekap data siswa, NIS, kelas, tanggal, waktu, status, dan metode absensi.

### ⚡ Optimasi APK
- Dependency `material-icons-extended` yang tidak digunakan dihapus.
- R8 minification + resource shrinking diaktifkan pada Debug dan Release.
- ABI split diaktifkan untuk `arm64-v8a`, `armeabi-v7a`, dan `x86_64`.
- Tidak lagi menghasilkan satu APK universal yang membawa seluruh native ABI.
- Packaging resource META-INF yang tidak diperlukan dikecualikan.
- GitHub Actions menampilkan ukuran APK hasil build.

### 🔧 Build
- Version Code: **3**
- Version Name: **1.2.0**
- Java 17
- AGP 8.5.2
- Kotlin 2.0.21
- Gradle 8.7
- Compile / Target SDK 35

> Catatan: chat antar-perangkat secara realtime membutuhkan backend/cloud. Versi lokal ini menyimpan pesan di database perangkat dan menjadi fondasi untuk integrasi backend berikutnya.
