package ru.profw.repofinder.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.room.Room
import ru.profw.repofinder.dao.AppDatabase

class LikedRepoContentProvider : ContentProvider() {

    private lateinit var db: AppDatabase
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    companion object {
        const val AUTHORITY = "ru.profw.repofinder.provider"
        val CONTENT_URI: Uri = "content://$AUTHORITY/liked_repositories".toUri()
        const val LIKED_REPOSITORIES = 1
        const val LIKED_REPOSITORY_ID = 2
    }

    override fun onCreate(): Boolean {
        db = Room.databaseBuilder(
            context!!.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()

        uriMatcher.addURI(AUTHORITY, "liked_repositories", LIKED_REPOSITORIES)
        uriMatcher.addURI(AUTHORITY, "liked_repositories/#", LIKED_REPOSITORY_ID)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d("GitHubSearchProvider", "Query received: $uri")
        return when (uriMatcher.match(uri)) {
            LIKED_REPOSITORIES -> {
                db.likedRepositoryDao().getAllCursor()
            }

            LIKED_REPOSITORY_ID -> {
                val id = uri.lastPathSegment?.toLongOrNull()
                db.likedRepositoryDao().getByIdCursor(id)
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            LIKED_REPOSITORIES -> "vnd.android.cursor.dir/vnd.ru.profw.repofinder.liked_repositories"
            LIKED_REPOSITORY_ID -> "vnd.android.cursor.item/vnd.ru.profw.repofinder.liked_repositories"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = db.likedRepositoryDao().insertFromContentValues(values)
        return Uri.withAppendedPath(CONTENT_URI, id.toString())
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return when (uriMatcher.match(uri)) {
            LIKED_REPOSITORY_ID -> {
                val id = uri.lastPathSegment?.toLongOrNull()
                db.likedRepositoryDao().deleteById(id)
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return when (uriMatcher.match(uri)) {
            LIKED_REPOSITORY_ID -> {
                val id = uri.lastPathSegment?.toLongOrNull()
                db.likedRepositoryDao().updateFromContentValues(id, values)
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}