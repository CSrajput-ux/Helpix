package com.healthai.app.ui.screens.vault

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.remote.api.HelpixRepository
import com.healthai.app.data.remote.api.VaultFileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

sealed class VaultUiState {
    object Loading : VaultUiState()
    data class Success(val files: List<VaultFileResponse>) : VaultUiState()
    data class Error(val message: String) : VaultUiState()
}

@HiltViewModel
class HealthVaultViewModel @Inject constructor(
    private val repository: HelpixRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VaultUiState>(VaultUiState.Loading)
    val uiState: StateFlow<VaultUiState> = _uiState

    init {
        fetchFiles()
    }

    fun fetchFiles() {
        _uiState.value = VaultUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.listVaultFiles()
                if (response.isSuccessful) {
                    _uiState.value = VaultUiState.Success(response.body() ?: emptyList())
                } else {
                    _uiState.value = VaultUiState.Error("Failed to fetch files")
                }
            } catch (e: Exception) {
                _uiState.value = VaultUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun uploadFile(context: Context, uri: Uri, title: String, category: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    var filename = title.replace(" ", "_")
                    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1 && cursor.moveToFirst()) {
                            val originalName = cursor.getString(nameIndex)
                            val extension = originalName.substringAfterLast('.', "")
                            if (extension.isNotEmpty() && !filename.endsWith(".$extension")) {
                                filename += ".$extension"
                            }
                        }
                    }

                    val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData("file", filename, requestBody)
                    val typeBody = category.toRequestBody("text/plain".toMediaTypeOrNull())

                    val response = repository.uploadVaultFile(filePart, typeBody)
                    if (response.isSuccessful) {
                        fetchFiles() // Refresh list on success
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }
}
