package systems.alderaan.bookworm.ui

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
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import systems.alderaan.bookworm.TAG
import systems.alderaan.bookworm.ui.screens.BookwormAppScreen
import systems.alderaan.bookworm.ui.screens.bookdetails.BookDetailScreen
import systems.alderaan.bookworm.ui.screens.bookdetails.BookDetailsScreenViewModel
import systems.alderaan.bookworm.ui.screens.home.HomeScreen
import systems.alderaan.bookworm.ui.screens.home.HomeScreenViewModel
import systems.alderaan.bookworm.ui.screens.camera.CameraScreen
import systems.alderaan.bookworm.ui.screens.camera.CameraScreenViewModel
import systems.alderaan.bookworm.ui.screens.search.SearchScreen
import systems.alderaan.bookworm.ui.screens.search.SearchViewModel
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = navBackStackEntry?.destination
    Log.d(TAG, "currentScreen = $currentScreen")

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->

        val homeScreenViewModel: HomeScreenViewModel = viewModel(
            factory = HomeScreenViewModel.Factory
        )

        val searchViewModel: SearchViewModel = viewModel(
            factory = SearchViewModel.Factory
        )

        val bookDetailsScreenViewModel: BookDetailsScreenViewModel = viewModel(
            factory = BookDetailsScreenViewModel.Factory
        )

        val cameraScreenViewModel = CameraScreenViewModel()

        val coroutineScope = rememberCoroutineScope()

        NavHost(
            navController = navController,
            startDestination = BookwormAppScreen.Home.name,
            modifier = modifier.padding(innerPadding)
        ) {

            composable(BookwormAppScreen.Home.name) {
                HomeScreen(
                    searchbarState = searchViewModel.searchbarState,
                    resultsGridState = searchViewModel.resultsGridState,
                    onSearchStringChanged = { searchString ->
                        homeScreenViewModel.onSearchbarInput(searchString)
                        searchViewModel.onSearchbarInput(searchString)
                    },
                    onSearchStringCleared = {
                        homeScreenViewModel.onSearchbarInput("")
                        searchViewModel.resetSearchbar()
                    },
                    onCoverClicked = { bookId ->
                        bookDetailsScreenViewModel.loadBook(bookId)
                        navController.navigate(BookwormAppScreen.BookDetails.name)
                    },
                    retryAction = { searchViewModel.search() },
                    resetSearchbar = {
                        homeScreenViewModel.onSearchbarInput("")
                        searchViewModel.resetSearchbar()
                        navController.popBackStack()
                    },
                    searchByImage = {
                        cameraScreenViewModel.reset()
                        navController.navigate(BookwormAppScreen.Camera.name)
                    },
                    homeScreenViewModel = homeScreenViewModel,
                    modifier = modifier
                )
            }

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
                        searchViewModel.imageSearch(imageUri, context)
                        navController.navigate(BookwormAppScreen.Search.name) {
                            Log.i(TAG, "popUpTo(${BookwormAppScreen.Home.name}")
                            popUpTo(BookwormAppScreen.Home.name)
                        }
                    }
                )
            }

            composable(BookwormAppScreen.Search.name) {
                SearchScreen(
                    searchbarState = searchViewModel.searchbarState,
                    resultsGridState = searchViewModel.resultsGridState,
                    onSearchStringChanged = { searchString ->
                        searchViewModel.onSearchbarInput(searchString)
                    },
                    onSearchStringCleared = { searchViewModel.resetSearchbar() },
                    onBookSelected = { bookId ->
                        bookDetailsScreenViewModel.loadBook(bookId)
                        navController.navigate(BookwormAppScreen.BookDetails.name)
                    },
                    retryAction = { searchViewModel.search() },
                    resetSearchbar = {
                        homeScreenViewModel.resetState()
                        searchViewModel.resetSearchbar()
                    },
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

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    currentScreen: NavDestination?
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {


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