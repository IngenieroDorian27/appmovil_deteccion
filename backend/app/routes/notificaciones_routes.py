from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from app.models.piscina_model import Piscina

from app.database.connection import get_db
from app.models.alerta_superficie_model import AlertaSuperficie
from app.models.monitoreo_model import Monitoreo
from app.models.usuario_model import Usuario
from app.schemas.alerta_superficie_schema import (NotificacionPendienteResponse,
                                                  DetalleNotificacionResponse, HistorialSuperficieResponse)
from app.models.alerta_alimento_model import AlertaAcumulacion
from app.schemas.alerta_alimento_schema import (NotificacionPendienteAcumulacionResponse,
                                                    DetalleNotificacionAcumulacionResponse,
                                                    HistorialAcumulacionResponse)
from app.models.alerta_espuma_model import AlertaEspuma
from app.schemas.alerta_espuma_schema import (NotificacionPendienteEspumaResponse,
                                               DetalleNotificacionEspumaResponse,
                                               HistorialEspumaResponse)
from app.services.s3_service import generar_url_temporal_s3
from app.schemas.historial_general_schema import HistorialGeneralResponse
from sqlalchemy import literal



router = APIRouter(
    prefix="/notificaciones",
    tags=["Notificaciones"]
)


@router.put("/marcar-vista/superficie/{id_alerta}")
def marcar_notificacion_superficie_como_vista(
    id_alerta: int,
    db: Session = Depends(get_db)
):
    alerta = db.query(AlertaSuperficie).filter(
        AlertaSuperficie.id_alerta_superficie == id_alerta
    ).first()

    if not alerta:
        raise HTTPException(
            status_code=404,
            detail="Notificación no encontrada"
        )

    if alerta.visto:
        return {
            "mensaje": "La notificación ya estaba vista",
            "contador_actualizado": False
        }

    alerta.visto = True

    monitoreo = db.query(Monitoreo).order_by(
        Monitoreo.id_monitoreo.desc()
    ).first()

    if monitoreo:
        if monitoreo.notificaciones_pendientes > 0:
            monitoreo.notificaciones_pendientes -= 1

    db.commit()
    db.refresh(alerta)

    if monitoreo:
        db.refresh(monitoreo)

    return {
        "mensaje": "Notificación marcada como vista",
        "contador_actualizado": True,
        "notificaciones_pendientes": monitoreo.notificaciones_pendientes if monitoreo else 0
    }


@router.get(
    "/superficie/pendientes",
    response_model=List[NotificacionPendienteResponse]
)
def obtener_notificaciones_pendientes_superficie(
    db: Session = Depends(get_db)
):
    resultados = (
        db.query(
            AlertaSuperficie.id_alerta_superficie,
            AlertaSuperficie.notificacion,
            AlertaSuperficie.fecha_alerta,
            AlertaSuperficie.hora_alerta,
            AlertaSuperficie.piscina,
            AlertaSuperficie.imagen_capturada,
            AlertaSuperficie.id_usuario,
            Usuario.nombre_operario,
            AlertaSuperficie.visto,
            AlertaSuperficie.latitud,
            AlertaSuperficie.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaSuperficie.id_usuario)
        .filter(AlertaSuperficie.visto == False)
        .order_by(
            AlertaSuperficie.fecha_alerta.asc(),
            AlertaSuperficie.hora_alerta.asc()
        )
        .all()
    )

    return resultados


