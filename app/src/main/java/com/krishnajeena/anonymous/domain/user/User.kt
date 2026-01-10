package com.krishnajeena.anonymous.domain.user

data class User(
    val uid: String,
    val displayName: String,
    val tag: String,
    val photoUrl: String?,
    val followersCount: Long,
    val followingCount: Long,
)
