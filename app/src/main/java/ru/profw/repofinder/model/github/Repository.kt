package ru.profw.repofinder.model.github

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.profw.repofinder.model.Repository

interface GitHubApiService {
    @GET("search/repositories")
    suspend fun searchRepos(@Query("q") query: String): Response<GitHubResponse>
}

data class GitHubResponse(
    val items: List<Repository>
)