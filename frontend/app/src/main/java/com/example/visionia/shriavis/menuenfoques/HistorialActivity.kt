package com.example.visionia.shriavis.menuenfoques

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.visionia.R

import android.content.Intent
import android.widget.EditText
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


class HistorialActivity : AppCompatActivity() {

    private lateinit var rvHistorial: RecyclerView
    private lateinit var tvBanner: TextView

    private lateinit var iconInicio: LinearLayout
    private lateinit var iconCapturar: LinearLayout
    private lateinit var iconAlertas: LinearLayout
    private lateinit var iconHistorial: LinearLayout

    private lateinit var etBuscarPiscina: EditText
    private lateinit var historialAdapter: HistorialGeneralAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_historial)

        rvHistorial = findViewById(R.id.rvHistorial)

        rvHistorial.layoutManager = LinearLayoutManager(this)

        etBuscarPiscina = findViewById(R.id.etBuscarPiscina)
        etBuscarPiscina.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                historialAdapter.filtrarPorPiscina(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        tvBanner = findViewById(R.id.navBanner)

        iconInicio = findViewById(R.id.navInicio)
        iconCapturar = findViewById(R.id.navCapturar)
        iconAlertas = findViewById(R.id.navAlertas)
        iconHistorial = findViewById(R.id.navHistorial)

        obtenerHistorial()
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

    private fun obtenerHistorial() {
        RetrofitClient.apiService.obtenerHistorialGeneral()
            .enqueue(object : Callback<List<HistorialGeneralResponse>> {

                override fun onResponse(
                    call: Call<List<HistorialGeneralResponse>>,
                    response: Response<List<HistorialGeneralResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val listaNotificaciones = response.body()!!

                        historialAdapter = HistorialGeneralAdapter(listaNotificaciones) { notificacion ->
                            abrirDetalleHistorial(notificacion)
                        }
                        rvHistorial.adapter = historialAdapter

                    } else {
                        Toast.makeText(
                            this@HistorialActivity,
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
                        this@HistorialActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun abrirDetalleHistorial(notificacion: HistorialGeneralResponse) {
        Toast.makeText(
            this,
            "Abrir Detalle Historial",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this, HistorialDetalleActivity::class.java)
        intent.putExtra("id_alerta", notificacion.id_alerta)
        intent.putExtra("tipo", notificacion.tipo)
        startActivity(intent)
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
}