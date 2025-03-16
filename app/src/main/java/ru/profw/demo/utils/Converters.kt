package ru.profw.demo.utils

import android.content.ContentValues
import ru.profw.demo.dao.LikedRepository

fun LikedRepository.toContentValues() =
    ContentValues().apply {
        put("id", id)
        put("name", name)
        put("ownerLogin", ownerLogin)
        put("avatarUrl", avatarUrl)
        put("htmlUrl", htmlUrl)
        put("description", description)
    }


fun ContentValues.toLikedRepository() =
    LikedRepository(
        id = getAsLong("name"),
        name = getAsString("name"),
        ownerLogin = getAsString("ownerLogin"),
        avatarUrl = getAsString("avatarUrl"),
        htmlUrl = getAsString("htmlUrl"),
        description = getAsString("description"),
    )
