package com.example.firebase_auth.dao

import android.net.Uri
import com.example.firebase_auth.models.Post
import com.example.firebase_auth.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {

    private val store = FirebaseStorage.getInstance().getReference("Images/")
    private val db = FirebaseDatabase.getInstance().getReference("posts/")
    private val auth = Firebase.auth

    fun createPost(imageUri: Uri, post: String) {
        val currentUserId = auth.currentUser!!.uid

        val currentTime = System.currentTimeMillis()
        val fileRef = store.child("$currentTime")
        var url: String?

        GlobalScope.launch(Dispatchers.IO) {
            url = fileRef.putFile(imageUri).await().storage.downloadUrl.await().toString()
            val user = UserDao().getUserById(currentUserId).await().getValue(User::class.java)
            val postClass = url?.let { Post(it, user, currentTime, post) }
            val id = db.push().key
            if (id != null) {
                db.child(id).setValue(postClass)
            }

        }
    }


}