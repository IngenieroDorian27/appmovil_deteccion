package com.example.visionia.network


import com.example.visionia.shriavis.network.ActualizarUbicacionRequest
import com.example.visionia.shriavis.network.DetalleNotificacionAcumulacionResponse
import com.example.visionia.shriavis.network.DetalleNotificacionEspumaResponse
import com.example.visionia.shriavis.network.NotificacionPendienteResponse
import com.example.visionia.shriavis.network.DetalleNotificacionResponse
import com.example.visionia.shriavis.network.HistorialGeneralResponse
import com.example.visionia.shriavis.network.PiscinaResponse
import com.example.visionia.shriavis.network.RegistrarPiscinaRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("usuarios/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>


    @GET("monitoreo/cantidad-piscinas")
    fun obtenerCantidadPiscinas(): Call<CantidadPiscinasResponse>


    @GET("monitoreo/total-monitoreos")
    fun obtenerTotalMonitoreos(): Call<TotalMonitoreosResponse>

    @GET("monitoreo/notificaciones-pendientes")
    fun obtenerNotificacionesPendientes(): Call<NotificacionesPendientesResponse>

    @Multipart
    @POST("captura/registrar")
    fun registrarCaptura(
        @Part imagen: MultipartBody.Part,
        @Part("piscina") piscina: RequestBody,
        @Part("id_usuario") idUsuario: RequestBody,
        @Part("latitud") latitud: RequestBody?,
        @Part("longitud") longitud: RequestBody?
    ): Call<RegistroCapturaResponse>

    @PUT("notificaciones/marcar-vista/superficie/{id_alerta}")
    fun marcarNotificacionSuperficieVista(
        @Path("id_alerta") idAlerta: Int
    ): Call<MarcarVistaResponse>


    @GET("notificaciones/superficie/pendientes")
    fun obtenerNotificacionesPendientesSuperficie(): Call<List<NotificacionPendienteResponse>>

    @GET("notificaciones/superficie/detalle/{id_alerta}")
    fun obtenerDetalleNotificacionSuperficie(
        @Path("id_alerta") idAlerta: Int
    ): Call<DetalleNotificacionResponse>

    @GET("notificaciones/superficie/historial")
    fun obtenerHistorialSuperficie(): Call<List<NotificacionPendienteResponse>>


    @GET("notificaciones/historial/general")
    fun obtenerHistorialGeneral(): Call<List<HistorialGeneralResponse>>


    @GET("notificaciones/pendientes/general")
    fun obtenerPendientesGeneral(): Call<List<HistorialGeneralResponse>>

    @Multipart
    @POST("acumulacion/registrar")
    fun registrarAcumulacion(
        @Part imagen: MultipartBody.Part,
        @Part("piscina") piscina: RequestBody,
        @Part("id_usuario") idUsuario: RequestBody,
        @Part("latitud") latitud: RequestBody?,
        @Part("longitud") longitud: RequestBody?
    ): Call<RegistroCapturaAcumulacionResponse>


    @Multipart
    @POST("espuma/registrar")
    fun registrarEspuma(
        @Part imagen: MultipartBody.Part,
        @Part("piscina") piscina: RequestBody,
        @Part("id_usuario") idUsuario: RequestBody,
        @Part("latitud") latitud: RequestBody?,
        @Part("longitud") longitud: RequestBody?
    ): Call<RegistroCapturaEspumaResponse>

    @GET("notificaciones/acumulacion/detalle/{id_alerta}")
    fun obtenerDetalleNotificacionAcumulacion(
        @Path("id_alerta") idAlerta: Int
    ): Call<DetalleNotificacionAcumulacionResponse>


    @GET("notificaciones/espuma/detalle/{id_alerta}")
    fun obtenerDetalleNotificacionEspuma(
        @Path("id_alerta") idAlerta: Int
    ): Call<DetalleNotificacionEspumaResponse>

    @PUT("notificaciones/marcar-vista/acumulacion/{id_alerta}")
    fun marcarNotificacionAcumulacionVista(
        @Path("id_alerta") idAlerta: Int
    ): Call<MarcarVistaResponse>

    @PUT("notificaciones/marcar-vista/espuma/{id_alerta}")
    fun marcarNotificacionEspumaVista(
        @Path("id_alerta") idAlerta: Int
    ): Call<MarcarVistaResponse>

    @GET("piscinas/listar")
    fun listarPiscinas(
        @Query("id_usuario") idUsuario: Int? = null
    ): Call<List<PiscinaResponse>>

    @POST("piscinas/registrar")
    fun registrarPiscina(
        @Body request: RegistrarPiscinaRequest
    ): Call<PiscinaResponse>

    @PUT("piscinas/actualizar-ubicacion/{id_piscina}")
    fun actualizarUbicacionPiscina(
        @Path("id_piscina") idPiscina: Int,
        @Body request: ActualizarUbicacionRequest
    ): Call<PiscinaResponse>

    @DELETE("piscinas/eliminar/{id_piscina}")
    fun eliminarPiscina(
        @Path("id_piscina") idPiscina: Int
    ): Call<Map<String, String>>


}