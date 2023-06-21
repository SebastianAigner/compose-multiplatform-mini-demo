import androidx.compose.runtime.saveable.mapSaver
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import model.BirdImage

class BirdsViewModel {
    val scope = CoroutineScope(SupervisorJob())

    val httpClient = HttpClient { install(ContentNegotiation) { json() } }

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _allImages = MutableStateFlow<List<BirdImage>>(emptyList())

    private val _selectedImages = MutableStateFlow<List<BirdImage>>(emptyList())
    val selectedImages = _selectedImages.asStateFlow()

    init {
        fun updateSelectedImages() {
            _selectedImages.value =
                _allImages.value.filter { pic -> pic.category == selectedCategory.value }
        }
        scope.launch {
            selectedCategory.collect { updateSelectedImages() }
        }
        scope.launch {
            _allImages.collect { updateSelectedImages() }
        }
        updateImages()
    }

    val categories = _allImages.map { images ->
        images.map { it.category }.toSet()
    }

    // https://developer.android.com/kotlin/coroutines/coroutines-best-practices#viewmodel-coroutines
    fun updateImages() = scope.launch {
        val images = httpClient
            .get("https://sebastianaigner.github.io/demo-image-api/pictures.json")
            .body<List<BirdImage>>()

        _allImages.value = images
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    companion object {
        val saver = mapSaver(save = {
            mapOf("category" to it.selectedCategory.value)
        }, restore = { saved ->
            BirdsViewModel().apply {
                selectCategory(saved["category"] as String)
            }
        })
    }
}