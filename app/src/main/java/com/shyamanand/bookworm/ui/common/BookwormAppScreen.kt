package com.shyamanand.bookworm.ui.common

import androidx.annotation.DrawableRes
import com.shyamanand.bookworm.R

enum class BookwormAppScreen(
    @DrawableRes val filledIcon: Int? = null,
    @DrawableRes val outlinedIcon: Int? = null
) {
    BookDetails(),
    Bookshelf(R.drawable.bookstack_special_flat, R.drawable.bookstack_special_lineal),
    Search(R.drawable.search_filled, R.drawable.search_outlined),
    Camera(R.drawable.camera_filled, R.drawable.camera_outlined),
    PermissionsRequest()
}
