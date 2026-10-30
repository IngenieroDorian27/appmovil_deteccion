package com.example.visionia.shriavis.menuenfoques

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.visionia.R

import android.content.Intent
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.visionia.network.NotificacionesPendientesResponse
import com.example.visionia.shriavis.network.NotificacionPendienteResponse
import com.example.visionia.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.visionia.shriavis.efectos.EfectoNavInicio
import com.example.visionia.shriavis.efectos.EfectoNavAlertas
import com.example.visionia.shriavis.efectos.EfectoNavCaptura
import com.example.visionia.shriavis.efectos.EfectoNavHistorial
import com.example.visionia.shriavis.network.HistorialGeneralResponse


class NotificacionesActivity : AppCompatActivity() {

    private lateinit var rvNotificaciones: RecyclerView
    private lateinit var tvBanner: TextView

    private lateinit var iconInicio: LinearLayout
    private lateinit var iconCapturar: LinearLayout
    private lateinit var iconAlertas: LinearLayout
    private lateinit var iconHistorial: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notificaciones)


        rvNotificaciones = findViewById(R.id.rvNotificaciones)

        rvNotificaciones.layoutManager = LinearLayoutManager(this)
        tvBanner = findViewById(R.id.navBanner)

        iconInicio = findViewById(R.id.navInicio)
        iconCapturar = findViewById(R.id.navCapturar)
        iconAlertas = findViewById(R.id.navAlertas)
        iconHistorial = findViewById(R.id.navHistorial)

        obtenerNotificacionesPendientes()
        obetonerPendientes()

        iconInicio.setOnClickListener {

            EfectoNavInicio.aplicar(iconInicio) {
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            }

        }

        iconCapturar.setOnClickListener {

            EfectoNavCaptura.aplicar(iconCapturar){
                val intent = Intent(this, MenuCapturasActivity::class.java)
                startActivity(intent)
            }

        }

        iconAlertas.setOnClickListener {

            EfectoNavAlertas.aplicar(iconAlertas){
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

    private fun obetonerPendientes() {
        RetrofitClient.apiService.obtenerNotificacionesPendientes()
            .enqueue(object : Callback<NotificacionesPendientesResponse> {

                override fun onResponse(
                    call: Call<NotificacionesPendientesResponse>,
                    response: Response<NotificacionesPendientesResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val total = response.body()?.notificaciones_pendientes ?: 0

                        tvBanner.text = total.toString()
                    } else {

                        tvBanner.text = "0"
                    }
                }

                override fun onFailure(call: Call<NotificacionesPendientesResponse>, t: Throwable) {
                    tvBanner.text = "0"
                }
            })
    }


    private fun obtenerNotificacionesPendientes() {
        RetrofitClient.apiService.obtenerPendientesGeneral()
            .enqueue(object : Callback<List<HistorialGeneralResponse>> {

                override fun onResponse(
                    call: Call<List<HistorialGeneralResponse>>,
                    response: Response<List<HistorialGeneralResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val listaNotificaciones = response.body()!!

                        val adapter = HistorialGeneralAdapter(listaNotificaciones) { notificacion ->
                            abrirDetalleNotificacion(notificacion)
                        }

                        rvNotificaciones.adapter = adapter

                    } else {
                        Toast.makeText(
                            this@NotificacionesActivity,
                            "No se pudieron cargar las notificaciones",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<HistorialGeneralResponse>>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@NotificacionesActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }



    private fun abrirDetalleNotificacion(notificacion: HistorialGeneralResponse) {
        Toast.makeText(
            this,
            "Abrir detalle de alerta",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this, DetallesActivity::class.java)
        intent.putExtra("id_alerta", notificacion.id_alerta)
        intent.putExtra("tipo", notificacion.tipo)
        startActivity(intent)
    }

}