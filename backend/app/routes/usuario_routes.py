from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.models.usuario_model import Usuario
from app.schemas.usuario_schema import UsuarioResponse, LoginRequest, TokenResponse, UsuarioCreate, UsuarioUpdate, \
    LoginResponse
from app.core.security import crear_token_acceso


router = APIRouter(
    prefix="/usuarios",
    tags=["Usuarios"]
)


@router.get("/mostrar", response_model=list[UsuarioResponse])
def listar_usuarios(db: Session = Depends(get_db)):
    usuarios = db.query(Usuario).all()
    return usuarios



@router.post("/login", response_model=LoginResponse)
def login(datos_login: LoginRequest, db: Session = Depends(get_db)):
    usuario_encontrado = db.query(Usuario).filter(
        Usuario.usuario == datos_login.usuario,
        Usuario.contrasena == datos_login.contrasena
    ).first()

    if not usuario_encontrado:
        raise HTTPException(
            status_code=401,
            detail="Usuario o contraseña incorrectos"
        )

    return {
        "mensaje": "Login exitoso",
        "id_usuario": usuario_encontrado.id_usuario,
        "usuario": usuario_encontrado.usuario,
        "nombre_operario": usuario_encontrado.nombre_operario,
        "rol_usuario": usuario_encontrado.rol_usuario
    }
