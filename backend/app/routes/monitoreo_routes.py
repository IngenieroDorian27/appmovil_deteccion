from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.models.monitoreo_model import Monitoreo
from app.schemas.monitoreo_schema import (CantidadPiscinasResponse, HistorialAcumulacionResponse,
                                          HistorialSuperficieResponse, HistorialEspumaResponse,
                                          TotalMonitoreosResponse, NotificacionesPendientesResponse)


router = APIRouter(
    prefix="/monitoreo",
    tags=["Monitoreo"]
)


@router.get("/cantidad-piscinas", response_model=CantidadPiscinasResponse)
def obtener_cantidad_piscinas(db: Session = Depends(get_db)):
    monitoreo = db.query(Monitoreo).order_by(
        Monitoreo.id_monitoreo.desc()
    ).first()

    if not monitoreo:
        return {
            "cantidad_piscinas": 0
        }

    return {
        "cantidad_piscinas": monitoreo.numero_piscinas
    }


@router.get("/historial-acumulacion", response_model=HistorialAcumulacionResponse)
def obtener_historial_acumulacion(db: Session = Depends(get_db)):
    monitoreo = db.query(Monitoreo).order_by(
        Monitoreo.id_monitoreo.desc()
    ).first()

    if not monitoreo:
        return {
            "acumulacion": 0
        }

    return {
        "acumulacion": monitoreo.acumulacion
    }

@router.get("/historial-superficie", response_model=HistorialSuperficieResponse)
def obtener_historial_superficie(db: Session = Depends(get_db)):
    monitoreo = db.query(Monitoreo).order_by(
        Monitoreo.id_monitoreo.desc()
    ).first()

    if not monitoreo:
        return {
            "superficie": 0
        }

    return {
        "superficie": monitoreo.superficie
    }

@router.get("/historial-espuma", response_model=HistorialEspumaResponse)
def obtener_historial_espuma(db: Session = Depends(get_db)):
    monitoreo = db.query(Monitoreo).order_by(
        Monitoreo.id_monitoreo.desc()
    ).first()

    if not monitoreo:
        return {
            "espuma": 0
        }

    return {
        "espuma": monitoreo.espuma
    }

@router.get("/total-monitoreos", response_model=TotalMonitoreosResponse)
def obtener_total_monitoreos(db: Session = Depends(get_db)):
    monitoreo = db.query(Monitoreo).order_by(
        Monitoreo.id_monitoreo.desc()
    ).first()

    if not monitoreo:
        return {
            "total_monitoreos": 0
        }

    total = monitoreo.acumulacion + monitoreo.superficie + monitoreo.espuma

    return {
        "total_monitoreos": total
    }

@router.get("/notificaciones-pendientes", response_model=NotificacionesPendientesResponse)
def obtener_notificaciones_pendientes(db: Session = Depends(get_db)):
    monitoreo = db.query(Monitoreo).order_by(
        Monitoreo.id_monitoreo.desc()
    ).first()

    if not monitoreo:
        return {
            "notificaciones_pendientes": 0
        }

    return {
        "notificaciones_pendientes": monitoreo.notificaciones_pendientes
    }