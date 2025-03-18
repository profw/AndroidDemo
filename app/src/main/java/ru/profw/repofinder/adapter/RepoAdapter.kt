package ru.profw.repofinder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import ru.profw.repofinder.R
import ru.profw.repofinder.model.Repository

class RepoAdapter(
    private val onItemClick: (Repository) -> Unit,
    private val onLikeClick: (Repository) -> Unit,
) : RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {

    var repositories: List<Repository> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.repoName)
        val owner: TextView = itemView.findViewById(R.id.repoOwner)
        val avatar: ImageView = itemView.findViewById(R.id.repoAvatar)
        val url: TextView = itemView.findViewById(R.id.repoUrl)
        val likedSign: ImageView = itemView.findViewById(R.id.likedImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false)
        return RepoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = repositories[position]

        with(holder) {
            name.text = repo.name
            owner.text = repo.owner.login
            url.text = repo.htmlUrl

            avatar.load(repo.owner.avatarUrl)

            likedSign.setImageResource(
                if (repo.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like
            )

            likedSign.setOnClickListener {
                onLikeClick(repo)
                notifyItemChanged(position) // Обновляем UI
            }

            itemView.setOnClickListener {
                onItemClick(repo)
            }
        }
    }

    override fun getItemCount() = repositories.size
}