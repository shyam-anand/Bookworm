package com.shyamanand.bookworm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.shyamanand.bookworm.R
import com.shyamanand.bookworm.data.model.Book
import com.shyamanand.bookworm.ui.common.BookCover
import com.shyamanand.bookworm.ui.theme.BookwormTheme

@Composable
fun BookshelfScreen(
    onCoverClicked: (String) -> Unit,
    searchForBooks: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookshelfScreenViewModel = viewModel(
        factory = BookshelfScreenViewModel.Factory
    )
) {
    val bookshelfScreenState by viewModel.bookshelfScreenState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (bookshelfScreenState.books.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.size(height = 16.dp, width = 16.dp))
                Icon(
                    painterResource(R.drawable.books), contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                )
                Text(
                    stringResource(R.string.your_books),
                    modifier = modifier
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Divider(color = MaterialTheme.colorScheme.secondary, thickness = 1.dp)
            BooksGrid(bookshelfScreenState.books, onCoverClicked, modifier)
        } else {
            NoBooksInShelf(modifier, searchForBooks)
        }
    }
}

@Composable
fun BooksGrid(
    books: List<Book>,
    onCoverClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp)
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

@Composable
fun NoBooksInShelf(modifier: Modifier = Modifier, searchForBooks: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.no_books_in_shelf),
            style = MaterialTheme.typography.displayLarge
        )
        Button(onClick = searchForBooks) {
            Text(
                stringResource(R.string.search_for_books),
                style = MaterialTheme.typography.displaySmall
            )
        }
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