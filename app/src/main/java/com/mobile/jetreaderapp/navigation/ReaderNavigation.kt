package com.mobile.jetreaderapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mobile.jetreaderapp.screens.create.ReaderCreateAccountScreen
import com.mobile.jetreaderapp.screens.detail.ReaderBookDetailScreen
import com.mobile.jetreaderapp.screens.home.HomeScreenViewModel
import com.mobile.jetreaderapp.screens.home.ReaderHomeScreen
import com.mobile.jetreaderapp.screens.login.ReaderLoginScreen
import com.mobile.jetreaderapp.screens.search.BookSearchViewModel
import com.mobile.jetreaderapp.screens.search.ReaderSearchScreen
import com.mobile.jetreaderapp.screens.splash.ReaderSplashScreen
import com.mobile.jetreaderapp.screens.stats.ReaderStatsScreen
import com.mobile.jetreaderapp.screens.update.ReaderUpdateScreen

@ExperimentalComposeUiApi
@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name) {
        composable(ReaderScreens.SplashScreen.name) {
            ReaderSplashScreen(navController = navController)
        }
        composable(ReaderScreens.LoginScreen.name) {
            ReaderLoginScreen(navController = navController)
        }
        composable(ReaderScreens.CreateAccountScreen.name) {
            ReaderCreateAccountScreen(navController = navController)
        }
        composable(ReaderScreens.ReaderHomeScreen.name) {
            val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
            ReaderHomeScreen(navController = navController,homeScreenViewModel)
        }
        composable(ReaderScreens.SearchScreen.name) {
            val bookSearchViewModel = hiltViewModel<BookSearchViewModel>()
            ReaderSearchScreen(
                navController = navController,
                bookSearchViewModel = bookSearchViewModel
            )
        }
        val detailScreen = ReaderScreens.DetailScreen.name
        composable("$detailScreen/{bookId}", arguments = listOf(navArgument("bookId") {
            type = NavType.StringType
        })) { backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let {
                if (it != null) {
                    ReaderBookDetailScreen(navController = navController, it)
                }
            }
        }

        val updateScreen = ReaderScreens.UpdateScreen.name
        composable("$updateScreen/{bookId}", arguments = listOf(navArgument("bookId"){
            type = NavType.StringType
        })) { backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let {
                val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
                ReaderUpdateScreen(navController = navController,it,homeScreenViewModel)
            }
        }
        composable(ReaderScreens.ReaderStatsScreen.name) {
            val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
            ReaderStatsScreen(navController = navController,homeScreenViewModel)
        }

    }
}