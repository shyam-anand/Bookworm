package systems.alderaan.bookworm.ui.screens.search

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shyamanand.bookworm.R
import systems.alderaan.bookworm.ui.screens.common.BooksGrid
import systems.alderaan.bookworm.ui.screens.common.LoadingScreen
import systems.alderaan.bookworm.ui.state.ResultsGridState
import systems.alderaan.bookworm.ui.state.SearchbarState
import systems.alderaan.bookworm.ui.theme.BookwormTheme

@Composable
fun SearchScreen(
    searchbarState: SearchbarState,
    resultsGridState: ResultsGridState,
    onSearchStringChanged: (String) -> Unit,
    onSearchStringCleared: () -> Unit,
    onBookSelected: (String) -> Unit,
    retryAction: () -> Unit,
    resetSearchbar: () -> Unit,
    searchByImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        if (searchbarState is SearchbarState.ImageSearch) {
            ImageSearchBar(
                image = searchbarState.image,
                resetSearchbar = resetSearchbar,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            val searchString = if (searchbarState is SearchbarState.HasInput) {
                searchbarState.searchString
            } else {
                ""
            }
            Searchbar(
                searchString = searchString,
                onSearchStringChanged = onSearchStringChanged,
                onSearchStringCleared = onSearchStringCleared,
                onSearchByImageClicked = searchByImage,
                modifier = modifier,
            )
        }

        when (resultsGridState) {
            is ResultsGridState.Success -> {
                Log.i("HomeScreen", "${resultsGridState.searchResult} results")
                when (resultsGridState.searchResult.size) {
                    0 -> NoResults()
                    else -> BooksGrid(
                        onCoverClicked = onBookSelected,
                        books = resultsGridState.searchResult,
                        modifier = modifier
                    )
                }
            }
            is ResultsGridState.Loading -> LoadingScreen(
                text = if (
                    searchbarState is SearchbarState.ImageSearch ||
                    (searchbarState is SearchbarState.HasInput &&
                            searchbarState.searchString.isNotEmpty() &&
                            searchbarState.searchString.length >= 3)
                ) {
                    stringResource(R.string.loading)
                } else {
                    stringResource(R.string.start_typing)
                },
                modifier = modifier
            )
            is ResultsGridState.Error -> ErrorScreen(retryAction, modifier)
            is ResultsGridState.Empty -> LoadingScreen("")
        }
    }
}

@Composable
fun ImageSearchBar(
    image: Uri,
    resetSearchbar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.finding_books_by_image),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.Start)
        )
        ImageBox(
            image = image,
            resetSearchbar = resetSearchbar,
            modifier = modifier
                .wrapContentWidth(Alignment.End)
                .weight(1f)
        )
    }
    Divider()
}

@Composable
fun ImageBox(modifier: Modifier = Modifier, image: Uri, resetSearchbar: () -> Unit) {
    val iconSize = 24.dp
    val offsetInPx = LocalDensity.current.run { (iconSize / 2).roundToPx() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding((iconSize / 2))
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(start = 8.dp)
                .wrapContentWidth(Alignment.Start)
                .align(Alignment.CenterEnd)
                .size(height = 120.dp, width = 72.dp)
        ) {
            AsyncImage(
                model = image,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        IconButton(
            onClick = resetSearchbar,
            modifier = Modifier
                .offset {
                    IntOffset(x = +offsetInPx, y = -offsetInPx)
                }
                .clip(CircleShape)
                .background(Color.White)
                .size(iconSize)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Clear",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.search_failed))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun NoResults(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.no_results))
        Text(text = stringResource(R.string.no_results_caption))
    }
}

@Composable
fun Searchbar(
    searchString: String,
    onSearchStringChanged: (String) -> Unit,
    onSearchStringCleared: () -> Unit,
    onSearchByImageClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            SearchTextField(
                modifier = modifier,
                searchString = searchString,
                onSearchStringChanged = onSearchStringChanged,
                onSearchFieldFocusChanged = {}
            )
        }
        if (searchString.isNotEmpty()) {
            IconButton(
                onClick = onSearchStringCleared,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = stringResource(R.string.clear_search_box),
                    modifier = Modifier
                        .size(36.dp)
                        .padding(end = 10.dp)
                )
            }
        } else {
            IconButton(
                onClick = onSearchByImageClicked,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.camera_outlined),
                    contentDescription = stringResource(id = R.string.search_by_image),
                    modifier = Modifier
                        .size(36.dp)
                        .padding(end = 10.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    searchString: String,
    onSearchStringChanged: (String) -> Unit,
    onSearchFieldFocusChanged: () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp, end = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = searchString,
                onValueChange = onSearchStringChanged,
                textStyle = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { onSearchFieldFocusChanged() },
                colors = TextFieldDefaults
                    .textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                shape = CircleShape,
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.search_outlined),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )

            if (searchString.isEmpty()) {
                Text(
                    stringResource(R.string.searchbox_placeholder),
                    style = MaterialTheme.typography.labelSmall
                )
            }

        }

    }


}

@Preview
@Composable
fun SearchbarPreview(modifier: Modifier = Modifier) {
    BookwormTheme {
        Searchbar(
            searchString = "",
            onSearchStringChanged = { },
            onSearchStringCleared = { },
            onSearchByImageClicked = {},
            modifier = modifier
        )
    }

}