package com.example.firebase_auth.models

data class Post(
    val imagesUri: String,
    val createdBy: User? = null,
    val createdAt: Long = 0L,
    val post: String? = null,
    val likedBy: ArrayList<User> = ArrayList()
) {
    constructor() : this("", null, 0, null, arrayListOf())
}
