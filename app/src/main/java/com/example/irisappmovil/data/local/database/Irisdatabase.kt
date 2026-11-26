package com.example.irisappmovil.data.local.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.irisappmovil.data.local.chat.ChatMessageDao
import com.example.irisappmovil.data.local.dojo.DojoModuleDao
import com.example.irisappmovil.data.local.mood.MoodDao
import com.example.irisappmovil.data.local.usuarios.UserDao
import com.example.irisappmovil.data.local.chat.ChatMessageEntity
import com.example.irisappmovil.data.local.dojo.DojoModuleEntity
import com.example.irisappmovil.data.local.mood.MoodEntryEntity
import com.example.irisappmovil.data.local.usuarios.UserEntity

@Database(
    entities = [
        UserEntity::class,
        MoodEntryEntity::class,
        DojoModuleEntity::class,
        ChatMessageEntity::class,
    ],
    // v3: users sin columna password (SCRUM-76). Destructive migration en DatabaseModule.
    version = 3,
    exportSchema = false
)
abstract class IrisDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun moodDao(): MoodDao
    abstract fun dojoModuleDao(): DojoModuleDao
    abstract fun chatMessageDao(): ChatMessageDao
}