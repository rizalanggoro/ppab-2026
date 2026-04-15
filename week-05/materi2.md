# Compose Navigation

## Evolusi Navigasi di Android

Di ekosistem Android, cara perpindahan antar layar (_screen routing_) telah berkembang melalui tiga generasi:

### 1. Navigation 1 — Era Klasik

Cara "jadul" sebelum Google merilis arsitektur standar. Navigasi antar layar masih dilakukan serba manual.

**Cara kerjanya:**

- Untuk pindah ke layar penuh yang baru → gunakan `Intent` untuk memanggil `Activity`
- Untuk mengganti sebagian isi layar → gunakan `FragmentManager` dan `FragmentTransaction`

**Kenapa mulai ditinggalkan?**

Pendekatannya sangat _imperatif_ — sistem harus diperintah langkah demi langkah. Efeknya:

1. Kode menjadi sangat panjang (_boilerplate_)
2. Rawan _crash_ (biasanya karena masalah _state loss_)
3. Membingungkan saat melacak tumpukan riwayat layar (_back stack_) ketika user menekan tombol _back_

### 2. Navigation 2 — Jetpack Navigation Component

Standar industri yang paling banyak dipakai dalam beberapa tahun terakhir.

**Konsep utama:**

Google merilis _library_ ini untuk menstandarkan navigasi menjadi satu sumber kebenaran (_single source of truth_). Tiga komponen pentingnya: `NavHost`, `NavController`, dan grafik navigasi (awalnya XML, lalu diadaptasi ke _string routes_ untuk Compose).

**Karakteristik:**

Sistem navigasi memegang kendali penuh atas _back stack_. Di Jetpack Compose, perpindahan layar terlihat seperti ini:

```kotlin
navController.navigate("detail_screen/123")
```

**Kekurangan di Compose:**

Karena pondasinya didesain untuk sistem View/XML sekitar 7 tahun lalu, pendekatannya kadang terasa kaku di Compose. Tumpukan riwayat layar disembunyikan di dalam _library_, sehingga developer tidak bisa bebas melihat atau memanipulasinya layaknya data biasa.

### 3. Navigation 3 — Jetpack Navigation 3

Teknologi terbaru yang dibangun dari nol, khusus untuk Jetpack Compose dan Kotlin Multiplatform (KMP). Versi stabil dirilis akhir tahun 2025.

**Konsep utama:**

Pendekatannya murni berbasis _State_ (_State-Driven_). Tidak ada lagi `NavController` yang rumit.

**Karakteristik:**

Developer memegang kendali penuh atas _back stack_. Tumpukan layar hanyalah sebuah `List` biasa (`SnapshotStateList`). Komponen utamanya bernama `NavDisplay` yang bertugas mendengarkan list tersebut.

| Aksi                        | Cara                           |
| --------------------------- | ------------------------------ |
| Pindah ke layar baru        | `backStack.add(Route)`         |
| Kembali ke layar sebelumnya | `backStack.removeLastOrNull()` |

**Kelebihan:**

- Sangat transparan dan mudah diuji (_testable_)
- Animasi transisi antar layar lebih mudah dibuat
- _Adaptive layouts_ (layar terbagi) menjadi jauh lebih sederhana

> Referensi resmi: https://developer.android.com/guide/navigation/navigation-3?hl=id

## Praktikum: Implementasi Navigation 3

Langkah-langkah menerapkan Navigation 3 ke dalam proyek aplikasi _ToDo List_.

> **Gambaran alurnya:**
> `backStack` (List) → didengarkan oleh `NavDisplay` → menampilkan layar yang sesuai.
> Navigasi = memanipulasi list. Sesederhana itu.

### Langkah 1 — Setup Dependency

Tambahkan dependency ke file `gradle/libs.versions.toml`:

