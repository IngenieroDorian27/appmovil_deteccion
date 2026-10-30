from datetime import datetime
from typing import Optional

from fastapi import APIRouter, Depends, UploadFile, File, Form, HTTPException
from sqlalchemy.orm import Session
from app.database.connection import get_db
from app.models.alerta_superficie_model import AlertaSuperficie
from app.schemas.alerta_superficie_schema import RegistroCapturaResponse
from app.services.s3_service import subir_imagen_s3
from app.models.monitoreo_model import Monitoreo
from app.ai.analizador_superficie import analizar_imagen_superficie
import os
import tempfile

router = APIRouter(
    prefix="/superficie",
    tags=["Superficie"]
)

@router.post("/registrar", response_model=RegistroCapturaResponse)
def registrar_superficie(
    imagen: UploadFile = File(...),
    piscina: str = Form(...),
    id_usuario: int = Form(...),
    latitud: Optional[float] = Form(None),
    longitud: Optional[float] = Form(None),
    db: Session = Depends(get_db)
):
    ruta_temporal = None

    try:
        fecha_hora_actual = datetime.now()

        extension = imagen.filename.split(".")[-1] if imagen.filename else "jpg"

        with tempfile.NamedTemporaryFile(delete=False, suffix=f".{extension}") as archivo_temporal:
            contenido_imagen = imagen.file.read()
            archivo_temporal.write(contenido_imagen)
            ruta_temporal = archivo_temporal.name

        resultado_ia = analizar_imagen_superficie(ruta_temporal)

        notificacion_ia = resultado_ia["notificacion"]

        with open(ruta_temporal, "rb") as archivo_para_s3:
            url_imagen = subir_imagen_s3(
                archivo=archivo_para_s3,
                nombre_original=imagen.filename
            )

        nueva_alerta = AlertaSuperficie(
            notificacion=notificacion_ia,
            fecha_alerta=fecha_hora_actual.date(),
            hora_alerta=fecha_hora_actual.time(),
            piscina=piscina,
            imagen_capturada=url_imagen,
            id_usuario=id_usuario,
            visto=False,
            latitud=latitud,
            longitud=longitud
        )

        db.add(nueva_alerta)

        monitoreo = db.query(Monitoreo).order_by(
            Monitoreo.id_monitoreo.desc()
        ).first()

        if monitoreo:
            monitoreo.notificaciones_pendientes += 1
            monitoreo.superficie += 1

        db.commit()
        db.refresh(nueva_alerta)

        return {
            "mensaje": "Captura analizada y registrada correctamente",
            "id_alerta_superficie": nueva_alerta.id_alerta_superficie,
            "notificacion": nueva_alerta.notificacion,
            "fecha_alerta": nueva_alerta.fecha_alerta,
            "hora_alerta": nueva_alerta.hora_alerta,
            "piscina": nueva_alerta.piscina,
            "imagen_capturada": nueva_alerta.imagen_capturada,
            "id_usuario": nueva_alerta.id_usuario,
            "visto": nueva_alerta.visto,
            "latitud": nueva_alerta.latitud,
            "longitud": nueva_alerta.longitud
        }

    except Exception as error:
        db.rollback()
        print("ERROR AL REGISTRAR SUPERFICIE:", error)

        raise HTTPException(
            status_code=500,
            detail=f"Error al registrar captura: {str(error)}"
        )

    finally:
        if ruta_temporal and os.path.exists(ruta_temporal):
            os.remove(ruta_temporal)