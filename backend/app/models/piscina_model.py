from sqlalchemy import Column, Integer, String, Numeric, ForeignKey
from app.database.connection import Base


class Piscina(Base):
    __tablename__ = "piscina"

    id_piscina = Column(Integer, primary_key=True, index=True)
    nombre_piscina = Column(String(150), nullable=False)
    latitud = Column(Numeric(10, 8), nullable=True)
    longitud = Column(Numeric(11, 8), nullable=True)

class UsuarioPiscina(Base):
    __tablename__ = "usuario_piscina"

    id_usuario_piscina = Column(Integer, primary_key=True, index=True)
    id_usuario = Column(Integer, ForeignKey("usuario.id_usuario"), nullable=False)
    id_piscina = Column(Integer, ForeignKey("piscina.id_piscina"), nullable=False)