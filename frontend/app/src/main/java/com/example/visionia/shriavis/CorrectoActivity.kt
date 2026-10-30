package com.example.visionia.shriavis

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.visionia.R
import com.example.visionia.shriavis.efectos.VerificationCircleView
import com.example.visionia.shriavis.menuenfoques.MenuActivity

class CorrectoActivity : AppCompatActivity() {

    private lateinit var verificationCircle: VerificationCircleView
    private lateinit var tvEstado: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_correcto)

        verificationCircle = findViewById(R.id.verificationCircle)
        tvEstado = findViewById(R.id.tvEstado)

        val resultado = intent.getStringExtra("resultado")

        Handler(Looper.getMainLooper()).postDelayed({

            if (resultado == "correcto") {
                verificationCircle.setCorrecto()
                tvEstado.text = "Acceso Concedido"
                tvEstado.setTextColor(getColor(R.color.color_verde))

                // Esperar 3 segundos más e ir al Menu
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)

            } else {
                verificationCircle.setIncorrecto()
                tvEstado.text = "Acceso Denegado"
                tvEstado.setTextColor(getColor(R.color.color_rojo))

                // Esperar 3 segundos más y regresar al Login
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
            }

        }, 2000)

    }
}