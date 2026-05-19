# Dependency Injection dengan Koin

Koin adalah framework dependency injection yang ringan dan pragmatis untuk Kotlin. Berbeda dengan Dagger/Hilt yang berbasis code generation pada waktu kompilasi, Koin bekerja sepenuhnya pada runtime menggunakan Kotlin DSL yang bersih dan mudah dibaca. Koin tidak memerlukan anotasi yang rumit, tidak menghasilkan kode tambahan, dan tidak membutuhkan reflection — menjadikannya salah satu pilihan DI paling sederhana untuk proyek Android dan Kotlin Multiplatform.

Koin menyediakan cara deklaratif untuk mendefinisikan dependensi menggunakan **modul**, lalu **menyuntikkannya** ke dalam kelas Android seperti Activity, Fragment, ViewModel, dan lainnya.

---

## Menambahkan Dependensi

Tambahkan dependensi Koin ke file `app/build.gradle` Anda:

<details>
<summary>Groovy</summary>

```groovy
dependencies {
    // Koin untuk Android
    implementation "io.insert-koin:koin-android:4.0.0"

    // Koin untuk Jetpack ViewModel
    implementation "io.insert-koin:koin-androidx-viewmodel:4.0.0"

    // Koin untuk Jetpack Compose (opsional)
    implementation "io.insert-koin:koin-androidx-compose:4.0.0"

    // Koin untuk testing (opsional)
    testImplementation "io.insert-koin:koin-test:4.0.0"
    testImplementation "io.insert-koin:koin-test-junit4:4.0.0"
}
```

</details>

<details>
<summary>Kotlin</summary>

```kotlin
dependencies {
    // Koin untuk Android
    implementation("io.insert-koin:koin-android:4.0.0")

    // Koin untuk Jetpack ViewModel
    implementation("io.insert-koin:koin-androidx-viewmodel:4.0.0")

    // Koin untuk Jetpack Compose (opsional)
    implementation("io.insert-koin:koin-androidx-compose:4.0.0")

    // Koin untuk testing (opsional)
    testImplementation("io.insert-koin:koin-test:4.0.0")
    testImplementation("io.insert-koin:koin-test-junit4:4.0.0")
}
```

</details>

> **Catatan:** Koin tidak memerlukan plugin Gradle tambahan di root `build.gradle`. Cukup tambahkan dependensi di level modul aplikasi saja.  
> Koin sepenuhnya ditulis dalam Kotlin dan memanfaatkan fitur-fitur Kotlin seperti lambda, extension functions, dan DSL — sehingga proyek Anda harus menggunakan Kotlin.

---

## Memulai Koin di Kelas Application

Semua aplikasi yang menggunakan Koin harus menginisialisasi Koin di dalam kelas `Application` menggunakan fungsi `startKoin`:

```kotlin
class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Menyediakan konteks Android ke Koin
            androidContext(this@ExampleApplication)

            // Mendaftarkan semua modul dependensi
            modules(analyticsModule, networkModule)
        }
    }
}
```

Fungsi `startKoin` menerima blok konfigurasi di mana Anda:

- Memberikan `androidContext()` agar Koin dapat menyediakan `Context` secara otomatis.
- Mendaftarkan satu atau lebih modul menggunakan `modules(...)`.

> **Catatan:** Jangan lupa mendaftarkan kelas `Application` kustom Anda di `AndroidManifest.xml`:
>
> ```xml
> <application
>     android:name=".ExampleApplication"
>     ...>
> </application>
> ```

---

## Mendefinisikan Modul Koin

Di Koin, semua dependensi didefinisikan di dalam **modul**. Modul adalah unit logis tempat Anda mendeklarasikan bagaimana setiap tipe objek dibuat dan disediakan.

### Fungsi `module { }`

Gunakan fungsi `module { }` untuk membuat modul Koin:

```kotlin
val analyticsModule = module {
    // Definisi dependensi di sini
}
```

Di dalam blok `module`, Anda menggunakan keyword seperti `single`, `factory`, `viewModel`, dan `bind` untuk mendeklarasikan dependensi.

---

## Cara Menyediakan Dependensi

### `factory` — Instance Baru Setiap Kali

Gunakan `factory` ketika Anda ingin Koin membuat instance baru setiap kali dependensi diminta:

