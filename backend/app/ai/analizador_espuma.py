from ultralytics import YOLO


modelo_espumas = YOLO("ia_models/espuma_y8n.pt")


def analizar_imagen_espumas(ruta_imagen: str) -> dict:
    resultados = modelo_espumas(ruta_imagen)

    detecciones = []

    for resultado in resultados:
        for caja in resultado.boxes:
            id_clase = int(caja.cls[0])
            confianza = float(caja.conf[0])
            nombre_objeto = modelo_espumas.names[id_clase]

            detecciones.append({
                "objeto": nombre_objeto,
                "confianza": round(confianza * 100, 2)
            })

    if len(detecciones) == 0:
        notificacion = (
            "La IA analizó la imagen capturada, pero no detectó objetos "
            "con el modelo preentrenado utilizado para esta prueba."
        )
    else:
        objetos_detectados = [deteccion["objeto"] for deteccion in detecciones]

        objetos_unicos = list(dict.fromkeys(objetos_detectados))

        texto_objetos = ", ".join(objetos_unicos)

        notificacion = (
            f"La IA analizó la imagen capturada y detectó los siguientes objetos: "
            f"{texto_objetos}."
        )

    return {
        "notificacion": notificacion,
        "detecciones": detecciones
    }