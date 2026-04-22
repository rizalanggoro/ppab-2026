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

Jika belum ada, buat file `data/Todo.kt`:

```kotlin
data class Todo(
    val id: String,
    val title: String,
    val description: String,
    val isDone: Boolean = false
)
```

### 2. Buat TodoItem Composable

Buat file baru `feature/todo/presentation/components/TodoItem.kt`. Komponen ini merepresentasikan satu baris todo di dalam list.

```kotlin
@Composable
fun TodoItem(
    todo: Todo,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isDone,
                onCheckedChange = onCheckedChange
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (todo.isDone) TextDecoration.LineThrough else null
                )
                if (todo.description.isNotBlank()) {
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
```

### 3. Update ListTodoScreen Menggunakan LazyColumn

Update `feature/todo/presentation/ListTodoScreen.kt`:

```kotlin
@Composable
fun ListTodoScreen() {
    val backStack = LocalBackStack.current

    // Data dummy sementara
    val todos = remember {
        mutableStateListOf(
            Todo(id = "1", title = "Belajar Jetpack Compose", description = "Pelajari komponen dasar"),
            Todo(id = "2", title = "Implementasi Navigation 3", description = "Gunakan NavDisplay dan backStack"),
            Todo(id = "3", title = "Buat fitur login", description = ""),
            Todo(id = "4", title = "Tulis unit test", description = "Minimal 80% coverage"),
            Todo(id = "5", title = "Review pull request teman", description = "Cek logika dan styling"),
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Daftar Todo") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { backStack.add(Routes.CreateTodoRoute) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Todo")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 8.dp
            )
        ) {
            items(todos, key = { it.id }) { todo ->
                TodoItem(
                    todo = todo,
                    onClick = {
                        backStack.add(Routes.DetailTodoRoute(id = todo.id))
                    },
                    onCheckedChange = { isChecked ->
                        val index = todos.indexOf(todo)
                        todos[index] = todo.copy(isDone = isChecked)
                    }
                )
            }
        }
    }
}
```

> Perhatikan parameter `key = { it.id }` pada `items()`. Ini membantu Compose mengidentifikasi item secara unik sehingga animasi dan _recomposition_ lebih efisien.

## Implementasi LazyVerticalGrid

Selain list vertikal, kita bisa menampilkan todo dalam format grid. Ini berguna misalnya untuk tampilan "card" yang lebih lebar.

### 1. Buat TodoGridItem Composable

Buat file `feature/todo/presentation/components/TodoGridItem.kt`:

```kotlin
@Composable
fun TodoGridItem(
    todo: Todo,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (todo.isDone)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = if (todo.isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (todo.isDone)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (todo.isDone) TextDecoration.LineThrough else null
                )
                if (todo.description.isNotBlank()) {
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
```

### 2. Tambahkan Toggle Tampilan di ListTodoScreen

Update `ListTodoScreen.kt` untuk mendukung perpindahan antara `LazyColumn` dan `LazyVerticalGrid`:

```kotlin
@Composable
fun ListTodoScreen() {
    val backStack = LocalBackStack.current
    var isGridView by remember { mutableStateOf(false) }

    val todos = remember { /* ... data sama seperti sebelumnya ... */ }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Todo") },
                actions = {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle View"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { backStack.add(Routes.CreateTodoRoute) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Todo")
            }
        }
    ) { innerPadding ->
        val topPad = innerPadding.calculateTopPadding() + 8.dp
        val botPad = innerPadding.calculateBottomPadding() + 8.dp

        if (isGridView) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = topPad, bottom = botPad
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todos, key = { it.id }) { todo ->
                    TodoGridItem(
                        todo = todo,
                        onClick = { backStack.add(Routes.DetailTodoRoute(id = todo.id)) }
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = topPad, bottom = botPad)
            ) {
                items(todos, key = { it.id }) { todo ->
                    TodoItem(
                        todo = todo,
                        onClick = { backStack.add(Routes.DetailTodoRoute(id = todo.id)) },
                        onCheckedChange = { isChecked ->
                            val index = todos.indexOf(todo)
                            todos[index] = todo.copy(isDone = isChecked)
                        }
                    )
                }
            }
        }
    }
}
```

## Alert Dialog — Konfirmasi Hapus Todo

`AlertDialog` digunakan untuk meminta konfirmasi pengguna sebelum melakukan aksi yang tidak bisa dibatalkan, seperti menghapus todo.

### 1. Buat DeleteTodoDialog Composable

Buat file `feature/todo/presentation/components/DeleteTodoDialog.kt`:

```kotlin
@Composable
fun DeleteTodoDialog(
    todoTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(text = "Hapus Todo")
        },
        text = {
            Text(
                text = "Apakah kamu yakin ingin menghapus \"$todoTitle\"? " +
                       "Tindakan ini tidak dapat dibatalkan."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Hapus")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
```

### 2. Integrasikan Dialog ke ListTodoScreen

Update `ListTodoScreen.kt` untuk menampilkan dialog saat item di-_long click_:

