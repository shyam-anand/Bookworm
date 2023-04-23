package com.shyamanand.bookworm.ui

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.ui.screens.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
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
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ) {

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentScreen = navBackStackEntry?.destination
                Log.d(TAG, "currentScreen = $currentScreen")

                BookwormAppScreen.values()
                    .filter {
                        it.filledIcon != null
                    }
                    .forEach { screen ->
                        val isCurrentScreen = currentScreen?.hierarchy?.any { it.route == screen.name } == true
                        val icon = if (isCurrentScreen) {
                            screen.filledIcon
                        } else {
                            screen.outlinedIcon
                        }
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    painterResource(icon!!),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
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
                            },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.tertiary
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

        val coroutineScope = rememberCoroutineScope()

        val startScreen = if (true) {
            BookwormAppScreen.Bookshelf
        } else {
            BookwormAppScreen.PermissionsRequest
        }

        val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

        NavHost(
            navController = navController,
            startDestination = startScreen.name,
            modifier = modifier.padding(innerPadding)
        ) {

            composable(BookwormAppScreen.PermissionsRequest.name) {
                PermissionRequest(
                    cameraPermissionState = cameraPermissionState,
                    modifier = modifier
                )
            }

            composable(BookwormAppScreen.Camera.name) {
                CameraScreen(modifier)
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
                    onSearchStringCleared = { searchScreenViewModel.onSearchbarInput("") },
                    onBookSelected = { bookId ->
                        bookDetailsScreenViewModel.loadBook(bookId)
                        navController.navigate(BookwormAppScreen.BookDetails.name)
                    },
                    retryAction = { searchScreenViewModel.search() },
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequest(modifier: Modifier = Modifier, cameraPermissionState: PermissionState) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Camera permission required")
        Button(
            onClick = { cameraPermissionState.launchPermissionRequest() },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text("PermissionRequest in BookwormApp.kt")
            Toast.makeText(
                LocalContext.current, "Permissions required", Toast.LENGTH_SHORT
            ).show()
        }
    }
}