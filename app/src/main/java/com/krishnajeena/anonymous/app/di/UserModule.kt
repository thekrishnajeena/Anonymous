package com.krishnajeena.anonymous.app.di

import com.google.firebase.firestore.FirebaseFirestore
import com.krishnajeena.anonymous.data.FirestoreUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    fun provideFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    fun provideUserRepository(
        firestore: FirebaseFirestore
    ): FirestoreUserRepository =
        FirestoreUserRepository(firestore)
}