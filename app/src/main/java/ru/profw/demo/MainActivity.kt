package ru.profw.demo

import ru.profw.demo.viewmodel.GitHubViewModel
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ru.profw.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewModel: GitHubViewModel by viewModels()
    private lateinit var adapter: RepoAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        adapter = RepoAdapter()
        binding.reposRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reposRecyclerView.adapter = adapter

        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isEmpty()) {
                return@setOnClickListener
            }
            viewModel.searchRepositories(query)
        }

        viewModel.repositories.observe(this) { repositories ->
            adapter.repositories = repositories
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}