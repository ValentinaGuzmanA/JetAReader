package com.mobile.jetreaderapp.navigation

enum class ReaderScreens {
    SplashScreen,
    LoginScreen,
    CreateAccountScreen,
    ReaderHomeScreen,
    SearchScreen,
    DetailScreen,
    UpdateScreen,
    ReaderStatsScreen;

    companion object{
        fun fromRoute(route : String?) : ReaderScreens =
            when(route?.substringBefore("/")){
                SplashScreen.name -> SplashScreen
                LoginScreen.name -> LoginScreen
                CreateAccountScreen.name -> CreateAccountScreen
                ReaderHomeScreen.name -> ReaderHomeScreen
                DetailScreen.name -> DetailScreen
                ReaderStatsScreen.name -> ReaderStatsScreen
                SearchScreen.name -> SearchScreen
                UpdateScreen.name -> UpdateScreen
                null -> ReaderHomeScreen
                else -> throw IllegalArgumentException("Route $route is not recognised")
            }
    }
}