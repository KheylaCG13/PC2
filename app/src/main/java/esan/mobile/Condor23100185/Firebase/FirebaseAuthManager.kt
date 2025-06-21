package esan.mobile.Condor23100185.Firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseAuthManager {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getConversionRates(baseCurrency: String): Result<Map<String, Double>> {
        return try {
            val snapshot = db.collection("MonedasCambio")
                .document(baseCurrency)
                .get()
                .await()

            if (snapshot.exists()) {
                val rates = snapshot.data?.mapValues { it.value.toString().toDouble() } ?: emptyMap()
                Result.success(rates)
            } else {
                Result.failure(Exception("No se encontró la moneda base: $baseCurrency"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun guardarConversion(
        monto: Double,
        monedaOrigen: String,
        monedaDestino: String,
        resultado: Double
    ) {
        val uid = auth.currentUser?.uid ?: return

        val data = hashMapOf(
            "uid" to uid,
            "fechaHora" to com.google.firebase.Timestamp.now(), // Ahora debería estar bien
            "monto" to monto,
            "monedaOrigen" to monedaOrigen,
            "monedaDestino" to monedaDestino,
            "resultado" to resultado
        )

        db.collection("Historial").add(data).await()
    }

    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}