```kotlin
@Composable
fun ListTodoScreen() {
    // ...state sebelumnya...

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTodo by remember { mutableStateOf<Todo?>(null) }

    Scaffold(/* ... */) { innerPadding ->
        LazyColumn(/* ... */) {
            items(todos, key = { it.id }) { todo ->
                TodoItem(
                    todo = todo,
                    onClick = {
                        backStack.add(Routes.DetailTodoRoute(id = todo.id))
                    },
                    onLongClick = {
                        selectedTodo = todo
                        showDeleteDialog = true
                    },
                    onCheckedChange = { isChecked ->
                        val index = todos.indexOf(todo)
                        todos[index] = todo.copy(isDone = isChecked)
                    }
                )
            }
        }
    }

    // Tampilkan dialog konfirmasi
    if (showDeleteDialog && selectedTodo != null) {
        DeleteTodoDialog(
            todoTitle = selectedTodo!!.title,
            onConfirm = {
                todos.remove(selectedTodo)
                showDeleteDialog = false
                selectedTodo = null
            },
            onDismiss = {
                showDeleteDialog = false
                selectedTodo = null
            }
        )
    }
}
```

> Perhatikan bahwa `AlertDialog` diletakkan **di luar** `Scaffold`, bukan di dalam `content`-nya. Ini agar dialog bisa muncul di atas seluruh konten layar.

## Bottom Sheet — Detail Todo

`ModalBottomSheet` digunakan untuk menampilkan detail atau aksi tambahan dari bawah layar tanpa harus berpindah halaman sepenuhnya.

### 1. Buat TodoDetailBottomSheet Composable

Buat file `feature/todo/presentation/components/TodoDetailBottomSheet.kt`:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailBottomSheet(
    todo: Todo,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (todo.isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (todo.isDone)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (todo.isDone) TextDecoration.LineThrough else null
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status badge
            Surface(
                shape = RoundedCornerShape(50),
                color = if (todo.isDone)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = if (todo.isDone) "Selesai" else "Belum Selesai",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            if (todo.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Deskripsi",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = todo.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol aksi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus")
                }

                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
            }
        }
    }
}
```

### 2. Integrasikan Bottom Sheet ke ListTodoScreen

Update `ListTodoScreen.kt` agar bottom sheet muncul saat item diklik:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTodoScreen() {
    val backStack = LocalBackStack.current

    val todos = remember { mutableStateListOf(/* data dummy */) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedTodo by remember { mutableStateOf<Todo?>(null) }

    Scaffold(/* ... */) { innerPadding ->
        LazyColumn(/* ... */) {
            items(todos, key = { it.id }) { todo ->
                TodoItem(
                    todo = todo,
                    onClick = {
                        selectedTodo = todo
                        showBottomSheet = true
                    },
                    onLongClick = {
                        selectedTodo = todo
                        showDeleteDialog = true
                    },
                    onCheckedChange = { isChecked ->
                        val index = todos.indexOf(todo)
                        todos[index] = todo.copy(isDone = isChecked)
                    }
                )
            }
        }
    }

    // Alert Dialog
    if (showDeleteDialog && selectedTodo != null) {
        DeleteTodoDialog(
            todoTitle = selectedTodo!!.title,
            onConfirm = {
                todos.remove(selectedTodo)
                showDeleteDialog = false
                selectedTodo = null
            },
            onDismiss = {
                showDeleteDialog = false
                selectedTodo = null
            }
        )
    }

    // Bottom Sheet
    if (showBottomSheet && selectedTodo != null) {
        TodoDetailBottomSheet(
            todo = selectedTodo!!,
            onDismiss = {
                showBottomSheet = false
                selectedTodo = null
            },
            onEdit = {
                showBottomSheet = false
                backStack.add(Routes.DetailTodoRoute(id = selectedTodo!!.id))
                selectedTodo = null
            },
            onDelete = {
                showBottomSheet = false
                showDeleteDialog = true
                // selectedTodo tetap terisi untuk dialog hapus
            }
        )
    }
}
```

## Rangkuman Interaksi

| Aksi                  | Komponen               | Hasil                             |
| --------------------- | ---------------------- | --------------------------------- |
| Klik item todo        | `TodoItem`             | Membuka `TodoDetailBottomSheet`   |
| Long click item todo  | `TodoItem`             | Membuka `DeleteTodoDialog`        |
| Centang checkbox      | `TodoItem`             | Status `isDone` toggle            |
| Konfirmasi hapus      | `AlertDialog`          | Item terhapus dari list           |
| Batal hapus           | `AlertDialog`          | Dialog tertutup, data aman        |
| Tombol Edit di sheet  | `ModalBottomSheet`     | Navigasi ke halaman detail/edit   |
| Tombol Hapus di sheet | `ModalBottomSheet`     | Memunculkan `DeleteTodoDialog`    |
| Klik tombol grid/list | `IconButton` di TopBar | Toggle tampilan `LazyColumn`/Grid |

## Resources

- [Jetpack Compose Lists & Grids](https://developer.android.com/develop/ui/compose/lists)
- [AlertDialog — Material3](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#AlertDialog)
- [ModalBottomSheet — Material3](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ModalBottomSheet)
- [combinedClickable](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/package-summary#(androidx.compose.ui.Modifier).combinedClickable>)
