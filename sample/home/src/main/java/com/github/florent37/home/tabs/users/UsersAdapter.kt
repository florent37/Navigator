package com.github.florent37.home.tabs.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.florent37.home.R
import com.github.florent37.user.core.User
import kotlinx.android.synthetic.main.cell_user.view.*

typealias UserClickListener = (User) -> Unit

class UsersAdapter(val listener: UserClickListener) : RecyclerView.Adapter<UserViewHolder>() {

    var items = listOf<User>()
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
    private val listener: UserClickListener
) : RecyclerView.ViewHolder(view) {

    companion object {
        fun newInstance(
            parent: ViewGroup,
            listener: UserClickListener
        ) = LayoutInflater.from(parent.context).inflate(R.layout.cell_user, parent, false).let {
            UserViewHolder(it, listener)
        }
    }

    private var user: User? = null

    init {
        itemView.setOnClickListener {
            user?.let(listener)
        }
    }

    fun bind(user: User) {
        this.user = user
        itemView.userName.text = user.name
        Glide.with(itemView.context).load(user.avatarUrl).into(itemView.userImage)
    }
}