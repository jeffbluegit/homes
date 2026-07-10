package com.geo.androidstudio.screens.dashboard

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.geo.androidstudio.models.House
import com.geo.androidstudio.navigation.ROUTE_PAYMENT
import com.geo.androidstudio.navigation.ROUTE_TENANT_DASHBOARD
import com.geo.androidstudio.navigation.ROUTE_USER_DASHBOARD
import com.geo.androidstudio.navigation.ROUTE_VIEW_HOUSES
import com.geo.androidstudio.viewmodel.AuthViewModel
import com.geo.androidstudio.viewmodel.tenantdashboard.TenantDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantDashboardScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: TenantDashboardViewModel = viewModel()
    val authViewModel = remember { AuthViewModel(navController, context) }
    val houses by viewModel.houses.observeAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.loadAvailableHouses()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Welcome Home",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
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
                    selected = currentDestination == ROUTE_TENANT_DASHBOARD || currentDestination == ROUTE_USER_DASHBOARD,
                    onClick = { 
                        if (currentDestination != ROUTE_TENANT_DASHBOARD) {
                            navController.navigate(ROUTE_TENANT_DASHBOARD) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Quick Actions / Search Bar
            OutlinedCard(
                onClick = { navController.navigate(ROUTE_VIEW_HOUSES) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Search for houses...", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Featured Houses",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
                TextButton(onClick = { navController.navigate(ROUTE_VIEW_HOUSES) }) {
                    Text(text = "See All", color = Color(0xFF1A237E))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (houses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No houses available right now.", color = Color.Gray)
                }
            } else {
                // Show the first few houses as "Featured"
                houses.take(5).forEach { house ->
                    HouseCard(
                        house = house,
                        onBookClick = { 
                            viewModel.bookHouse(house, context) {
                                navController.navigate("$ROUTE_PAYMENT/${house.houseId}/${house.price}")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun HouseCard(house: House, onBookClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for House Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFD700)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Color(0xFF1A237E))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = house.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
                Text(
                    text = house.location,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kshs ${house.price} / month",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A237E)
                )
            }
            
            Button(
                onClick = onBookClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Book Now", fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TenantDashboardPreview() {
    TenantDashboardScreen(rememberNavController())
}
