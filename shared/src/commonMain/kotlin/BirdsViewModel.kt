import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import model.BirdImage

class BirdsViewModel {
    val httpClient = HttpClient { install(ContentNegotiation) { json() } }

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _allImages = MutableStateFlow<List<BirdImage>>(emptyList())

    val selectedImages = selectedCategory.map { category ->
        _allImages.value.filter {
            it.type == category
        }
    }

    val categories = _allImages.map { images ->
        images.map { it.type }.toSet()
    }

    suspend fun getImages() {
        val images = httpClient
            .get("https://sebastianaigner.github.io/demo-image-api/pictures.json")
            .body<List<BirdImage>>()

        _allImages.value = images
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }
}