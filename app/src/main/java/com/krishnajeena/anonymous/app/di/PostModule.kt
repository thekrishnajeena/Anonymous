package com.krishnajeena.anonymous.app.di

import com.google.firebase.firestore.FirebaseFirestore
import com.krishnajeena.anonymous.data.post.FirestorePostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PostModule {

    @Provides
    fun providePostRepository(
        firestore: FirebaseFirestore
    ): FirestorePostRepository = FirestorePostRepository(firestore)

}