package com.example.proyek_papb_fe.navigation

import com.example.proyek_papb_fe.core.navigation.TopLevelRoute
import androidx.annotation.DrawableRes
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.freelancer.chat.navigation.ChatRoute
import com.example.proyek_papb_fe.feature.freelancer.dashboard.navigation.DashboardRoute
import com.example.proyek_papb_fe.feature.freelancer.searchproject.navigation.SearchProjectRoute
import com.example.proyek_papb_fe.feature.freelancer.submission.navigation.SubmissionRoute
import com.example.proyek_papb_fe.feature.profile.navigation.ProfileRoute

enum class TopLevelDestination(
    val label: String,
    @param:DrawableRes val iconRes: Int,
    val route: TopLevelRoute
) {
    HOME(
        label = "Beranda",
        iconRes = R.drawable.ic_nav_home,
        route = DashboardRoute
    ),
    SEARCH_PROJECT(
        label = "Cari Proyek",
        iconRes = R.drawable.ic_nav_travel_explore,
        route = SearchProjectRoute
    ),
    SUBMISSION(
        label = "Proyek",
        iconRes = R.drawable.ic_nav_project,
        route = SubmissionRoute
    ),
    CHAT(
        label = "Chat",
        iconRes = R.drawable.ic_nav_chat_bubble,
        route = ChatRoute
    ),
    PROFILE(
        label = "Profil",
        iconRes = R.drawable.ic_nav_account_circle,
        route = ProfileRoute
    )
}

fun NavDestination?.isTopLevelDestination(
    destination: TopLevelDestination
): Boolean =
    this?.hierarchy?.any {
        it.hasRoute(destination.route::class)
    } == true
