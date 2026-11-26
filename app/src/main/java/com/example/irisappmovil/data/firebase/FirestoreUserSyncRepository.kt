package com.example.irisappmovil.data.firebase

import com.example.irisappmovil.data.local.dojo.DojoModuleDao
import com.example.irisappmovil.data.local.dojo.DojoModuleDefaults
import com.example.irisappmovil.data.local.dojo.DojoModuleEntity
import com.example.irisappmovil.data.local.dojo.DojoModuleStatus
import com.example.irisappmovil.data.local.usuarios.UserDao
import com.example.irisappmovil.data.local.usuarios.UserEntity
import com.example.irisappmovil.domain.LevelSystem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class FirestoreUserSyncRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
    private val dojoModuleDao: DojoModuleDao
) {

    suspend fun pullAndMergeLocal(uid: String, firebaseUser: FirebaseUser) {
        val ref = firestore.collection(FirestoreConstants.COLECCION_USUARIOS).document(uid)
        val snap = ref.get().await()
        val entity = if (!snap.exists()) {
            val nuevo = UserEntity(
                id = uid,
                name = firebaseUser.displayName?.takeIf { it.isNotBlank() }
                    ?: (firebaseUser.email?.substringBefore("@") ?: "Usuario"),
                email = firebaseUser.email.orEmpty(),
                avatarUrl = firebaseUser.photoUrl?.toString(),
                rank = LevelSystem.rankForLevel(1),
                totalXp = 0,
                currentStreak = 0,
                totalMinutesCalm = 0,
                level = 1,
                isActive = true
            )
            ref.set(usuarioAMap(nuevo), SetOptions.merge()).await()
            nuevo
        } else {
            val d = snap.data ?: emptyMap()
            UserEntity(
                id = uid,
                name = d[FirestoreConstants.CAMPO_NOMBRE_PERFIL] as? String
                    ?: firebaseUser.displayName.orEmpty().ifBlank { "Usuario" },
                email = d[FirestoreConstants.CAMPO_CORREO_ELECTRONICO] as? String
                    ?: firebaseUser.email.orEmpty(),
                avatarUrl = d[FirestoreConstants.CAMPO_URL_FOTO_PERFIL] as? String
                    ?: firebaseUser.photoUrl?.toString(),
                rank = d[FirestoreConstants.CAMPO_RANGO] as? String ?: LevelSystem.rankForLevel(1),
                totalXp = (d[FirestoreConstants.CAMPO_EXPERIENCIA_TOTAL] as? Number)?.toInt() ?: 0,
                currentStreak = (d[FirestoreConstants.CAMPO_RACHA_ACTUAL] as? Number)?.toInt() ?: 0,
                totalMinutesCalm = (d[FirestoreConstants.CAMPO_MINUTOS_CALMA_TOTAL] as? Number)?.toInt() ?: 0,
                level = (d[FirestoreConstants.CAMPO_NIVEL] as? Number)?.toInt() ?: 1,
                isActive = true
            )
        }
        userDao.deactivateAll()
        userDao.insertUser(entity)

        // Progreso del Dojo compartido con la web: se parte de los 4 módulos por defecto y se
        // aplica encima lo guardado en la nube. La web antigua usaba ids white/yellow/purple/black.
        val modulosSnap = ref.collection(FirestoreConstants.SUBCOLECCION_MODULOS_DOJO).get().await()
        val completados = modulosSnap.documents.mapNotNull { doc ->
            val m = doc.data ?: return@mapNotNull null
            val estado = m[FirestoreConstants.CAMPO_ESTADO] as? String
            if (estado != DojoModuleStatus.COMPLETED.raw) return@mapNotNull null
            val id = normalizarIdModulo(m[FirestoreConstants.CAMPO_ID_MODULO] as? String ?: doc.id)
                ?: return@mapNotNull null
            id to (m[FirestoreConstants.CAMPO_COMPLETADO_EN_MS] as? Number)?.toLong()
        }.toMap()

        val base = DojoModuleDefaults.modulosIniciales().sortedBy { it.order }
        var siguienteDesbloqueado = false
        val lista = base.map { modulo ->
            when {
                completados.containsKey(modulo.id) ->
                    modulo.copy(status = DojoModuleStatus.COMPLETED.raw, completedAt = completados[modulo.id])
                !siguienteDesbloqueado -> {
                    siguienteDesbloqueado = true
                    modulo.copy(status = DojoModuleStatus.IN_PROGRESS.raw, completedAt = null)
                }
                else -> modulo.copy(status = DojoModuleStatus.LOCKED.raw, completedAt = null)
            }
        }
        dojoModuleDao.deleteAllModules()
        dojoModuleDao.insertModules(lista)

        // Limpia chats o estados de ánimo subidos por versiones antiguas de la app.
        runCatching { purgeSensitiveCloudData(uid) }
    }

    suspend fun pushUsuario(uid: String, user: UserEntity) {
        val ref = firestore.collection(FirestoreConstants.COLECCION_USUARIOS).document(uid)
        ref.set(usuarioAMap(user), SetOptions.merge()).await()
    }

    suspend fun fetchUsuarioCamposPerfil(uid: String): PerfilFirestoreCampos? {
        val snap = firestore.collection(FirestoreConstants.COLECCION_USUARIOS).document(uid).get().await()
        if (!snap.exists()) return null
        val d = snap.data ?: return null
        return PerfilFirestoreCampos(
            ultimoCambioNombreMs = (d[FirestoreConstants.CAMPO_ULTIMO_CAMBIO_NOMBRE_PERFIL_MS] as? Number)?.toLong(),
            ultimoCambioCorreoMs = (d[FirestoreConstants.CAMPO_ULTIMO_CAMBIO_CORREO_MS] as? Number)?.toLong()
        )
    }

    suspend fun marcarCambioNombre(uid: String, epochMs: Long) {
        firestore.collection(FirestoreConstants.COLECCION_USUARIOS).document(uid)
            .set(
                mapOf(FirestoreConstants.CAMPO_ULTIMO_CAMBIO_NOMBRE_PERFIL_MS to epochMs),
                SetOptions.merge()
            ).await()
    }

    suspend fun marcarCambioCorreo(uid: String, epochMs: Long) {
        firestore.collection(FirestoreConstants.COLECCION_USUARIOS).document(uid)
            .set(
                mapOf(FirestoreConstants.CAMPO_ULTIMO_CAMBIO_CORREO_MS to epochMs),
                SetOptions.merge()
            ).await()
    }

    suspend fun pushModuloDojo(uid: String, modulo: DojoModuleEntity) {
        val ref = firestore.collection(FirestoreConstants.COLECCION_USUARIOS).document(uid)
            .collection(FirestoreConstants.SUBCOLECCION_MODULOS_DOJO).document(modulo.id)
        ref.set(
            mapOf(
                FirestoreConstants.CAMPO_ID_MODULO to modulo.id,
                FirestoreConstants.CAMPO_TITULO to modulo.title,
                FirestoreConstants.CAMPO_DESCRIPCION to modulo.description,
                FirestoreConstants.CAMPO_TIPO_ICONO to modulo.iconType,
                FirestoreConstants.CAMPO_ESTADO to modulo.status,
                FirestoreConstants.CAMPO_ORDEN to modulo.order,
                FirestoreConstants.CAMPO_XP_RECOMPENSA to modulo.xpReward,
                FirestoreConstants.CAMPO_COMPLETADO_EN_MS to modulo.completedAt,
                FirestoreConstants.CAMPO_ACTUALIZADO_EN to Timestamp.now()
            ),
            SetOptions.merge()
        ).await()
    }

    /**
     * Privacidad: las conversaciones con la IA y el estado de ánimo ya NO se suben a la nube.
     * Versiones anteriores de la app sí los guardaban; aquí se borran los que queden.
     */
    suspend fun purgeSensitiveCloudData(uid: String) {
        val ref = firestore.collection(FirestoreConstants.COLECCION_USUARIOS).document(uid)
        listOf(
            FirestoreConstants.SUBCOLECCION_MENSAJES_IA,
            FirestoreConstants.SUBCOLECCION_ENTRADAS_ESTADO_ANIMO
        ).forEach { name -> deleteCollection(ref.collection(name)) }
    }

    /** Borra todos los datos de la persona en Firestore (al eliminar la cuenta). */
    suspend fun deleteAllUserData(uid: String) {
        val ref = firestore.collection(FirestoreConstants.COLECCION_USUARIOS).document(uid)
        listOf(
            FirestoreConstants.SUBCOLECCION_MODULOS_DOJO,
            FirestoreConstants.SUBCOLECCION_MENSAJES_IA,
            FirestoreConstants.SUBCOLECCION_ENTRADAS_ESTADO_ANIMO
        ).forEach { name -> deleteCollection(ref.collection(name)) }
        ref.delete().await()
    }

    /** mod_01…mod_04, o los ids antiguos de la web (white, yellow, purple, black). */
    private fun normalizarIdModulo(id: String): String? = when (id) {
        "mod_01", "white" -> "mod_01"
        "mod_02", "yellow" -> "mod_02"
        "mod_03", "purple" -> "mod_03"
        "mod_04", "black" -> "mod_04"
        else -> null
    }

    private suspend fun deleteCollection(collection: com.google.firebase.firestore.CollectionReference) {
        while (true) {
            val batchDocs = collection.limit(200).get().await()
            if (batchDocs.isEmpty) return
            val batch = firestore.batch()
            batchDocs.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
    }

    private fun usuarioAMap(user: UserEntity): Map<String, Any?> = mapOf(
        FirestoreConstants.CAMPO_NOMBRE_PERFIL to user.name,
        FirestoreConstants.CAMPO_CORREO_ELECTRONICO to user.email,
        FirestoreConstants.CAMPO_URL_FOTO_PERFIL to user.avatarUrl,
        FirestoreConstants.CAMPO_RANGO to user.rank,
        FirestoreConstants.CAMPO_EXPERIENCIA_TOTAL to user.totalXp,
        FirestoreConstants.CAMPO_RACHA_ACTUAL to user.currentStreak,
        FirestoreConstants.CAMPO_NIVEL to user.level,
        FirestoreConstants.CAMPO_MINUTOS_CALMA_TOTAL to user.totalMinutesCalm,
        FirestoreConstants.CAMPO_ACTUALIZADO_EN to Timestamp.now()
    )
}

data class PerfilFirestoreCampos(
    val ultimoCambioNombreMs: Long?,
    val ultimoCambioCorreoMs: Long?
)
