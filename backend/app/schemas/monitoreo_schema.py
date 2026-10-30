from pydantic import BaseModel


class CantidadPiscinasResponse(BaseModel):
    cantidad_piscinas: int

class HistorialAcumulacionResponse(BaseModel):
    acumulacion: int

class HistorialSuperficieResponse(BaseModel):
    superficie: int

class HistorialEspumaResponse(BaseModel):
    espuma: int

class TotalMonitoreosResponse(BaseModel):
    total_monitoreos: int

class NotificacionesPendientesResponse(BaseModel):
    notificaciones_pendientes: int

