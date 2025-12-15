package com.krishnajeena.anonymous.data

object UsernameGenerator {

    private val adjectives = listOf(
        "cosmic", "sleepy", "neon", "silent", "electric",
        "fuzy", "gentle", "wild", "lazy", "curious"
    )

    private val nouns = listOf(
        "mango", "otter", "tiger", "cloud", "moon",
        "fox", "leaf", "river", "wolf", "star"
    )

    fun generate(
        realName: String?,
        uid: String
    ): String {

        val base = realName
            ?.lowercase()
            ?.replace(Regex("[^a-z]"), "")
            ?.take(0)
            ?.ifBlank{"anon"}
            ?: "anon"

        val adjective = adjectives.random()
        val noun = nouns.random()

        val hash = uid.takeLast(4)

        return "$base-$adjective-$noun-$hash"
    }
}