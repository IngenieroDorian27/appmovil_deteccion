from datetime import date, time
from typing import Optional
from pydantic import BaseModel


class RegistroCapturaEspumaResponse(BaseModel):
    mensaje: str
    id_alerta_espuma: int
    notificacion: str
    fecha_alerta: date
    hora_alerta: time
    piscina: str
    imagen_capturada: str
    id_usuario: int
    visto: bool
    latitud: Optional[float] = None
    longitud: Optional[float] = None

    class Config:
        from_attributes = True



class NotificacionPendienteEspumaResponse(BaseModel):
    id_alerta_espuma: int
    notificacion: str
    fecha_alerta: date
    hora_alerta: time
    piscina: str
    imagen_capturada: str
    id_usuario: int
    nombre_operario: str
    visto: bool
    latitud: Optional[float] = None
    longitud: Optional[float] = None

    class Config:
        from_attributes = True


class DetalleNotificacionEspumaResponse(BaseModel):
    id_alerta_espuma: int
    notificacion: str
    fecha_alerta: date
    hora_alerta: time
    piscina: str
    imagen_capturada: str
    id_usuario: int
    nombre_operario: str
    visto: bool
    latitud: Optional[float] = None
    longitud: Optional[float] = None

    class Config:
        from_attributes = True


class HistorialEspumaResponse(BaseModel):
    id_alerta_espuma: int
    notificacion: str
    fecha_alerta: date
    hora_alerta: time
    piscina: str
    imagen_capturada: str
    id_usuario: int
    nombre_operario: str
    latitud: Optional[float] = None
    longitud: Optional[float] = None

    class Config:
        from_attributes = True