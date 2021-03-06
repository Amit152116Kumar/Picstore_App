package com.example.firebase_auth.dao

import com.example.firebase_auth.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserDao {

    private val auth = Firebase.auth
    private val userDB = FirebaseDatabase.getInstance().getReference("users")

    suspend fun addUser(user: User, uid: String) {
        userDB.child(uid).setValue(user).await()
    }

    fun getUserById(uid: String): Task<DataSnapshot> {
        return userDB.child(uid).get()
    }
}