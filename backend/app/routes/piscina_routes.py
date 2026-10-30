from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.database.connection import get_db
from app.models.piscina_model import Piscina, UsuarioPiscina
from app.schemas.piscina_schema import (
    RegistrarPiscinaRequest,
    ActualizarUbicacionRequest,
    PiscinaResponse
)
from typing import List, Optional

router = APIRouter(
    prefix="/piscinas",
    tags=["Piscinas"]
)


@router.get("/listar", response_model=List[PiscinaResponse])
def obtener_piscinas(id_usuario: Optional[int] = None, db: Session = Depends(get_db)):
    try:
        if id_usuario is not None:
            ids_asignadas = db.query(UsuarioPiscina.id_piscina).filter(
                UsuarioPiscina.id_usuario == id_usuario
            ).subquery()
            piscinas = db.query(Piscina).filter(
                Piscina.id_piscina.in_(ids_asignadas)
            ).all()
        else:
            piscinas = db.query(Piscina).all()

        resultado = []
        for p in piscinas:
            resultado.append({
                "id_piscina": p.id_piscina,
                "nombre_piscina": p.nombre_piscina,
                "latitud": float(p.latitud) if p.latitud is not None else None,
                "longitud": float(p.longitud) if p.longitud is not None else None,
                "ubicacion_piscina": "Registrado" if p.latitud is not None and p.longitud is not None else "Sin registrar"
            })
        return resultado
    except Exception as error:
        raise HTTPException(
            status_code=500,
            detail=f"Error al obtener piscinas: {str(error)}"
        )


@router.post("/registrar", response_model=PiscinaResponse)
def registrar_piscina(
    datos: RegistrarPiscinaRequest,
    db: Session = Depends(get_db)
):
    try:
        nueva_piscina = Piscina(
            nombre_piscina=datos.nombre_piscina.strip().capitalize()
        )
        db.add(nueva_piscina)
        db.commit()
        db.refresh(nueva_piscina)

        return {
            "id_piscina": nueva_piscina.id_piscina,
            "nombre_piscina": nueva_piscina.nombre_piscina,
            "latitud": None,
            "longitud": None,
            "ubicacion_piscina": "Sin registrar"
        }
    except Exception as error:
        db.rollback()
        raise HTTPException(
            status_code=500,
            detail=f"Error al registrar piscina: {str(error)}"
        )


@router.put("/actualizar-ubicacion/{id_piscina}", response_model=PiscinaResponse)
def actualizar_ubicacion(
    id_piscina: int,
    datos: ActualizarUbicacionRequest,
    db: Session = Depends(get_db)
):
    try:
        piscina = db.query(Piscina).filter(
            Piscina.id_piscina == id_piscina
        ).first()

        if not piscina:
            raise HTTPException(
                status_code=404,
                detail="Piscina no encontrada"
            )

        piscina.latitud = datos.latitud
        piscina.longitud = datos.longitud
        db.commit()
        db.refresh(piscina)

        return {
            "id_piscina": piscina.id_piscina,
            "nombre_piscina": piscina.nombre_piscina,
            "latitud": float(piscina.latitud),
            "longitud": float(piscina.longitud),
            "ubicacion_piscina": "Registrado"
        }
    except HTTPException:
        raise
    except Exception as error:
        db.rollback()
        raise HTTPException(
            status_code=500,
            detail=f"Error al actualizar ubicación: {str(error)}"
        )

@router.delete("/eliminar/{id_piscina}")
def eliminar_piscina(
    id_piscina: int,
    db: Session = Depends(get_db)
):
    try:
        piscina = db.query(Piscina).filter(
            Piscina.id_piscina == id_piscina
        ).first()

        if not piscina:
            raise HTTPException(
                status_code=404,
                detail="Piscina no encontrada"
            )

        db.delete(piscina)
        db.commit()

        return {"mensaje": "Piscina eliminada correctamente"}

    except HTTPException:
        raise
    except Exception as error:
        db.rollback()
        raise HTTPException(
            status_code=500,
            detail=f"Error al eliminar piscina: {str(error)}"
        )