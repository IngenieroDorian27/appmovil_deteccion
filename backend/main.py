from fastapi import FastAPI
from starlette.middleware.cors import CORSMiddleware

from app.routes.usuario_routes import router as usuario_router
from app.routes.monitoreo_routes import router as monitoreo_router
from app.routes.captura_routes import router as captura_router
from app.routes.notificaciones_routes import router as notificaciones_router
from app.routes.acumulacion_routes import router as acumulacion_router
from app.routes.superficie_routes import router as superficie_router
from app.routes.espuma_routes import router as espuma_router
from app.routes.piscina_routes import router as piscina_router

app = FastAPI(
    title="API Prueba Camaronera",
    description="API conectada a PostgreSQL en AWS",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:4200",
        "http://127.0.0.1:4200"
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def inicio():
    return {
        "mensaje": "API funcionando correctamente",
        "estado": "ok"
    }

app.include_router(usuario_router)
app.include_router(monitoreo_router)
app.include_router(captura_router)
app.include_router(notificaciones_router)
app.include_router(acumulacion_router)
app.include_router(superficie_router)
app.include_router(espuma_router)

app.include_router(piscina_router)