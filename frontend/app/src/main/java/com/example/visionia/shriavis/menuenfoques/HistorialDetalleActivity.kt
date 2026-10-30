package com.example.visionia.shriavis.menuenfoques

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.visionia.R
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.visionia.shriavis.network.DetalleNotificacionResponse
import com.example.visionia.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.Dialog
import android.view.Window
import android.view.WindowManager
import android.content.Intent
import android.net.Uri
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.visionia.shriavis.network.DetalleNotificacionAcumulacionResponse
import com.example.visionia.shriavis.network.DetalleNotificacionEspumaResponse

class HistorialDetalleActivity : AppCompatActivity() {

    private lateinit var ivImagenCapturada: ImageView
    private lateinit var tvAnalisisCompleto: TextView
    private lateinit var tvPiscinaDetalle: TextView
    private lateinit var tvFechaDetalle: TextView
    private lateinit var tvHoraDetalle: TextView
    private lateinit var tvOperarioDetalle: TextView

    private lateinit var btnVerUbicacion: Button
    private var latitudActual: Double? = null
    private var longitudActual: Double? = null
    private var urlImagenActual: String = ""
    private var idAlerta: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_historial_detalle)

        ivImagenCapturada = findViewById(R.id.ivImagenCapturada)
        tvAnalisisCompleto = findViewById(R.id.tvAnalisisCompleto)
        tvPiscinaDetalle = findViewById(R.id.tvPiscinaDetalle)
        tvFechaDetalle = findViewById(R.id.tvFechaDetalle)
        tvHoraDetalle = findViewById(R.id.tvHoraDetalle)
        tvOperarioDetalle = findViewById(R.id.tvOperarioDetalle)

        idAlerta = intent.getIntExtra("id_alerta", 0)
        val tipo = intent.getStringExtra("tipo") ?: "superficie"

        if (idAlerta == 0) {
            Toast.makeText(
                this,
                "No se encontró el ID de la alerta",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        obtenerDetalleHistorial(idAlerta, tipo)

        btnVerUbicacion = findViewById(R.id.btnVerUbicacion)
        btnVerUbicacion.setOnClickListener {
            abrirUbicacionEnMapa()
        }

    }

    private fun obtenerDetalleHistorial(idAlerta: Int, tipo: String) {
        when (tipo) {
            "acumulacion" -> {
                RetrofitClient.apiService.obtenerDetalleNotificacionAcumulacion(idAlerta)
                    .enqueue(object : Callback<DetalleNotificacionAcumulacionResponse> {
                        override fun onResponse(
                            call: Call<DetalleNotificacionAcumulacionResponse>,
                            response: Response<DetalleNotificacionAcumulacionResponse>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                val detalle = response.body()!!
                                tvAnalisisCompleto.text = detalle.notificacion
                                tvPiscinaDetalle.text = "Piscina: ${detalle.piscina}"
                                tvFechaDetalle.text = "Fecha: ${detalle.fecha_alerta}"
                                tvHoraDetalle.text = "Hora: ${detalle.hora_alerta}"
                                tvOperarioDetalle.text = "Operario: ${detalle.nombre_operario}"
                                urlImagenActual = detalle.imagen_capturada
                                latitudActual = detalle.latitud
                                longitudActual = detalle.longitud
                                Glide.with(this@HistorialDetalleActivity)
                                    .load(detalle.imagen_capturada)
                                    .placeholder(R.drawable.logobg)
                                    .error(R.drawable.logobg)
                                    .into(ivImagenCapturada)
                                ivImagenCapturada.setOnClickListener {
                                    mostrarImagenCompleta()
                                }
                            } else {
                                Toast.makeText(this@HistorialDetalleActivity, "No se pudo cargar el detalle", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<DetalleNotificacionAcumulacionResponse>, t: Throwable) {
                            Toast.makeText(this@HistorialDetalleActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
            }
            "espuma" -> {
                RetrofitClient.apiService.obtenerDetalleNotificacionEspuma(idAlerta)
                    .enqueue(object : Callback<DetalleNotificacionEspumaResponse> {
                        override fun onResponse(
                            call: Call<DetalleNotificacionEspumaResponse>,
                            response: Response<DetalleNotificacionEspumaResponse>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                val detalle = response.body()!!
                                tvAnalisisCompleto.text = detalle.notificacion
                                tvPiscinaDetalle.text = "Piscina: ${detalle.piscina}"
                                tvFechaDetalle.text = "Fecha: ${detalle.fecha_alerta}"
                                tvHoraDetalle.text = "Hora: ${detalle.hora_alerta}"
                                tvOperarioDetalle.text = "Operario: ${detalle.nombre_operario}"
                                urlImagenActual = detalle.imagen_capturada
                                latitudActual = detalle.latitud
                                longitudActual = detalle.longitud
                                Glide.with(this@HistorialDetalleActivity)
                                    .load(detalle.imagen_capturada)
                                    .placeholder(R.drawable.logobg)
                                    .error(R.drawable.logobg)
                                    .into(ivImagenCapturada)
                                ivImagenCapturada.setOnClickListener {
                                    mostrarImagenCompleta()
                                }
                            } else {
                                Toast.makeText(this@HistorialDetalleActivity, "No se pudo cargar el detalle", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<DetalleNotificacionEspumaResponse>, t: Throwable) {
                            Toast.makeText(this@HistorialDetalleActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
            }
            else -> {
                RetrofitClient.apiService.obtenerDetalleNotificacionSuperficie(idAlerta)
                    .enqueue(object : Callback<DetalleNotificacionResponse> {
                        override fun onResponse(
                            call: Call<DetalleNotificacionResponse>,
                            response: Response<DetalleNotificacionResponse>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                val detalle = response.body()!!
                                latitudActual = detalle.latitud
                                longitudActual = detalle.longitud
                                mostrarDetalle(detalle)
                            } else {
                                Toast.makeText(this@HistorialDetalleActivity, "No se pudo cargar el detalle", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<DetalleNotificacionResponse>, t: Throwable) {
                            Toast.makeText(this@HistorialDetalleActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }
    }
    private fun mostrarDetalle(detalle: DetalleNotificacionResponse) {
        tvAnalisisCompleto.text = detalle.notificacion
        tvPiscinaDetalle.text = "Piscina: ${detalle.piscina}"
        tvFechaDetalle.text = "Fecha: ${detalle.fecha_alerta}"
        tvHoraDetalle.text = "Hora: ${detalle.hora_alerta}"
        tvOperarioDetalle.text = "Operario: ${detalle.nombre_operario}"
        urlImagenActual = detalle.imagen_capturada

        Glide.with(this)
            .load(detalle.imagen_capturada)
            .placeholder(R.drawable.logobg)
            .error(R.drawable.logobg)
            .into(ivImagenCapturada)


        ivImagenCapturada.setOnClickListener {
            mostrarImagenCompleta()
        }

    }

    private fun mostrarImagenCompleta() {
        if (urlImagenActual.isEmpty()) {
            Toast.makeText(
                this,
                "No hay imagen disponible",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_imagen_completa)

        val imagenCompleta = dialog.findViewById<ImageView>(R.id.ivImagenCompleta)

        Glide.with(this)
            .load(urlImagenActual)
            .placeholder(R.drawable.logobg)
            .error(R.drawable.logobg)
            .into(imagenCompleta)

        imagenCompleta.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.black)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        dialog.show()
    }

    private fun abrirUbicacionEnMapa() {
        val lat = latitudActual
        val lng = longitudActual

        if (lat == null || lng == null) {
            Toast.makeText(this, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}