from sqlalchemy import Column, Integer
from app.database.connection import Base


class Monitoreo(Base):
    __tablename__ = "monitoreo"

    id_monitoreo = Column(Integer, primary_key=True, index=True)
    numero_piscinas = Column(Integer, nullable=False)
    acumulacion = Column(Integer, nullable=False)
    superficie = Column(Integer, nullable=False)
    espuma = Column(Integer, nullable=False)
    notificaciones_pendientes = Column(Integer, nullable=False)