@router.get(
    "/superficie/detalle/{id_alerta}",
    response_model=DetalleNotificacionResponse
)
def obtener_detalle_notificacion_superficie(
    id_alerta: int,
    db: Session = Depends(get_db)
):
    resultado = (
        db.query(
            AlertaSuperficie.id_alerta_superficie,
            AlertaSuperficie.notificacion,
            AlertaSuperficie.fecha_alerta,
            AlertaSuperficie.hora_alerta,
            AlertaSuperficie.piscina,
            AlertaSuperficie.imagen_capturada,
            AlertaSuperficie.id_usuario,
            Usuario.nombre_operario,
            AlertaSuperficie.visto,
            AlertaSuperficie.latitud,
            AlertaSuperficie.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaSuperficie.id_usuario)
        .filter(AlertaSuperficie.id_alerta_superficie == id_alerta)
        .first()
    )

    if not resultado:
        raise HTTPException(
            status_code=404,
            detail="Notificación no encontrada"
        )

    url_temporal_imagen = generar_url_temporal_s3(resultado.imagen_capturada)

    return {
        "id_alerta_superficie": resultado.id_alerta_superficie,
        "notificacion": resultado.notificacion,
        "fecha_alerta": resultado.fecha_alerta,
        "hora_alerta": resultado.hora_alerta,
        "piscina": resultado.piscina,
        "imagen_capturada": url_temporal_imagen,
        "id_usuario": resultado.id_usuario,
        "nombre_operario": resultado.nombre_operario,
        "visto": resultado.visto,
        "latitud": resultado.latitud,
        "longitud": resultado.longitud
    }

@router.get(
    "/superficie/historial",
    response_model=list[HistorialSuperficieResponse]
)
def obtener_historial_superficie(
    db: Session = Depends(get_db)
):
    resultados = (
        db.query(
            AlertaSuperficie.id_alerta_superficie,
            AlertaSuperficie.notificacion,
            AlertaSuperficie.fecha_alerta,
            AlertaSuperficie.hora_alerta,
            AlertaSuperficie.piscina,
            AlertaSuperficie.imagen_capturada,
            AlertaSuperficie.id_usuario,
            Usuario.nombre_operario,
            AlertaSuperficie.visto,
            AlertaSuperficie.latitud,
            AlertaSuperficie.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaSuperficie.id_usuario)
        .order_by(
            AlertaSuperficie.fecha_alerta.desc(),
            AlertaSuperficie.hora_alerta.desc()
        )
        .all()
    )

    return resultados


#acumulacion

@router.put("/marcar-vista/acumulacion/{id_alerta}")
def marcar_notificacion_acumulacion_como_vista(
    id_alerta: int,
    db: Session = Depends(get_db)
):
    alerta = db.query(AlertaAcumulacion).filter(
        AlertaAcumulacion.id_alerta_acumulacion == id_alerta
    ).first()

    if not alerta:
        raise HTTPException(
            status_code=404,
            detail="Notificación no encontrada"
        )

    if alerta.visto:
        return {
            "mensaje": "La notificación ya estaba vista",
            "contador_actualizado": False
        }

    alerta.visto = True

    monitoreo = db.query(Monitoreo).order_by(
        Monitoreo.id_monitoreo.desc()
    ).first()

    if monitoreo:
        if monitoreo.notificaciones_pendientes > 0:
            monitoreo.notificaciones_pendientes -= 1

    db.commit()
    db.refresh(alerta)

    if monitoreo:
        db.refresh(monitoreo)

    return {
        "mensaje": "Notificación marcada como vista",
        "contador_actualizado": True,
        "notificaciones_pendientes": monitoreo.notificaciones_pendientes if monitoreo else 0
    }

@router.get(
    "/acumulacion/pendientes",
    response_model=List[NotificacionPendienteAcumulacionResponse]
)
def obtener_notificaciones_pendientes_acumulacion(
    db: Session = Depends(get_db)
):
    resultados = (
        db.query(
            AlertaAcumulacion.id_alerta_acumulacion,
            AlertaAcumulacion.notificacion,
            AlertaAcumulacion.fecha_alerta,
            AlertaAcumulacion.hora_alerta,
            AlertaAcumulacion.piscina,
            AlertaAcumulacion.imagen_capturada,
            AlertaAcumulacion.id_usuario,
            Usuario.nombre_operario,
            AlertaAcumulacion.visto,
            AlertaAcumulacion.latitud,
            AlertaAcumulacion.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaAcumulacion.id_usuario)
        .filter(AlertaAcumulacion.visto == False)
        .order_by(
            AlertaAcumulacion.fecha_alerta.asc(),
            AlertaAcumulacion.hora_alerta.asc()
        )
        .all()
    )

    return resultados

