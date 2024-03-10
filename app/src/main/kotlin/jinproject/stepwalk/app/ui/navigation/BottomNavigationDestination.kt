package jinproject.stepwalk.app.ui.navigation

import androidx.annotation.DrawableRes
import com.beank.profile.navigation.profileRoute
import jinproject.stepwalk.design.R
import jinproject.stepwalk.home.navigation.homeGraph
import jinproject.stepwalk.home.navigation.homeRoute
import jinproject.stepwalk.ranking.navigation.rankingRoute
import jinproject.stepwalk.mission.navigation.missionRoute

enum class BottomNavigationDestination(
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
        icon = R.drawable.ic_rankboard,
        iconClicked = R.drawable.ic_rankboard
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