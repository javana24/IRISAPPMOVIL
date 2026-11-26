package com.example.irisappmovil.data.firebase

import com.example.irisappmovil.data.local.chat.ChatMessageEntity
import com.example.irisappmovil.data.local.dojo.DojoModuleDao
import com.example.irisappmovil.data.local.dojo.DojoModuleDefaults
import com.example.irisappmovil.data.local.dojo.DojoModuleEntity
import com.example.irisappmovil.data.local.mood.MoodEntryEntity
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

        val modulosSnap = ref.collection(FirestoreConstants.SUBCOLECCION_MODULOS_DOJO).get().await()
        if (!modulosSnap.isEmpty) {
            val lista = modulosSnap.documents.mapNotNull { doc ->
                val m = doc.data ?: return@mapNotNull null
                DojoModuleEntity(
                    id = m[FirestoreConstants.CAMPO_ID_MODULO] as? String ?: doc.id,
                    title = m[FirestoreConstants.CAMPO_TITULO] as? String ?: "",
                    description = m[FirestoreConstants.CAMPO_DESCRIPCION] as? String ?: "",
                    iconType = m[FirestoreConstants.CAMPO_TIPO_ICONO] as? String ?: "EYE",
                    status = m[FirestoreConstants.CAMPO_ESTADO] as? String ?: "LOCKED",
                    order = (m[FirestoreConstants.CAMPO_ORDEN] as? Number)?.toInt() ?: 0,
                    xpReward = (m[FirestoreConstants.CAMPO_XP_RECOMPENSA] as? Number)?.toInt() ?: 500,
                    completedAt = (m[FirestoreConstants.CAMPO_COMPLETADO_EN_MS] as? Number)?.toLong()
                )
            }
            if (lista.isNotEmpty()) {
                dojoModuleDao.deleteAllModules()
                dojoModuleDao.insertModules(lista)
            }
        } else {
            dojoModuleDao.deleteAllModules()
            dojoModuleDao.insertModules(DojoModuleDefaults.modulosIniciales())
        }
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

    suspend fun pushMensajeIa(uid: String, mensaje: ChatMessageEntity) {
        val ref = firestore.collection(FirestoreConstants.COLECCION_USUARIOS).document(uid)
            .collection(FirestoreConstants.SUBCOLECCION_MENSAJES_IA).document()
        ref.set(
            mapOf(
                FirestoreConstants.CAMPO_ID_MENSAJE_LOCAL to mensaje.id,
                FirestoreConstants.CAMPO_ID_CONVERSACION to mensaje.conversationId,
                FirestoreConstants.CAMPO_ROL to mensaje.role,
                FirestoreConstants.CAMPO_CONTENIDO to mensaje.content,
                FirestoreConstants.CAMPO_MODO_CHAT to mensaje.chatMode,
                FirestoreConstants.CAMPO_CREADO_EN to mensaje.timestamp
            )
        ).await()
    }

    suspend fun pushEntradaEstadoAnimo(uid: String, entrada: MoodEntryEntity) {
        val ref = firestore.collection(FirestoreConstants.COLECCION_USUARIOS).document(uid)
            .collection(FirestoreConstants.SUBCOLECCION_ENTRADAS_ESTADO_ANIMO).document()
        ref.set(
            mapOf(
                FirestoreConstants.CAMPO_ESTADO_ANIMO to entrada.mood,
                FirestoreConstants.CAMPO_CREADO_EN to entrada.timestamp
            )
        ).await()
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
