package com.geo.androidstudio.models

import com.geo.androidstudio.R

data class OnboardingItem(
    val title: String,
    val description: String,
    val imageRes: Int
)

val onboardingItems = listOf(
    OnboardingItem(
        title = "Welcome to A4DABL3 RENTALS",
        description = "Find your perfect home at a price that fits your budget.",
        imageRes = R.drawable.house1
    ),
    OnboardingItem(
        title = "Modern Living Spaces",
        description = "Browse through thousands of affordable apartments and houses.",
        imageRes = R.drawable.house2
    ),
    OnboardingItem(
        title = "Easy & Secure Booking",
        description = "Secure your next home with our seamless and safe booking process.",
        imageRes = R.drawable.house3
    )
)
