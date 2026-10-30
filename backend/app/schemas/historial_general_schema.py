from datetime import date, time
from typing import Optional
from pydantic import BaseModel


class HistorialGeneralResponse(BaseModel):
    id_alerta: int
    tipo: str
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