```kotlin
val analyticsModule = module {
    factory { AnalyticsAdapter(get()) }
}
```

Fungsi `get()` di dalam blok digunakan untuk mengambil dependensi lain yang sudah terdaftar di Koin. Koin akan secara otomatis menyediakan `AnalyticsService` jika sudah didefinisikan di modul manapun.

### `single` — Instance Tunggal (Singleton)

Gunakan `single` ketika Anda ingin Koin hanya membuat satu instance dan menggunakannya kembali sepanjang siklus hidup aplikasi:

```kotlin
val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://example.com")
            .build()
            .create(AnalyticsService::class.java)
    }
}
```

Ini setara dengan `@Singleton` di Hilt. Instance dibuat satu kali dan di-cache selama `KoinApplication` masih aktif.

### `viewModel` — Untuk Jetpack ViewModel

Gunakan `viewModel` untuk mendefinisikan ViewModel agar dapat diintegrasikan dengan siklus hidup Android:

```kotlin
val appModule = module {
    viewModel { ExampleViewModel(get()) }
}
```

---

## Menyuntikkan Dependensi ke Kelas Android

### Inject di Activity

Gunakan delegasi `by inject()` untuk menyuntikkan dependensi secara lazy di dalam Activity:

```kotlin
class ExampleActivity : AppCompatActivity() {

    // Lazy injection — dependensi diminta saat pertama kali diakses
    private val analytics: AnalyticsAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // analytics sudah siap digunakan
        analytics.track("screen_open")
    }
}
```

### Inject di Fragment

Cara yang sama berlaku untuk Fragment:

```kotlin
class ExampleFragment : Fragment() {

    private val analytics: AnalyticsAdapter by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analytics.track("fragment_open")
    }
}
```

### Inject ViewModel di Activity

Gunakan delegasi `by viewModel()` untuk menyuntikkan ViewModel:

```kotlin
class ExampleActivity : AppCompatActivity() {

    private val viewModel: ExampleViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData()
    }
}
```

### Inject ViewModel di Fragment

```kotlin
class ExampleFragment : Fragment() {

    // ViewModel milik fragment ini sendiri
    private val viewModel: ExampleViewModel by viewModel()

    // ViewModel yang dibagikan dengan Activity induk
    private val sharedViewModel: SharedViewModel by activityViewModel()
}
```

> **Catatan:** `by inject()` bersifat lazy — dependensi baru dibuat/diambil saat pertama kali properti diakses. Jika Anda ingin mengambil dependensi secara langsung (eager), gunakan `val analytics = get<AnalyticsAdapter>()` di dalam fungsi seperti `onCreate`.

---

## Menyuntikkan Interface

Untuk menyuntikkan interface, Anda perlu memberi tahu Koin implementasi mana yang harus digunakan. Gunakan keyword `bind` atau deklarasikan dengan tipe interface secara eksplisit:

### Cara 1: Menggunakan `bind`

```kotlin
val analyticsModule = module {
    single { AnalyticsServiceImpl(get()) } bind AnalyticsService::class
}
```

### Cara 2: Deklarasi Tipe Eksplisit

```kotlin
val analyticsModule = module {
    single<AnalyticsService> { AnalyticsServiceImpl(get()) }
}
```

Kedua cara ini memberitahu Koin bahwa ketika tipe `AnalyticsService` diminta, Koin akan menyediakan instance `AnalyticsServiceImpl`.

Contoh kelas yang diinjeksikan:

```kotlin
class AnalyticsAdapter(private val service: AnalyticsService) {
    fun track(event: String) = service.send(event)
}

interface AnalyticsService {
    fun send(event: String)
}

class AnalyticsServiceImpl(private val context: Context) : AnalyticsService {
    override fun send(event: String) { /* implementasi */ }
}
```

Modul:

```kotlin
val analyticsModule = module {
    single<AnalyticsService> { AnalyticsServiceImpl(androidContext()) }
    factory { AnalyticsAdapter(get()) }
}
```

---

## Menyediakan Beberapa Binding untuk Tipe yang Sama

Jika Anda membutuhkan beberapa implementasi berbeda dari tipe yang sama, gunakan **named qualifier** di Koin dengan fungsi `named()`:

### Mendefinisikan Qualifier

