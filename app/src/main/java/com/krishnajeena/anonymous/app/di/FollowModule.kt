package com.krishnajeena.anonymous.app.di

import com.google.firebase.firestore.FirebaseFirestore
import com.krishnajeena.anonymous.data.follow.FollowRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FollowModule {

    @Provides
    fun provideFollowRepository(
        firestore: FirebaseFirestore
    ): FollowRepository = FollowRepository(firestore)
}
