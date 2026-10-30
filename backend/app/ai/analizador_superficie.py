from ultralytics import YOLO

modelo_superficie = YOLO("ia_models/superficie_y11n.pt")


def analizar_imagen_superficie(ruta_imagen: str) -> dict:
    resultados = modelo_superficie(ruta_imagen)

    detecciones = []

    for resultado in resultados:
        if resultado.masks is None:
            continue

        for i, mascara in enumerate(resultado.masks):
            id_clase = int(resultado.boxes[i].cls[0])
            confianza = float(resultado.boxes[i].conf[0])
            nombre_objeto = modelo_superficie.names[id_clase]

            detecciones.append({
                "objeto": nombre_objeto,
                "confianza": round(confianza * 100, 2)
            })

    if len(detecciones) == 0:
        notificacion = (
            "La IA analizó la imagen capturada, pero no detectó "
            "cambios en la superficie con el modelo preentrenado utilizado."
        )
    else:
        objetos_detectados = [deteccion["objeto"] for deteccion in detecciones]

        objetos_unicos = list(dict.fromkeys(objetos_detectados))

        texto_objetos = ", ".join(objetos_unicos)

        notificacion = (
            f"La IA analizó la imagen capturada y detectó cambios "
            f"en la superficie: {texto_objetos}."
        )

    return {
        "notificacion": notificacion,
        "detecciones": detecciones
    }