```kotlin
val networkModule = module {

    single(named("auth")) {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
    }

    single(named("other")) {
        OkHttpClient.Builder()
            .addInterceptor(OtherInterceptor())
            .build()
    }
}
```

### Menggunakan Qualifier saat Inject

Saat menyuntikkan dengan `get()` di dalam modul lain:

```kotlin
val analyticsModule = module {
    single {
        AnalyticsService(
            okHttpClient = get(named("auth"))
        )
    }
}
```

Saat menyuntikkan dengan `by inject()` di Activity/Fragment:

```kotlin
class ExampleActivity : AppCompatActivity() {

    private val authClient: OkHttpClient by inject(named("auth"))
    private val otherClient: OkHttpClient by inject(named("other"))
}
```

> **Praktik terbaik:** Gunakan konstanta string atau enum sebagai nama qualifier untuk menghindari typo:
>
> ```kotlin
> object Qualifiers {
>     const val AUTH_CLIENT = "auth"
>     const val OTHER_CLIENT = "other"
> }
> ```

---

## Menggunakan Context Android

Koin secara otomatis menyediakan `Context` Android setelah Anda memanggil `androidContext()` di `startKoin`. Gunakan fungsi ekstensi berikut di dalam blok modul:

### `androidContext()` — Application Context

```kotlin
val appModule = module {
    single { AnalyticsServiceImpl(androidContext()) }
}
```

### `androidApplication()` — Instance Application

```kotlin
val appModule = module {
    single { AppDatabase.getInstance(androidApplication()) }
}
```

Ini setara dengan `@ApplicationContext` di Hilt. Koin tidak memiliki `@ActivityContext` bawaan karena Koin tidak memiliki konsep component yang ter-scope ke Activity seperti Hilt — namun Anda bisa meneruskan Activity context secara manual melalui parameter.

---

## Scope di Koin

Secara default:

- `single` → instance dibuat sekali, hidup selama aplikasi aktif (setara `@Singleton`)
- `factory` → instance baru dibuat setiap kali diminta (tidak ada scope, tidak di-cache)

Untuk scope yang lebih granular, Koin mendukung **Scope** kustom:

### Mendefinisikan Scope Kustom

```kotlin
val sessionModule = module {
    scope(named("UserSession")) {
        scoped { UserRepository(get()) }
        scoped { UserPreferences(get()) }
    }
}
```

### Membuat dan Menggunakan Scope

```kotlin
// Membuat scope saat user login
val userScope = GlobalContext.get().createScope("user_session_id", named("UserSession"))

// Mengambil dependensi dari scope
val userRepo = userScope.get<UserRepository>()

// Menutup scope saat user logout (dependensi scoped akan dihancurkan)
userScope.close()
```

> **Catatan:** Scope kustom berguna untuk kasus seperti sesi pengguna yang harus hidup lebih lama dari satu Activity tetapi tidak sepanjang siklus aplikasi.

---

## Parameter Dinamis saat Inject

Koin mendukung pengiriman parameter dinamis ke dalam factory saat dependensi diminta. Ini berguna ketika sebagian argumen baru diketahui saat runtime:

### Mendefinisikan factory dengan Parameter

```kotlin
val appModule = module {
    factory { (userId: String) -> UserProfileViewModel(userId, get()) }
}
```

### Menyuntikkan dengan Parameter

Di Activity atau Fragment:

```kotlin
private val profileViewModel: UserProfileViewModel by viewModel { parametersOf("user_123") }
```

Di dalam modul lain:

```kotlin
val otherModule = module {
    factory { get<UserProfileViewModel> { parametersOf("user_456") } }
}
```

---

## Menyuntikkan Dependensi di Kelas yang Tidak Didukung Secara Native

Untuk kelas yang tidak memiliki integrasi Koin bawaan (misalnya `ContentProvider`, Worker, atau kelas custom), Anda dapat mengambil dependensi secara manual menggunakan `KoinComponent` atau `GlobalContext`:

### Menggunakan `KoinComponent`

```kotlin
class ExampleContentProvider : ContentProvider(), KoinComponent {

    // Inject melalui delegasi seperti biasa
    private val analyticsService: AnalyticsService by inject()

    override fun query(...): Cursor {
        analyticsService.send("query_called")
        // ...
    }
}
```

### Menggunakan `GlobalContext` (tanpa mewarisi KoinComponent)

