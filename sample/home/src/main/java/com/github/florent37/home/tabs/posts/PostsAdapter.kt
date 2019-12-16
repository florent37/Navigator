package com.github.florent37.home.tabs.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.florent37.home.R
import com.github.florent37.post.core.Post
import kotlinx.android.synthetic.main.cell_post.view.*

typealias PostClickListener = (Post) -> Unit

class PostsAdapter(val listener: PostClickListener) : RecyclerView.Adapter<UserViewHolder>() {

    var items = listOf<Post>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserViewHolder.newInstance(parent, listener)

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class UserViewHolder(
    view: View,
    val listener: PostClickListener
) : RecyclerView.ViewHolder(view) {

    companion object {
        fun newInstance(
            parent: ViewGroup,
            listener: PostClickListener
        ) = LayoutInflater.from(parent.context).inflate(R.layout.cell_user, parent, false).let {
            UserViewHolder(it, listener)
        }
    }

    private var post: Post? = null

    init {
        itemView.setOnClickListener {
            post?.let(listener)
        }
    }

    fun bind(post: Post) {
        this.post = post
        itemView.text.text = post.text
    }
}