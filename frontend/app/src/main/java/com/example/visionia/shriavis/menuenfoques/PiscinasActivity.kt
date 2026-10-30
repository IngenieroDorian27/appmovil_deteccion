package com.example.visionia.shriavis.menuenfoques

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.visionia.R
import com.example.visionia.network.RetrofitClient
import com.example.visionia.shriavis.network.ActualizarUbicacionRequest
import com.example.visionia.shriavis.network.PiscinaResponse
import com.example.visionia.shriavis.network.RegistrarPiscinaRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class PiscinasActivity : AppCompatActivity() {

    private lateinit var rvPiscinas: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var adapter: PiscinasAdapter
    private var listaCompleta: MutableList<PiscinaResponse> = mutableListOf()

    private var idUsuario: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_piscinas)

        val sharedPreferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("id_usuario", 0)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        rvPiscinas = findViewById(R.id.rvPiscinas)
        etBuscar = findViewById(R.id.etBuscarPiscina)


        adapter = PiscinasAdapter(
            mutableListOf(),
            onUbicacionClick = {  }

        )

        rvPiscinas.layoutManager = LinearLayoutManager(this)
        rvPiscinas.adapter = adapter

        cargarPiscinas()


        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarPiscinas(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun cargarPiscinas() {
        RetrofitClient.apiService.listarPiscinas(idUsuario)
            .enqueue(object : Callback<List<PiscinaResponse>> {
                override fun onResponse(
                    call: Call<List<PiscinaResponse>>,
                    response: Response<List<PiscinaResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        listaCompleta = response.body()!!.toMutableList()
                        adapter.actualizarLista(listaCompleta)
                    } else {
                        Toast.makeText(
                            this@PiscinasActivity,
                            "Error al cargar piscinas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<PiscinaResponse>>, t: Throwable) {
                    Toast.makeText(
                        this@PiscinasActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }



    private fun actualizarUbicacion(piscina: PiscinaResponse) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_ubicacion, null)

        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)
        val btnSi = dialogView.findViewById<Button>(R.id.btnSi)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        btnSi.setOnClickListener {
            dialog.dismiss()
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    102
                )
                return@setOnClickListener
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val request = ActualizarUbicacionRequest(
                        latitud = location.latitude,
                        longitud = location.longitude
                    )
                    RetrofitClient.apiService.actualizarUbicacionPiscina(piscina.id_piscina, request)
                        .enqueue(object : Callback<PiscinaResponse> {
                            override fun onResponse(
                                call: Call<PiscinaResponse>,
                                response: Response<PiscinaResponse>
                            ) {
                                if (response.isSuccessful && response.body() != null) {
                                    val actualizada = response.body()!!
                                    val index = listaCompleta.indexOfFirst {
                                        it.id_piscina == piscina.id_piscina
                                    }
                                    if (index != -1) {
                                        listaCompleta[index] = actualizada
                                        adapter.actualizarLista(listaCompleta)
                                    }
                                    Toast.makeText(
                                        this@PiscinasActivity,
                                        "Ubicación registrada correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@PiscinasActivity,
                                        "Error al actualizar ubicación",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<PiscinaResponse>,
                                t: Throwable
                            ) {
                                Toast.makeText(
                                    this@PiscinasActivity,
                                    "Error de conexión: ${t.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else {
                    Toast.makeText(
                        this@PiscinasActivity,
                        "No se pudo obtener la ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialog.show()
    }


    private fun filtrarPiscinas(query: String) {
        val listaFiltrada = if (query.isEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter {
                it.nombre_piscina.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        adapter.actualizarLista(listaFiltrada)
    }
}