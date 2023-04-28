package com.shyamanand.bookworm.ui.screens.common

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.network.model.SearchResult
import com.shyamanand.bookworm.network.model.SearchResultItem

const val TAG = "SearchResultsGrid"

@Composable
fun SearchResultsGrid(
    onPreviewClicked: (String) -> Unit,
    searchResult: SearchResult,
    modifier: Modifier = Modifier
) {
    Log.i(TAG, "Rendering search results")
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
            Log.i(TAG, "Rendering results grid with ${searchResult.items.size} results")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookPreview(
    searchResultItem: SearchResultItem,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.i(
        TAG,
        "Rendering preview for ${searchResultItem.volumeInfo.title} (${searchResultItem.selfLink}"
    )
    val book = searchResultItem.toBook()
    ElevatedCard(
        onClick = { onClick(searchResultItem.id) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = modifier.padding(top = 2.dp, bottom = 2.dp)
        ) {
            BookCover(
                searchResultItem = searchResultItem,
                modifier = modifier,
                onClick = onClick
            )
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = searchResultItem.volumeInfo.title,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = modifier.padding(top = 12.dp),
                    maxLines = 2
                )
                if (book.authors.isNotEmpty()) {
                    Text(
                        text = searchResultItem.volumeInfo.authors.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
            }
        }
    }
}