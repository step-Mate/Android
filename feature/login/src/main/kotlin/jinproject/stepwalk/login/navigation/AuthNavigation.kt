package jinproject.stepwalk.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.core.slideLeftIn
import jinproject.stepwalk.login.screen.login.LoginScreen
import jinproject.stepwalk.login.screen.findid.FindIdScreen
import jinproject.stepwalk.login.screen.findpassword.FindPasswordScreen
import jinproject.stepwalk.login.screen.signup.SignUpScreen
import jinproject.stepwalk.login.screen.signupdetail.SignUpDetailScreen
import jinproject.stepwalk.core.slideLeftOut
import jinproject.stepwalk.core.slideRightIn
import jinproject.stepwalk.core.slideRightOut

const val loginGraph = "loginGraph"
const val loginRoute = "login"
private const val signUpRoute = "signUp"
private const val signUpDetailRoute = "signUpDetail"
private const val signUpDetailLink = "$signUpDetailRoute/{id}/{password}"
private const val findIdRoute = "findId"
private const val findPasswordRoute = "findPassword"

fun NavGraphBuilder.authNavGraph(
    navigateToSignUp : () -> Unit,
    navigateToSignUpDetail : (String,String) -> Unit,
    navigateToFindId : () -> Unit,
    navigateToFindPassword : () -> Unit,
    popBackStack: () -> Unit,
    backStackToHome: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
){
    navigation(
        startDestination = loginRoute,
        route = loginGraph
    ){
        composable(
            route = loginRoute,
            enterTransition = slideRightIn(500),
            exitTransition = slideLeftOut(500),
        ){
            LoginScreen(
                navigateToSignUp = navigateToSignUp,
                navigateToFindId = navigateToFindId,
                navigateToFindPassword = navigateToFindPassword,
                popBackStack = backStackToHome,
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = signUpRoute,
            enterTransition = slideLeftIn(500),
            exitTransition = slideRightOut(500),
        ){
            SignUpScreen(
                navigateToSignUpDetail = navigateToSignUpDetail,
                popBackStack = popBackStack,
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = signUpDetailLink,
            enterTransition = slideLeftIn(500),
            exitTransition = slideRightOut(500),
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("password"){
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ){
            SignUpDetailScreen(
                popBackStack = popBackStack,
                backStackToLogin = backStackToHome,
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = findIdRoute,
            enterTransition = slideLeftIn(500),
            exitTransition = slideRightOut(500),
        ){
            FindIdScreen(
                popBackStack = popBackStack,
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = findPasswordRoute,
            enterTransition = slideLeftIn(500),
            exitTransition = slideRightOut(500),
        ){
            FindPasswordScreen (
                popBackStack = popBackStack,
                showSnackBar = showSnackBar
            )
        }
    }
}

fun NavController.navigateToLogin(navOptions: NavOptions?) {
    this.navigate(loginRoute, navOptions = navOptions)
}

fun NavController.navigateToSignUp() {
    this.navigate(signUpRoute){
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToSignUpDetail(id : String, password : String) {
    this.navigate("$signUpDetailRoute/$id/$password"){
        launchSingleTop = true
    }
}

fun NavController.navigateToFindId() {
    this.navigate(findIdRoute){
        launchSingleTop = true
    }
}

fun NavController.navigateToFindPassword() {
    this.navigate(findPasswordRoute){
        launchSingleTop = true
    }
}