# Dependency Injection di Android: Hilt vs Koin

## Apa Itu Dependency Injection?

Sebelum masuk ke Hilt dan Koin, kita perlu paham dulu masalah yang ingin diselesaikan.

Jika kalian punya aplikasi sederhana. Ada kelas `UserRepository` yang butuh `ApiService` untuk mengambil data dari internet, dan `ApiService` itu butuh `OkHttpClient` agar bisa terkoneksi ke jaringan.

Tanpa DI, kamu harus menulis kode seperti ini di setiap Activity yang membutuhkannya:

```kotlin
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val okHttpClient = OkHttpClient()              // buat sendiri
        val apiService   = ApiService(okHttpClient)    // rakitin sendiri
        val userRepo     = UserRepository(apiService)  // rakitin lagi

        userRepo.getUsers()
    }
}
```

Sekarang kalau ada 10 Activity yang semuanya butuh `UserRepository`. Berarti harus menulis semua baris diatas di setiap activity. Kalau suatu hari `OkHttpClient` butuh parameter tambahan, harus merubah di 10 tempat sekaligus dan akhirnya akan ribet. Ini yang disebut kode duplikat, dan itu masalah besar.

**Intinya: DI adalah teknik di mana objek tidak membuat dependensinya sendiri, tapi menerima dependensi dari luar. Kamu bilang "saya butuh ini", sistem DI yang mengurus selebihnya.**

Di dunia Android ada dua library DI yang paling populer: **Hilt** (dari Google) dan **Koin** (dari komunitas open source). Keduanya menyelesaikan masalah yang sama, tapi dengan pendekatan yang berbeda. Untuk praktikum kali ini kalian bebas mau pilih yang mana. Tapi penjelasan code nya akan menggunakan library **Hilt**.

---

# BAGIAN 1 — HILT

## Apa Itu Hilt?

Hilt adalah library DI resmi dari Google untuk Android. Hilt dibangun di atas Dagger — framework DI yang sudah lama ada dan sangat powerful, tapi juga dikenal rumit dan butuh banyak kode tambahan. Hilt hadir untuk menyederhanakan Dagger khusus untuk kebutuhan Android.

**Cara kerja Hilt: berbasis anotasi dan code generation.**

Hilt membaca anotasi yang kamu tulis (seperti `@HiltAndroidApp`, `@AndroidEntryPoint`, `@Inject`) saat proses build, lalu secara otomatis menghasilkan kode Dagger di balik layar. Jadi ketika aplikasi berjalan, semua sudah siap.

---

## Setup Hilt

**Langkah 1** — Tambahkan plugin di `build.gradle` level project (root):

```kotlin
// build.gradle.kts (Project level)
plugins {
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
}
```

**Langkah 2** — Tambahkan plugin dan dependensi di `build.gradle` level app:

```kotlin
// build.gradle.kts (App level)
plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-android-compiler:2.59.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
}
```

**Langkah 3** — Aktifkan Java 8:

```kotlin
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
```

---

## Memulai Hilt di Kelas Application

Setiap aplikasi yang menggunakan Hilt wajib memiliki kelas `Application` yang diberi anotasi `@HiltAndroidApp`:

```kotlin
@HiltAndroidApp
class MyApplication : Application() {
    // Tidak perlu isi apa-apa di sini.
    // Hilt otomatis menyiapkan semua container dependensi.
}
```

Jangan lupa daftarkan kelas ini di `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApplication"
    ...>
</application>
```

Anotasi `@HiltAndroidApp` ini ibarat "tombol ON" untuk Hilt. Begitu dipasang, Hilt mulai menyiapkan semua container yang diperlukan untuk menyimpan dan mengelola dependensi.

---

## Menyuntikkan Dependensi ke Activity dan Fragment

