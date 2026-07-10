package com.geo.androidstudio.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class AdminViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference

    private val _totalUsers = mutableStateOf(0)
    val totalUsers: State<Int> = _totalUsers

    private val _totalLandlords = mutableStateOf(0)
    val totalLandlords: State<Int> = _totalLandlords

    private val _totalTenants = mutableStateOf(0)
    val totalTenants: State<Int> = _totalTenants

    private val _totalHouses = mutableStateOf(0)
    val totalHouses: State<Int> = _totalHouses

    private val _totalBookings = mutableStateOf(0)
    val totalBookings: State<Int> = _totalBookings

    init {
        fetchStats()
    }

    private fun fetchStats() {
        // Fetch User Stats
        database.child("User").get().addOnSuccessListener { snapshot ->
            var userCount = 0
            var landlordCount = 0
            var tenantCount = 0
            for (child in snapshot.children) {
                userCount++
                val role = child.child("role").value.toString()
                if (role == "landlord") landlordCount++
                if (role == "tenant") tenantCount++
            }
            _totalUsers.value = userCount
            _totalLandlords.value = landlordCount
            _totalTenants.value = tenantCount
        }

        // Fetch House Stats
        database.child("houses").get().addOnSuccessListener { snapshot ->
            _totalHouses.value = snapshot.childrenCount.toInt()
        }

        // Fetch Booking Stats
        database.child("bookings").get().addOnSuccessListener { snapshot ->
            _totalBookings.value = snapshot.childrenCount.toInt()
        }
    }
}