@router.get(
    "/acumulacion/detalle/{id_alerta}",
    response_model=DetalleNotificacionAcumulacionResponse
)
def obtener_detalle_notificacion_acumulacion(
    id_alerta: int,
    db: Session = Depends(get_db)
):
    resultado = (
        db.query(
            AlertaAcumulacion.id_alerta_acumulacion,
            AlertaAcumulacion.notificacion,
            AlertaAcumulacion.fecha_alerta,
            AlertaAcumulacion.hora_alerta,
            AlertaAcumulacion.piscina,
            AlertaAcumulacion.imagen_capturada,
            AlertaAcumulacion.id_usuario,
            Usuario.nombre_operario,
            AlertaAcumulacion.visto,
            AlertaAcumulacion.latitud,
            AlertaAcumulacion.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaAcumulacion.id_usuario)
        .filter(AlertaAcumulacion.id_alerta_acumulacion == id_alerta)
        .first()
    )

    if not resultado:
        raise HTTPException(
            status_code=404,
            detail="Notificación no encontrada"
        )

    url_temporal_imagen = generar_url_temporal_s3(resultado.imagen_capturada)

    return {
        "id_alerta_acumulacion": resultado.id_alerta_acumulacion,
        "notificacion": resultado.notificacion,
        "fecha_alerta": resultado.fecha_alerta,
        "hora_alerta": resultado.hora_alerta,
        "piscina": resultado.piscina,
        "imagen_capturada": url_temporal_imagen,
        "id_usuario": resultado.id_usuario,
        "nombre_operario": resultado.nombre_operario,
        "visto": resultado.visto,
        "latitud": resultado.latitud,
        "longitud": resultado.longitud
    }

@router.get(
    "/acumulacion/historial",
    response_model=list[HistorialAcumulacionResponse]
)
def obtener_historial_acumulacion(
    db: Session = Depends(get_db)
):
    resultados = (
        db.query(
            AlertaAcumulacion.id_alerta_acumulacion,
            AlertaAcumulacion.notificacion,
            AlertaAcumulacion.fecha_alerta,
            AlertaAcumulacion.hora_alerta,
            AlertaAcumulacion.piscina,
            AlertaAcumulacion.imagen_capturada,
            AlertaAcumulacion.id_usuario,
            Usuario.nombre_operario,
            AlertaAcumulacion.latitud,
            AlertaAcumulacion.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaAcumulacion.id_usuario)
        .order_by(
            AlertaAcumulacion.fecha_alerta.desc(),
            AlertaAcumulacion.hora_alerta.desc()
        )
        .all()
    )

    return resultados


#espuma

@router.put("/marcar-vista/espuma/{id_alerta}")
def marcar_notificacion_espuma_como_vista(
    id_alerta: int,
    db: Session = Depends(get_db)
):
    alerta = db.query(AlertaEspuma).filter(
        AlertaEspuma.id_alerta_espuma == id_alerta
    ).first()

    if not alerta:
        raise HTTPException(
            status_code=404,
            detail="Notificación no encontrada"
        )

    if alerta.visto:
        return {
            "mensaje": "La notificación ya estaba vista",
            "contador_actualizado": False
        }

    alerta.visto = True

    monitoreo = db.query(Monitoreo).order_by(
        Monitoreo.id_monitoreo.desc()
    ).first()

    if monitoreo:
        if monitoreo.notificaciones_pendientes > 0:
            monitoreo.notificaciones_pendientes -= 1

    db.commit()
    db.refresh(alerta)

    if monitoreo:
        db.refresh(monitoreo)

    return {
        "mensaje": "Notificación marcada como vista",
        "contador_actualizado": True,
        "notificaciones_pendientes": monitoreo.notificaciones_pendientes if monitoreo else 0
    }

