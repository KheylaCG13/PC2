package esan.mobile.Condor23100185.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MonedaViewModel : ViewModel() {
    private val _conversionRates = mutableStateOf<Map<String, Double>>(emptyMap())
    val conversionRates: State<Map<String, Double>> = _conversionRates

    fun loadRates(baseCurrency: String) {
        viewModelScope.launch {
            // Simulación o reemplazo con API real
            val rates = when (baseCurrency) {
                "USD" -> mapOf("EUR" to 0.91, "PEN" to 3.75, "JPY" to 157.4)
                "EUR" -> mapOf("USD" to 1.1, "PEN" to 4.1, "JPY" to 172.0)
                "PEN" -> mapOf("USD" to 0.27, "EUR" to 0.24, "JPY" to 42.0)
                "JPY" -> mapOf("USD" to 0.0064, "EUR" to 0.0058, "PEN" to 0.023)
                else -> emptyMap()
            }
            _conversionRates.value = rates
        }
    }

    private val _availableCurrencies = mutableStateOf<List<String>>(emptyList())
    val availableCurrencies: State<List<String>> = _availableCurrencies

    fun fetchAvailableCurrencies() {
        viewModelScope.launch {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("Monedas")
                    .get()
                    .await()

                val monedas = snapshot.documents.mapNotNull { it.getString("nombre") }
                _availableCurrencies.value = monedas
            } catch (e: Exception) {
                println("Error al cargar monedas: ${e.message}")
            }
        }
    }


}

