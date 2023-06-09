package systems.alderaan.bookworm.ui.screens.bookdetails

import android.graphics.Typeface
import android.util.Log
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shyamanand.bookworm.R
import systems.alderaan.bookworm.TAG
import systems.alderaan.bookworm.data.model.Book
import systems.alderaan.bookworm.ui.screens.common.LoadingScreen
import systems.alderaan.bookworm.ui.state.BookDetailsScreenState
import systems.alderaan.bookworm.ui.theme.BookwormTheme

@Composable
fun BookDetailScreen(
    state: BookDetailsScreenState,
    onAddToShelfClicked: () -> Unit,
    onRemoveFromShelfClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is BookDetailsScreenState.Loading -> LoadingScreen(
            stringResource(R.string.loading),
            modifier
        )
        is BookDetailsScreenState.Error -> ErrorCard(R.string.something_went_wrong, modifier)
        is BookDetailsScreenState.Success -> BookDetails(
            state.book,
            state.inShelf,
            onAddToShelfClicked,
            onRemoveFromShelfClicked,
            modifier
        )
    }
}

@Composable
fun BookDetails(
    book: Book,
    inShelf: Boolean,
    onAddToShelfClicked: () -> Unit,
    onRemoveFromShelfClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(verticalArrangement = Arrangement.Top) {
            book.imageUrl?.let {
                CoverImageContainer(imageUrl = book.imageUrl, modifier = modifier)
            }

            BookInfo(book = book, modifier = modifier)

            ShelfButton(
                modifier = modifier.fillMaxWidth(),
                inShelf = inShelf,
                onRemoveFromShelfClicked = onRemoveFromShelfClicked,
                onAddToShelfClicked = onAddToShelfClicked
            )
        }
        Divider(thickness = 1.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (book.ratingsCount > 0) {
                Ratings(book.ratingsCount, book.averageRating, modifier)
            }
            book.description?.let {
                Description(description = book.description, modifier = modifier)
            }
        }
    }
}

@Composable
fun CoverImageContainer(imageUrl: String, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (imageUrl.isNotEmpty()) {
            Log.d(TAG, "Loading $imageUrl")
            CoverImage(imageUrl, modifier)
        }
    }
}

@Composable
fun CoverImage(imageUrl: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(imageUrl.replace("http:", "https:"))
                .crossfade(true)
                .build(),
            contentDescription = null,
            error = painterResource(R.drawable.no_image),
            placeholder = painterResource(R.drawable.sand_clock),
            modifier = modifier
                .fillMaxWidth()
                .size(320.dp)
                .padding(16.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun BookInfo(book: Book, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = book.title,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            modifier = modifier.align(Alignment.CenterHorizontally)
        )

        if (book.subtitle?.isNotEmpty() == true) {
            Text(
                text = book.subtitle,
                style = MaterialTheme.typography.displaySmall,
                modifier = modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
        }
        if (book.authors.isNotEmpty()) {
            Text(
                text = book.authors,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun Ratings(ratingsCount: Int, averageRating: Float, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(start = 16.dp)) {
        Row(modifier = modifier
            .height(IntrinsicSize.Min)
            .padding(4.dp)) {
            Row(modifier = Modifier.padding(start = 4.dp, end = 4.dp)) {
                Text(
                    text = "$averageRating",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                )
                Icon(
                    painterResource(R.drawable.star_gold),
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
            }
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
            )
            Text(
                text = "$ratingsCount ratings",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun Description(description: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Log.i(TAG, "Description: $description")
        if (description.isNotEmpty()) {
            val resolver = LocalFontFamilyResolver.current
            val style = MaterialTheme.typography.bodyMedium
            val htmlTypeface: Typeface = remember(resolver, style) {
                resolver.resolve(
                    fontFamily = style.fontFamily,
                    fontWeight = style.fontWeight ?: FontWeight.Normal,
                    fontStyle = style.fontStyle ?: FontStyle.Normal,
                    fontSynthesis = style.fontSynthesis ?: FontSynthesis.None
                )
            }.value as Typeface

            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        text =
                            HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
                        typeface = htmlTypeface

                    }
                },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun ShelfButton(
    modifier: Modifier = Modifier,
    inShelf: Boolean,
    onRemoveFromShelfClicked: () -> Unit,
    onAddToShelfClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (inShelf) {
            ShelfButton(
                imageVector = Icons.Default.Clear,
                description = "Clear",
                label = R.string.remove_from_shelf,
                color = MaterialTheme.colorScheme.secondary
            ) {
                onRemoveFromShelfClicked()
            }
        } else {
            ShelfButton(
                imageVector = Icons.Default.Add,
                description = "Add",
                label = R.string.add_to_shelf,
                color = MaterialTheme.colorScheme.primary
            ) {
                onAddToShelfClicked()
            }
        }
    }
}

@Composable
fun ShelfButton(
    imageVector: ImageVector,
    description: String?,
    label: Int,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(color)
    ) {
        Icon(imageVector = imageVector, contentDescription = description)
        Text(
            stringResource(label),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun ErrorCard(@StringRes error: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(error),
            style = MaterialTheme.typography.displayLarge
        )
    }
}

@Preview
@Composable
fun BookDetailScreenPreview(modifier: Modifier = Modifier) {
    BookwormTheme {
        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            BookDetails(
                book = Book(
                    title = "Book Title",
                    subtitle = "Read this book",
                    authors = "Shyam Anand",
                    ratingsCount = 300,
                    averageRating = 4.1f,
                    description = "<b>This is a book.</b><p>It contains sentences made using words, that are themselves made of letters.</p>"
                ),
                inShelf = true,
                onAddToShelfClicked = {},
                onRemoveFromShelfClicked = {},
                modifier = modifier
            )

        }
    }
}