@router.get(
    "/espuma/pendientes",
    response_model=List[NotificacionPendienteEspumaResponse]
)
def obtener_notificaciones_pendientes_espuma(
    db: Session = Depends(get_db)
):
    resultados = (
        db.query(
            AlertaEspuma.id_alerta_espuma,
            AlertaEspuma.notificacion,
            AlertaEspuma.fecha_alerta,
            AlertaEspuma.hora_alerta,
            AlertaEspuma.piscina,
            AlertaEspuma.imagen_capturada,
            AlertaEspuma.id_usuario,
            Usuario.nombre_operario,
            AlertaEspuma.visto,
            AlertaEspuma.latitud,
            AlertaEspuma.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaEspuma.id_usuario)
        .filter(AlertaEspuma.visto == False)
        .order_by(
            AlertaEspuma.fecha_alerta.asc(),
            AlertaEspuma.hora_alerta.asc()
        )
        .all()
    )

    return resultados

@router.get(
    "/espuma/detalle/{id_alerta}",
    response_model=DetalleNotificacionEspumaResponse
)
def obtener_detalle_notificacion_espuma(
    id_alerta: int,
    db: Session = Depends(get_db)
):
    resultado = (
        db.query(
            AlertaEspuma.id_alerta_espuma,
            AlertaEspuma.notificacion,
            AlertaEspuma.fecha_alerta,
            AlertaEspuma.hora_alerta,
            AlertaEspuma.piscina,
            AlertaEspuma.imagen_capturada,
            AlertaEspuma.id_usuario,
            Usuario.nombre_operario,
            AlertaEspuma.visto,
            AlertaEspuma.latitud,
            AlertaEspuma.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaEspuma.id_usuario)
        .filter(AlertaEspuma.id_alerta_espuma == id_alerta)
        .first()
    )

    if not resultado:
        raise HTTPException(
            status_code=404,
            detail="Notificación no encontrada"
        )

    url_temporal_imagen = generar_url_temporal_s3(resultado.imagen_capturada)

    return {
        "id_alerta_espuma": resultado.id_alerta_espuma,
        "notificacion": resultado.notificacion,
        "fecha_alerta": resultado.fecha_alerta,
        "hora_alerta": resultado.hora_alerta,
        "piscina": resultado.piscina,
        "imagen_capturada": url_temporal_imagen,
        "id_usuario": resultado.id_usuario,
        "nombre_operario": resultado.nombre_operario,
        "visto": resultado.visto,
        "latitud": resultado.latitud,
        "longitud": resultado.longitud
    }


@router.get(
    "/espuma/historial",
    response_model=list[HistorialEspumaResponse]
)
def obtener_historial_espuma(
    db: Session = Depends(get_db)
):
    resultados = (
        db.query(
            AlertaEspuma.id_alerta_espuma,
            AlertaEspuma.notificacion,
            AlertaEspuma.fecha_alerta,
            AlertaEspuma.hora_alerta,
            AlertaEspuma.piscina,
            AlertaEspuma.imagen_capturada,
            AlertaEspuma.id_usuario,
            Usuario.nombre_operario,
            AlertaEspuma.latitud,
            AlertaEspuma.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaEspuma.id_usuario)
        .order_by(
            AlertaEspuma.fecha_alerta.desc(),
            AlertaEspuma.hora_alerta.desc()
        )
        .all()
    )

    return resultados


