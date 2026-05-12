Rencana Implementasi: ViewModel + UiState untuk fitur TODO

Ringkasan singkat
- Tujuan: Menambahkan ViewModel dan UiState untuk setiap screen di fitur `todo` (List, Create/Edit, Detail) mengikuti pola yang sudah ada di `feature/test/presentation` (mis. TestVM, TestUiState, factory). Tujuan praktis: memisahkan logika presentasi dari Composable, memudahkan testing, dan menyederhanakan navigasi/handling side-effects.
- Lokasi file rencana: plan-todoFeatureViewmodels.prompt.md

Arsitektur & Prinsip
- Gunakan arsitektur MVI-lite: UiState (immutable data class) + ViewModel (StateFlow/MutableStateFlow) + Intent/Action (opsional, bisa method pada ViewModel).
- ViewModel hanya berinteraksi dengan repository (inject via factory). Jangan letakkan Android-specific code di UiState.
- UiState bersifat serializable/parcelable hanya jika perlu untuk navigation state; cukup data immutable yang diperlukan untuk render.
- Side-effects (toast, navigasi per-event) dicatat sebagai satu-shot events: Channel/SharedFlow atau pattern yang sama di project (ikuti pola di `feature/test/presentation`).

Goal
1. Buat ViewModel + UiState untuk:
   - ListTodoScreen
   - CreateTodoScreen (juga dipakai untuk Edit)
   - DetailTodoScreen
2. Pastikan ViewModel dapat:
   - Mendapatkan data dari repository
   - Menangani loading/error/empty states
   - Memicu navigasi/one-shot events
   - Menangani operasi CRUD (create/update/delete) yang diperlukan
3. Ikuti pola coding dan file layout yang konsisten dengan `feature/test/presentation`.

Desain UiState per screen

1) ListTodoUiState
- fields:
  - todos: List<Todo> (initial empty list)
  - isLoading: Boolean
  - error: String? (atau domain Error type)
  - filterCategoryId: String? (opsional)
  - ownerEmail: String? (opsional, jika repo mem-filter by owner)
  - empty: Boolean (derived = !isLoading && todos.isEmpty())
- events:
  - navigateToCreate: Unit (one-shot)
  - navigateToDetail(todoId)
  - showToast(message)

2) TodoFormUiState (Create/Edit)
- fields:
  - title: String
  - description: String
  - dueDate: String? / Long? (sesuaikan domain)
  - categoryId: String? (for category dropdown)
  - isSaving: Boolean
  - validationErrors: Map<Field, String> atau simple flags
  - existingTodoId: String? (null = create; non-null = edit)
  - ownerEmail: String? (if required)
- events:
  - navigateBackWithResult(success: Boolean)
  - showToast(message)

3) DetailTodoUiState
- fields:
  - todo: Todo? (nullable while loading)
  - isLoading: Boolean
  - error: String?
  - isDeleting: Boolean
- events:
  - navigateBackAfterDelete
  - showToast(message)

File-by-file (apa yang dibuat / diubah)
- feature/todo/presentation/
  - ListTodoViewModel.kt (baru)
  - ListTodoUiState.kt (baru)
  - CreateTodoViewModel.kt (baru)
  - TodoFormUiState.kt (baru)
  - DetailTodoViewModel.kt (baru)
  - DetailTodoUiState.kt (baru)
  - viewmodel factories jika project tidak memakai DI (mis. ListTodoViewModelFactory.kt)
  - Update composable files to accept ViewModel or UiState + event lambdas:
    - ListTodoScreen.kt -> accept viewModel: ListTodoViewModel (default via viewModel()) atau (uiState: ListTodoUiState, onEvent..)
    - CreateTodoScreen.kt -> accept viewModel: CreateTodoViewModel
    - DetailTodoScreen.kt -> accept viewModel: DetailTodoViewModel

Catatan integrasi:
- Jika project memakai Hilt/DI: register ViewModel bindings.
- Jika tidak, buat ViewModelProvider.Factory untuk membuat VM dengan dependencies (repositories) yang sudah tersedia di `core/MyApplication.kt`.
- Lihat pattern di `feature/test/presentation` untuk nama class, events, dan penggunaan StateFlow / MutableStateFlow.

Contoh pola ViewModel & Factory (ringkas, Kotlin-like)

// UiState data class
data class ListTodoUiState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterCategoryId: String? = null
)

// ViewModel skeleton
class ListTodoViewModel(
    private val todoRepository: TodoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListTodoUiState())
    val uiState: StateFlow<ListTodoUiState> = _uiState.asStateFlow()

    init { loadTodos() }

    fun loadTodos() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            val todos = todoRepository.getAllTodos() // sesuaikan nama
            _uiState.update { it.copy(isLoading = false, todos = todos) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    fun onTodoClicked(todoId: String) { /* emit navigation event */ }
}

// Factory (jika perlu)
class ListTodoViewModelFactory(private val repo: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListTodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListTodoViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}

Langkah implementasi terurut
1. Baca dan catat pola di `feature/test/presentation` (nama class, StateFlow vs LiveData, event handling, factory pattern).
2. Tambah UiState data classes untuk ketiga screen di `feature/todo/presentation`.
3. Tambah ViewModel skeleton untuk ketiga screen, inject `TodoRepository` (dan `CategoryRepository`/`AuthRepository` jika perlu).
4. Tambah factories jika project tidak menggunakan DI.
5. Update Composable signatures: beri argumen ViewModel default (viewModel(factory = ...)) atau (uiState, onEvent).
6. Implementasikan load/save/delete logic pada ViewModel menggunakan repository; map hasil ke UiState.
7. Implement one-shot events menggunakan SharedFlow/Channel dan expose as Flow untuk Composable berlangganan via LaunchedEffect.
8. Buat unit tests minimal (jika project punya test infra) mirroring `feature/test/presentation` tests.
9. Jalankan build dan manual smoke test di emulator/device.

Testing checklist
- [ ] Build project tanpa error
- [ ] List screen: menampilkan loading -> data -> empty state
- [ ] Create form: validasi input, simpan -> navigasi kembali
- [ ] Edit flow: buka form dengan data terisi, ubah, simpan
- [ ] Detail screen: menampilkan item, delete -> konfirmasi navigasi/refresh
- [ ] Error state handling: repository throw -> error message muncul
- [ ] One-shot event handling di Composable (toast/navigate)
- [ ] ViewModel unit tests (menggunakan fake repository)

Estimasi
- Menulis UiState + ViewModel skeleton (3 screens): 1.5 - 2 jam
- Menghubungkan ke Composables & factories: 1 - 1.5 jam
- Menulis unit tests & manual testing: 1 - 2 jam
- Total: ~4 - 5.5 jam

Catatan / Risiko
- Pastikan API repository method names (getAll, getById, create, update, delete) sesuai dengan file `feature/todo/data/TodoRepository.kt`.
- Jika project menggunakan DI (Hilt), ikuti pattern DI; jika tidak, gunakan ViewModelProvider.Factory dan periksa `core/MyApplication.kt` untuk akses repository instances.
- Periksa fields domain `Todo` & `Category` (categoryId, ownerEmail) untuk menentukan pemfilteran di List screen dan prefill owner di form.

Langkah selanjutnya yang saya sarankan
1. Saya dapat membuat file-file Kotlin skeleton (UiState + ViewModel + factories) sesuai rencana, satu-per-commit, dan menjalankan build untuk memastikan tidak ada error.
2. Jika mau, saya juga bisa menulis contoh test unit sederhana untuk satu ViewModel untuk dijadikan template.

-- Akhir rencana --

