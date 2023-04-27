package com.shyamanand.bookworm.ui.screens.search

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shyamanand.bookworm.R
import com.shyamanand.bookworm.ui.screens.common.LoadingScreen
import com.shyamanand.bookworm.ui.screens.common.SearchResultsGrid
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
                onKeyboardDone = {},
                onSearchByImageClicked = searchByImage,
                modifier = modifier,
            )
        }

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
fun ImageSearchBar(image: Uri, resetSearchbar: () -> Unit, modifier: Modifier = Modifier) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Searchbar(
    searchString: String,
    onSearchStringChanged: (String) -> Unit,
    onSearchStringCleared: () -> Unit,
    onKeyboardDone: () -> Unit,
    onSearchByImageClicked: () -> Unit,
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

@Preview
@Composable
fun SearchbarPreview(modifier: Modifier = Modifier) {
    BookwormTheme(useDarkTheme = true) {
        Searchbar(
            searchString = "",
            onSearchStringChanged = { },
            onSearchStringCleared = { },
            onKeyboardDone = { },
            onSearchByImageClicked = {},
            modifier = modifier
        )
    }

}