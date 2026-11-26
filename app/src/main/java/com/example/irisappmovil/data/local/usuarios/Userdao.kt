package com.example.irisappmovil.data.local.usuarios


import androidx.room.*
import com.example.irisappmovil.data.local.usuarios.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isActive = 1 LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET currentStreak = :streak WHERE id = :userId")
    suspend fun updateStreak(userId: String, streak: Int)

    @Query("UPDATE users SET totalXp = totalXp + :xp WHERE id = :userId")
    suspend fun addXp(userId: String, xp: Int)

    @Query("UPDATE users SET totalMinutesCalm = totalMinutesCalm + :minutes WHERE id = :userId")
    suspend fun addCalmMinutes(userId: String, minutes: Int)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("UPDATE users SET isActive = 0")
    suspend fun deactivateAll()

    @Query("UPDATE users SET isActive = 1 WHERE id = :userId")
    suspend fun activateById(userId: String)
}