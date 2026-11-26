package com.example.irisappmovil.data.local.dojo

object DojoModuleDefaults {
    fun modulosIniciales(): List<DojoModuleEntity> = listOf(
        DojoModuleEntity("mod_01", "Reconociendo la Manipulación", "Identifica señales de alerta en entornos sociales y digitales.", "EYE", DojoModuleStatus.COMPLETED.raw, 1, 500),
        DojoModuleEntity("mod_02", "Límites Saludables", "Técnicas prácticas para establecer y mantener tus espacios.", "HAND", DojoModuleStatus.IN_PROGRESS.raw, 2, 750),
        DojoModuleEntity("mod_03", "Comunicación Asertiva", "El poder de tu voz en situaciones críticas.", "MEGAPHONE", DojoModuleStatus.LOCKED.raw, 3, 1000),
        DojoModuleEntity("mod_04", "Espacio Seguro", "Crea y protege tu entorno de seguridad personal.", "SHIELD", DojoModuleStatus.LOCKED.raw, 4, 1250),
    )
}
