## Plan: Todo List + Category + Auth (diperbarui)

Status saat ini (ringkasan perubahan yang sudah diterapkan):
- Aplikasi memakai Jetpack Compose + Navigation 3 dan sudah memiliki layar auth, todo (list/detail/form), dan category screens.
- Model domain `Todo` dan `Category` sudah diubah untuk menyimpan `categoryId` dan `ownerEmail`.
- Implementasi repository awal untuk `AuthRepository`, `TodoRepository`, dan `CategoryRepository` sudah dibuat dan terhubung di `core/MyApplication.kt`.
- Navigasi di-setup sehingga aplikasi mulai dari `AuthRoute` (login/register) dan berpindah ke flow todo saat session aktif.
- Saat ini data disimpan ke `SharedPreferences` menggunakan encoding custom (Base64 per-field, line records) agar segera bekerja.

Permintaan perubahan: gunakan library `kotlinx.serialization` (JSON) untuk proses encode/decode data yang disimpan di `SharedPreferences`. Oleh karena itu plan berikut akan menggantikan encoding custom dengan JSON serialization dan merapikan beberapa berkas agar konsisten.

### Goals
1. Tetap memakai `SharedPreferences` sebagai storage sementara (sesuai awal), tetapi menyimpan dataset sebagai JSON (menggunakan kotlinx.serialization).
2. Menghapus encoding/decoding manual (`PrefsCodec` dan format line-record) dan menggantinya dengan sebuah `JsonPrefsStore` yang generic dan aman.
3. Pastikan semua model yang diserialisasi diberi anotasi `@Serializable`.
4. Lakukan migrasi data (jika diperlukan) atau reset data dev saat mengubah format penyimpanan.

### Steps (file-by-file, urutan implementasi)
1. Dependencies
   - Pastikan dependency `kotlinx-serialization-json` sudah ditambahkan di `gradle/libs.versions.toml` dan `app/build.gradle.kts` (sudah ada di repo sekarang).

2. Model domain
   - Tandai model yang akan diserialisasi dengan `@Serializable` (mis. `domain/Todo.kt`, `domain/Category.kt`, `feature/auth/data/AuthModels.kt`).
   - Konfirmasi bahwa fields sudah representatif (id, title, description, isDone, categoryId, ownerEmail untuk `Todo`; id, name, ownerEmail untuk `Category`).

3. Gunakan `kotlinx.serialization` langsung di repository
   - Jangan buat `JsonPrefsStore` terpisah; repositori akan menggunakan `kotlinx.serialization.json.Json` langsung untuk encode/decode.
   - Contoh pendekatan di repository:
     - val raw = preferences.getString(KEY_TODOS, null)
     - val list = raw?.let { Json.decodeFromString<List<Todo>>(it) } ?: emptyList()
     - preferences.edit().putString(KEY_TODOS, Json.encodeToString(todos)).apply()
   - Gunakan konfigurasi Json yang konsisten: `Json { ignoreUnknownKeys = true; encodeDefaults = true }`.

4. Migrasi repository untuk memakai `JsonPrefsStore`
   - `AuthRepository`: gunakan `JsonPrefsStore.readList<AppUser>(KEY_USERS)` dan `writeList` untuk menyimpan daftar user; gunakan `writeString`/`readString` untuk session.
   - `TodoRepository`: ganti manual encode/decode file dengan `store.readList<Todo>(KEY_TODOS)` dan `store.writeList(KEY_TODOS, todos)`.
   - `CategoryRepository`: pakai `store.readList<Category>(KEY_CATEGORIES)` dan `store.writeList(...)`.

5. Hapus utilitas encoding lama
   - Hapus `PrefsCodec.kt` dan semua kode bergantung pada format line-record.

6. Migrasi data (opsional)
   - Jika ada data dev yang ingin dipertahankan, buat script migrasi yang membaca format lama (line records) dan menulis ulang sebagai JSON. Jika tidak, reset prefs (bersihkan) saat deploy dev.

7. Verifikasi integrasi UI
   - Periksa `feature/todo/presentation/*` dan `feature/category/presentation/*` supaya memanggil repository yang baru.
   - Pastikan `AuthScreen` / `AuthVM` memicu perubahan session yang mengakibatkan ComposeApp mengganti NavDisplay.

8. Testing & Build
   - Jalankan `./gradlew :app:compileDebugKotlin` dan perbaiki error compile bila ada.
   - Manual testing: register -> login -> tambah category -> tambah todo -> hapus category -> verifikasi todo terkait ikut terhapus.

### File-level checklist (ringkas)
- core/JsonPrefsStore.kt: (TIDAK DIPAKAI) tidak perlu membuat store terpisah; gunakan `Json` langsung di repositori
- domain/Todo.kt, domain/Category.kt, feature/auth/data/AuthModels.kt: pastikan `@Serializable`
- feature/auth/data/AuthRepository.kt: gunakan JsonPrefsStore
- feature/todo/data/TodoRepository.kt: gunakan JsonPrefsStore; hapus encode/decode manual
- feature/category/data/CategoryRepository.kt: gunakan JsonPrefsStore; hapus encode/decode manual
- core/PrefsCodec.kt: HAPUS (setelah migrasi selesai)
- core/MyApplication.kt: pastikan meng-instantiate repos dengan `this` untuk JsonPrefsStore
- Lakukan migrasi data atau reset prefs jika diperlukan

### Catatan implementasi
- Karena project sudah kompilasi dan berjalan dengan encoding manual, migrasi ke JSON harus dilakukan hati-hati:
  1) Untuk pengembangan lokal: lebih mudah menghapus prefs dan mulai dari nol (reset). Dokumentasikan ini.
  2) Untuk mempertahankan data: buat fungsi migrasi yang membaca format lama dan menulis JSON.

Jika setuju, saya bisa langsung mengerjakan perubahan ini (mengimplementasikan `JsonPrefsStore` yang menggunakan kotlinx.serialization, mengubah repository agar memakai store tersebut, menghapus `PrefsCodec`, dan menambahkan skrip migrasi sederhana). Saya akan:
1. Buat `JsonPrefsStore` di `core/`.
2. Ganti repositori (Auth/Todo/Category) untuk memakai store.
3. Hapus `PrefsCodec.kt` dan kode manual.
4. Jalankan build dan integrasi test flow (register/login/add/delete) lalu laporkan hasil dan langkah selanjutnya.


