package com.github.florent37.home.tabs.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.home.R
import kotlinx.android.synthetic.main.cell_post.view.*

typealias RecyclerItem = PostWithUser
typealias PostClickListener = (RecyclerItem) -> Unit

class PostsAdapter(val listener: PostClickListener) : RecyclerView.Adapter<UserViewHolder>() {

    var items = listOf<RecyclerItem>()
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
        ) = LayoutInflater.from(parent.context).inflate(R.layout.cell_post, parent, false).let {
            UserViewHolder(it, listener)
        }
    }

    private var item: RecyclerItem? = null

    init {
        itemView.setOnClickListener {
            item?.let(listener)
        }
    }

    fun bind(item: RecyclerItem) {
        this.item = item
        itemView.author.text = item.author?.name ?: ""
        itemView.text.text = item.post.text
    }
}