package com.geo.androidstudio.models

data class User(
    var fullname:String="",
    var email: String="",
    var password:String="",
    val userID:String="",
    var role:String = "User",
    val profileImage:String = "",

)