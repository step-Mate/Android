package jinproject.stepwalk.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.login.screen.login.LoginScreen
import jinproject.stepwalk.login.screen.findid.FindIdScreen
import jinproject.stepwalk.login.screen.findpassword.FindPasswordScreen
import jinproject.stepwalk.login.screen.signup.SignUpScreen
import jinproject.stepwalk.login.screen.signupdetail.SignUpDetailScreen
import jinproject.stepwalk.login.utils.slideDownOut
import jinproject.stepwalk.login.utils.slideLeftOut
import jinproject.stepwalk.login.utils.slideRightIn
import jinproject.stepwalk.login.utils.slideUpIn

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
    popBackStacks: (String,Boolean,Boolean) -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
){
    navigation(
        startDestination = loginGraph,
        route = loginRoute
    ){
        composable(
            route = loginGraph,
            enterTransition = slideUpIn(500),
            exitTransition = slideDownOut(500)
        ){
            LoginScreen(
                navigateToSignUp = navigateToSignUp,
                navigateToFindId = navigateToFindId,
                navigateToFindPassword = navigateToFindPassword,
                popBackStack = popBackStack,
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = signUpRoute,
            enterTransition = slideRightIn(500),
            exitTransition = slideLeftOut(500),
        ){
            SignUpScreen(
                navigateToSignUpDetail = navigateToSignUpDetail,
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = signUpDetailLink,
            enterTransition = slideRightIn(500),
            exitTransition = slideLeftOut(500),
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
                popBackStacks = popBackStacks,
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = findIdRoute,
            enterTransition = slideRightIn(500),
            exitTransition = slideLeftOut(500),
        ){
            FindIdScreen(
                popBackStack = popBackStack,
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = findPasswordRoute,
            enterTransition = slideRightIn(500),
            exitTransition = slideLeftOut(500),
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
    }
}

fun NavController.navigateToSignUpDetail(id : String, password : String) {
    this.navigate("$signUpDetailRoute/{$id}/{$password}"){
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