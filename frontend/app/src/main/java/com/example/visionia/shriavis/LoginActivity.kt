package com.example.visionia.shriavis

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.visionia.R
import com.example.visionia.network.LoginRequest
import com.example.visionia.network.LoginResponse
import com.example.visionia.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var btnLogin: Button
    private lateinit var etUsuario: EditText
    private lateinit var etContra: EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
        etUsuario = findViewById(R.id.etUsuario)
        etContra = findViewById(R.id.etContra)

        // ✅ Agregar esto
        autoScrollOnKeyboard()

        btnLogin.setOnClickListener {
            val usuarioIngresado = etUsuario.text.toString().trim()
            val contraIngresada = etContra.text.toString().trim()

            if (usuarioIngresado.isEmpty() || contraIngresada.isEmpty()) {
                Toast.makeText(
                    this,
                    "Ingrese usuario y contraseña",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            consumirLogin(usuarioIngresado, contraIngresada)
        }
    }


    private fun autoScrollOnKeyboard() {
        val scrollView = findViewById<NestedScrollView>(R.id.scrollLogin)
        val rootView = findViewById<View>(R.id.main)

        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                val focusedView = currentFocus
                if (focusedView != null) {
                    scrollView.post {
                        scrollView.smoothScrollTo(0, focusedView.bottom)
                    }
                }
            }
        }
    }


    private fun consumirLogin(usuario: String, contrasena: String) {

        val loginRequest = LoginRequest(
            usuario = usuario,
            contrasena = contrasena
        )

        RetrofitClient.apiService.login(loginRequest)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val loginResponse = response.body()

                        val sharedPreferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        editor.putInt("id_usuario", loginResponse?.id_usuario ?: 0)
                        editor.putString("usuario", loginResponse?.usuario)
                        editor.putString("nombre_operario", loginResponse?.nombre_operario)
                        editor.putString("rol_usuario", loginResponse?.rol_usuario)

                        editor.apply()


                        val intent = Intent(this@LoginActivity, CorrectoActivity::class.java)
                        intent.putExtra("resultado", "correcto")
                        startActivity(intent)

                    } else {
                        val intent = Intent(this@LoginActivity, CorrectoActivity::class.java)
                        intent.putExtra("resultado", "incorrecto")
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

}