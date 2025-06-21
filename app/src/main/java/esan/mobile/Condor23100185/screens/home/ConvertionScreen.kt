package esan.mobile.Condor23100185.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import esan.mobile.Condor23100185.Firebase.FirebaseAuthManager
import esan.mobile.Condor23100185.ViewModels.MonedaViewModel
import esan.mobile.Condor23100185.screens.Components.DropdownMenuField
import kotlinx.coroutines.launch
import androidx.compose.material.icons.*// similar visual
import androidx.compose.material3.TextButton


@Composable
fun ConversionScreen(viewModel: MonedaViewModel) {
    var baseCurrency by remember { mutableStateOf("USD") }
    var targetCurrency by remember { mutableStateOf("PEN") }
    var amount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val rates = viewModel.conversionRates.value
    val availableCurrencies = listOf("USD", "EUR", "PEN", "JPY")

    LaunchedEffect(baseCurrency) {
        viewModel.loadRates(baseCurrency)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(30.dp)) {

        Text("Conversor de Monedas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Sección de selección de monedas con icono central para invertir
        // DropDown Moneda Base
        Text("Moneda base")
        DropdownMenuField(
            options = availableCurrencies,
            selectedOption = baseCurrency,
            onOptionSelected = {
                baseCurrency = it
                viewModel.loadRates(baseCurrency)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

// Botón "Intercambiar"
        TextButton(
            onClick = {
                val temp = baseCurrency
                baseCurrency = targetCurrency
                targetCurrency = temp
                viewModel.loadRates(baseCurrency)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Intercambiar")
        }

        Spacer(modifier = Modifier.height(8.dp))

// DropDown Moneda Destino
        Text("Convertir a")
        DropdownMenuField(
            options = availableCurrencies,
            selectedOption = targetCurrency,
            onOptionSelected = { targetCurrency = it }
        )


        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Monto") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val value = amount.toDoubleOrNull()
            val rate = rates[targetCurrency]
            errorMessage = ""

            when {
                baseCurrency.isEmpty() || targetCurrency.isEmpty() -> {
                    errorMessage = "Selecciona ambas monedas."
                }
                baseCurrency == targetCurrency -> {
                    errorMessage = "Las monedas no pueden ser iguales."
                }
                value == null || value <= 0.0 -> {
                    errorMessage = "Ingresa un monto válido."
                }
                rate == null -> {
                    errorMessage = "No hay tasa de cambio disponible."
                }
                else -> {
                    result = String.format("%.2f", value * rate)
                    coroutineScope.launch {
                        FirebaseAuthManager.guardarConversion(
                            monto = value,
                            monedaOrigen = baseCurrency,
                            monedaDestino = targetCurrency,
                            resultado = result.toDouble()
                        )
                    }
                }
            }
        }) {
            Text("Convertir")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (result.isNotEmpty()) {
            Text("Resultado: $result", fontSize = 18.sp)
        }
    }
}