Untuk bisa menerima injeksi di Activity, tambahkan anotasi `@AndroidEntryPoint`:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeRepository: ThemeRepository

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeApp(
                themeRepository = themeRepository,
                authRepository = authRepository,
            )
        }
    }
}
```

Dua hal yang perlu diperhatikan:

- `@AndroidEntryPoint` memberitahu Hilt bahwa kelas ini siap menerima injeksi
- `@Inject` menandai field mana yang ingin diisi oleh Hilt
- Field yang di-inject **tidak boleh** `private`

Untuk Fragment caranya sama:

```kotlin
@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject lateinit var userRepository: UserRepository
}
```

> **Catatan:** Kalau kamu pakai `@AndroidEntryPoint` di Fragment, maka Activity yang menampung fragment itu juga harus diberi `@AndroidEntryPoint`.

---

## Memberitahu Hilt Cara Membuat Objek

Hilt perlu tahu cara membuat objek yang akan diinjeksikan. Ada tiga cara utama.

Gini:

---

### Cara 1 — Constructor Injection

Tambahkan `@Inject constructor` pada kelas yang ingin dikelola Hilt:

```kotlin
@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext context: Context,
) { ... }
```

Hilt akan otomatis membuat `AuthRepository` dan menyuntikkan `Context` aplikasi ke constructornya. Context ini digunakan AuthRepository untuk mengakses `SharedPreferences` — tempat data users dan session disimpan secara lokal.

### Cara 2 — Module dengan @Provides

Cara ini dipakai ketika kamu **tidak bisa** menambahkan `@Inject constructor` — misalnya untuk kelas dari library eksternal seperti Retrofit atau OkHttpClient. Kamu tidak bisa ubah kode library orang lain, jadi kamu buat modul yang menjelaskan cara membuatnya:

**Belum ada implementasi di Codenya**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
```

Penjelasan anotasi:

- `@Module` — menandai kelas ini sebagai modul Hilt
- `@InstallIn(SingletonComponent::class)` — modul ini berlaku di seluruh aplikasi
- `@Provides` — menandai fungsi sebagai penyedia dependensi
- `@Singleton` — Hilt hanya akan membuat satu instance dan dipakai ulang di mana saja

### Cara 3 — @Binds untuk Interface

Kalau kamu punya interface dan ingin memberitahu Hilt implementasi mana yang dipakai:

**Belum ada implementasi di Codenya**

```kotlin
interface UserRepository {
    fun getUsers(): List<User>
}

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {
    override fun getUsers() = apiService.fetchUsers()
}

@Module
@InstallIn(ActivityComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}
```

---

## Scope di Hilt: Berapa Lama Objek Hidup?

Secara default, setiap kali Hilt diminta membuat objek, dia akan membuat objek **baru**. Tapi kadang kita mau objek yang sama dipakai di mana saja. Di sinilah scope berperan.

| Anotasi            | Berlaku di       | Objek hidup selama |
| ------------------ | ---------------- | ------------------ |
| `@Singleton`       | Seluruh aplikasi | Aplikasi berjalan  |
| `@ActivityScoped`  | Satu Activity    | Activity aktif     |
| `@FragmentScoped`  | Satu Fragment    | Fragment aktif     |
| `@ViewModelScoped` | Satu ViewModel   | ViewModel aktif    |

Contoh penggunaan:

```kotlin
// OkHttpClient dibuat sekali, dipakai di seluruh aplikasi
@Singleton
@Provides
fun provideOkHttpClient(): OkHttpClient { ... }

// AnalyticsTracker dibuat baru untuk setiap Activity
@ActivityScoped
class AnalyticsTracker @Inject constructor() { ... }
```

---

## Inject ViewModel dengan Hilt

Untuk ViewModel, Hilt menggunakan anotasi `@HiltViewModel` agar Hilt tahu bahwa kelas ini adalah ViewModel yang perlu dikelola.

Contoh pada kode `CategoryViewModel`:

```kotlin
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() { ... }
```

`CategoryViewModel` membutuhkan dua dependensi — `AuthRepository` dan `CategoryRepository`. Keduanya tidak dibuat manual di dalam ViewModel, melainkan Hilt yang menyiapkan dan menyuntikkannya lewat constructor secara otomatis.

Lalu untuk memakainya di Activity atau Fragment, cukup gunakan delegasi `by viewModels()`:

```kotlin
@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private val viewModel: CategoryViewModel by viewModels()
}
```

Tidak perlu membuat `CategoryViewModel` secara manual. Hilt yang mengurus pembuatannya beserta semua dependensi yang dibutuhkan di dalamnya.

