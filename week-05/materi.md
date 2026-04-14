# Compose Navigation

---

## Evolusi Navigasi di Android

Di ekosistem Android, evolusi cara perpindahan antar layar (screen routing) umumnya dibagi ke dalam tiga fase atau generasi:

### 1. Navigation 1 (Era Klasik)

Ini adalah cara "jadul" sebelum Google merilis arsitektur standar. Di era ini, navigasi antar layar masih dilakukan serba manual.

**Gimana cara kerjanya?**

- Untuk pindah ke layar penuh yang baru, digunakan `Intent` untuk memanggil `Activity`.
- Kalau hanya ingin mengganti sebagian isi layar, komponen yang dipakai adalah `FragmentManager` dan `FragmentTransaction`.

**Kenapa sekarang mulai ditinggalkan?**
Pendekatannya sangat _imperatif_ (sistem harus diperintah langkah demi langkah). Efeknya:

1. Kodenya menjadi sangat panjang (_boilerplate_).
2. Rawan _crash_ (biasanya karena masalah _state loss_).
3. Membingungkan saat harus melacak tumpukan riwayat layar (_back stack_) ketika _user_ menekan tombol _back_.

### 2. Navigation 2 (Jetpack Navigation Component)

Ini adalah standar industri yang paling banyak dipakai dalam beberapa tahun terakhir.

**Konsep Utama:**
Google merilis _library_ ini untuk menstandarkan navigasi menjadi satu sumber kebenaran (_single source of truth_). Ada tiga komponen penting yang digunakan: `NavHost`, `NavController`, dan grafik navigasi (awalnya berbasis XML, lalu diadaptasi menggunakan _string routes_ untuk Compose).

**Karakteristik:**
Sistem navigasi memegang kendali penuh atas _back stack_. Di Jetpack Compose, pemanggilan pindah layar biasanya terlihat seperti ini: `navController.navigate("detail_screen/123")`.

**Kekurangan di Compose:**
Karena pondasi awalnya didesain untuk sistem View/XML sekitar 7 tahun lalu, pendekatannya kadang terasa kaku di Compose. Tumpukan riwayat layar disembunyikan di dalam _library_, sehingga _developer_ tidak bisa dengan bebas melihat atau memanipulasinya layaknya data biasa.

### 3. Navigation 3 (Jetpack Navigation 3)

Ini adalah teknologi terbaru (versi stabil rilis akhir tahun 2025) yang dibangun dari nol khusus untuk Jetpack Compose dan Kotlin Multiplatform (KMP).

**Konsep Utama:**
Pendekatannya murni berbasis _State_ (State-Driven). Tidak ada lagi `NavController` yang rumit.

**Karakteristik:**
_Developer_ memegang kendali penuh atas _back stack_. Tumpukan layar di sini hanyalah sebuah _List_ biasa (`SnapshotStateList`). Komponen utamanya bernama `NavDisplay` yang bertugas mendengarkan _list_ tersebut.

- Untuk pindah ke layar baru: Cukup tambahkan (_add_) layar ke dalam _list_.
- Untuk kembali (tombol _back_): Cukup hapus (_remove_) item terakhir dari _list_ tersebut.

**Kelebihan:**
Sangat transparan, mudah diuji (_testable_), dan membuat pembuatan animasi transisi antar layar atau membagi layar (_adaptive layouts_) menjadi jauh lebih sederhana.

> Referensi: https://developer.android.com/guide/navigation/navigation-3?hl=id

---

## Praktikum: Implementasi Navigation 3

Langkah-langkah menerapkan Navigation 3 ke dalam proyek aplikasi _ToDo List_ yang dibuat sebelumnya.

### 1. Setup Dependency

Tambahkan beberapa _dependency_ yang diperlukan ke file `gradle/libs.versions.toml` untuk mendukung fitur Navigation 3 dan proses serialisasi data pada aplikasi.

```toml
[versions]
nav3Core = "1.0.1"
lifecycleViewmodelNav3 = "2.11.0-alpha03"
kotlinSerialization = "2.2.21"
kotlinxSerializationCore = "1.9.0"
material3AdaptiveNav3 = "1.3.0-alpha09"

[libraries]
# Core Navigation 3 libraries
androidx-navigation3-runtime = { module = "androidx.navigation3:navigation3-runtime", version.ref = "nav3Core" }
androidx-navigation3-ui = { module = "androidx.navigation3:navigation3-ui", version.ref = "nav3Core" }

# Optional add-on libraries
androidx-lifecycle-viewmodel-navigation3 = { module = "androidx.lifecycle:lifecycle-viewmodel-navigation3", version.ref = "lifecycleViewmodelNav3" }
kotlinx-serialization-core = { module = "org.jetbrains.kotlinx:kotlinx-serialization-core", version.ref = "kotlinxSerializationCore" }
androidx-material3-adaptive-navigation3 = { group = "androidx.compose.material3.adaptive", name = "adaptive-navigation3", version.ref = "material3AdaptiveNav3" }

[plugins]
# Optional plugins
jetbrains-kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlinSerialization"}
```

