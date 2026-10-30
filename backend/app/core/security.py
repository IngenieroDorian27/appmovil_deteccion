from datetime import datetime, timedelta, timezone
from jose import jwt

from app.core.config import settings


def crear_token_acceso(data: dict):
    datos_token = data.copy()

    tiempo_expiracion = datetime.now(timezone.utc) + timedelta(
        minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES
    )

    datos_token.update({
        "exp": tiempo_expiracion
    })

    token_jwt = jwt.encode(
        datos_token,
        settings.SECRET_KEY,
        algorithm=settings.ALGORITHM
    )

    return token_jwt