package com.geo.androidstudio.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.geo.androidstudio.models.House
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HouseViewModel(var navController: NavHostController, var context: Context) {
    private val mAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    fun addHouse(title: String, description: String, price: String, location: String) {
        if (title.isBlank() || description.isBlank() || price.isBlank() || location.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val houseId = System.currentTimeMillis().toString()
        val landlordId = mAuth.currentUser?.uid ?: ""
        
        val newHouse = House(
            houseId = houseId,
            landlordId = landlordId,
            title = title,
            description = description,
            price = price.toDoubleOrNull() ?: 0.0,
            location = location,
            status = "available"
        )

        database.child("houses").child(houseId).setValue(newHouse).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "House added successfully", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateHouse(houseId: String, title: String, description: String, price: String, location: String, status: String) {
        val updateData = mapOf(
            "title" to title,
            "description" to description,
            "price" to (price.toDoubleOrNull() ?: 0.0),
            "location" to location,
            "status" to status
        )

        database.child("houses").child(houseId).updateChildren(updateData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fetchHouse(houseId: String, onResult: (House?) -> Unit) {
        database.child("houses").child(houseId).get().addOnSuccessListener {
            onResult(it.getValue(House::class.java))
        }
    }
}