```kotlin
class ExampleWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // Mengambil dependensi secara manual
        val analyticsService = GlobalContext.get().get<AnalyticsService>()
        analyticsService.send("work_started")
        return Result.success()
    }
}
```

> **Catatan:** Pendekatan `KoinComponent` lebih bersih karena memungkinkan penggunaan delegasi `by inject()`. Gunakan `GlobalContext.get()` hanya jika Anda tidak bisa mengubah hierarki kelas.

---

## Lazy vs Eager Injection

Koin mendukung dua mode pengambilan dependensi:

| Mode      | Sintaks       | Kapan digunakan                                                     |
| --------- | ------------- | ------------------------------------------------------------------- |
| **Lazy**  | `by inject()` | Sebagian besar kasus — dependensi diambil saat pertama kali diakses |
| **Eager** | `get()`       | Ketika dependensi harus tersedia segera, bukan secara lazy          |

Contoh:

```kotlin
class ExampleActivity : AppCompatActivity() {

    // Lazy — diambil saat pertama kali diakses
    private val analytics: AnalyticsAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Eager — diambil langsung saat onCreate dipanggil
        val config: AppConfig = get()
    }
}
```

---

## Verifikasi Modul (Opsional tapi Disarankan)

Koin menyediakan mekanisme untuk memverifikasi bahwa semua dependensi yang didefinisikan di modul dapat dipenuhi, tanpa harus menjalankan aplikasi:

```kotlin
class KoinModuleTest : KoinTest {

    @Test
    fun verifyKoinModules() {
        koinApplication {
            androidContext(mockk<Application>())
            modules(analyticsModule, networkModule, appModule)
        }.checkModules()
    }
}
```

Ini setara dengan pemeriksaan grafik dependensi yang dilakukan Dagger/Hilt pada waktu kompilasi, namun dilakukan Koin pada waktu testing.

> **Catatan:** Tambahkan dependensi testing berikut untuk menggunakan `checkModules()`:
>
> ```kotlin
> testImplementation("io.insert-koin:koin-test:4.0.0")
> testImplementation("io.insert-koin:koin-test-junit4:4.0.0")
> ```

---

## Ringkasan: Perbandingan Keyword Koin

| Keyword            | Deskripsi                           | Setara Hilt                               |
| ------------------ | ----------------------------------- | ----------------------------------------- |
| `single { }`       | Instance tunggal (singleton)        | `@Singleton` + `@Provides`/`@Binds`       |
| `factory { }`      | Instance baru setiap kali diminta   | `@Provides` tanpa scope                   |
| `viewModel { }`    | ViewModel yang sadar siklus hidup   | `@HiltViewModel`                          |
| `scoped { }`       | Instance hidup selama scope aktif   | `@ActivityScoped`, `@FragmentScoped`, dll |
| `get()`            | Mengambil dependensi lain dari Koin | Ditangani otomatis oleh Hilt              |
| `named("x")`       | Qualifier untuk membedakan binding  | `@Qualifier` custom annotation            |
| `bind`             | Mengikat implementasi ke interface  | `@Binds`                                  |
| `androidContext()` | Mengambil Application Context       | `@ApplicationContext`                     |
| `by inject()`      | Lazy injection di kelas Android     | `@Inject lateinit var`                    |
| `by viewModel()`   | Lazy ViewModel injection            | `@Inject` + `ViewModelProvider`           |

---

## Koin dan Kotlin Multiplatform

Salah satu keunggulan Koin dibanding Hilt adalah dukungan untuk **Kotlin Multiplatform (KMP)**. Koin menyediakan modul `koin-core` yang dapat digunakan di luar Android — termasuk iOS (via Kotlin/Native), desktop (Kotlin/JVM), dan web (Kotlin/JS):

```kotlin
// shared/src/commonMain/kotlin/AppModule.kt
val sharedModule = module {
    single { UserRepository(get()) }
    single { ApiClient() }
}

// Android-specific
val androidModule = module {
    single { AndroidAnalyticsService(androidContext()) }
}

// iOS-specific (via Kotlin/Native)
val iosModule = module {
    single { IosAnalyticsService() }
}
```

> Hilt hanya mendukung Android. Jika Anda berencana mengembangkan aplikasi multiplatform, Koin adalah pilihan yang jauh lebih fleksibel.