---

# BAGIAN 2 — KOIN

## Apa Itu Koin?

Koin adalah framework DI yang dibuat khusus untuk Kotlin. Berbeda dengan Hilt yang mengandalkan anotasi dan code generation, Koin bekerja dengan cara yang jauh lebih sederhana: menggunakan **Kotlin DSL** — yaitu sintaks Kotlin yang ditulis secara deklaratif seperti konfigurasi biasa.

**Cara kerja Koin: berbasis DSL dan runtime resolution.**

Koin tidak menghasilkan kode tambahan saat build. Tidak ada anotasi yang rumit. Semua dependensi didefinisikan menggunakan fungsi-fungsi Kotlin biasa di dalam blok `module { }`, dan semuanya berjalan di runtime.

---

## Setup Koin

Setup Koin jauh lebih simpel dibanding Hilt — tidak perlu plugin Gradle tambahan di level project:

```kotlin
// build.gradle.kts (App level)
dependencies {
    // Koin untuk Android
    implementation("io.insert-koin:koin-android:4.0.0")

    // Koin untuk ViewModel
    implementation("io.insert-koin:koin-androidx-viewmodel:4.0.0")
}
```

---

## Memulai Koin di Kelas Application

```kotlin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)           // berikan context aplikasi ke Koin
            modules(networkModule, repositoryModule)     // daftarkan semua modul
        }
    }
}
```

Di sini tidak ada anotasi seperti `@HiltAndroidApp`. Koin cukup dipanggil dengan fungsi `startKoin { }` di dalam `onCreate`. Lebih terasa seperti kode Kotlin biasa.

---

## Mendefinisikan Dependensi di Modul Koin

Semua cara membuat objek didefinisikan di dalam blok `module { }`:

```kotlin
val networkModule = module {

    // single = dibuat sekali, dipakai ulang (seperti @Singleton di Hilt)
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .client(get())   // get() = minta OkHttpClient yang sudah terdaftar
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

val repositoryModule = module {

    // factory = dibuat baru setiap kali diminta
    factory { UserRepository(get()) }   // get() = minta ApiService
}
```

Tiga keyword paling penting di Koin:

- `single { }` — objek dibuat sekali dan di-cache (setara `@Singleton` di Hilt)
- `factory { }` — objek dibuat baru setiap kali ada permintaan
- `get()` — perintah ke Koin untuk mengambil dependensi lain yang sudah terdaftar

---

## Menyuntikkan Dependensi di Activity dan Fragment

Tidak perlu anotasi `@AndroidEntryPoint` seperti di Hilt. Cukup gunakan delegasi `by inject()`:

```kotlin
class HomeActivity : AppCompatActivity() {

    // by inject() bersifat lazy — baru diambil saat pertama kali diakses
    private val userRepository: UserRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userRepository.getUsers()
    }
}
```

Di Fragment pun caranya sama:

```kotlin
class HomeFragment : Fragment() {

    private val userRepository: UserRepository by inject()
}
```

---

## Inject ViewModel dengan Koin

```kotlin
// Di modul
val appModule = module {
    viewModel { HomeViewModel(get()) }
}
```

```kotlin
// Di Activity
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadUsers()
    }
}
```

---

## Menyuntikkan Interface di Koin

Untuk interface, beritahu Koin implementasi mana yang dipakai menggunakan tipe eksplisit:

```kotlin
val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
}
```

Sintaks `single<UserRepository> { UserRepositoryImpl(get()) }` artinya: "kalau ada yang minta tipe `UserRepository`, berikan `UserRepositoryImpl`."

---

## Named Qualifier di Koin

Kalau kamu punya dua objek dengan tipe yang sama, gunakan `named()`:

```kotlin
val networkModule = module {

    single(named("auth")) {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
    }

    single(named("logging")) {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .build()
    }
}
```

Lalu saat menggunakan:

```kotlin
class HomeActivity : AppCompatActivity() {

    private val authClient: OkHttpClient by inject(named("auth"))
    private val loggingClient: OkHttpClient by inject(named("logging"))
}
```

---

