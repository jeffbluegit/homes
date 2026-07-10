package com.geo.androidstudio.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.geo.androidstudio.screens.auth.RoleSelectionScreen
import com.geo.androidstudio.screens.dashboard.AdminDashboardScreen
import com.geo.androidstudio.screens.dashboard.LandLordDashboard
import com.geo.androidstudio.screens.dashboard.TenantDashboardScreen
import com.geo.androidstudio.screens.houses.AddHouseScreen
import com.geo.androidstudio.screens.houses.HouseListingScreen
import com.geo.androidstudio.screens.login.LoginScreen
import com.geo.androidstudio.screens.onboarding.OnboardingScreen
import com.geo.androidstudio.screens.register.RegisterScreen
import com.geo.androidstudio.screens.splashscreen.SplashScreen
import com.geo.androidstudio.viewmodel.AuthViewModel
import com.geo.androidstudio.screens.payment.PaymentScreen
import com.geo.androidstudio.screens.profile.ProfileScreen
import com.geo.androidstudio.screens.settings.SettingsScreen
import com.geo.androidstudio.screens.houses.UpdateHouseScreen

@Composable
fun AppNavHost(
    modifier : Modifier =Modifier,
    navController: NavHostController= rememberNavController(),
    startDestination: String = ROUTE_SPLASHSCREEN,
) {
    NavHost(
        navController=navController,
        modifier = modifier,
        startDestination = startDestination
    ) {
        composable(ROUTE_SPLASHSCREEN) {
            val context = LocalContext.current
            val authViewModel = remember { AuthViewModel(navController, context) }
            SplashScreen {
                authViewModel.checkUserAndNavigate()
            }
        }
        composable(ROUTE_ONBOARDING) {
            OnboardingScreen(navController)
        }
        composable(ROUTE_LOGIN) {
            LoginScreen(navController)
        }
        composable(ROUTE_REGISTER) {
            RegisterScreen(navController)
        }
        composable(ROUTE_ROLE_SELECTION) {
            RoleSelectionScreen(navController)
        }
        composable(ROUTE_ADMIN_DASHBOARD) {
            AdminDashboardScreen(navController)
        }
        composable(ROUTE_LANDLORD_DASHBOARD){
            LandLordDashboard(navController)
        }
        composable(ROUTE_USER_DASHBOARD){
            TenantDashboardScreen(navController)
        }
        composable(ROUTE_TENANT_DASHBOARD){
            TenantDashboardScreen(navController)
        }
        composable(ROUTE_PROFILE) {
            ProfileScreen(navController)
        }
        composable(ROUTE_SETTINGS) {
            SettingsScreen(navController)
        }
        composable(ROUTE_ADD_HOUSE) {
            AddHouseScreen(navController)
        }
        composable(ROUTE_VIEW_HOUSES) {
            HouseListingScreen(navController)
        }
        composable("$ROUTE_UPDATE_HOUSE/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            UpdateHouseScreen(navController, id)
        }
        composable("$ROUTE_PAYMENT/{houseId}/{amount}") { backStackEntry ->
            val houseId = backStackEntry.arguments?.getString("houseId") ?: ""
            val amount = backStackEntry.arguments?.getString("amount") ?: ""
            PaymentScreen(navController, houseId, amount)
        }
    }
}
