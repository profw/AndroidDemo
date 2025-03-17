package ru.profw.repofinder

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ru.profw.repofinder.R
import ru.profw.repofinder.adapter.RepoAdapter
import ru.profw.repofinder.databinding.ActivityMainBinding
import ru.profw.repofinder.viewmodel.RepoViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: RepoViewModel by viewModels()
    private lateinit var adapter: RepoAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        adapter = RepoAdapter(
            onLikeClick = { repo ->
                viewModel.toggleLike(repo) // Обновляем состояние в ViewModel
            },
            onItemClick = {
                // TODO: Добавить логику для формы с деталями
            }
        )
        binding.reposRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reposRecyclerView.adapter = adapter

        binding.searchButton.setOnClickListener {
            search()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        viewModel.repositories.observe(this) { repositories ->
            //viewModel.loadLikedRepositories(repositories)
            adapter.repositories = repositories
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun search() {
        val query = binding.searchEditText.text.toString()
        if (query.isEmpty()) {
            return
        }
        viewModel.searchRepositories(query)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
}