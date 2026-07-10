package com.geo.androidstudio.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.geo.androidstudio.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun RoleSelectionScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to House Rental",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
        )
        Text(
            text = "Select your account type to continue",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        RoleCard(
            title = "Landlord",
            description = "I want to list and manage properties",
            icon = Icons.Default.Home,
            color = Color(0xFF1A237E),
            onClick = {
                updateRole(auth, database, "landlord", navController, context)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleCard(
            title = "Tenant",
            description = "I want to find and book a house",
            icon = Icons.Default.Person,
            color = Color(0xFF4CAF50),
            onClick = {
                updateRole(auth, database, "tenant", navController, context)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun updateRole(
    auth: FirebaseAuth,
    database: com.google.firebase.database.DatabaseReference,
    role: String,
    navController: NavHostController,
    context: android.content.Context
) {
    val userId = auth.currentUser?.uid
    if (userId != null) {
        database.child("User").child(userId).child("role").setValue(role)
            .addOnSuccessListener {
                Toast.makeText(context, "Role set as $role", Toast.LENGTH_SHORT).show()
                navController.navigate(ROUTE_LOGIN) {
                    popUpTo(0) // Clear backstack
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to set role", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(context, "Session expired, please login again", Toast.LENGTH_SHORT).show()
        navController.navigate(ROUTE_LOGIN)
    }
}
