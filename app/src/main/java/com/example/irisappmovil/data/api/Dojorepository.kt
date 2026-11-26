package com.example.irisappmovil.data.api


import com.example.irisappmovil.data.firebase.FirestoreUserSyncRepository
import com.example.irisappmovil.data.local.dojo.DojoModuleDao
import com.example.irisappmovil.data.local.dojo.DojoModuleDefaults
import com.example.irisappmovil.data.local.dojo.DojoModuleEntity
import com.example.irisappmovil.data.local.dojo.DojoModuleStatus
import com.example.irisappmovil.data.local.dojo.isLocked
import com.example.irisappmovil.data.local.usuarios.UserDao
import com.example.irisappmovil.domain.LevelSystem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DojoRepository @Inject constructor(
    private val dojoModuleDao: DojoModuleDao,
    private val userDao: UserDao,
    private val apiService: IrisApiService,
    private val firebaseAuth: FirebaseAuth,
    private val firestoreSync: FirestoreUserSyncRepository
) {
    fun getAllModules(): Flow<List<DojoModuleEntity>> = dojoModuleDao.getAllModules()

    /** @return true si el backend respondió y se actualizó la lista; false si se usaron datos locales / por defecto. */
    suspend fun refreshModules(): Boolean {
        return try {
            val response = fetchDojoModulesWithFallback()
            if (response != null && response.isSuccessful && response.body()?.success == true) {
                val existingMap = dojoModuleDao.getAllModulesOnce().associateBy { it.id }
                val modules = response.body()!!.data!!.map { dto ->
                    val existing = existingMap[dto.id]
                    DojoModuleEntity(
                        id = dto.id,
                        title = dto.title,
                        description = dto.description,
                        iconType = dto.iconType,
                        status = existing?.status ?: dto.status,
                        completedAt = existing?.completedAt,
                        order = dto.order,
                        xpReward = dto.xpReward
                    )
                }
                dojoModuleDao.insertModules(modules)
                true
            } else {
                ensureDefaultModules()
                false
            }
        } catch (e: Exception) {
            ensureDefaultModules()
            false
        }
    }

    private suspend fun fetchDojoModulesWithFallback(): Response<ApiResponse<List<DojoModuleDto>>>? {
        val primary = apiService.getDojoModules()
        if (primary.isSuccessful && primary.body()?.success == true) return primary
        val secondary = apiService.getDojoModulesUnderApi()
        if (secondary.isSuccessful && secondary.body()?.success == true) return secondary
        return primary
    }

    suspend fun ensureDefaultModules() {
        val count = dojoModuleDao.getTotalCount()
        if (count == 0) {
            dojoModuleDao.insertModules(DojoModuleDefaults.modulosIniciales())
        }
    }

    suspend fun completeModule(moduleId: String, score: Int): Result<Int> {
        return try {
            if (score < 70) {
                return Result.failure(IllegalStateException("Puntuación insuficiente para completar el módulo"))
            }
            val module = dojoModuleDao.getModuleById(moduleId)
                ?: return Result.failure(IllegalStateException("Módulo no encontrado"))
            dojoModuleDao.updateModuleStatus(moduleId, DojoModuleStatus.COMPLETED.raw, System.currentTimeMillis())
            unlockNextModule(moduleId)
            val xpGained = grantExperience(module, score)
            firebaseAuth.currentUser?.uid?.let { uid ->
                userDao.getCurrentUser().first()?.let { u ->
                    firestoreSync.pushUsuario(uid, u)
                }
                dojoModuleDao.getAllModulesOnce().forEach { m ->
                    firestoreSync.pushModuloDojo(uid, m)
                }
            }
            Result.success(xpGained)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProgress(): Float {
        val completed = dojoModuleDao.getCompletedCount()
        val total = dojoModuleDao.getTotalCount()
        return if (total == 0) 0f else completed.toFloat() / total.toFloat()
    }

    private suspend fun unlockNextModule(completedModuleId: String) {
        val all = dojoModuleDao.getAllModulesOnce()
        val completedIndex = all.indexOfFirst { it.id == completedModuleId }
        if (completedIndex == -1) return

        val next = all.getOrNull(completedIndex + 1) ?: return
        if (next.isLocked()) {
            dojoModuleDao.updateStatus(next.id, DojoModuleStatus.IN_PROGRESS.raw)
        }
    }

    private suspend fun grantExperience(module: DojoModuleEntity, score: Int): Int {
        val user = userDao.getCurrentUser().first() ?: return 0
        val difficultyMultiplier = when (module.order) {
            1 -> 1.0f
            2 -> 1.2f
            3 -> 1.35f
            4 -> 1.5f
            else -> 1.7f
        }
        val performanceMultiplier = 0.8f + (score / 100f) * 0.7f
        val xpGained = (module.xpReward * difficultyMultiplier * performanceMultiplier).toInt().coerceAtLeast(50)
        val totalXp = user.totalXp + xpGained
        val level = LevelSystem.levelForXp(totalXp)
        val rank = LevelSystem.rankForLevel(level)
        val calmMinutesReward = 5
        val newCalmMinutes = user.totalMinutesCalm + calmMinutesReward

        val updatedUser = user.copy(
            totalXp = totalXp,
            level = level,
            rank = rank,
            totalMinutesCalm = newCalmMinutes
        )
        userDao.updateUser(updatedUser)

        val uid = firebaseAuth.currentUser?.uid
        if (uid != null && uid == user.id) {
            firestoreSync.pushUsuario(uid, updatedUser)
        }

        return xpGained
    }
}