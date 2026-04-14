ref: https://developer.android.com/guide/navigation/navigation-3?hl=id

tambahkan beberapa deps yang diperlukan ke file toml

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

ubah build gradle di `app/build.gradle.kts`

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

buat daftar routes di file `core/Routes.kt`

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

gunakan data object jika route tidak menerima parameter. dan gunakan data class jika route dapat menerima parameter.

buat endpoint utama dari compose application di `core/ComposeApp.kt`

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

lakukan perubahan pada `MainActivity.kt` agar mengarah ke compose app

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

daftarkan setiap screen dengan routes di file `core/ComposeApp.kt`

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

untuk mempermudah proses navigasi ke depannya, tambahkan composition di `core/Compositions.kt`

```kt
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

val LocalBackStack = compositionLocalOf<NavBackStack<NavKey>> {
    error("error: LocalBackStack not provided")
}
```

selanjutnya, ubah endpoint compose app `core/ComposeApp.kt` agar mem-provide backstack sebagai berikut

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

karena sudah menggunakan composition, untuk melakukan perpindahan screen. dapat dilakukan dengan mudah seperti kode pada file `feature/auth/presentation/AuthScreen.kt`

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

untuk navigasi dengan parameter, dapat dilihat pada contoh file `feature/todo/presentation/ListTodoScreen.kt`

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

untuk kembali ke halaman sebelumnya, dapat dilakukan dengan cara berikut

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

untuk mendapatkan params dari navigasi, dapat dilakukan dengan memodifikasi file `ComposeApp.kt` sebagai berikut

```kt
entry<Routes.DetailTodoRoute> {
    val id = it.id

    DetailTodoScreen(id = id)
}
```
