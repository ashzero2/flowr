package com.ash.flowr.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ash.flowr.domain.Category

val Category.icon: ImageVector
    get() = when (this) {
        Category.FOOD -> Icons.Filled.Restaurant
        Category.TRANSPORT -> Icons.Filled.DirectionsCar
        Category.SHOPPING -> Icons.Filled.ShoppingBag
        Category.ENTERTAINMENT -> Icons.Filled.Movie
        Category.UTILITIES -> Icons.Filled.Bolt
        Category.HEALTH -> Icons.Filled.LocalHospital
        Category.OTHER -> Icons.Filled.Category
    }

val Category.color: Color
    get() = when (this) {
        Category.FOOD -> CategoryFood
        Category.TRANSPORT -> CategoryTransport
        Category.SHOPPING -> CategoryShopping
        Category.ENTERTAINMENT -> CategoryEntertainment
        Category.UTILITIES -> CategoryUtilities
        Category.HEALTH -> CategoryHealth
        Category.OTHER -> CategoryOther
    }

val Category.label: String
    get() = when (this) {
        Category.FOOD -> "Food"
        Category.TRANSPORT -> "Transport"
        Category.SHOPPING -> "Shopping"
        Category.ENTERTAINMENT -> "Entertainment"
        Category.UTILITIES -> "Utilities"
        Category.HEALTH -> "Health"
        Category.OTHER -> "Other"
    }
