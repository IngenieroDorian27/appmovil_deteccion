from pydantic import BaseModel


class UsuarioResponse(BaseModel):
    id_usuario: int
    usuario: str
    nombre_operario: str
    rol_usuario: str

    class Config:
        from_attributes = True


class UsuarioCreate(BaseModel):
    usuario: str
    contrasena: str
    nombre_operario: str


class UsuarioUpdate(BaseModel):
    usuario: str
    nombre_operario: str




class LoginRequest(BaseModel):
    usuario: str
    contrasena: str


class LoginResponse(BaseModel):
    mensaje: str
    id_usuario: int
    usuario: str
    nombre_operario: str
    rol_usuario: str



class TokenResponse(BaseModel):
    mensaje: str
    access_token: str
    token_type: str
    id_usuario: int
    usuario: str
    nombre_operario: str

