package com.shyamanand.bookworm.ui.screens

import androidx.annotation.DrawableRes
import com.shyamanand.bookworm.R

enum class BookwormAppScreen(
    @DrawableRes val filledIcon: Int? = null,
    @DrawableRes val outlinedIcon: Int? = null
) {
    Camera(),
    BookDetails(),
    PermissionsRequest(),
    Home(R.drawable.bookstack_special_flat, R.drawable.bookstack_special_lineal),
    Search(R.drawable.search_filled, R.drawable.search_outlined)
}
