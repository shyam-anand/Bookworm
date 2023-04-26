package com.shyamanand.bookworm.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.ui.screens.BookwormAppScreen
import com.shyamanand.bookworm.ui.screens.bookdetails.BookDetailScreen
import com.shyamanand.bookworm.ui.screens.bookdetails.BookDetailsScreenViewModel
import com.shyamanand.bookworm.ui.screens.bookshelf.BookshelfScreen
import com.shyamanand.bookworm.ui.screens.camera.CameraScreen
import com.shyamanand.bookworm.ui.screens.camera.CameraScreenViewModel
import com.shyamanand.bookworm.ui.screens.search.SearchScreen
import com.shyamanand.bookworm.ui.screens.search.SearchScreenViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookwormApp(
    modifier: Modifier = Modifier
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(MaterialTheme.colorScheme.background)

    val navController = rememberNavController()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.surface
            ) {

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentScreen = navBackStackEntry?.destination
                Log.d(TAG, "currentScreen = $currentScreen")

                BookwormAppScreen.values()
                    .filter {
                        it.filledIcon != null
                    }
                    .forEach { screen ->
                        val isCurrentScreen =
                            currentScreen?.hierarchy?.any { it.route == screen.name } == true
                        val icon = if (isCurrentScreen) {
                            screen.filledIcon
                        } else {
                            screen.outlinedIcon
                        }
                        BottomNavigationItem(
                            icon = {
                                Image(
                                    painter = painterResource(icon!!),
                                    contentDescription = null,
                                    modifier = modifier.size(24.dp)
                                )
                            },
                            selected = isCurrentScreen,
                            onClick = {
                                Log.d(TAG, "navController.navigate(${screen.name})")
                                navController.navigate(screen.name) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // re-selecting the same item
                                    launchSingleTop = true
                                    // Restore state when re-selecting a previously selected item
                                    restoreState = false
                                }
                            }
                        )
                    }
            }
        }
    ) { innerPadding ->

        val searchScreenViewModel: SearchScreenViewModel = viewModel(
            factory = SearchScreenViewModel.Factory
        )

        val bookDetailsScreenViewModel: BookDetailsScreenViewModel = viewModel(
            factory = BookDetailsScreenViewModel.Factory
        )

        val cameraScreenViewModel = CameraScreenViewModel()

        val coroutineScope = rememberCoroutineScope()

        val startScreen = BookwormAppScreen.Bookshelf

        NavHost(
            navController = navController,
            startDestination = startScreen.name,
            modifier = modifier.padding(innerPadding)
        ) {

            composable(BookwormAppScreen.Camera.name) {
                CameraScreen(
                    state = cameraScreenViewModel.state,
                    modifier = modifier,
                    onPermissionGranted = { cameraScreenViewModel.permissionGranted() },
                    onTakePicture = { imageCapture, context ->
                        cameraScreenViewModel.takePicture(imageCapture, context)
                    },
                    onTakeAgainClicked = { cameraScreenViewModel.showCameraPreview() },
                    onSearchClicked = { imageUri, context ->
                        searchScreenViewModel.imageSearch(imageUri, context)
                        navController.navigate(BookwormAppScreen.Search.name)
                    }
                )
            }

            composable(BookwormAppScreen.Bookshelf.name) {
                BookshelfScreen(
                    onCoverClicked = { bookId ->
                        bookDetailsScreenViewModel.loadBook(bookId)
                        navController.navigate(BookwormAppScreen.BookDetails.name)
                    },
                    searchForBooks = { navController.navigate(BookwormAppScreen.Search.name) },
                    modifier = modifier
                )
            }

            composable(BookwormAppScreen.Search.name) {
                SearchScreen(
                    searchbarState = searchScreenViewModel.searchbarState,
                    resultsGridState = searchScreenViewModel.resultsGridState,
                    onSearchStringChanged = { searchString ->
                        searchScreenViewModel.onSearchbarInput(searchString)
                    },
                    onSearchStringCleared = { searchScreenViewModel.resetSearchbar() },
                    onBookSelected = { bookId ->
                        bookDetailsScreenViewModel.loadBook(bookId)
                        navController.navigate(BookwormAppScreen.BookDetails.name)
                    },
                    retryAction = { searchScreenViewModel.search() },
                    resetSearchbar = { searchScreenViewModel.resetSearchbar() },
                    searchByImage = {
                    cameraScreenViewModel.reset()
                        navController.navigate(BookwormAppScreen.Camera.name)
                                    },
                    modifier = modifier
                )
            }

            composable(BookwormAppScreen.BookDetails.name) {
                BookDetailScreen(
                    state = bookDetailsScreenViewModel.state,
                    onAddToShelfClicked = {
                        coroutineScope.launch {
                            bookDetailsScreenViewModel.addBook()
                        }
                    },
                    onRemoveFromShelfClicked = {
                        coroutineScope.launch {
                            bookDetailsScreenViewModel.removeBook()
                        }
                    },
                    modifier = modifier
                )
            }
        }
    }
}
