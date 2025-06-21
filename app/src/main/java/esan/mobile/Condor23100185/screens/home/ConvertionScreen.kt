package esan.mobile.Condor23100185.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import esan.mobile.Condor23100185.Firebase.FirebaseAuthManager
import esan.mobile.Condor23100185.ViewModels.MonedaViewModel
import kotlinx.coroutines.launch


@Composable
fun ConversionScreen(viewModel: MonedaViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val rates = viewModel.conversionRates.value
    val availableCurrencies = viewModel.availableCurrencies.value

    var baseCurrency by remember { mutableStateOf("") }
    var targetCurrency by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    var expandedBase by remember { mutableStateOf(false) }
    var expandedTarget by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchAvailableCurrencies()
    }

    Column(modifier = Modifier.padding(30.dp)) {
        Text("Conversor de Monedas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown: Moneda base
        Text("Moneda base")
        Box {
            OutlinedTextField(
                value = baseCurrency,
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecciona") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedBase = true }
            )
            DropdownMenu(
                expanded = expandedBase,
                onDismissRequest = { expandedBase = false }
            ) {
                availableCurrencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            baseCurrency = currency
                            expandedBase = false
                            viewModel.loadRates(currency)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de Intercambiar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = {
                val temp = baseCurrency
                baseCurrency = targetCurrency
                targetCurrency = temp
                if (baseCurrency.isNotEmpty()) {
                    viewModel.loadRates(baseCurrency)
                }
            }) {
                Text("Intercambiar", fontWeight = FontWeight.Bold)
            }
        }

        // Dropdown: Moneda destino
        Text("Convertir a")
        Box {
            OutlinedTextField(
                value = targetCurrency,
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecciona") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedTarget = true }
            )
            DropdownMenu(
                expanded = expandedTarget,
                onDismissRequest = { expandedTarget = false }
            ) {
                availableCurrencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            targetCurrency = currency
                            expandedTarget = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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




