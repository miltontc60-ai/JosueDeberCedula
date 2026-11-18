package com.josue.verificarcedula

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class VerificarCedula : AppCompatActivity() {

    // Componentes de la interfaz
    private lateinit var txtCedula: EditText
    private lateinit var btnVerificar: Button
    private lateinit var txtResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verificar_cedula)

        // Inicialización de vistas
        txtCedula = findViewById(R.id.txt_cedula)
        btnVerificar = findViewById(R.id.btn_verificar)
        txtResultado = findViewById(R.id.txt_resultado)

        // Evento de verificación
        btnVerificar.setOnClickListener {
            val valorCedula = txtCedula.text.toString().trim()

            // Validación de 10 dígitos
            if (valorCedula.isEmpty() || valorCedula.length != 10) {
                txtResultado.text = "La cédula debe tener exactamente 10 dígitos"
                txtResultado.setTextColor(Color.parseColor("#DC2626"))
                Toast.makeText(this, "La cédula debe tener exactamente 10 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Consulta al servidor
            realizarConsulta(valorCedula)
        }
    }

    // Envío de petición HTTP POST
    private fun realizarConsulta(cedula: String) {
        val url = "http://192.168.1.6/ws/persona.php"
        // val url = "http://10.0.2.2/ws/persona.php"

        txtResultado.text = "Consultando..."
        txtResultado.setTextColor(Color.parseColor("#64748B"))

        // Construcción del JSON
        val parametros = JSONObject()
        parametros.put("accion", "verificar_cedula")
        parametros.put("cedula", cedula)

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            parametros,
            { response ->
                procesar(response)
            },
            { error ->
                mostrarError(error)
            }
        )

        // Configuración de timeout
        request.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        Volley.newRequestQueue(this).add(request)
    }

    // Procesamiento de respuesta JSON
    private fun procesar(respuesta: JSONObject) {
        try {
            val estado = respuesta.getBoolean("estado")
            val mensaje = respuesta.getString("mensaje")

            if (estado) {
                // Cédula encontrada
                txtResultado.text = "REGISTRADA\n$mensaje"
                txtResultado.setTextColor(Color.parseColor("#DC2626"))
            } else {
                // Cédula no registrada
                txtResultado.text = "DISPONIBLE\n$mensaje"
                txtResultado.setTextColor(Color.parseColor("#059669"))
            }
        } catch (e: Exception) {
            txtResultado.text = "Error al procesar: ${e.message}"
            txtResultado.setTextColor(Color.parseColor("#DC2626"))
        }
    }

    // Manejo de errores de conexión
    private fun mostrarError(error: VolleyError) {
        val detalle = when (error) {
            is NoConnectionError -> "Error de red: No hay conexión con el servidor (Verifica si Apache está encendido en XAMPP)."
            is TimeoutError -> "Error de red: Se agotó el tiempo de espera del servidor."
            is ServerError -> "Error en el servidor: Código 500 (Revisa el script de PHP)."
            else -> "Error de comunicación: ${error.message ?: "Error desconocido"}"
        }

        txtResultado.text = detalle
        txtResultado.setTextColor(Color.parseColor("#DC2626"))
        Toast.makeText(this, detalle, Toast.LENGTH_LONG).show()
    }
}
