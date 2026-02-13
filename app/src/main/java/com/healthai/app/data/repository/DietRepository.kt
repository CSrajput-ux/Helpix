package com.healthai.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.healthai.app.domain.model.DietPlan
import com.healthai.app.domain.model.Meal
import kotlinx.coroutines.tasks.await

class DietRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // This is a temporary function to add the initial diet plan to Firestore. Run this once.
    suspend fun addInitialDietPlan() {
        val dietPlan = DietPlan(
            name = "Balanced Diet",
            dailyMeals = listOf(
                Meal(
                    name = "Breakfast",
                    foodItems = listOf("Oatmeal with berries", "1 boiled egg", "Green tea"),
                    calories = 350
                ),
                Meal(
                    name = "Lunch",
                    foodItems = listOf("Grilled chicken breast", "Quinoa salad", "Steamed vegetables"),
                    calories = 550
                ),
                Meal(
                    name = "Dinner",
                    foodItems = listOf("Baked salmon", "Brown rice", "Asparagus"),
                    calories = 450
                ),
                Meal(
                    name = "Snacks",
                    foodItems = listOf("Greek yogurt", "Handful of almonds"),
                    calories = 200
                )
            )
        )
        firestore.collection("dietPlans").document("balanced_diet").set(dietPlan).await()
    }

    suspend fun getDietPlan(): DietPlan? {
        val snapshot = firestore.collection("dietPlans").document("balanced_diet").get().await()
        return snapshot.toObject(DietPlan::class.java)
    }
}