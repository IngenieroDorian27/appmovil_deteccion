from sqlalchemy import Column, Integer, String
from app.database.connection import Base


class Usuario(Base):
    __tablename__ = "usuario"

    id_usuario = Column(Integer, primary_key=True, index=True)
    usuario = Column(String(100), nullable=False)
    contrasena = Column(String(100), nullable=False)
    nombre_operario = Column(String(150), nullable=False)
    rol_usuario = Column(String(50), nullable=False)