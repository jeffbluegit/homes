package com.geo.androidstudio.models

data class House(
    val houseId: String="",
    val landlordId: String="",
    val title: String="",
    val description: String="",
    val price: Double = 0.0,
    val location: String="",
    val status: String="available"



)