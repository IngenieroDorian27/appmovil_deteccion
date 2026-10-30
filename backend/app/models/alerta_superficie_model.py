from sqlalchemy import Column, Integer, Text, Date, Time, String, Boolean, ForeignKey, Numeric
from app.database.connection import Base


class AlertaSuperficie(Base):
    __tablename__ = "alerta_superficie"

    id_alerta_superficie = Column(Integer, primary_key=True, index=True)
    notificacion = Column(Text, nullable=False)
    fecha_alerta = Column(Date, nullable=False)
    hora_alerta = Column(Time, nullable=False)
    piscina = Column(String(150), nullable=False)
    imagen_capturada = Column(String(500), nullable=False)
    id_usuario = Column(Integer, ForeignKey("usuario.id_usuario"), nullable=False)
    visto = Column(Boolean, nullable=False, default=False)
    latitud = Column(Numeric(10, 8), nullable=True)
    longitud = Column(Numeric(11, 8), nullable=True)