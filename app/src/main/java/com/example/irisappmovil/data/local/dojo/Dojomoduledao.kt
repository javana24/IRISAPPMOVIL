package com.example.irisappmovil.data.local.dojo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DojoModuleDao {
    @Query("SELECT * FROM dojo_modules ORDER BY `order` ASC")
    fun getAllModules(): Flow<List<DojoModuleEntity>>

    @Query("SELECT * FROM dojo_modules WHERE id = :moduleId")
    suspend fun getModuleById(moduleId: String): DojoModuleEntity?

    @Query("SELECT * FROM dojo_modules WHERE status = 'IN_PROGRESS' LIMIT 1")
    suspend fun getCurrentModule(): DojoModuleEntity?

    @Query("SELECT * FROM dojo_modules ORDER BY `order` ASC")
    suspend fun getAllModulesOnce(): List<DojoModuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: DojoModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<DojoModuleEntity>)

    @Update
    suspend fun updateModule(module: DojoModuleEntity)

    @Query("UPDATE dojo_modules SET status = :status, completedAt = :completedAt WHERE id = :moduleId")
    suspend fun updateModuleStatus(moduleId: String, status: String, completedAt: Long? = null)

    @Query("UPDATE dojo_modules SET status = :status WHERE id = :moduleId")
    suspend fun updateStatus(moduleId: String, status: String)

    @Delete
    suspend fun deleteModule(module: DojoModuleEntity)

    @Query("SELECT COUNT(*) FROM dojo_modules WHERE status = 'COMPLETED'")
    suspend fun getCompletedCount(): Int

    @Query("SELECT COUNT(*) FROM dojo_modules")
    suspend fun getTotalCount(): Int

    @Query("DELETE FROM dojo_modules")
    suspend fun deleteAllModules()
}