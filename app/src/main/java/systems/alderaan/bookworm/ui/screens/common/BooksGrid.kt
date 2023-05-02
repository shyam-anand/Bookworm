package systems.alderaan.bookworm.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.shyamanand.bookworm.R
import systems.alderaan.bookworm.data.model.Book
import systems.alderaan.bookworm.network.model.SearchResultItem

@Composable
fun BooksGrid(
    books: List<Book>,
    onCoverClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(start = 4.dp, end = 4.dp)
    ) {
        items(
            items = books,
            key = { book -> book.id }
        ) {
            BookCover(
                book = it,
                onClick = onCoverClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCover(
    book: Book,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    height: Int = 200,
    width: Int = 130
) {
    ElevatedCard(
        onClick = { onClick(book.id) },
        shape = RectangleShape,
        modifier = modifier
            .padding(start = 2.dp, end = 2.dp, top = 2.dp)
            .size(height = height.dp, width = width.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        if (book.imageUrl != null && book.imageUrl.isNotEmpty()) {
            val imageUrl = book.imageUrl.replace("http:", "https:")
            Row {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .size(Size.ORIGINAL)
                        .crossfade(true)
                        .build()
                )

                if (painter.state is AsyncImagePainter.State.Success) {
                    AsyncImage(
                        model = book.imageUrl.replace("http:", "https:"),
                        contentDescription = stringResource(R.string.cover_image, book.title),
                        contentScale = ContentScale.Fit,
                        modifier = modifier.size(200.dp),
                        error = painterResource(R.drawable.no_image),
                        onLoading = {},
                    )
                } else {
                    CoverPlaceholder(title = book.title, author = book.authors)
                }
            }
        } else {
            CoverPlaceholder(title = book.title, author = book.authors)
        }
    }
}

@Composable
fun BookCover(
    searchResultItem: SearchResultItem,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    height: Int = 200,
    width: Int = 130
) {
    BookCover(
        book = searchResultItem.toBook(),
        onClick = onClick,
        modifier = modifier,
        height = height,
        width = width
    )
}

@Composable
fun CoverPlaceholder(title: String, author: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = author,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            fontSize = 10.sp
        )
    }
}