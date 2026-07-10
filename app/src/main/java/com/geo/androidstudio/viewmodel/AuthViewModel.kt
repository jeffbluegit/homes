package com.geo.androidstudio.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.geo.androidstudio.models.User
import com.geo.androidstudio.navigation.ROUTE_ADMIN_DASHBOARD
import com.geo.androidstudio.navigation.ROUTE_LANDLORD_DASHBOARD
import com.geo.androidstudio.navigation.ROUTE_LOGIN
import com.geo.androidstudio.navigation.ROUTE_ONBOARDING
import com.geo.androidstudio.navigation.ROUTE_ROLE_SELECTION
import com.geo.androidstudio.navigation.ROUTE_SPLASHSCREEN
import com.geo.androidstudio.navigation.ROUTE_TENANT_DASHBOARD
import com.geo.androidstudio.navigation.ROUTE_USER_DASHBOARD
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel(var navController:NavHostController,var context: Context) {
    var mAuth = FirebaseAuth.getInstance()

    //Register function
    fun signup(fullname: String, email: String, password: String, confirmpassword: String ) {

        if (email.isBlank() || password.isBlank() || confirmpassword.isBlank()) {
            //validation tell the user email and password cannot be blank
            Toast.makeText(context, "Email and Password cannot be blank", Toast.LENGTH_LONG).show()
            return
        } else if (password != confirmpassword) {
            //tell the user that both passwords should match if the passwords are not the same
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
        } else {
            //allow the user to Log in and save the user to realtime database.
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUserId = mAuth.currentUser?.uid ?: ""

                        //2.
                        val userdata = User(fullname, email, password, currentUserId, "user")
                        //3.

                        //saving the user in real time database
                        val regRef = FirebaseDatabase.getInstance().getReference()
                            .child("User")
                            .child(currentUserId)

                        regRef.setValue(userdata).addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "User Registered Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate(ROUTE_ROLE_SELECTION)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Database Error : ${dbTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        // a failed authentication or a user that is already in use
                        Toast.makeText(
                            context,
                            "Auth Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser?.uid
                if (userId != null) {
                    //fetch the user Role
                    FirebaseDatabase.getInstance().reference.child("User")
                        .child(userId).get().addOnSuccessListener { snapshot ->
                            val role = snapshot.child("role").value.toString()
                            Toast.makeText(context, "Successfully Logged in", Toast.LENGTH_SHORT).show()
                            //role based navigation
                            when (role) {
                                "admin" -> navController.navigate(ROUTE_ADMIN_DASHBOARD)
                                "user" -> navController.navigate(ROUTE_USER_DASHBOARD)
                                "tenant" -> navController.navigate(ROUTE_TENANT_DASHBOARD)
                                "landlord" -> navController.navigate(ROUTE_LANDLORD_DASHBOARD)
                                else -> navController.navigate(ROUTE_USER_DASHBOARD)
                            }
                        }.addOnFailureListener {
                            Toast.makeText(context, "Failed to fetch user role", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun logout() {
        mAuth.signOut()
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun checkUserAndNavigate() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            FirebaseDatabase.getInstance().reference.child("User")
                .child(userId).get().addOnSuccessListener { snapshot ->
                    val role = snapshot.child("role").value.toString()
                    when (role) {
                        "admin" -> navController.navigate(ROUTE_ADMIN_DASHBOARD) {
                            popUpTo(ROUTE_SPLASHSCREEN) { inclusive = true }
                        }
                        "user" -> navController.navigate(ROUTE_USER_DASHBOARD) {
                            popUpTo(ROUTE_SPLASHSCREEN) { inclusive = true }
                        }
                        "tenant" -> navController.navigate(ROUTE_TENANT_DASHBOARD) {
                            popUpTo(ROUTE_SPLASHSCREEN) { inclusive = true }
                        }
                        "landlord" -> navController.navigate(ROUTE_LANDLORD_DASHBOARD) {
                            popUpTo(ROUTE_SPLASHSCREEN) { inclusive = true }
                        }
                        else -> navController.navigate(ROUTE_USER_DASHBOARD) {
                            popUpTo(ROUTE_SPLASHSCREEN) { inclusive = true }
                        }
                    }
                }.addOnFailureListener {
                    navController.navigate(ROUTE_ONBOARDING) {
                        popUpTo(ROUTE_SPLASHSCREEN) { inclusive = true }
                    }
                }
        } else {
            navController.navigate(ROUTE_ONBOARDING) {
                popUpTo(ROUTE_SPLASHSCREEN) { inclusive = true }
            }
        }
    }
}





