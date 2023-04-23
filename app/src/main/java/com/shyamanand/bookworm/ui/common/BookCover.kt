package com.shyamanand.bookworm.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shyamanand.bookworm.R
import com.shyamanand.bookworm.data.model.Book


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCover(
    book: Book,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    height: Int =  200,
    width: Int = 130
) {
    ElevatedCard(
        onClick = { onClick(book.id) },
        shape = RectangleShape,
        modifier = modifier
            .padding(start = 2.dp, end = 2.dp, top = 10.dp)
            .size(height = height.dp, width = width.dp)
    ) {
        if (book.imageUrl != null && book.imageUrl.isNotEmpty()) {
            AsyncImage(
                model = book.imageUrl.replace("http:", "https:"),
                contentDescription = stringResource(R.string.cover_image, book.title),
                contentScale = ContentScale.FillHeight,
                modifier = modifier.size(200.dp),
                error = painterResource(R.drawable.no_image)
            )
        } else {
            CoverPlaceholder(title = book.title, author = book.authors)
        }
    }
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        )
        Text(
            text = author,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        )
    }
}