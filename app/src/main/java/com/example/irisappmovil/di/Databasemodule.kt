package com.example.irisappmovil.di


import android.content.Context
import androidx.room.Room
import com.example.irisappmovil.data.local.database.IrisDatabase
import com.example.irisappmovil.data.local.chat.ChatMessageDao
import com.example.irisappmovil.data.local.dojo.DojoModuleDao
import com.example.irisappmovil.data.local.mood.MoodDao
import com.example.irisappmovil.data.local.usuarios.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): IrisDatabase {
        return Room.databaseBuilder(
            context,
            IrisDatabase::class.java,
            "iris_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(db: IrisDatabase): UserDao = db.userDao()

    @Provides
    fun provideMoodDao(db: IrisDatabase): MoodDao = db.moodDao()

    @Provides
    fun provideDojoModuleDao(db: IrisDatabase): DojoModuleDao = db.dojoModuleDao()

    @Provides
    fun provideChatMessageDao(db: IrisDatabase): ChatMessageDao = db.chatMessageDao()
}