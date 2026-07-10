package com.geo.androidstudio.screens.onboarding

import com.geo.androidstudio.R

data class OnboardingItem(
    val title: String,
    val description: String,
    val imageRes: Int
)
val onboardingItems=listOf(
    OnboardingItem(
        title="welcome to  keto mart",
        description = "get to descover amazing products",
        imageRes=R.drawable.house1
    ),
    OnboardingItem(
        title = "Discover amazing products",
        description = "get to browse thousand of amazing products",
        imageRes =R.drawable.house2
    ),
    OnboardingItem(
            title = "fast and secure delivery",
             description = "enjoy seamless ordering and delivering of products",
             imageRes =R.drawable.house3
)


)