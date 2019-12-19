package com.github.florent37.home.tabs.posts

import com.github.florent37.post.core.Post
import com.github.florent37.post.core.PostRepository
import com.github.florent37.user.core.User
import com.github.florent37.user.core.UserRepository

class PostWithUser(val post: Post, val author: User? = null)

class PostsUseCase(val postRepository: PostRepository, val userRepository: UserRepository) {

    suspend fun getPosts(ofUserId: Int? = null): List<PostWithUser> {
        return if (ofUserId != null) {
            val user: User? = userRepository.getUser(ofUserId)
            postRepository.getPostsOf(userId = ofUserId)
                .map { post ->
                    PostWithUser(
                        post = post,
                        author = user
                    )
                }
        } else {
            val users = userRepository.getUsers()
            postRepository.getPosts().map {
                PostWithUser(
                    post = it,
                    author = users[it.authorId]
                )
            }
        }
    }
}