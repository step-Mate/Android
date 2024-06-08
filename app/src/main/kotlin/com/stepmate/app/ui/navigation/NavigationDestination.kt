package com.stepmate.app.ui.navigation

import androidx.annotation.DrawableRes
import com.stepmate.design.R
import com.stepmate.home.navigation.HomeRoute.homeRoute
import com.stepmate.mission.navigation.missionRoute
import com.stepmate.profile.navigation.profileRoute
import com.stepmate.ranking.navigation.rankingRoute

enum class NavigationDestination(
    val title: String,
    val route: String,
    @DrawableRes val icon: Int,
    @DrawableRes val iconClicked: Int,
) {
    Home(
        title = "홈",
        route = homeRoute,
        icon = R.drawable.ic_home,
        iconClicked = R.drawable.ic_home_clicked
    ),
    Ranking(
        title = "랭킹",
        route = rankingRoute,
        icon = R.drawable.ic_leaderboard_outline,
        iconClicked = R.drawable.ic_leaderboard_solid
    ),
    Mission(
        title = "미션",
        route = missionRoute,
        icon = R.drawable.ic_bookmark,
        iconClicked = R.drawable.ic_bookmark_solid
    ),
    Profile(
        title = "프로필",
        route = profileRoute,
        icon = R.drawable.ic_setting,
        iconClicked = R.drawable.ic_setting_clicked
    ),
}