package com.geo.androidstudio.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.geo.androidstudio.models.Booking
import com.geo.androidstudio.models.House
import com.geo.androidstudio.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LandlordDashboardViewModel : ViewModel() {
    private val mAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val landlordId = mAuth.currentUser?.uid ?: ""

    private val _houses = mutableStateOf<List<House>>(emptyList())
    val houses: State<List<House>> = _houses

    private val _tenants = mutableStateOf<List<User>>(emptyList())
    val tenants: State<List<User>> = _tenants

    private val _tenantCount = mutableStateOf(0)
    val tenantCount: State<Int> = _tenantCount

    private val _occupiedCount = mutableStateOf(0)
    val occupiedCount: State<Int> = _occupiedCount

    private val _vacantCount = mutableStateOf(0)
    val vacantCount: State<Int> = _vacantCount

    private val _totalRent = mutableStateOf(0.0)
    val totalRent: State<Double> = _totalRent

    init {
        fetchLandlordData()
    }

    private fun fetchLandlordData() {
        if (landlordId.isEmpty()) return

        // Fetch Houses
        database.child("houses").orderByChild("landlordId").equalTo(landlordId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val houseList = mutableListOf<House>()
                    var vacant = 0
                    var occupied = 0
                    var total = 0.0

                    for (child in snapshot.children) {
                        val house = child.getValue(House::class.java)
                        if (house != null) {
                            houseList.add(house)
                            if (house.status == "available") vacant++ else occupied++
                            total += house.price
                        }
                    }
                    _houses.value = houseList
                    _vacantCount.value = vacant
                    _occupiedCount.value = occupied
                    _totalRent.value = total
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        // Fetch Bookings to find Tenants
        database.child("bookings").orderByChild("landlordId").equalTo(landlordId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tenantIds = mutableSetOf<String>()
                    for (child in snapshot.children) {
                        val booking = child.getValue(Booking::class.java)
                        booking?.tenantId?.let { tenantIds.add(it) }
                    }
                    _tenantCount.value = tenantIds.size
                    fetchTenantDetails(tenantIds.toList())
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun fetchTenantDetails(ids: List<String>) {
        val userList = mutableListOf<User>()
        var count = 0
        if (ids.isEmpty()) {
            _tenants.value = emptyList()
            return
        }
        for (id in ids) {
            database.child("User").child(id).get().addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                if (user != null) userList.add(user)
                count++
                if (count == ids.size) {
                    _tenants.value = userList
                }
            }
        }
    }

    fun deleteHouse(houseId: String, callback: (Boolean) -> Unit) {
        database.child("houses").child(houseId).removeValue().addOnCompleteListener {
            callback(it.isSuccessful)
        }
    }
}
