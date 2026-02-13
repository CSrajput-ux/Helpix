package com.healthai.app.domain.model

data class Meal(
    val name: String = "", // e.g., Breakfast, Lunch, Dinner
    val foodItems: List<String> = emptyList(),
    val calories: Int = 0
)