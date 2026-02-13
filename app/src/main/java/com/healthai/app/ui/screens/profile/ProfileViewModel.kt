package com.healthai.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.healthai.app.data.repository.UserRepository
import com.healthai.app.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = auth.currentUser?.uid ?: return@launch
            _user.value = userRepository.getUserById(userId) // Assuming you add getUserById in UserRepository
            _isLoading.value = false
        }
    }

    fun logout() {
        auth.signOut()
    }
}