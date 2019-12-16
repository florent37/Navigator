package com.github.florent37.user.core

class UserRepositoryImpl : UserRepository {

    private val users = listOf<User>(
        User(id = 1, name = "florent", avatarUrl = "https://picsum.photos/200/300"),
        User(id = 2, name = "florian", avatarUrl = "https://picsum.photos/200/301"),
        User(id = 3, name = "florence", avatarUrl = "https://picsum.photos/200/302"),
        User(id = 4, name = "olivier", avatarUrl = "https://picsum.photos/200/303"),
        User(id = 5, name = "jonathan", avatarUrl = "https://picsum.photos/200/304")
    )

    override suspend fun getUsers() = users

    override suspend fun getUser(id: Int): User? {
        return users.find { it.id == id }
    }
}