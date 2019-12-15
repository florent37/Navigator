package com.github.florent37.user.core

class UserRepositoryImpl : UserRepository {

    private val users = listOf<User>(
        User(id = 1, name = "florent"),
        User(id = 2, name = "florian"),
        User(id = 3, name = "florence"),
        User(id = 4, name = "olivier"),
        User(id = 5, name = "jonathan")
    )

    override suspend fun getUsers() = users

    override suspend fun getUser(id: Int): User? {
        return users.find { it.id == id }
    }
}