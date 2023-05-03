package systems.alderaan.bookworm.ui.screens.home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shyamanand.bookworm.R
import systems.alderaan.bookworm.data.model.Book
import systems.alderaan.bookworm.ui.screens.common.BooksGrid
import systems.alderaan.bookworm.ui.screens.common.LoadingScreen
import systems.alderaan.bookworm.ui.screens.search.ErrorScreen
import systems.alderaan.bookworm.ui.screens.search.ImageSearchBar
import systems.alderaan.bookworm.ui.screens.search.NoResults
import systems.alderaan.bookworm.ui.screens.search.Searchbar
import systems.alderaan.bookworm.ui.state.HomeScreenState
import systems.alderaan.bookworm.ui.state.ResultsGridState
import systems.alderaan.bookworm.ui.state.SearchbarState
import systems.alderaan.bookworm.ui.theme.BookwormTheme

const val TAG = "HomeScreen"
@Composable
fun HomeScreen(
    searchbarState: SearchbarState,
    resultsGridState: ResultsGridState,
    onSearchStringChanged: (String) -> Unit,
    onSearchStringCleared: () -> Unit,
    retryAction: () -> Unit,
    resetSearchbar: () -> Unit,
    searchByImage: () -> Unit,
    onCoverClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel
) {

    Column(
        modifier = modifier.fillMaxSize()
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
                onSearchStringChanged = {
                    onSearchStringChanged(it)
                },
                onSearchStringCleared = onSearchStringCleared,
                onSearchByImageClicked = searchByImage
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        when (homeScreenViewModel.state) {
            is HomeScreenState.Search -> {
                BackHandler {
                    resetSearchbar()
                }
                SearchResults(
                    modifier = modifier,
                    resultsGridState = resultsGridState,
                    searchbarState = searchbarState,
                    onBookSelected = onCoverClicked,
                    retryAction = retryAction
                )
            }

            is HomeScreenState.Shelf -> BookshelfScreen(
                homeScreenState = homeScreenViewModel.state as HomeScreenState.Shelf,
                modifier = modifier,
                onCoverClicked = onCoverClicked
            )
            else -> LoadingScreen(stringResource(R.string.loading_shelf))
        }
    }
}

@Composable
fun SearchResults(
    modifier: Modifier = Modifier,
    resultsGridState: ResultsGridState,
    onBookSelected: (String) -> Unit,
    searchbarState: SearchbarState,
    retryAction: () -> Unit
) {
    when (resultsGridState) {
        is ResultsGridState.Success -> {
            Log.i(TAG, "${resultsGridState.searchResult} results")
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

@Composable
fun BookshelfScreen(
    homeScreenState: HomeScreenState.Shelf,
    modifier: Modifier = Modifier,
    onCoverClicked: (String) -> Unit
) {
    if (homeScreenState.books.isNotEmpty()) {
        Bookshelf(
            modifier = modifier,
            homeScreenState = homeScreenState,
            onCoverClicked = onCoverClicked
        )
    } else {
        EmptyShelf(modifier)
    }
}

@Composable
fun Bookshelf(
    modifier: Modifier = Modifier,
    homeScreenState: HomeScreenState.Shelf,
    onCoverClicked: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.size(height = 16.dp, width = 16.dp))
        Image(
            painterResource(R.drawable.bookstack_special_flat),
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
        )
        Text(
            stringResource(R.string.your_books),
            modifier = modifier
                .padding(8.dp),
            style = MaterialTheme.typography.displayMedium
        )
    }

    BooksGrid(homeScreenState.books, onCoverClicked, modifier)
}

@Composable
fun EmptyShelf(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.no_books_in_shelf),
            style = MaterialTheme.typography.displayLarge
        )
    }
}

@Preview
@Composable
fun BooksGridPreview(modifier: Modifier = Modifier) {
    val books = listOf(
        Book(
            id = "abc",
            title = "Book Title",
            subtitle = "Subtitle",
            authors = "Alice, Bob",
            description = "This is the description.",
            categories = "cat1, cat2, cat3",
            imageUrl = null
        ),
        Book(
            id = "abc",
            title = "Book Title",
            subtitle = "Subtitle",
            authors = "Alice, Bob",
            description = "This is the description.",
            categories = "cat1, cat2, cat3",
            imageUrl = null
        ),
        Book(
            id = "abc",
            title = "Book Title",
            subtitle = "Subtitle",
            authors = "Alice, Bob",
            description = "This is the description.",
            categories = "cat1, cat2, cat3",
            imageUrl = null
        ),
        Book(
            id = "abc",
            title = "Book Title",
            subtitle = "Subtitle",
            authors = "Alice, Bob",
            description = "This is the description.",
            categories = "cat1, cat2, cat3",
            imageUrl = null
        )
    )
    BookwormTheme {
        BooksGrid(books = books, onCoverClicked = {}, modifier = modifier)
    }
}