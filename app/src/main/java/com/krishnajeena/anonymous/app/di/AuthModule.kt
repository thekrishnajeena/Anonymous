package com.krishnajeena.anonymous.app.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.krishnajeena.anonymous.data.auth.FirebaseAuthRepository
import com.krishnajeena.anonymous.data.auth.GoogleSignInManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth
    ): FirebaseAuthRepository =
        FirebaseAuthRepository(auth)

    @Provides
    fun provideGoogleSignInManager(
        @ApplicationContext context: Context
    ): GoogleSignInManager =
        GoogleSignInManager(context)
}
