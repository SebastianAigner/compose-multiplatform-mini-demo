package model

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

class BirdsViewModel {
    val httpClient = HttpClient { install(ContentNegotiation) { json() } }

    private val _images = MutableStateFlow(listOf<BirdImage>())
    val images = _images.asStateFlow()


    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow(setOf<String>())
    val categories = _categories.asStateFlow()

    private var allImages = listOf<BirdImage>()

    val scope = CoroutineScope(SupervisorJob())

    init {
        scope.launch {
            _selectedCategory
                .collect { category ->
                    _images.value = allImages.filter { it.type == category }
                }
        }
    }

    suspend fun getImages() {
        val images = httpClient
            .get("https://sebastianaigner.github.io/demo-image-api/pictures.json")
            .body<List<BirdImage>>()

        allImages = images

        _categories.update {
            images.map { it.type }.toSet()
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.update {
            category
        }
    }
}