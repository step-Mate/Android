package jinproject.stepwalk.app.ui.navigation

import androidx.annotation.DrawableRes
import jinproject.stepwalk.design.R
import jinproject.stepwalk.home.navigation.homeGraph

sealed class BottomNavigationDestination(
    val title:String,
    val route:String,
    @DrawableRes val icon:Int,
    @DrawableRes val iconClicked:Int,
) {
    data object HOME: BottomNavigationDestination(
        title = "홈",
        route = homeGraph,
        icon = R.drawable.ic_home,
        iconClicked = R.drawable.ic_home_clicked
    )

    data object SETTING: BottomNavigationDestination(
        title = "설정",
        route = "setting",
        icon = R.drawable.ic_setting,
        iconClicked = R.drawable.ic_setting_clicked
    )

    companion object {
        val values = listOf(HOME, SETTING)
    }
}