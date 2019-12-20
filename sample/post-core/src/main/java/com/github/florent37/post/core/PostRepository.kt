package com.github.florent37.post.core

interface PostRepository {
    suspend fun getPosts() : List<Post>
    suspend fun getPostsOf(userId: Int) : List<Post>
}