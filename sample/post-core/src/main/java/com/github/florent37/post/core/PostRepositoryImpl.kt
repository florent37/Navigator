package com.github.florent37.post.core

class PostRepositoryImpl : PostRepository {

    private val posts = listOf<Post>(
        Post(id = 1, authorId = 1, text = "Ya le mec à Milla"),
        Post(id = 2, authorId = 1, text = "T'as pas de shampoing ?"),
        Post(id = 3, authorId = 2, text = "Mais t'es pas nette !"),
        Post(
            id = 4,
            authorId = 3,
            text = "Ya baptiste qui est en train de faire bruler un truc dans sa chambre"
        ),
        Post(id = 5, authorId = 2, text = "Mais si jsuis très net")
    )

    override suspend fun getPosts(): List<Post> = posts

    override suspend fun getPostsOf(userId: Int): List<Post> = posts.filter {
        it.authorId == userId
    }
}