package esan.mobile.Condor23100185.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esan.mobile.Condor23100185.Firebase.FirebaseAuthManager
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            val result = FirebaseAuthManager.loginUser(email, password)
            _loading.value = false
            onResult(result.isSuccess, result.exceptionOrNull()?.message)
        }
    }
}