package com.shyamanand.bookworm.ui.screens.search

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shyamanand.bookworm.R
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.network.model.SearchResult
import com.shyamanand.bookworm.network.model.SearchResultItem
import com.shyamanand.bookworm.ui.screens.common.BookCover
import com.shyamanand.bookworm.ui.state.ResultsGridState
import com.shyamanand.bookworm.ui.state.SearchbarState
import com.shyamanand.bookworm.ui.theme.BookwormTheme

@Composable
fun SearchScreen(
    searchbarState: SearchbarState,
    resultsGridState: ResultsGridState,
    onSearchStringChanged: (String) -> Unit,
    onSearchStringCleared: () -> Unit,
    onBookSelected: (String) -> Unit,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Searchbar(
            searchString = searchbarState.searchString,
            onSearchStringChanged = onSearchStringChanged,
            onSearchStringCleared = onSearchStringCleared,
            onKeyboardDone = {},
            modifier = modifier,
        )
        when (resultsGridState) {
            is ResultsGridState.Success -> {
                Log.i("HomeScreen", "${resultsGridState.searchResult.totalItems} results")
                when (resultsGridState.searchResult.items.size) {
                    0 -> NoResults()
                    else -> SearchResultsGrid(
                        onPreviewClicked = onBookSelected,
                        searchResult = resultsGridState.searchResult,
                        modifier = modifier
                    )
                }
            }
            is ResultsGridState.Loading -> LoadingScreen(
                textId = if (
                    searchbarState.searchString.isNotEmpty() &&
                    searchbarState.searchString.length >= 3
                ) {
                    R.string.loading
                } else {
                    R.string.start_typing
                },
                modifier = modifier
            )
            is ResultsGridState.Error -> ErrorScreen(retryAction, modifier)
        }
    }
}

@Composable
fun LoadingScreen(@StringRes textId: Int, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(textId),
            style = MaterialTheme.typography.displayLarge
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Searchbar(
    searchString: String,
    onSearchStringChanged: (String) -> Unit,
    onSearchStringCleared: () -> Unit,
    onKeyboardDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            val focusRequester = remember { FocusRequester() }
            OutlinedTextField(
                value = searchString,
                onValueChange = onSearchStringChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardActions = KeyboardActions(
                    onDone = { onKeyboardDone() }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.searchbox_placeholder)
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
            if (searchString.isEmpty()) {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
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
        }
    }
}

@Composable
fun SearchResultsGrid(
    onPreviewClicked: (String) -> Unit,
    searchResult: SearchResult,
    modifier: Modifier = Modifier
) {
    Log.i("HomeScreen", "Rendering search results")
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LazyColumn(
            contentPadding = PaddingValues(0.dp),
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Log.i("HomeScreen", "Rendering results grid with ${searchResult.items.size} results")
            items(
                items = searchResult.items,
                key = { book -> book.id }
            ) {
                BookPreview(
                    searchResultItem = it,
                    onClick = { id ->
                        run {
                            Log.d(TAG, "Clicked $id")
                            onPreviewClicked(id)
                        }
                    },
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun BookPreview(
    searchResultItem: SearchResultItem,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val book = searchResultItem.toBook()
    Log.i("HomeScreen", "Rendering preview for ${book.title}")
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable { onClick(searchResultItem.id) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = modifier.padding(top = 2.dp, bottom = 2.dp)
        ) {
            BookCover(
                book = book,
                onClick = { onClick(searchResultItem.id) },
                modifier = modifier
            )
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = modifier.padding(top = 12.dp)
                )
                book.subtitle?.let {
                    if (book.subtitle.isNotEmpty()) {
                        Text(
                            text = book.subtitle,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                if (book.authors.isNotEmpty()) {
                    Text(
                        text = book.authors,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchbarPreview(modifier: Modifier = Modifier) {
    BookwormTheme(useDarkTheme = true) {
        Searchbar(
            searchString = "book",
            onSearchStringChanged = { },
            onSearchStringCleared = { },
            onKeyboardDone = { },
            modifier = modifier
        )
    }

}

//@Preview
//@Composable
//fun SearchResultsGridPreview(modifier: Modifier = Modifier) {
//    val books = listOf(
//        SearchResultItem(
//            id = "IYaPEAAAQBAJ",
//            selfLink = "https://www.googleapis.com/books/v1/volumes/IYaPEAAAQBAJ",
//            volumeInfo = VolumeInfo(
//                title = "Sapiens",
//                imageLinks = ImageLinks(
//                    smallThumbnail = "http://books.google.com/books/content?id=1EiJAwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
//                    thumbnail = "http://books.google.com/books/content?id=1EiJAwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
//                )
//            )
//        ),
//        SearchResultItem(
//            id = "1Gd0QgAACAAJ",
//            selfLink = "https://www.googleapis.com/books/v1/volumes/1Gd0QgAACAAJ",
//            volumeInfo = VolumeInfo(
//                title = "Mapi, Sapi, and Tapi",
//                imageLinks = ImageLinks(
//                    smallThumbnail = "http://books.google.com/books/content?id=1Gd0QgAACAAJ&printsec=frontcover&img=1&zoom=5&source=gbs_api",
//                    thumbnail = "http://books.google.com/books/content?id=1Gd0QgAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api"
//                )
//            )
//        )
//    )
//    BookwormTheme(useDarkTheme = false) {
//        SearchResultsGrid(
//            onPreviewClicked = { id ->
//                Log.d("HomeScreenPreview", "clicked $id")
//            },
//            searchResult = SearchResult(2, books),
//            modifier = modifier
//        )
//    }
//}