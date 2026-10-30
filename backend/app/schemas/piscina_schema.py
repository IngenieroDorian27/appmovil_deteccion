from typing import Optional
from pydantic import BaseModel


class RegistrarPiscinaRequest(BaseModel):
    nombre_piscina: str

    class Config:
        from_attributes = True


class ActualizarUbicacionRequest(BaseModel):
    latitud: float
    longitud: float

    class Config:
        from_attributes = True


class PiscinaResponse(BaseModel):
    id_piscina: int
    nombre_piscina: str
    latitud: Optional[float] = None
    longitud: Optional[float] = None
    ubicacion_piscina: str

    class Config:
        from_attributes = True