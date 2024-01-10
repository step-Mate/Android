package jinproject.stepwalk.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import jinproject.stepwalk.login.screen.LoginScreen
import jinproject.stepwalk.login.screen.findid.FindIdScreen
import jinproject.stepwalk.login.screen.findpassword.FindPasswordScreen
import jinproject.stepwalk.login.screen.signup.SignUpScreen
import jinproject.stepwalk.login.screen.signupdetail.SignUpDetailScreen
import jinproject.stepwalk.login.utils.SnackBarMessage
import jinproject.stepwalk.login.utils.slideDownOut
import jinproject.stepwalk.login.utils.slideLeftOut
import jinproject.stepwalk.login.utils.slideRightIn
import jinproject.stepwalk.login.utils.slideUpIn

private const val loginRoute = "login"
private const val signUpRoute = "signUp"
private const val signUpDetailRoute = "signUpDetail"
private const val signUpDetailLink = "$signUpDetailRoute?id={id}&password={password}"
private const val findIdRoute = "findId"
private const val findPasswordRoute = "findPassword"

fun NavGraphBuilder.loginNavGraph(
    navigateToSignUp : () -> Unit,
    navigateToSignUpDetail : (String,String) -> Unit,
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
){
    composable(
        route = loginRoute,
        enterTransition = slideUpIn(500),
        exitTransition = slideDownOut(500)
    ){
        LoginScreen(
            navigateToSignUp = navigateToSignUp,
            showSnackBar = showSnackBar
        )
    }

    composable(
        route = signUpRoute,
        enterTransition = slideRightIn(500),
        exitTransition = slideLeftOut(500),
    ){
        SignUpScreen(
            navigateToSignUpDetail = navigateToSignUpDetail
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
    ){ navBackStackEntry ->
        val id = navBackStackEntry.arguments?.getString("id") ?: ""
        val password = navBackStackEntry.arguments?.getString("password") ?: ""
        SignUpDetailScreen(
            id = id,
            password = password
        )
    }

    composable(
        route = findIdRoute,
        enterTransition = slideRightIn(500),
        exitTransition = slideLeftOut(500),
    ){
        FindIdScreen(
            popBackStack = popBackStack
        )
    }

    composable(
        route = findPasswordRoute,
        enterTransition = slideRightIn(500),
        exitTransition = slideLeftOut(500),
    ){
        FindPasswordScreen (
            popBackStack = popBackStack
        )
    }

}

fun NavController.navigateToLogin() {
    this.navigate(loginRoute)
}

fun NavController.navigateToSignUp() {
    this.navigate(signUpRoute)
}

fun NavController.navigateToSignUpDetail(id : String, password : String) {
    this.navigate("$signUpDetailRoute?id={$id}&password={$password}")
}