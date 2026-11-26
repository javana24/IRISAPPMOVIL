package com.example.irisappmovil.di

import com.example.irisappmovil.data.firebase.FirebaseSessionCoordinator
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FirebaseSyncEntryPoint {
    fun firebaseSessionCoordinator(): FirebaseSessionCoordinator
}
