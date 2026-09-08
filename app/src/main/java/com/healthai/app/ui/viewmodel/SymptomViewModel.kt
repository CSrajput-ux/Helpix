package com.healthai.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.remote.api.HelpixRepository
import com.healthai.app.data.remote.api.SymptomCheckResponse
import com.healthai.app.data.remote.api.SymptomItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SymptomChatMessageUi(
    val text: String,
    val isUser: Boolean,
    val suggestions: List<String> = emptyList()
)

data class SymptomUiState(
    val isLoading: Boolean = false,
    val isChatLoading: Boolean = false,
    val allSymptoms: List<SymptomItem> = emptyList(),
    val filteredSymptoms: List<SymptomItem> = emptyList(),
    val selectedSymptoms: Set<String> = emptySet(),
    val searchQuery: String = "",
    val chatMessages: List<SymptomChatMessageUi> = listOf(
        SymptomChatMessageUi("Namaste! Main aapka AI Symptom Doctor hoon. Kripya apne lakshan (symptoms) batayein, jaise sirdard, bukhar ya khansi.", false, listOf("Fever", "Headache", "Cough"))
    ),
    val currentSessionId: String? = null,
    val analysisResult: SymptomCheckResponse? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class SymptomViewModel @Inject constructor(
    private val repository: HelpixRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SymptomUiState())
    val uiState: StateFlow<SymptomUiState> = _uiState.asStateFlow()

    init {
        loadSupportedSymptoms()
    }

    fun loadSupportedSymptoms() {
        viewModelScope.launch {
            try {
                val res = repository.getSupportedSymptoms()
                if (res.isSuccessful && res.body() != null) {
                    val symptoms = res.body()!!.symptoms
                    _uiState.update { 
                        it.copy(
                            allSymptoms = symptoms,
                            filteredSymptoms = symptoms
                        ) 
                    }
                } else {
                    // Fallback popular symptoms
                    val fallback = listOf(
                        SymptomItem("high_fever", "High Fever"),
                        SymptomItem("cough", "Cough"),
                        SymptomItem("headache", "Headache"),
                        SymptomItem("fatigue", "Fatigue / Weakness"),
                        SymptomItem("throat_irritation", "Sore Throat"),
                        SymptomItem("muscle_pain", "Body Ache"),
                        SymptomItem("nausea", "Nausea"),
                        SymptomItem("vomiting", "Vomiting"),
                        SymptomItem("chest_pain", "Chest Pain"),
                        SymptomItem("breathlessness", "Breathlessness"),
                        SymptomItem("stomach_pain", "Stomach Pain"),
                        SymptomItem("diarrhoea", "Diarrhea"),
                        SymptomItem("chills", "Chills / Shivering"),
                        SymptomItem("skin_rash", "Skin Rash"),
                        SymptomItem("joint_pain", "Joint Pain")
                    )
                    _uiState.update { 
                        it.copy(
                            allSymptoms = fallback,
                            filteredSymptoms = fallback
                        ) 
                    }
                }
            } catch (e: Exception) {
                // Keep default state gracefully
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) {
                state.allSymptoms
            } else {
                state.allSymptoms.filter { 
                    it.label.contains(query, ignoreCase = true) || it.key.contains(query, ignoreCase = true) 
                }
            }
            state.copy(searchQuery = query, filteredSymptoms = filtered)
        }
    }

    fun toggleSymptom(symptomKeyOrLabel: String) {
        _uiState.update { state ->
            val current = state.selectedSymptoms.toMutableSet()
            if (current.contains(symptomKeyOrLabel)) {
                current.remove(symptomKeyOrLabel)
            } else {
                current.add(symptomKeyOrLabel)
            }
            state.copy(selectedSymptoms = current)
        }
    }

    fun addSymptom(symptom: String) {
        if (symptom.isBlank()) return
        _uiState.update { state ->
            val current = state.selectedSymptoms.toMutableSet()
            current.add(symptom.trim())
            state.copy(selectedSymptoms = current)
        }
    }

    fun removeSymptom(symptom: String) {
        _uiState.update { state ->
            val current = state.selectedSymptoms.toMutableSet()
            current.remove(symptom)
            state.copy(selectedSymptoms = current)
        }
    }

    fun clearSymptoms() {
        _uiState.update { it.copy(selectedSymptoms = emptySet()) }
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val currentMessages = _uiState.value.chatMessages.toMutableList()
        currentMessages.add(SymptomChatMessageUi(userText, true))
        
        // Auto-extract and add symptom if recognized
        val lower = userText.lowercase()
        if (lower.contains("fever") || lower.contains("bukhar")) addSymptom("high_fever")
        if (lower.contains("headache") || lower.contains("sirdard")) addSymptom("headache")
        if (lower.contains("cough") || lower.contains("khansi")) addSymptom("cough")
        if (lower.contains("cold") || lower.contains("jukam")) addSymptom("continuous_sneezing")
        if (lower.contains("vomiting") || lower.contains("ulti")) addSymptom("vomiting")
        if (lower.contains("stomach") || lower.contains("pet")) addSymptom("stomach_pain")
        if (lower.contains("chest")) addSymptom("chest_pain")

        _uiState.update { 
            it.copy(
                chatMessages = currentMessages,
                isChatLoading = true
            ) 
        }

        viewModelScope.launch {
            try {
                val res = repository.chatWithDoctor(userText, _uiState.value.currentSessionId)
                if (res.isSuccessful && res.body() != null) {
                    val body = res.body()!!
                    val updated = _uiState.value.chatMessages.toMutableList()
                    updated.add(SymptomChatMessageUi(body.reply, false, body.suggestions))
                    _uiState.update { 
                        it.copy(
                            chatMessages = updated,
                            currentSessionId = body.session_id,
                            isChatLoading = false
                        ) 
                    }
                } else {
                    val updated = _uiState.value.chatMessages.toMutableList()
                    updated.add(SymptomChatMessageUi("Samajh gaya. Kya aapko sirdard, thakaan ya koi anya lakshan bhi hai?", false, listOf("Check Symptoms", "Fever", "Headache")))
                    _uiState.update { it.copy(chatMessages = updated, isChatLoading = false) }
                }
            } catch (e: Exception) {
                val updated = _uiState.value.chatMessages.toMutableList()
                updated.add(SymptomChatMessageUi("Main aapke lakshano ko samajh raha hoon. Kripya aur lakshan chunein ya sidha analysis karein.", false))
                _uiState.update { it.copy(chatMessages = updated, isChatLoading = false) }
            }
        }
    }

    fun analyzeSymptoms(onComplete: (Boolean) -> Unit) {
        val symptomsToAnalyze = _uiState.value.selectedSymptoms.toList()
        if (symptomsToAnalyze.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Kripya kam se kam 1 lakshan chunein.") }
            onComplete(false)
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val res = repository.checkSymptoms(symptomsToAnalyze)
                if (res.isSuccessful && res.body() != null) {
                    val result = res.body()!!
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            analysisResult = result,
                            errorMessage = null
                        ) 
                    }
                    onComplete(true)
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Analysis mein truti aayi. Kripya punah prayas karein."
                        ) 
                    }
                    onComplete(false)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${e.localizedMessage}"
                    ) 
                }
                onComplete(false)
            }
        }
    }

    fun resetAnalysis() {
        _uiState.update { 
            it.copy(
                analysisResult = null,
                errorMessage = null,
                isLoading = false
            ) 
        }
    }
}
