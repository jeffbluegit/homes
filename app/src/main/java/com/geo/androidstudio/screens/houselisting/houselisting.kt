package com.geo.androidstudio.screens.houselisting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.geo.androidstudio.models.House
import com.geo.androidstudio.viewmodel.tenantdashboard.TenantDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseListingScreen(navController: NavHostController){
    val viewModel: TenantDashboardViewModel = viewModel()
    val houses by viewModel.houses.observeAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.loadAvailableHouses()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {Text("Available Rentals")})
        }
    ){innerpadding->
        LazyColumn(modifier = Modifier
            .padding(innerpadding)) {
            items(houses) { house->
                HouseItem(house)
            }
        }
    }
}
@Composable
fun HouseItem(house: House){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ){
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = house.title, style = MaterialTheme.typography.titleLarge)
            Text(text = house.location, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "$${house.price} / month",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Button(onClick = { /* Navigate to Details */ }) {
                Text("View Details")
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HouseListingScreenPreview(){
    HouseListingScreen(rememberNavController())
}
