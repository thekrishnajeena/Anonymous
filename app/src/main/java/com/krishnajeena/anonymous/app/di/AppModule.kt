package com.krishnajeena.anonymous.app.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.krishnajeena.anonymous.data.FirestoreUserRepository
import com.krishnajeena.anonymous.data.auth.FirebaseAuthRepository
import com.krishnajeena.anonymous.data.auth.GoogleSignInManager
import com.krishnajeena.anonymous.data.follow.FollowRepository
import com.krishnajeena.anonymous.data.post.FirestorePostRepository
import com.krishnajeena.anonymous.data.post.LikeRepository
import com.krishnajeena.anonymous.feature_comment.CommentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // -------------------- FIREBASE CORE --------------------

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    // -------------------- AUTH --------------------

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth
    ): FirebaseAuthRepository =
        FirebaseAuthRepository(auth)

    @Provides
    @Singleton
    fun provideGoogleSignInManager(
        @ApplicationContext context: Context
    ): GoogleSignInManager =
        GoogleSignInManager(context)

    // -------------------- USER --------------------

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore
    ): FirestoreUserRepository =
        FirestoreUserRepository(firestore)

    // -------------------- POSTS --------------------

    @Provides
    @Singleton
    fun providePostRepository(
        firestore: FirebaseFirestore
    ): FirestorePostRepository =
        FirestorePostRepository(firestore)

    @Provides
    @Singleton
    fun provideLikeRepository(
        firestore: FirebaseFirestore
    ): LikeRepository =
        LikeRepository(firestore)

    @Provides
    @Singleton
    fun provideCommentRepository(
        firestore: FirebaseFirestore
    ): CommentRepository =
        CommentRepository(firestore)

    // -------------------- FOLLOW --------------------

    @Provides
    @Singleton
    fun provideFollowRepository(
        firestore: FirebaseFirestore
    ): FollowRepository =
        FollowRepository(firestore)
}
