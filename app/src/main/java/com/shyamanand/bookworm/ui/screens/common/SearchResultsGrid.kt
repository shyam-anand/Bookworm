package com.shyamanand.bookworm.ui.screens.common

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.network.model.SearchResult
import com.shyamanand.bookworm.network.model.SearchResultItem

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
                Divider()
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