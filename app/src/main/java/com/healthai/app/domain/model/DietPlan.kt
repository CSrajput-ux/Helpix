package com.healthai.app.domain.model

data class DietPlan(
    val name: String = "", // e.g., "Weight Loss Plan", "Muscle Gain Plan"
    val dailyMeals: List<Meal> = emptyList()
)