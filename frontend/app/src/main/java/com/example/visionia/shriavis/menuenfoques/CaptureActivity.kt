package com.example.visionia.shriavis.menuenfoques

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.visionia.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.ArrayAdapter
import android.widget.Spinner
import java.io.File
import androidx.camera.core.ImageCaptureException
import com.example.visionia.network.RegistroCapturaResponse
import com.example.visionia.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AlertDialog
import com.example.visionia.network.MarcarVistaResponse
import android.app.NotificationChannel
import android.app.Notification
import android.app.NotificationManager
import android.os.Build
import android.app.PendingIntent
import android.content.Intent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.visionia.network.NotificacionesPendientesResponse
import com.example.visionia.shriavis.efectos.EfectoNavInicio
import com.example.visionia.shriavis.efectos.EfectoNavAlertas
import com.example.visionia.shriavis.efectos.EfectoNavCaptura
import com.example.visionia.shriavis.efectos.EfectoNavHistorial
import com.example.visionia.shriavis.network.PiscinaResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class CaptureActivity : AppCompatActivity() {

    private lateinit var cameraPreview: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null

    private lateinit var spinnerPiscinas: Spinner
    private var piscinaSeleccionada: String = ""

    private lateinit var tvBanner: TextView

    private lateinit var iconInicio: LinearLayout
    private lateinit var iconCapturar: LinearLayout
    private lateinit var iconAlertas: LinearLayout
    private lateinit var iconHistorial: LinearLayout

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitudActual: Double? = null
    private var longitudActual: Double? = null

    private var listaPiscinas: List<PiscinaResponse> = emptyList()


    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
        private const val REQUEST_NOTIFICATION_PERMISSION = 101
        private const val CHANNEL_ID_ANALISIS = "canal_analisis_ia"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContentView(R.layout.activity_capture)

        spinnerPiscinas = findViewById(R.id.spinnerPiscinas)
        tvBanner = findViewById(R.id.navBanner)

        iconInicio = findViewById(R.id.navInicio)
        iconCapturar = findViewById(R.id.navCapturar)
        iconAlertas = findViewById(R.id.navAlertas)
        iconHistorial = findViewById(R.id.navHistorial)


        crearCanalNotificaciones()
        pedirPermisoNotificaciones()
        obtenerNotificacionesPendientes()

        cargarPiscinasSpinner()

        cameraPreview = findViewById(R.id.cameraPreview)
        cameraExecutor = Executors.newSingleThreadExecutor()


        val btnCapturar = findViewById<android.widget.Button>(R.id.btnCapturarImagen)
        btnCapturar.setOnClickListener {
            piscinaSeleccionada = listaPiscinas[spinnerPiscinas.selectedItemPosition].nombre_piscina
            tomarFoto()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }

        iconInicio.setOnClickListener {

            EfectoNavInicio.aplicar(iconInicio) {
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            }

        }

        iconCapturar.setOnClickListener {

            EfectoNavCaptura.aplicar(iconCapturar){
                val intent = Intent(this, CaptureActivity::class.java)
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

    private fun cargarPiscinasSpinner() {
        RetrofitClient.apiService.listarPiscinas()
            .enqueue(object : Callback<List<PiscinaResponse>> {
                override fun onResponse(
                    call: Call<List<PiscinaResponse>>,
                    response: Response<List<PiscinaResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        listaPiscinas = response.body()!!
                        val adapter = PiscinaSpinnerAdapter(
                            this@CaptureActivity,
                            listaPiscinas
                        )
                        spinnerPiscinas.adapter = adapter
                    } else {
                        Toast.makeText(
                            this@CaptureActivity,
                            "Error al cargar piscinas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<PiscinaResponse>>, t: Throwable) {
                    Toast.makeText(
                        this@CaptureActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = cameraPreview.surfaceProvider
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Error al iniciar cámara", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun tomarFoto() {
        val imageCaptureActual = imageCapture

        if (imageCaptureActual == null) {
            Toast.makeText(this, "La cámara aún no está lista", Toast.LENGTH_SHORT).show()
            return
        }

        obtenerUbicacion {
            val nombreArchivo = "captura_${System.currentTimeMillis()}.jpg"
            val archivoImagen = File(cacheDir, nombreArchivo)
            val outputOptions = ImageCapture.OutputFileOptions.Builder(archivoImagen).build()

            imageCaptureActual.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {

                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val nombrePiscina = listaPiscinas[spinnerPiscinas.selectedItemPosition].nombre_piscina
                        Toast.makeText(
                            this@CaptureActivity,
                            "Imagen capturada para $nombrePiscina",
                            Toast.LENGTH_SHORT
                        ).show()
                        enviarCapturaBackend(archivoImagen)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            this@CaptureActivity,
                            "Error al capturar imagen: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    private fun enviarCapturaBackend(archivoImagen: File) {
        val sharedPreferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("id_usuario", 0)

        if (idUsuario == 0) {
            Toast.makeText(this, "No se encontró el usuario en sesión", Toast.LENGTH_LONG).show()
            return
        }

        val requestFile = archivoImagen.asRequestBody("image/jpeg".toMediaTypeOrNull())

        val imagenPart = MultipartBody.Part.createFormData(
            "imagen",
            archivoImagen.name,
            requestFile
        )

        val piscinaBody = piscinaSeleccionada.toRequestBody("text/plain".toMediaTypeOrNull())
        val idUsuarioBody = idUsuario.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val latitudBody = latitudActual?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudBody = longitudActual?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

        RetrofitClient.apiService.registrarCaptura(
            imagen = imagenPart,
            piscina = piscinaBody,
            idUsuario = idUsuarioBody,
            latitud = latitudBody,
            longitud = longitudBody
        ).enqueue(object : Callback<RegistroCapturaResponse> {

            @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
            override fun onResponse(
                call: Call<RegistroCapturaResponse>,
                response: Response<RegistroCapturaResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val respuesta = response.body()!!
                    Toast.makeText(
                        this@CaptureActivity,
                        respuesta.mensaje,
                        Toast.LENGTH_LONG
                    ).show()
                    mostrarNotificacionAnalisis(respuesta)
                } else {
                    Toast.makeText(
                        this@CaptureActivity,
                        "Error al registrar captura: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<RegistroCapturaResponse>, t: Throwable) {
                Toast.makeText(
                    this@CaptureActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun mostrarBannerAnalisis(respuesta: RegistroCapturaResponse) {
        AlertDialog.Builder(this)
            .setTitle("Análisis IA")
            .setMessage(respuesta.notificacion)
            .setPositiveButton("Ver análisis") { dialog, _ ->
                marcarNotificacionComoVista(respuesta.id_alerta_superficie)
                dialog.dismiss()
            }
            .setNegativeButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun marcarNotificacionComoVista(idAlerta: Int) {
        RetrofitClient.apiService.marcarNotificacionSuperficieVista(idAlerta)
            .enqueue(object : Callback<MarcarVistaResponse> {

                override fun onResponse(
                    call: Call<MarcarVistaResponse>,
                    response: Response<MarcarVistaResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(
                            this@CaptureActivity,
                            response.body()?.mensaje ?: "Notificación actualizada",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@CaptureActivity,
                            "No se pudo actualizar la notificación",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MarcarVistaResponse>, t: Throwable) {
                    Toast.makeText(
                        this@CaptureActivity,
                        "Error al actualizar notificación: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun crearCanalNotificaciones() {
        val nombreCanal = "Análisis IA"
        val descripcionCanal = "Notificaciones del análisis de imágenes con IA"
        val importancia = NotificationManager.IMPORTANCE_HIGH

        val canal = NotificationChannel(
            CHANNEL_ID_ANALISIS,
            nombreCanal,
            importancia
        ).apply {
            description = descripcionCanal
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(canal)
    }

    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }

    private fun mostrarNotificacionAnalisis(respuesta: RegistroCapturaResponse) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permisoNotificacion = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permisoNotificacion != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Permiso de notificaciones no concedido",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }


        val intent = Intent(this, MenuActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("id_alerta_superficie", respuesta.id_alerta_superficie)
            putExtra("abrir_desde_notificacion", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            respuesta.id_alerta_superficie,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mensajeCorto = "Nuevo Análisis Disponible, Revise por Favor"

        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID_ANALISIS)
            .setSmallIcon(R.drawable.robot_2)
            .setContentTitle("Análisis IA completado")
            .setContentText(mensajeCorto)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(mensajeCorto)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(
            respuesta.id_alerta_superficie,
            notificacion
        )
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



    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun obtenerUbicacion(onUbicacionObtenida: () -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                102
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitudActual = location.latitude
                longitudActual = location.longitude
            }
            onUbicacionObtenida()
        }.addOnFailureListener {
            latitudActual = null
            longitudActual = null
            onUbicacionObtenida()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}