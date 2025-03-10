package ru.profw.demo.model

data class Repository(
    val name: String,
    val owner: Owner,
    val htmlUrl: String
)

data class Owner(
    val login: String,
    val avatarUrl: String
)