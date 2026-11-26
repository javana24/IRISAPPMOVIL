package com.example.irisappmovil.data.local.dojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dojo_modules")
data class DojoModuleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconType: String,
    val status: String,
    val order: Int,
    val xpReward: Int = 500,
    val completedAt: Long? = null
)

enum class DojoModuleStatus(val raw: String) {
    COMPLETED("COMPLETED"),
    IN_PROGRESS("IN_PROGRESS"),
    LOCKED("LOCKED");

    companion object {
        fun fromRaw(value: String): DojoModuleStatus {
            return entries.firstOrNull { it.raw == value } ?: LOCKED
        }
    }
}

fun DojoModuleEntity.statusEnum(): DojoModuleStatus = DojoModuleStatus.fromRaw(status)
fun DojoModuleEntity.isCompleted(): Boolean = statusEnum() == DojoModuleStatus.COMPLETED
fun DojoModuleEntity.isInProgress(): Boolean = statusEnum() == DojoModuleStatus.IN_PROGRESS
fun DojoModuleEntity.isLocked(): Boolean = statusEnum() == DojoModuleStatus.LOCKED