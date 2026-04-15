# Tugas Praktikum PAB (Week 5)

Pada praktikum minggu lalu, kalian telah merancang seluruh UI/UX dari aplikasi. Pada minggu ini, tugas kalian adalah menghubungkan screen yang telah dibuat menggunakan Jetpack Navigation 3.

## Ketentuan Tugas

1. Menggunakan Material 3 dan Jetpack Compose.
2. Mengimplementasikan Navigation 3 (State-driven navigation menggunakan `NavDisplay` dan `LocalBackStack`) sesuai dengan modul praktikum.
3. Misi Navigasi Utama:
   - Basic Routing: Seluruh tombol utama (misal: Login, Daftar, Tambah) harus berfungsi untuk memindahkan pengguna ke screen selanjutnya yang relevan (backStack.add).

   - Back Navigation: Terdapat fitur tombol kembali (Back Arrow / Tombol Batal) yang mengembalikan pengguna ke layar sebelumnya tanpa error (backStack.removeLastOrNull).

   - Passing Parameter: Terdapat minimal satu alur navigasi yang membawa/mengirim data antar layar. Misalnya: Membawa "ID" atau "Nama Item" dari halaman List ke halaman Detail.

4. Mengikuti struktur folder proyek yang telah disarankan pada saat praktikum untuk mempermudah pengembangan (misal: pemisahan file Routes.kt dan Compositions.kt). Namun, jika memiliki struktur yang lebih sesuai dengan kebutuhan proyek, diperbolehkan untuk menggunakannya.
5. _(Nilai Plus) Conditional Navigation_: Implementasikan logika dasar pada navigasi. Contoh: Pengguna hanya bisa pindah dari Halaman Login ke Halaman Utama JIKA textfield tidak kosong.

## Ketentuan Source Code Aplikasi

    1. Menggunakan Android Studio
    2. Menggunakan bahasa pemrograman Kotlin
    3. Aplikasi tidak boleh mengalami force close (crash) saat melakukan perpindahan layar secara berulang

## Detail Pengumpulan Praktikum

Tugas dikumpulkan dalam bentuk _link GitHub_ yang sama seperti sebelumnya (dengan commit terbaru), file laporan dalam format PDF dengan nama _PPAB-05_NIM_Nama.pdf_, serta link YouTube yang menampilkan _seluruh alur navigasi aplikasi kelompok_.

Video dapat berupa screen recording sederhana tanpa editing, cukup menampilkan demo aplikasi dari awal hingga akhir (menekan tombol maju, mundur, dan melihat detail) yang dijalankan pada _Emulator atau Device Fisik (bukan melalui preview Compose)_.

- Link Github (penamaan bebas)
- Laporan: PPAB-05_NIM_Nama.pdf
- Link YT: PPAB-05_NIM_Nama (Penamaan judul video YouTube)

(Catatan: 05 menunjukkan week praktikum saat ini)

## Penilaian didasarkan pada

1. Aplikasi berjalan normal (20%): Aplikasi bebas dari error atau crash ketika dijalankan dan dilakukan perpindahan antar layar (maju & mundur).
2. Relevansi Source Code (40%): Ketepatan penggunaan State-driven Navigation 3, pembuatan Routes, dan implementasi CompositionLocal.
3. Kompleksitas Program (30%): Keberhasilan menerapkan alur navigasi yang membawa parameter data (Passing Data) dan logika kondisi.
4. Kreativitas Individu (10%): Kerapian struktur kode dan kelancaran transisi pengalaman pengguna (UX) secara keseluruhan.