Tambahkan plugin dan dependency berikut ke file `app/build.gradle.kts`:

```kts
plugins {
    ...
    // Optional, provides the @Serialize annotation for autogeneration of Serializers.
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

dependencies {
    ...
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)
}
```

**_(Jangan lupa klik Sync Now)_**

### 2. Mendefinisikan Rute (Routes)

Selanjutnya, buat daftar routes di file `core/Routes.kt`. File ini berfungsi untuk mendefinisikan semua rute (halaman) yang akan digunakan dalam navigasi aplikasi. Setiap route dapat berupa data object (jika tidak membutuhkan parameter) atau data class (jika membutuhkan parameter).

```kt
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

### 3. Setup Endpoint Utama (ComposeApp)

Selanjutnya, buat endpoint utama aplikasi Compose di file `core/ComposeApp.kt`. Endpoint ini berfungsi sebagai titik awal aplikasi, yang akan mengatur navigasi dan menampilkan tampilan utama menggunakan Navigation 3.

```kt
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun ComposeApp() {
    val backStack = rememberNavBackStack(Routes.AuthRoute)

    TodoListTheme {
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                // Add the default decorators for managing scenes and saving state
                rememberSaveableStateHolderNavEntryDecorator(),
                // Then add the view model store decorator
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {

            }
        )
    }
}
```

### 4. Menghubungkan ComposeApp ke MainActivity

Ubah file `MainActivity.kt` agar menjalankan aplikasi berbasis Compose yang telah dibuat.

```kt
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

### 5. Menghubungkan Rute dengan Halaman UI

Daftarkan setiap screen (halaman) pada aplikasi dengan routes yang telah dibuat di file `core/ComposeApp.kt`. Dengan demikian, setiap route akan terhubung ke composable screen yang sesuai.

```kt
entryProvider = entryProvider {
    // auth
    entry<Routes.AuthRoute> { AuthScreen() }

    // todo
    entry<Routes.ListTodoRoute> { ListTodoScreen() }
    entry<Routes.CreateTodoRoute> { CreateTodoScreen() }
    entry<Routes.DetailTodoRoute> { DetailTodoScreen() }
}
```

### 6. Membuat Penyedia Akses Navigasi (CompositionLocal)

Agar proses navigasi antar screen lebih mudah dan terpusat, tambahkan composition di file `core/Compositions.kt`. Dengan cara ini, backStack dapat diakses dari mana saja di dalam composable.

```kt
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

val LocalBackStack = compositionLocalOf<NavBackStack<NavKey>> {
    error("error: LocalBackStack not provided")
}
```

### 7. Mendistribusikan Akses Navigasi ke Seluruh Layar

Setelah menambahkan composition, update fungsi ComposeApp di `core/ComposeApp.kt` agar mem-provide backStack ke seluruh composable.

```kt
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
                    // Add the default decorators for managing scenes and saving state
                    rememberSaveableStateHolderNavEntryDecorator(),
                    // Then add the view model store decorator
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    // auth
                    entry<Routes.AuthRoute> { AuthScreen() }

                    // todo
                    entry<Routes.ListTodoRoute> { ListTodoScreen() }
                    entry<Routes.CreateTodoRoute> { CreateTodoScreen() }
                    entry<Routes.DetailTodoRoute> { DetailTodoScreen() }
                }
            )
        }
    }
}
```

### 8. Pindah ke Halaman Baru (Tanpa Parameter)

Setelah menggunakan composition, perpindahan antar screen dapat dilakukan dengan mudah menggunakan LocalBackStack. Contoh implementasi pada file `feature/auth/presentation/AuthScreen.kt`:

```kt
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

### 9. Pindah ke Halaman Baru (Dengan Parameter)

Untuk melakukan navigasi ke screen lain dengan membawa parameter, gunakan pola berikut. Contoh implementasi dapat dilihat pada file `feature/todo/presentation/ListTodoScreen.kt`:

```kt
@Composable
fun ListTodoScreen() {
    val backStack = LocalBackStack.current

    Content(
        onClickCreate = {
            backStack.add(Routes.CreateTodoRoute)
        },
        onClickTodo = {
            backStack.add(Routes.DetailTodoRoute("todo-id-$it"))
        }
    )
}
```

### 10. Kembali ke Halaman Sebelumnya (Pop Back Stack)

Untuk kembali ke halaman sebelumnya (pop back stack), gunakan kode berikut pada screen yang diinginkan. Contoh implementasi:

```kt
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

### 11. Menangkap Parameter di Halaman Tujuan

Untuk mengambil parameter yang dikirim melalui navigasi (misal id pada DetailTodoRoute), modifikasi entry pada `ComposeApp.kt` seperti berikut:

```kt
entry<Routes.DetailTodoRoute> {
    val id = it.id

    DetailTodoScreen(id = id)
}
```