# BAGIAN 3 — PERBANDINGAN HILT VS KOIN

Setelah kenal keduanya, sekarang kita bandingkan secara langsung.

## Perbedaan Cara Kerja

| Aspek                            | Hilt                      | Koin                          |
| -------------------------------- | ------------------------- | ----------------------------- |
| Pendekatan                       | Anotasi + Code Generation | Kotlin DSL + Runtime          |
| Kapan error ketahuan             | Saat build (lebih aman)   | Saat aplikasi jalan (runtime) |
| Kecepatan startup                | Lebih cepat               | Sedikit lebih lambat          |
| Perlu plugin Gradle ekstra?      | Ya                        | Tidak                         |
| Dukungan resmi Google?           | Ya                        | Tidak (komunitas open source) |
| Bisa untuk Kotlin Multiplatform? | Tidak                     | Ya                            |
| Kemudahan belajar                | Lebih curam               | Lebih mudah                   |

---

## Perbedaan Sintaks: Kasus yang Sama

Berikut perbandingan kode untuk menyelesaikan kasus yang identik.

### Mendefinisikan Singleton

**Hilt:**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }
}
```

**Koin:**

```kotlin
val networkModule = module {
    single { OkHttpClient.Builder().build() }
}
```

---

### Inject di Activity

**Hilt:**

```kotlin
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @Inject lateinit var userRepository: UserRepository
}
```

**Koin:**

```kotlin
class HomeActivity : AppCompatActivity() {

    private val userRepository: UserRepository by inject()
}
```

---

### Inject ViewModel

**Hilt:**

```kotlin
// Di ViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel()

// Di Activity
private val viewModel: HomeViewModel by viewModels()
```

**Koin:**

```kotlin
// Di modul
val appModule = module {
    viewModel { HomeViewModel(get()) }
}

// Di Activity
private val viewModel: HomeViewModel by viewModel()
```

---

### Binding Interface

**Hilt:**

```kotlin
@Module
@InstallIn(ActivityComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
```

**Koin:**

```kotlin
val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
}
```

---

## Kapan Pakai Hilt, Kapan Pakai Koin?

Ini bukan soal mana yang lebih baik secara absolut. Keduanya punya kelebihan yang cocok untuk situasi berbeda.

**Pilih Hilt kalau:**

- Kamu mengerjakan proyek Android murni (bukan multiplatform)
- Kamu ingin error ketahuan lebih awal, yaitu saat proses build, bukan saat aplikasi jalan
- Tim kamu sudah familiar dengan ekosistem Google
- Proyek kamu besar dan butuh performa terbaik saat runtime

**Pilih Koin kalau:**

- Kamu baru belajar DI dan ingin sintaks yang lebih mudah dibaca
- Proyek kamu pakai Kotlin Multiplatform (Android + iOS + Desktop)
- Kamu ingin setup yang cepat tanpa plugin Gradle tambahan
- Tim kamu lebih nyaman dengan Kotlin DSL daripada banyak anotasi

---

## Ringkasan Padanan Keyword

| Konsep                                  | Hilt                                 | Koin                                |
| --------------------------------------- | ------------------------------------ | ----------------------------------- |
| Inisialisasi                            | `@HiltAndroidApp` di Application     | `startKoin { }` di Application      |
| Aktifkan injection di Activity/Fragment | `@AndroidEntryPoint`                 | Tidak perlu anotasi                 |
| Inject field                            | `@Inject lateinit var`               | `by inject()`                       |
| Singleton                               | `@Singleton` + `@Provides`           | `single { }`                        |
| Instance baru setiap request            | `@Provides` tanpa scope              | `factory { }`                       |
| ViewModel                               | `@HiltViewModel` + `by viewModels()` | `viewModel { }` + `by viewModel()`  |
| Ambil dependensi lain                   | Ditangani otomatis oleh Hilt         | `get()`                             |
| Beberapa binding tipe sama              | Custom `@Qualifier` annotation       | `named("nama")`                     |
| Binding interface                       | `@Binds`                             | `single<Interface> { Impl(get()) }` |
| Context aplikasi                        | `@ApplicationContext`                | `androidContext()`                  |
