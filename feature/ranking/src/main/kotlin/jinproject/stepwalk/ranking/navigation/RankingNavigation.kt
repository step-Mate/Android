package jinproject.stepwalk.ranking.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.ranking.detail.UserDetailScreen
import jinproject.stepwalk.ranking.noti.RankNotificationScreen
import jinproject.stepwalk.ranking.rank.RankingScreen

const val rankingGraph = "rankingGraph"
const val rankingRoute = "ranking"
const val rankingUserDetailRoute = "ranking_userDetail"
private const val rankingUserDetailLink = "$rankingUserDetailRoute?userName={userName}&maxStep={maxStep}"
private const val rankingNotificationRoute = "ranking_noti"

fun NavGraphBuilder.rankingNavGraph(
    navigateToRankingUserDetail: (String, Int) -> Unit,
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    navigateToLogin: (NavOptions?) -> Unit,
    navigateToNoti: () -> Unit,
) {
    navigation(
        startDestination = rankingRoute,
        route = rankingGraph
    ) {
        composable(route = rankingRoute,) {
            RankingScreen(
                popBackStack = popBackStack,
                showSnackBar = showSnackBar,
                navigateToRankingUserDetail = { userName, maxStep ->
                    navigateToRankingUserDetail(userName, maxStep)
                },
                navigateToLogin = navigateToLogin,
                navigateToNoti = navigateToNoti,
            )
        }
        composable(
            route = rankingUserDetailLink,
            arguments = listOf(
                navArgument("userName") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("maxStep") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            ),
        ) {
            UserDetailScreen(
                popBackStack = popBackStack,
                showSnackBar = showSnackBar,
            )
        }
        composable(
            route = rankingNotificationRoute
        ) {
            RankNotificationScreen(
                popBackStack = popBackStack,
                showSnackBar = showSnackBar,
            )
        }
    }
}

fun NavController.navigateToRankingUserDetail(userName: String, maxStep: Int) =
    this.navigate("$rankingUserDetailRoute?userName=$userName&maxStep=$maxStep")

fun NavController.navigateToNotification() = this.navigate(rankingNotificationRoute)