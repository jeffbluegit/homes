package com.geo.androidstudio.screens.dashboard

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.geo.androidstudio.models.House
import com.geo.androidstudio.navigation.ROUTE_ADD_HOUSE
import com.geo.androidstudio.navigation.ROUTE_LANDLORD_DASHBOARD
import com.geo.androidstudio.navigation.ROUTE_PROFILE
import com.geo.androidstudio.navigation.ROUTE_SETTINGS
import com.geo.androidstudio.navigation.ROUTE_UPDATE_HOUSE
import com.geo.androidstudio.viewmodel.AuthViewModel
import com.geo.androidstudio.viewmodel.LandlordDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandLordDashboard(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: LandlordDashboardViewModel = viewModel()
    val authViewModel = remember { AuthViewModel(navController, context) }
    val houses by viewModel.houses
    val tenants by viewModel.tenants
    val bookings by viewModel.bookings

    var selectedFilter by remember { mutableStateOf("ALL_HOUSES") }
    var searchQuery by remember { mutableStateOf("") }
    val filteredTenants = tenants.filter { 
        it.fullname.contains(searchQuery, ignoreCase = true) || 
        it.email.contains(searchQuery, ignoreCase = true) 
    }

    val filteredHouses = when(selectedFilter) {
        "OCCUPIED" -> houses.filter { it.status != "available" }
        "VACANT" -> houses.filter { it.status == "available" }
        else -> houses
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Landlord Dashboard", fontWeight = FontWeight.Bold) },
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
                    selected = currentDestination == ROUTE_LANDLORD_DASHBOARD,
                    onClick = { 
                        if (currentDestination != ROUTE_LANDLORD_DASHBOARD) {
                            navController.navigate(ROUTE_LANDLORD_DASHBOARD) {
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
                    selected = currentDestination == ROUTE_PROFILE,
                    onClick = { navController.navigate(ROUTE_PROFILE) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White) },
                    label = { Text("Profile", color = Color.White) }
                )
                NavigationBarItem(
                    selected = currentDestination == ROUTE_SETTINGS,
                    onClick = { navController.navigate(ROUTE_SETTINGS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White) },
                    label = { Text("Settings", color = Color.White) }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(ROUTE_ADD_HOUSE) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Property") },
                containerColor = Color(0xFF1A237E),
                contentColor = Color.White
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search tenants...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            item {
                Text("Business Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        label = "Properties", 
                        value = houses.size.toString(), 
                        icon = Icons.Default.Home, 
                        isSelected = selectedFilter == "ALL_HOUSES",
                        onClick = { selectedFilter = "ALL_HOUSES" },
                        modifier = Modifier.width(100.dp)
                    )
                    StatCard(
                        label = "Tenants", 
                        value = viewModel.tenantCount.value.toString(), 
                        icon = Icons.Default.Person, 
                        isSelected = selectedFilter == "TENANTS",
                        onClick = { selectedFilter = "TENANTS" },
                        modifier = Modifier.width(100.dp)
                    )
                    StatCard(
                        label = "Occupied", 
                        value = viewModel.occupiedCount.value.toString(), 
                        icon = Icons.Default.Lock, 
                        isSelected = selectedFilter == "OCCUPIED",
                        onClick = { selectedFilter = "OCCUPIED" },
                        modifier = Modifier.width(100.dp)
                    )
                    StatCard(
                        label = "Vacant", 
                        value = viewModel.vacantCount.value.toString(), 
                        icon = Icons.Default.CheckCircle, 
                        isSelected = selectedFilter == "VACANT",
                        onClick = { selectedFilter = "VACANT" },
                        modifier = Modifier.width(100.dp)
                    )
                }
            }

            item {
                Card(
                    onClick = { selectedFilter = "FINANCE" },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedFilter == "FINANCE") Color(0xFF1A237E).copy(alpha = 0.1f) else Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = if (selectedFilter == "FINANCE") BorderStroke(1.dp, Color(0xFF1A237E)) else null
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Financial Summary", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                            if (selectedFilter == "FINANCE") {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF1A237E))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        RentRow("Total Rent Collected", "Kshs ${viewModel.totalRent.value}", Icons.Default.Done, Color(0xFF4CAF50))
                        RentRow("Outstanding Rent", "Kshs 0.00", Icons.Default.Warning, Color(0xFFF44336))
                        RentRow("Rent Due", "Kshs 0.00", Icons.Default.Notifications, Color(0xFFFF9800))
                    }
                }
            }

            if (selectedFilter == "FINANCE") {
                item {
                    Text("Financial Details", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                items(bookings) { booking ->
                    val tenant = tenants.find { it.userID == booking.tenantId }
                    val house = houses.find { it.houseId == booking.houseId }
                    FinanceItem(booking, tenant, house)
                }
            } else if (searchQuery.isNotEmpty() || selectedFilter == "TENANTS") {
                item { 
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (searchQuery.isNotEmpty()) "Search Results" else "Your Tenants", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        if (selectedFilter == "TENANTS") {
                            TextButton(onClick = { selectedFilter = "ALL_HOUSES" }) {
                                Text("Show Houses", color = Color(0xFF1A237E))
                            }
                        }
                    }
                }
                items(if (searchQuery.isNotEmpty()) filteredTenants else tenants) { tenant ->
                    TenantItem(tenant)
                }
            } else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when(selectedFilter) {
                                "OCCUPIED" -> "Occupied Properties"
                                "VACANT" -> "Vacant Properties"
                                else -> "Your Properties"
                            }, 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold
                        )
                        if (selectedFilter != "ALL_HOUSES") {
                            TextButton(onClick = { selectedFilter = "ALL_HOUSES" }) {
                                Text("Show All", color = Color(0xFF1A237E))
                            }
                        } else {
                            Text("${houses.size} Total", color = Color.Gray)
                        }
                    }
                }

                items(filteredHouses) { house ->
                    val booking = bookings.find { it.houseId == house.houseId }
                    val tenant = tenants.find { it.userID == booking?.tenantId }
                    PropertyCard(
                        house = house,
                        tenantName = tenant?.fullname,
                        onEdit = { navController.navigate("$ROUTE_UPDATE_HOUSE/${house.houseId}") },
                        onDelete = {
                            viewModel.deleteHouse(house.houseId) { success ->
                                if (success) Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun FinanceItem(booking: com.geo.androidstudio.models.Booking, tenant: com.geo.androidstudio.models.User?, house: House?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF1A237E))
                Spacer(modifier = Modifier.width(8.dp))
                Text(tenant?.fullname ?: "Unknown Tenant", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Kshs ${house?.price ?: "0"}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Property: ${house?.title ?: "Unknown"}", fontSize = 12.sp, color = Color.Gray)
            Text("Status: Paid", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatCard(label: String, value: String, icon: ImageVector, isSelected: Boolean = false, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1A237E).copy(alpha = 0.1f) else Color(0xFFF5F5F5)
        ),
        border = if (isSelected) BorderStroke(1.dp, Color(0xFF1A237E)) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1A237E), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun RentRow(label: String, amount: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 14.sp)
        Text(amount, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun TenantItem(tenant: com.geo.androidstudio.models.User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(tenant.fullname, fontWeight = FontWeight.Bold)
                Text(tenant.email, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PropertyCard(house: House, tenantName: String? = null, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(house.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(house.location, fontSize = 14.sp, color = Color.Gray)
                }
                Text(
                    text = house.status.replaceFirstChar { it.uppercase() },
                    color = if (house.status == "available") Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Kshs ${house.price} / month", fontWeight = FontWeight.SemiBold, color = Color(0xFF1A237E))
                if (tenantName != null) {
                    Text("Tenant: $tenantName", fontSize = 14.sp, color = Color(0xFF1A237E))
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)
            
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF1A237E))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandLordDashboardPreview() {
    LandLordDashboard(rememberNavController())
}
