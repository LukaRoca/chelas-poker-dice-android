package com.example.pokerdice.ui.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ABOUT_ROUTE = "about"

fun NavController.navigateToAbout() {
    navigate(ABOUT_ROUTE)
}

fun NavGraphBuilder.aboutScreen(
    onBack : () -> Unit
) {
    composable(ABOUT_ROUTE) {
        AboutScreen(onBack = onBack)
    }
}