package com.healthai.app.domain.usecase

import kotlinx.coroutines.delay
import javax.inject.Inject

class LoginUserUseCase @Inject constructor() {
    suspend operator fun invoke(email: String, password: String): Boolean {
        // Simulation of a network login call
        delay(1500)
        return email.isNotEmpty() && password.isNotEmpty()
    }
}