```toml
[versions]
nav3Core = "1.0.1"
lifecycleViewmodelNav3 = "2.11.0-alpha03"
kotlinSerialization = "2.2.21"
kotlinxSerializationCore = "1.9.0"
material3AdaptiveNav3 = "1.3.0-alpha09"

[libraries]
# Core Navigation 3
androidx-navigation3-runtime = { module = "androidx.navigation3:navigation3-runtime", version.ref = "nav3Core" }
androidx-navigation3-ui = { module = "androidx.navigation3:navigation3-ui", version.ref = "nav3Core" }

# Add-on libraries
androidx-lifecycle-viewmodel-navigation3 = { module = "androidx.lifecycle:lifecycle-viewmodel-navigation3", version.ref = "lifecycleViewmodelNav3" }
kotlinx-serialization-core = { module = "org.jetbrains.kotlinx:kotlinx-serialization-core", version.ref = "kotlinxSerializationCore" }
androidx-material3-adaptive-navigation3 = { group = "androidx.compose.material3.adaptive", name = "adaptive-navigation3", version.ref = "material3AdaptiveNav3" }

[plugins]
jetbrains-kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlinSerialization" }
```

Tambahkan plugin dan dependency ke `app/build.gradle.kts`:

```kotlin
plugins {
    // ...
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

dependencies {
    // ...
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)
}
```

> ⚠️ Jangan lupa klik **Sync Now** setelah mengubah file Gradle.

### Langkah 2 — Mendefinisikan Rute (Routes)

Buat file `core/Routes.kt`. File ini mendefinisikan semua halaman yang bisa dikunjungi dalam aplikasi.

**Aturan sederhana:**

- Halaman **tanpa parameter** → gunakan `data object`
- Halaman **dengan parameter** → gunakan `data class`

```kotlin
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object Routes {
    @Serializable
    data object AuthRoute : NavKey

    @Serializable
    data object ListTodoRoute : NavKey

    @Serializable
    data object CreateTodoRoute : NavKey

    @Serializable
    data class DetailTodoRoute(
        val id: String
    ) : NavKey
}
```

### Langkah 3 — Setup Endpoint Utama (ComposeApp)

Buat file `core/ComposeApp.kt` sebagai titik masuk utama aplikasi Compose. Di sinilah `backStack` dibuat dan `NavDisplay` dipasang.

```kotlin
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun ComposeApp() {
    // AuthRoute dijadikan layar pertama yang muncul saat app dibuka
    val backStack = rememberNavBackStack(Routes.AuthRoute)

    TodoListTheme {
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                // Menyimpan state composable saat navigasi
                rememberSaveableStateHolderNavEntryDecorator(),
                // Mengelola ViewModel per layar
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                // Daftar layar akan ditambahkan di langkah berikutnya
            }
        )
    }
}
```

### Langkah 4 — Menghubungkan ComposeApp ke MainActivity

Ubah `MainActivity.kt` agar menjalankan `ComposeApp` sebagai konten utama:

```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.asprak.todolistd.core.ComposeApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeApp()
        }
    }
}
```

### Langkah 5 — Menghubungkan Rute dengan Halaman UI

Daftarkan setiap screen ke route-nya masing-masing di dalam `entryProvider` pada `core/ComposeApp.kt`:

```kotlin
entryProvider = entryProvider {
    // Auth
    entry<Routes.AuthRoute> { AuthScreen() }

    // Todo
    entry<Routes.ListTodoRoute> { ListTodoScreen() }
    entry<Routes.CreateTodoRoute> { CreateTodoScreen() }
    entry<Routes.DetailTodoRoute> { DetailTodoScreen() }
}
```

### Langkah 6 — Membuat Penyedia Akses Navigasi (CompositionLocal)

Agar `backStack` bisa diakses dari layar mana saja tanpa perlu dikirim sebagai parameter, buat file `core/Compositions.kt`:

```kotlin
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

val LocalBackStack = compositionLocalOf<NavBackStack<NavKey>> {
    error("error: LocalBackStack not provided")
}
```

