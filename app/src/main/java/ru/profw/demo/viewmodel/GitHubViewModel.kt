package ru.profw.demo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.profw.demo.dao.AppDatabase
import ru.profw.demo.dao.LikedRepository
import ru.profw.demo.model.Repository
import ru.profw.demo.model.github.GitHubApiService

class GitHubViewModel(application: Application) : AndroidViewModel(application) {
    val repositories = MutableLiveData<List<Repository>>()
    private val database = AppDatabase.getDatabase(application)
    private val likedRepositoryDao = database.likedRepositoryDao()

    val cacheInterceptor = Interceptor { chain ->
        val originalResponse = chain.proceed(chain.request())
        originalResponse.newBuilder()
            .header("Cache-Control", "public, max-age=360")
            .build()
    }

    val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(cacheInterceptor)
        .build()

    val gson: Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val apiService = retrofit.create(GitHubApiService::class.java)

    fun searchRepositories(query: String) = viewModelScope.launch {
        try {
            val response = apiService.searchRepos(query)
            if (response.isSuccessful) {
                val repos = response.body()?.items
                if (!repos.isNullOrEmpty()) {
                    loadLikedRepositories(repos)
                } else {
                    repositories.value = emptyList()
                }
            } else {
                Log.i(TAG, "Query $query is not successful")
            }
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
    }


    fun toggleLike(repo: Repository) = viewModelScope.launch {
        if (repo.isLiked) {
            // Удаляем из базы данных, если лайк убран
            val likedRepo = LikedRepository(
                id = repo.id,
                name = repo.name,
                ownerLogin = repo.owner.login,
                avatarUrl = repo.owner.avatarUrl,
                htmlUrl = repo.htmlUrl,
                description = repo.description,
            )
            likedRepositoryDao.delete(likedRepo)
        } else {
            // Добавляем в базу данных, если лайк поставлен
            val likedRepo = LikedRepository(
                id = repo.id,
                name = repo.name,
                ownerLogin = repo.owner.login,
                avatarUrl = repo.owner.avatarUrl,
                htmlUrl = repo.htmlUrl,
                description = repo.description,
            )
            likedRepositoryDao.insert(likedRepo)
        }
        val updatedList = repositories.value?.map {
            if (it == repo) it.copy(isLiked = !it.isLiked) else it
        }
        repositories.value = updatedList ?: emptyList()
    }

    fun loadLikedRepositories(reposFromApi: List<Repository>) = viewModelScope.launch {
        // Получаем все лайкнутые репозитории из базы данных
        val likedRepos = likedRepositoryDao.getAll()

        // Обновляем состояние isLiked для каждого репозитория
        val updatedRepos = reposFromApi.map { repo ->
            val isLiked = likedRepos.any { it.id == repo.id }
            repo.copy(isLiked = isLiked)
        }
        repositories.value = updatedRepos
    }

    companion object {
        private const val TAG = "GitHubViewModel"
    }
}