@router.get(
    "/historial/general",
    response_model=list[HistorialGeneralResponse]
)
def obtener_historial_general(
    db: Session = Depends(get_db)
):
    superficie = (
        db.query(
            AlertaSuperficie.id_alerta_superficie.label("id_alerta"),
            literal("superficie").label("tipo"),
            AlertaSuperficie.notificacion,
            AlertaSuperficie.fecha_alerta,
            AlertaSuperficie.hora_alerta,
            AlertaSuperficie.piscina,
            AlertaSuperficie.imagen_capturada,
            AlertaSuperficie.id_usuario,
            Usuario.nombre_operario,
            AlertaSuperficie.visto,
            AlertaSuperficie.latitud,
            AlertaSuperficie.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaSuperficie.id_usuario)
        .all()
    )

    acumulacion = (
        db.query(
            AlertaAcumulacion.id_alerta_acumulacion.label("id_alerta"),
            literal("acumulacion").label("tipo"),
            AlertaAcumulacion.notificacion,
            AlertaAcumulacion.fecha_alerta,
            AlertaAcumulacion.hora_alerta,
            AlertaAcumulacion.piscina,
            AlertaAcumulacion.imagen_capturada,
            AlertaAcumulacion.id_usuario,
            Usuario.nombre_operario,
            AlertaAcumulacion.visto,
            AlertaAcumulacion.latitud,
            AlertaAcumulacion.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaAcumulacion.id_usuario)
        .all()
    )

    espuma = (
        db.query(
            AlertaEspuma.id_alerta_espuma.label("id_alerta"),
            literal("espuma").label("tipo"),
            AlertaEspuma.notificacion,
            AlertaEspuma.fecha_alerta,
            AlertaEspuma.hora_alerta,
            AlertaEspuma.piscina,
            AlertaEspuma.imagen_capturada,
            AlertaEspuma.id_usuario,
            Usuario.nombre_operario,
            AlertaEspuma.visto,
            AlertaEspuma.latitud,
            AlertaEspuma.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaEspuma.id_usuario)
        .all()
    )

    todos = list(superficie) + list(acumulacion) + list(espuma)

    todos_ordenados = sorted(
        todos,
        key=lambda x: (x.fecha_alerta, x.hora_alerta),
        reverse=True
    )

    return todos_ordenados


@router.get(
    "/pendientes/general",
    response_model=list[HistorialGeneralResponse]
)
def obtener_pendientes_general(
    db: Session = Depends(get_db)
):
    superficie = (
        db.query(
            AlertaSuperficie.id_alerta_superficie.label("id_alerta"),
            literal("superficie").label("tipo"),
            AlertaSuperficie.notificacion,
            AlertaSuperficie.fecha_alerta,
            AlertaSuperficie.hora_alerta,
            AlertaSuperficie.piscina,
            AlertaSuperficie.imagen_capturada,
            AlertaSuperficie.id_usuario,
            Usuario.nombre_operario,
            AlertaSuperficie.visto,
            AlertaSuperficie.latitud,
            AlertaSuperficie.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaSuperficie.id_usuario)
        .filter(AlertaSuperficie.visto == False)
        .all()
    )

    acumulacion = (
        db.query(
            AlertaAcumulacion.id_alerta_acumulacion.label("id_alerta"),
            literal("acumulacion").label("tipo"),
            AlertaAcumulacion.notificacion,
            AlertaAcumulacion.fecha_alerta,
            AlertaAcumulacion.hora_alerta,
            AlertaAcumulacion.piscina,
            AlertaAcumulacion.imagen_capturada,
            AlertaAcumulacion.id_usuario,
            Usuario.nombre_operario,
            AlertaAcumulacion.visto,
            AlertaAcumulacion.latitud,
            AlertaAcumulacion.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaAcumulacion.id_usuario)
        .filter(AlertaAcumulacion.visto == False)
        .all()
    )

    espuma = (
        db.query(
            AlertaEspuma.id_alerta_espuma.label("id_alerta"),
            literal("espuma").label("tipo"),
            AlertaEspuma.notificacion,
            AlertaEspuma.fecha_alerta,
            AlertaEspuma.hora_alerta,
            AlertaEspuma.piscina,
            AlertaEspuma.imagen_capturada,
            AlertaEspuma.id_usuario,
            Usuario.nombre_operario,
            AlertaEspuma.visto,
            AlertaEspuma.latitud,
            AlertaEspuma.longitud
        )
        .join(Usuario, Usuario.id_usuario == AlertaEspuma.id_usuario)
        .filter(AlertaEspuma.visto == False)
        .all()
    )

    todos = list(superficie) + list(acumulacion) + list(espuma)

    todos_ordenados = sorted(
        todos,
        key=lambda x: (x.fecha_alerta, x.hora_alerta),
        reverse=True
    )

    return todos_ordenados

