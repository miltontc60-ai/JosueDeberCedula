package com.josue.verificarcedula

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var txtUsuario: EditText
    private lateinit var txtClave: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnIrVerificar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtUsuario = findViewById(R.id.txt_usuario)
        txtClave = findViewById(R.id.txt_clave)
        btnLogin = findViewById(R.id.btn_login)
        btnIrVerificar = findViewById(R.id.btn_ir_verificar)

        // Navegación a la actividad de verificación
        btnIrVerificar.setOnClickListener {
            val intent = Intent(this, VerificarCedula::class.java)
            startActivity(intent)
        }

        // Petición de login
        btnLogin.setOnClickListener {
            val user = txtUsuario.text.toString().trim()
            val pass = txtClave.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = "http://192.168.1.6/ws/persona.php"
            val data = JSONObject()
            data.put("accion", "login")
            data.put("usuario", user)
            data.put("clave", pass)

            val req = JsonObjectRequest(
                Request.Method.POST,
                url,
                data,
                { resp ->
                    Toast.makeText(this, resp.optString("mensaje"), Toast.LENGTH_LONG).show()
                },
                { err ->
                    Toast.makeText(this, "Error de red", Toast.LENGTH_LONG).show()
                }
            )

            Volley.newRequestQueue(this).add(req)
        }
    }
}
