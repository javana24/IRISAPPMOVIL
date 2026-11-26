package com.example.irisappmovil.data.firebase

/**
 * Rutas y nombres de campos en Firestore (español).
 *
 * Colección principal: [COLECCION_USUARIOS]/{uid}
 * Subcolecciones bajo cada usuario: progreso del Dojo, chat IA y estado de ánimo.
 */
object FirestoreConstants {
    const val COLECCION_USUARIOS = "usuarios"
    const val SUBCOLECCION_MODULOS_DOJO = "modulos_dojo"
    const val SUBCOLECCION_MENSAJES_IA = "mensajes_ia"
    const val SUBCOLECCION_ENTRADAS_ESTADO_ANIMO = "entradas_estado_animo"

    const val CAMPO_NOMBRE_PERFIL = "nombre_perfil"
    const val CAMPO_CORREO_ELECTRONICO = "correo_electronico"
    const val CAMPO_URL_FOTO_PERFIL = "url_foto_perfil"
    const val CAMPO_RANGO = "rango"
    const val CAMPO_EXPERIENCIA_TOTAL = "experiencia_total"
    const val CAMPO_RACHA_ACTUAL = "racha_actual"
    const val CAMPO_NIVEL = "nivel"
    const val CAMPO_MINUTOS_CALMA_TOTAL = "minutos_calma_total"
    const val CAMPO_ULTIMO_CAMBIO_NOMBRE_PERFIL_MS = "ultimo_cambio_nombre_perfil_ms"
    const val CAMPO_ULTIMO_CAMBIO_CORREO_MS = "ultimo_cambio_correo_ms"
    const val CAMPO_CREADO_EN = "creado_en"
    const val CAMPO_ACTUALIZADO_EN = "actualizado_en"

    const val CAMPO_ID_MODULO = "id_modulo"
    const val CAMPO_TITULO = "titulo"
    const val CAMPO_DESCRIPCION = "descripcion"
    const val CAMPO_TIPO_ICONO = "tipo_icono"
    const val CAMPO_ESTADO = "estado"
    const val CAMPO_ORDEN = "orden"
    const val CAMPO_XP_RECOMPENSA = "xp_recompensa"
    const val CAMPO_COMPLETADO_EN_MS = "completado_en_ms"

    const val CAMPO_ID_CONVERSACION = "id_conversacion"
    const val CAMPO_ROL = "rol"
    const val CAMPO_CONTENIDO = "contenido"
    const val CAMPO_MODO_CHAT = "modo_chat"
    const val CAMPO_ID_MENSAJE_LOCAL = "id_mensaje_local"

    const val CAMPO_ESTADO_ANIMO = "estado_animo"
}
