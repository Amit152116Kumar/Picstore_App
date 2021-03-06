package com.example.firebase_auth.models

data class User(
    val userName: String,
    val photoUrl: String? = null,
    val fullName: String,
    val email: String
) {
    constructor() : this("", null, "", "")
}
