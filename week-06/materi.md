# Android UI Components 2

Pada praktikum ini kita akan mengimplementasikan **Lazy List**, **Alert Dialog**, dan **Bottom Sheet** ke dalam aplikasi **ToDo List** yang sudah kita buat sebelumnya.

Di Jetpack Compose, ada beberapa jenis lazy list yang tersedia, seperti:

- LazyColumn: untuk menampilkan daftar item secara vertikal
- LazyRow: untuk menampilkan daftar item secara horizontal
- LazyVerticalGrid: untuk menampilkan daftar item dalam bentuk grid vertikal
- LazyHorizontalGrid: untuk menampilkan daftar item dalam bentuk grid horizontal

## Outline

- [Persiapan Project](#persiapan-project)
- [Implementasi LazyColumn untuk Daftar Todo](#implementasi-lazycolumn-untuk-daftar-todo)
- [Implementasi LazyVerticalGrid](#implementasi-lazyvertical-grid)
- [Alert Dialog — Konfirmasi Hapus Todo](#alert-dialog--konfirmasi-hapus-todo)
- [Bottom Sheet — Detail Todo](#bottom-sheet--detail-todo)

## Persiapan Project

Gunakan project **ToDo List** yang sudah dibuat pada pertemuan sebelumnya. Pastikan dependency berikut sudah ada di `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
}
```

> ⚠️ Klik **Sync Now** jika ada perubahan pada file Gradle.

## Implementasi LazyColumn untuk Daftar Todo

Praktikum kali ini implementasi lazy list menggunakan lazy column dan lazy vertical grid.

### 1. Pastikan Data Class Todo Sudah Ada

Jika belum ada, buat file `domain/Todo.kt`:

```kotlin
data class Todo(
    val id: String,
    val title: String,
    val description: String,
    val isDone: Boolean = false
)
```

### 2. Buat TodoItem Composable

Buat file baru `feature/todo/presentation/ListTodoScreen.kt`. Komponen ini merepresentasikan satu baris todo di dalam list.

```kotlin

@Composable
fun ListTodoScreen() {
    val backStack = LocalBackStack.current

    Content(
        todos = dummyTodos,
        onClickCreate = {
            backStack.add(Routes.CreateTodoRoute)
        },
        onClickTodo = {
            backStack.add(Routes.DetailTodoRoute(it))
        }
    )
}

@Composable
private fun Content(
    todos: List<Todo> = emptyList(),
    isLoading: Boolean = false,
    onClickCreate: () -> Unit = {},
    onClickTodo: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Todo")
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            when (isLoading) {
                true -> Unit
                else -> FloatingActionButton(
                    onClick = onClickCreate
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null,
                    )
                }
            }
        }
    ) {
        when (isLoading) {
            true -> Box(modifier = Modifier.fillMaxSize()) {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            false -> Column(modifier = Modifier.padding(it)) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Semua") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Belum selesai") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Selesai") }
                    )
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(todos) {
                        TodoListItem(
                            todo = it,
                            onClickTodo = {
                                onClickTodo(it.id)
                            }
                        )
                    }

                    item {
                        Box(modifier = Modifier.height((56 + 32).dp))
                    }
                }
            }
        }
    }
}
```

Pada code diatas, kita telah menerapkan apa yang dinamakan lazy column. Seperti yang udah dijelaskan di pertemuan ke 3, kalian pasti sudah banyak yang menggunakan hal ini.

## Alert Dialog — Konfirmasi Hapus Todo

`AlertDialog` digunakan untuk meminta konfirmasi pengguna sebelum melakukan aksi yang tidak bisa dibatalkan, seperti menghapus todo.

### 1. Buat DeleteTodoDialog Composable

Buat file `feature/todo/presentation/DetailTodoScreen.kt` dengan code seperti dibawah ini:

```kotlin
@Composable
fun DetailTodoScreen(
    id: Int = 0
) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    var isDeleteDialogOpen by remember { mutableStateOf(false) }

    Content(
        id = id,
        onClickBack = {
            backStack.removeLastOrNull()
        },
        onClickDelete = {
            isDeleteDialogOpen = true
        }
    )

    if (isDeleteDialogOpen) {
        DeleteDialog(
            onDismissRequest = {
                isDeleteDialogOpen = false
            },
            onConfirm = {
                isDeleteDialogOpen = false

                // hapus todo

                backStack.removeLastOrNull()
                Toast.makeText(
                    context, "Todo berhasil dihapus!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}

@Composable
private fun Content(
    id: Int = 0,
    isLoading: Boolean = false,
    onClickBack: () -> Unit = {},
    onClickDelete: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = { Text("Detail Todo") },
                actions = {
                    if (!isLoading) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.Edit, contentDescription = null)
                        }
                        IconButton(onClick = onClickDelete) {
                            Icon(Icons.Rounded.Delete, contentDescription = null)
                        }
                    }
                }
            )
        }
    ) {
        when (isLoading) {
            true -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text("Todo title", style = MaterialTheme.typography.titleMedium)
                Text("Todo detail", style = MaterialTheme.typography.bodyMedium)
                Text("id: $id", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun DeleteDialog(
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Hapus Todo")
        },
        text = {
            Text("Apakah Anda yakin ingin menghapus todo ini?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Batal")
            }
        }
    )
}
```

Pada code ini alert dialog dimunculkan setelah mengklik DetailTodoScreen.kt. Alert ini menggunakan state untuk mengontrol Dialog. Statenya seperti dibawah ini.

```kotlin
var isDeleteDialogOpen by remember { mutableStateOf(false) }
```

State ini digunakan untuk mengontrol kapan alert UI dibuka, defaultnya adalah false.

```kotlin
 onClickDelete = {
            isDeleteDialogOpen = true
}
```

Code diatas merupakan trigger ketika alert di klik, yang mana akan merubah state yang sudah didefinisi sebelumnya false menjadi true.

```kotlin
if (isDeleteDialogOpen) {
        DeleteDialog(
            onDismissRequest = {
                isDeleteDialogOpen = false
            },
            onConfirm = {
                isDeleteDialogOpen = false

                // hapus todo

                backStack.removeLastOrNull()
                Toast.makeText(
                    context, "Todo berhasil dihapus!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
```

Alert Dialog ini akan dirender ketika state berubah menjadi true, dan inilah yang dinamakan conditional UI rendering.

```kotlin
@Composable
private fun DeleteDialog(
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Hapus Todo")
        },
        text = {
            Text("Apakah Anda yakin ingin menghapus todo ini?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Batal")
            }
        }
    )
}
```

Code tersebut adalah komponen komponen dari alert dialog. Bagian-bagiannya:

- title = judul dialog
- text = isi pesan
- confirmButton = tombol aksi utama (hapus)
- dismissButton = tombol batal

## Bottom Sheet — Detail Todo

`ModalBottomSheet` digunakan untuk menampilkan detail atau aksi tambahan dari bawah layar tanpa harus berpindah halaman sepenuhnya.

### 1. Buat TodoDetailBottomSheet Composable

Buat file `feature/todo/presentation/CreateTodoScreen.kt` seperti dibawah ini:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTodoScreen() {
    val backStack = LocalBackStack.current

    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Content(
        selectedCategory = dummyCategories.firstOrNull {
            it.id == selectedCategoryId
        },
        onClickSelectCategory = {
            isBottomSheetOpen = true
        },
        onClickBack = {
            backStack.removeLastOrNull()
        },
        onClickSave = {}
    )

    if (isBottomSheetOpen) {
        BottomSheetSelectCategory(
            sheetState = bottomSheetState,
            selectedCategoryId = selectedCategoryId,
            categories = dummyCategories,
            onClickItem = {
                scope.launch {
                    selectedCategoryId = it.id
                    if (bottomSheetState.isVisible) {
                        bottomSheetState.hide()
                    }
                }.invokeOnCompletion { isBottomSheetOpen = false }
            },
            onDismissRequest = {
                scope.launch {
                    if (bottomSheetState.isVisible) {
                        bottomSheetState.hide()
                    }
                }.invokeOnCompletion { isBottomSheetOpen = false }
            }
        )
    }
}

@Composable
private fun Content(
    isLoading: Boolean = false,
    title: String = "",
    onChangeTitle: (String) -> Unit = {},
    detail: String = "",
    onChangeDetail: (String) -> Unit = {},
    selectedCategory: Category? = null,
    onClickSelectCategory: () -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickSave: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text("Create Todo")
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = title,
                        onValueChange = onChangeTitle,
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    TextField(
                        value = detail,
                        onValueChange = onChangeDetail,
                        label = { Text("Detail") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        minLines = 5
                    )
                }

                ListItem(
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Category,
                            contentDescription = null
                        )
                    },
                    headlineContent = {
                        Text("Kategori")
                    },
                    supportingContent = {
                        Text(
                            selectedCategory?.name.let { category ->
                                if (category.isNullOrEmpty()) "Pilih kategori"
                                else category
                            }
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable(enabled = !isLoading) {
                        onClickSelectCategory()
                    }
                )
            }

            when (isLoading) {
                true -> ContainedLoadingIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )

                else -> Button(
                    onClick = onClickSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetSelectCategory(
    sheetState: SheetState = rememberModalBottomSheetState(),
    selectedCategoryId: Int? = null,
    categories: List<Category> = emptyList(),
    onClickItem: (Category) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        LazyColumn {
            items(categories) {
                ListItem(
                    leadingContent = {
                        RadioButton(
                            selected = it.id == selectedCategoryId,
                            onClick = null
                        )
                    },
                    headlineContent = {
                        Text(it.name)
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.clickable {
                        onClickItem(it)
                    }
                )
            }
        }

        OutlinedButton(
            onClick = onDismissRequest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Batal")
        }
    }
}
```

Bottom Sheet adalah panel yang akan muncul dari bawah layar untuk menampilkan pilihan atau aksi tambahan tanpa pindah halaman. Pada code kali ini fungsi nya untuk memilih kategori todo.

```kotlin
var isBottomSheetOpen by remember { mutableStateOf(false) }
val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
```

isBottomSheetOpen digunakan untuk menentukan apakah sheet ditampilkan, lalu bottomSheetState digunakan untuk mengontrol animasi & state (open/close).

```kotlin
onClickSelectCategory = {
    isBottomSheetOpen = true
}
```

Seperti dikonsep Dialog Alert sebelumnya, jadi saat user klik "Kategori" state jadi true dan bottom sheet muncul. Untuk event membuka Bottom Sheet ada dicode di bawah ini.

```kotlin
if (isBottomSheetOpen) {
    BottomSheetSelectCategory(...)
}
```

Ditampilkan secara conditional dan akan di render hanya saat dibutuhkan.

```kotlin
ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        LazyColumn {
            items(categories) {
                ListItem(
                    leadingContent = {
                        RadioButton(
                            selected = it.id == selectedCategoryId,
                            onClick = null
                        )
                    },
                    headlineContent = {
                        Text(it.name)
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.clickable {
                        onClickItem(it)
                    }
                )
            }
        }

        OutlinedButton(
            onClick = onDismissRequest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Batal")
        }
    }
```

Code berikut merupakan isi utama dari ModalBottomSheet yang mana versi modern (overlay dan fokus ke user) serta bisa ditutup dengan hanya swipe atau klik diluar. Isi kategori pada code tersebut itu berada di dalam LazyColumn, kenapa pakai lazy? karena list ini bisa banyak tergantung nanti inputkan banyak kategori didalam aplikasinya.

```kotlin
onClickItem = {
    scope.launch {
        selectedCategoryId = it.id
        if (bottomSheetState.isVisible) {
            bottomSheetState.hide()
        }
    }.invokeOnCompletion { isBottomSheetOpen = false }
}
```

Alurnya adalah

- User klik kategori
- Simpan selectedCategoryId
- Tutup Bottom Sheet (pakai coroutine karena animasi)
- Setelah selesai set isBottomSheetOpen = false

```kotlin
OutlinedButton(
    onClick = onDismissRequest
)
```

Code diatas untuk menutup Bottom Sheet tanpa memilih. Lalu, bottom sheet membutuhkan coroutine untuk hide karena menggunakan animasi suspend function mereka digunakan untuk menutup bottom sheet dengan animasi tanpa nge-freeze UI. Oleh karena itu mengapa terdapat code di bawah ini.

```kotlin
scope.launch { sheetState.hide() }
```

## Glosarium

- Coroutine = cara menjalankan proses asynchronous (tidak blocking) dengan kode yang tetap kelihatan seperti synchronous. Lebih mudahnya adalah menjalankan tugas di background tanpa nge-freeze UI.
- Suspend function = fungsi yang bisa “ditunda” tanpa blocking thread. Suspend function ini hanya bisa dipanggil dari coroutine (launch, async) atau suspend function lain.

## Resources

- [Jetpack Compose Lists & Grids](https://developer.android.com/develop/ui/compose/lists)
- [AlertDialog — Material3](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#AlertDialog)
- [ModalBottomSheet — Material3](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ModalBottomSheet)
- [combinedClickable](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/package-summary#(androidx.compose.ui.Modifier).combinedClickable>)
