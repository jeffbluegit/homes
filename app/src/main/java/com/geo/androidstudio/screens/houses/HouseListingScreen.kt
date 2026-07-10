package com.geo.androidstudio.screens.houses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.geo.androidstudio.navigation.ROUTE_LANDLORD_DASHBOARD
import com.geo.androidstudio.navigation.ROUTE_PAYMENT
import com.geo.androidstudio.navigation.ROUTE_TENANT_DASHBOARD
import com.geo.androidstudio.viewmodel.tenantdashboard.TenantDashboardViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseListingScreen(navController: NavHostController) {
    val viewModel: TenantDashboardViewModel = viewModel()
    val houses by viewModel.houses.observeAsState(initial = emptyList())
    var userRole by remember { mutableStateOf("") }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        viewModel.loadAvailableHouses()
        if (currentUserId.isNotEmpty()) {
            FirebaseDatabase.getInstance().getReference("User").child(currentUserId)
                .child("role").get().addOnSuccessListener {
                    userRole = it.value.toString()
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Available Houses", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        when (userRole) {
                            "landlord" -> navController.navigate(ROUTE_LANDLORD_DASHBOARD)
                            "tenant", "user" -> navController.navigate(ROUTE_TENANT_DASHBOARD)
                            else -> navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E))
            )
        }
    ) { padding ->
        val context = LocalContext.current
        if (houses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1A237E))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(houses) { house ->
                    HouseListItem(
                        house = house,
                        userRole = userRole,
                        onBookClick = { 
                            viewModel.bookHouse(house, context) {
                                navController.navigate("$ROUTE_PAYMENT/${house.houseId}/${house.price}")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HouseListItem(house: House, userRole: String, onBookClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            
            Column {
                Button(
                    onClick = { /* View Details Logic */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Details", fontSize = 12.sp)
                }
                
                if (userRole == "tenant" || userRole == "user") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onBookClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Book", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HouseListingScreenPreview() {
    HouseListingScreen(rememberNavController())
}
