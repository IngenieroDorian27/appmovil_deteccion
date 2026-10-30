import uuid
import boto3
from app.core.config import settings
from urllib.parse import urlparse, unquote


s3_client = boto3.client(
    "s3",
    region_name=settings.AWS_REGION,
    aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
    aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY
)


def subir_imagen_s3(archivo, nombre_original: str) -> str:
    extension = nombre_original.split(".")[-1]

    nombre_unico = f"{uuid.uuid4()}.{extension}"

    ruta_s3 = f"{settings.AWS_S3_FOLDER}/{nombre_unico}"

    s3_client.upload_fileobj(
        archivo,
        settings.AWS_BUCKET_NAME,
        ruta_s3,
        ExtraArgs={
            "ContentType": "image/jpeg"
        }
    )

    url_imagen = (
        f"https://{settings.AWS_BUCKET_NAME}.s3."
        f"{settings.AWS_REGION}.amazonaws.com/{ruta_s3}"
    )

    return url_imagen

def generar_url_temporal_s3(url_imagen: str, expiracion: int = 3600) -> str:
    """
    Genera una URL temporal firmada para poder visualizar una imagen privada de S3.
    La URL dura por defecto 3600 segundos, es decir, 1 hora.
    """

    url_parseada = urlparse(url_imagen)

    if url_parseada.netloc:
        ruta_s3 = unquote(url_parseada.path.lstrip("/"))
    else:
        ruta_s3 = url_imagen

    url_temporal = s3_client.generate_presigned_url(
        "get_object",
        Params={
            "Bucket": settings.AWS_BUCKET_NAME,
            "Key": ruta_s3
        },
        ExpiresIn=expiracion
    )

    return url_temporal
