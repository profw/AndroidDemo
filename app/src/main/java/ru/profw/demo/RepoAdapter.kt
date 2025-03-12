package ru.profw.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.profw.demo.model.Repository

class RepoAdapter() :
    RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {

    var repositories: List<Repository> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.repoName)
        val owner: TextView = itemView.findViewById(R.id.repoOwner)
        val avatar: ImageView = itemView.findViewById(R.id.repoAvatar)
        val url: TextView = itemView.findViewById(R.id.repoUrl)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false)
        return RepoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = repositories[position]
        holder.name.text = repo.name
        holder.owner.text = repo.owner.login
        holder.url.text = repo.htmlUrl

        Picasso.get()
            .load(repo.owner.avatarUrl)
            .into(holder.avatar)
    }

    override fun getItemCount() = repositories.size
}