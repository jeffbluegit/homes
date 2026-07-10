package com.geo.androidstudio.screens.dashboard

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.geo.androidstudio.navigation.ROUTE_ADMIN_DASHBOARD
import com.geo.androidstudio.navigation.ROUTE_VIEW_HOUSES
import com.geo.androidstudio.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavHostController) {
    val viewModel: AdminViewModel = viewModel()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Console", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { (context as? Activity)?.finish() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Exit", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1A237E),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            val currentDestination = navController.currentBackStackEntry?.destination?.route
            NavigationBar(
                containerColor = Color(0xFF1A237E),
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = currentDestination == ROUTE_ADMIN_DASHBOARD,
                    onClick = { 
                        if (currentDestination != ROUTE_ADMIN_DASHBOARD) {
                            navController.navigate(ROUTE_ADMIN_DASHBOARD) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
                    label = { Text("Home", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { Toast.makeText(context, "Profile coming soon", Toast.LENGTH_SHORT).show() },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White) },
                    label = { Text("Profile", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { Toast.makeText(context, "Settings coming soon", Toast.LENGTH_SHORT).show() },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White) },
                    label = { Text("Settings", color = Color.White) }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Overview Header
            item {
                Text(
                    text = "Platform Overview",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }

            // Statistics Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AdminStatCard("Total Users", viewModel.totalUsers.value.toString(), Icons.Default.Person, Color(0xFF2196F3), Modifier.weight(1f))
                        AdminStatCard("Total Houses", viewModel.totalHouses.value.toString(), Icons.Default.Home, Color(0xFF4CAF50), Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AdminStatCard("Landlords", viewModel.totalLandlords.value.toString(), Icons.Default.Face, Color(0xFFFF9800), Modifier.weight(1f))
                        AdminStatCard("Tenants", viewModel.totalTenants.value.toString(), Icons.Default.AccountCircle, Color(0xFF9C27B0), Modifier.weight(1f))
                    }
                }
            }

            // Actions Section
            item {
                Text(
                    text = "Oversee Dashboards",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            item {
                OverseeCard(
                    title = "Oversee Landlords",
                    subtitle = "Manage property listings and landlords",
                    icon = Icons.Default.List,
                    color = Color(0xFFFF9800),
                    onClick = { navController.navigate(ROUTE_VIEW_HOUSES) }
                )
            }

            item {
                OverseeCard(
                    title = "Oversee Tenants",
                    subtitle = "View active bookings and tenant activity",
                    icon = Icons.Default.Info,
                    color = Color(0xFF9C27B0),
                    onClick = { /* Navigate to bookings list if available */ }
                )
            }

            item {
                OverseeCard(
                    title = "System Health",
                    subtitle = "View application logs and reports",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF607D8B),
                    onClick = { /* Navigate to logs */ }
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverseeCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(8.dp).size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    AdminDashboardScreen(rememberNavController())
}
