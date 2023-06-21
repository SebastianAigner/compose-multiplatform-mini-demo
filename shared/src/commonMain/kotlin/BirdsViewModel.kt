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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.BirdImage

data class BirdsUiState(
    val categories: Set<String> = emptySet(),
    val selectedCategory: String? = null,
    val images: List<BirdImage> = emptyList(),
) {
    val selectedImages = images.filter { it.category == selectedCategory }
}

class BirdsViewModel {
    val scope = CoroutineScope(SupervisorJob())

    private val _uiState = MutableStateFlow<BirdsUiState>(BirdsUiState())
    val uiState = _uiState.asStateFlow()

    val httpClient = HttpClient { install(ContentNegotiation) { json() } }

    init {
        updateImages()
    }

    // https://developer.android.com/kotlin/coroutines/coroutines-best-practices#viewmodel-coroutines
    fun updateImages() = scope.launch {
        val images = httpClient
            .get("https://sebastianaigner.github.io/demo-image-api/pictures.json")
            .body<List<BirdImage>>()

        _uiState.update {
            it.copy(
                categories = images.map { it.category }.toSet(),
                images = images
            )
        }
    }

    fun selectCategory(category: String) {
        _uiState.update {
            it.copy(selectedCategory = category)
        }
    }

    companion object {
        val saver = mapSaver(save = {
            mapOf("category" to it._uiState.value.selectedCategory)
        }, restore = { saved ->
            BirdsViewModel().apply {
                selectCategory(saved["category"] as String)
            }
        })
    }
}