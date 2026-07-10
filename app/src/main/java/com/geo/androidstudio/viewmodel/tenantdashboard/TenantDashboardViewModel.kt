package com.geo.androidstudio.viewmodel.tenantdashboard

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geo.androidstudio.models.Booking
import com.geo.androidstudio.models.House
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class TenantDashboardViewModel : ViewModel(){
    private val database = FirebaseDatabase.getInstance().reference

    private val _houses = MutableLiveData   <List<House>>()
    val houses : LiveData<List<House>> get() = _houses


    fun loadAvailableHouses() {
        database.child("houses").get().addOnSuccessListener { snapshot ->
            val houseList = mutableListOf<House>()
            for (child in snapshot.children) {
                val house = child.getValue(House::class.java)
                if (house != null && house.status == "available") {
                    houseList.add(house)
                }
            }
            _houses.value = houseList
        }
    }

    fun bookHouse(house: House, context: Context, onSuccess: () -> Unit = {}) {
        val tenantId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (tenantId.isEmpty()) {
            Toast.makeText(context, "Please login to book", Toast.LENGTH_SHORT).show()
            return
        }

        val bookingRef = database.child("bookings").push()
        val bookingId = bookingRef.key ?: ""
        val booking = Booking(
            bookingId = bookingId,
            houseId = house.houseId,
            tenantId = tenantId,
            landlordId = house.landlordId,
            status = "pending"
        )

        bookingRef.setValue(booking).addOnSuccessListener {
            database.child("houses").child(house.houseId).child("status").setValue("pending")
            Toast.makeText(context, "Booking requested successfully", Toast.LENGTH_SHORT).show()
            loadAvailableHouses()
            onSuccess()
        }.addOnFailureListener {
            Toast.makeText(context, "Booking failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}