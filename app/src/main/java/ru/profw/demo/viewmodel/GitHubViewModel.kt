package ru.profw.demo.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.profw.demo.model.Repository
import ru.profw.demo.model.github.GitHubApiService

class GitHubViewModel : ViewModel() {
    private val _repositories = MutableLiveData<List<Repository>>()
    val repositories get() = _repositories

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

    fun searchRepositories(query: String) {
        viewModelScope.launch {
            try {
                val response = apiService.searchRepos(query)
                if (response.isSuccessful) {
                    _repositories.value = response.body()?.items
                } else {
                    Log.i(TAG, "Query $query is not successful")
                }
            }catch (e: Exception) {
                Log.w(TAG, e)
            }
        }
    }

    companion object {
        private const val TAG = "GitHubViewModel"
    }
}