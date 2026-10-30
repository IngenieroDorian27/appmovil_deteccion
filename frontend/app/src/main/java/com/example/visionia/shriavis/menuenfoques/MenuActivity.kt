package com.example.visionia.shriavis.menuenfoques

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.visionia.R
import com.example.visionia.network.CantidadPiscinasResponse
import com.example.visionia.network.NotificacionesPendientesResponse
import com.example.visionia.network.RetrofitClient
import com.example.visionia.network.TotalMonitoreosResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import com.example.visionia.network.MarcarVistaResponse
import com.example.visionia.shriavis.efectos.EfectoNavInicio
import com.example.visionia.shriavis.efectos.EfectoNavAlertas
import com.example.visionia.shriavis.efectos.EfectoNavCaptura
import com.example.visionia.shriavis.efectos.EfectoNavHistorial




class MenuActivity : AppCompatActivity() {

    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvNumPiscinas: TextView
    private lateinit var tvNumMonitoreos: TextView
    private lateinit var tvNumNotificaciones: TextView
    private lateinit var tvBanner: TextView

    private lateinit var iconInicio: ImageView

    private lateinit var iconCamera: ImageView
    private lateinit var iconAlerta: ImageView

    private lateinit var iconHistorial: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)
        tvNumPiscinas = findViewById(R.id.tvNumPiscinas)
        tvNumMonitoreos = findViewById(R.id.tvNumMonitoreos)
        tvNumNotificaciones = findViewById(R.id.tvNumNotificaciones)
        tvBanner = findViewById(R.id.navBanner)
        iconInicio =findViewById(R.id.iconInicio)
        iconCamera = findViewById(R.id.iconCamera)
        iconAlerta = findViewById(R.id.iconAlert)
        iconHistorial = findViewById(R.id.iconHistorial)



        procesarNotificacionSiExiste(intent)
        obtenerTotalMonitoreos()
        obtenerNotificacionesPendientes()


        val sharedPreferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
        val nombreOperario = sharedPreferences.getString("nombre_operario", "Operario Campo")

        tvNombreUsuario.text = nombreOperario


        val layoutCapturar = findViewById<LinearLayout>(R.id.layoutCapturar)
        val layoutNotificaciones = findViewById<LinearLayout>(R.id.layoutNotificaciones)
        val layoutHistorial = findViewById<LinearLayout>(R.id.layoutHistorial)
        val tarjetaPiscinas = findViewById<LinearLayout>(R.id.tarjetaPiscinas)


        tarjetaPiscinas.setOnClickListener {
            val intent = Intent(this, PiscinasActivity::class.java)
            startActivity(intent)
        }

        layoutCapturar.setOnClickListener {
            val intent = Intent(this, MenuCapturasActivity::class.java)
            startActivity(intent)
        }

        layoutNotificaciones.setOnClickListener {
            val intent = Intent(this, NotificacionesActivity::class.java)
            startActivity(intent)
        }

        layoutHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        iconInicio.setOnClickListener {

            EfectoNavCaptura.aplicar(iconInicio){
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            }

        }

        iconCamera.setOnClickListener {

            EfectoNavCaptura.aplicar(iconCamera){
                val intent = Intent(this, MenuCapturasActivity::class.java)
                startActivity(intent)
            }

        }

        iconAlerta.setOnClickListener {
            EfectoNavAlertas.aplicar(iconAlerta){
                val intent = Intent(this, NotificacionesActivity::class.java)
                startActivity(intent)
            }
        }

        iconHistorial.setOnClickListener {
            EfectoNavHistorial.aplicar(iconHistorial){
                val intent = Intent(this, HistorialActivity::class.java)
                startActivity(intent)
            }
        }


    }

    override fun onResume() {
        super.onResume()
        obtenerNotificacionesPendientes()
        obtenerTotalMonitoreos()
        obtenerCantidadPiscinas()
    }



    private fun obtenerCantidadPiscinas() {
        RetrofitClient.apiService.obtenerCantidadPiscinas()
            .enqueue(object : Callback<CantidadPiscinasResponse> {

                override fun onResponse(
                    call: Call<CantidadPiscinasResponse>,
                    response: Response<CantidadPiscinasResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val cantidad = response.body()?.cantidad_piscinas ?: 0
                        tvNumPiscinas.text = cantidad.toString()
                    } else {
                        tvNumPiscinas.text = "0"
                    }
                }

                override fun onFailure(call: Call<CantidadPiscinasResponse>, t: Throwable) {
                    tvNumPiscinas.text = "0"
                }
            })
    }

    private fun obtenerTotalMonitoreos() {
        RetrofitClient.apiService.obtenerTotalMonitoreos()
            .enqueue(object : Callback<TotalMonitoreosResponse> {

                override fun onResponse(
                    call: Call<TotalMonitoreosResponse>,
                    response: Response<TotalMonitoreosResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val total = response.body()?.total_monitoreos ?: 0
                        tvNumMonitoreos.text = total.toString()
                    } else {
                        tvNumMonitoreos.text = "0"
                    }
                }

                override fun onFailure(call: Call<TotalMonitoreosResponse>, t: Throwable) {
                    tvNumMonitoreos.text = "0"
                }
            })
    }

    private fun obtenerNotificacionesPendientes() {
        RetrofitClient.apiService.obtenerNotificacionesPendientes()
            .enqueue(object : Callback<NotificacionesPendientesResponse> {

                override fun onResponse(
                    call: Call<NotificacionesPendientesResponse>,
                    response: Response<NotificacionesPendientesResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val total = response.body()?.notificaciones_pendientes ?: 0
                        tvNumNotificaciones.text = total.toString()
                        tvBanner.text = total.toString()
                    } else {
                        tvNumNotificaciones.text = "0"
                        tvBanner.text = "0"
                    }
                }

                override fun onFailure(call: Call<NotificacionesPendientesResponse>, t: Throwable) {
                    tvNumNotificaciones.text = "0"
                }
            })
    }



    private fun procesarNotificacionSiExiste(intent: Intent?) {
        val vieneDeNotificacion = intent?.getBooleanExtra("abrir_desde_notificacion", false) ?: false

        if (vieneDeNotificacion) {
            Toast.makeText(
                this,
                "Revise alerta pendiente por favor",
                Toast.LENGTH_LONG
            ).show()

            val i = Intent(this, NotificacionesActivity::class.java)
            startActivity(i)

            intent?.removeExtra("abrir_desde_notificacion")
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        procesarNotificacionSiExiste(intent)
    }

}