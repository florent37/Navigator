package com.github.florent37.user.core

interface UserRepository {
    suspend fun getUsers() : List<User>
    suspend fun getUser(id: Int) : User?
}