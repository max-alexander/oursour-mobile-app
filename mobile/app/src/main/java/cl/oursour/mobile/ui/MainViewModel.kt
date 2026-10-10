package cl.oursour.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _status = MutableStateFlow("Sin conectar")
    val status: StateFlow<String> = _status.asStateFlow()

    fun ping() {
        _status.value = "Conectando..."
        viewModelScope.launch {
            _status.value = try {
                val response = ApiClient.api.ping()
                "Backend: ${response.status} (${response.timestamp})"
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }
    }
}
