import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import model.BirdImage

@Composable
fun BirdAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(primary = Color.Black, background = Color.Black),
        shapes = MaterialTheme.shapes.copy(
            AbsoluteCutCornerShape(0.dp),
            AbsoluteCutCornerShape(0.dp),
            AbsoluteCutCornerShape(0.dp),
        ),
    ) {
        content()
    }
}

@Composable
fun App() {
    BirdAppTheme {
        val vm = rememberSaveable(saver = BirdsViewModel.saver) { BirdsViewModel() }
        BirdsPage(vm)
    }
}

@Composable
fun BirdsPage(viewModel: BirdsViewModel) {
    val imageList by viewModel.selectedImages.collectAsState(emptyList())
    val categories by viewModel.categories.collectAsState(emptyList())
    LaunchedEffect(Unit) {
        viewModel.updateImages()
    }
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            Modifier.fillMaxWidth().padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            for (category in categories) {
                Button(
                    modifier = Modifier.aspectRatio(1.0f).fillMaxSize().weight(1.0f),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        focusedElevation = 0.dp
                    ),
                    onClick = {
                        viewModel.selectCategory(category)
                    }) {
                    Text(category)
                }
            }
        }
        AnimatedVisibility(imageList.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                content = {
                    items(imageList) { photo ->
                        BirdImageCell(photo)
                    }
                },
                modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp)
            )
        }
    }
}

@Composable
fun BirdImageCell(b: BirdImage) {
    KamelImage(
        asyncPainterResource(data = "https://sebastianaigner.github.io/demo-image-api/${b.path}"),
        null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().aspectRatio(1.0f)
    )
}

expect fun getPlatformName(): String