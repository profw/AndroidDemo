package ru.profw.demo.model

data class Repository(
    val id: Long,
    val name: String,
    val owner: Owner,
    val htmlUrl: String,
    val description: String?,
    val stargazersCount: Int,
    val forksCount: Int,
    var isLiked: Boolean = false,
)

data class Owner(
    val login: String,
    val avatarUrl: String,
)