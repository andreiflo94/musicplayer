package com.example.musicplayer.presentation.ui.navhosts

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import com.example.musicplayer.presentation.ui.theme.Motion

/**
 * Depth-style push/pop shared by every NavHost: the new screen glides in from the side while
 * the old one recedes slightly into the background; popping plays it in reverse.
 */
internal object NavTransitions {
    val enter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(Motion.fluid()) { it / 3 } + fadeIn(tween(260))
    }
    val exit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(Motion.fluid()) { -it / 8 } + scaleOut(
            Motion.fluid(),
            targetScale = 0.94f
        ) + fadeOut(tween(220))
    }
    val popEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(Motion.fluid()) { -it / 8 } + scaleIn(
            Motion.fluid(),
            initialScale = 0.94f
        ) + fadeIn(tween(260))
    }
    val popExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(Motion.fluid()) { it / 3 } + fadeOut(tween(200))
    }
}