> 💡 `compositionLocalOf` bekerja mirip seperti "variabel global" khusus di dalam Compose. Nilainya disediakan di satu tempat (di `ComposeApp`), lalu bisa dibaca dari composable mana saja di bawahnya.

### Langkah 7 — Mendistribusikan Akses Navigasi ke Seluruh Layar

Update `ComposeApp.kt` untuk mem-_provide_ `backStack` lewat `CompositionLocalProvider`:

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.asprak.todolistd.feature.auth.presentation.AuthScreen
import com.asprak.todolistd.feature.todo.presentation.CreateTodoScreen
import com.asprak.todolistd.feature.todo.presentation.DetailTodoScreen
import com.asprak.todolistd.feature.todo.presentation.ListTodoScreen
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun ComposeApp() {
    val backStack = rememberNavBackStack(Routes.AuthRoute)

    CompositionLocalProvider(LocalBackStack provides backStack) {
        TodoListTheme {
            NavDisplay(
                backStack = backStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<Routes.AuthRoute> { AuthScreen() }
                    entry<Routes.ListTodoRoute> { ListTodoScreen() }
                    entry<Routes.CreateTodoRoute> { CreateTodoScreen() }
                    entry<Routes.DetailTodoRoute> { DetailTodoScreen() }
                }
            )
        }
    }
}
```

### Langkah 8 — Navigasi ke Halaman Baru (Tanpa Parameter)

Setelah `LocalBackStack` tersedia, navigasi tinggal memanggil `.add()`. Contoh di `feature/auth/presentation/AuthScreen.kt`:

```kotlin
@Composable
fun AuthScreen() {
    val backStack = LocalBackStack.current

    Content(
        onClickSubmit = {
            backStack.add(Routes.ListTodoRoute)
        }
    )
}
```

### Langkah 9 — Navigasi ke Halaman Baru (Dengan Parameter)

Untuk membawa data saat berpindah layar, cukup isi properti di `data class` route-nya. Contoh di `feature/todo/presentation/ListTodoScreen.kt`:

```kotlin
@Composable
fun ListTodoScreen() {
    val backStack = LocalBackStack.current

    Content(
        onClickCreate = {
            backStack.add(Routes.CreateTodoRoute)
        },
        onClickTodo = { todoIndex ->
            backStack.add(Routes.DetailTodoRoute("todo-id-$todoIndex"))
        }
    )
}
```

### Langkah 10 — Kembali ke Halaman Sebelumnya (Pop Back Stack)

Untuk tombol _back_ atau aksi sejenisnya, cukup hapus item terakhir dari list. Contoh di `DetailTodoScreen`:

```kotlin
@Composable
fun DetailTodoScreen() {
    val backStack = LocalBackStack.current

    Content(
        onClickBack = {
            backStack.removeLastOrNull()
        }
    )
}
```

### Langkah 11 — Menangkap Parameter di Halaman Tujuan

Parameter yang dikirim lewat `data class` route bisa ditangkap langsung di dalam `entryProvider`. Update `ComposeApp.kt`:

```kotlin
entry<Routes.DetailTodoRoute> {
    val id = it.id

    DetailTodoScreen(id = id)
}
```

Di sini, `it` merujuk pada objek `DetailTodoRoute` yang sudah berisi nilai `id` yang dikirim saat navigasi.

## Ringkasan Konsep

| Konsep          | Navigation 2                      | Navigation 3                         |
| --------------- | --------------------------------- | ------------------------------------ |
| Pindah layar    | `navController.navigate("route")` | `backStack.add(Route)`               |
| Kembali         | `navController.popBackStack()`    | `backStack.removeLastOrNull()`       |
| Back stack      | Tersembunyi di library            | `List` biasa, bisa diakses bebas     |
| Kirim parameter | String route: `"detail/123"`      | Data class: `DetailTodoRoute("123")` |
| Ambil parameter | `savedStateHandle["id"]`          | `it.id` langsung di `entryProvider`  |
