package com.example.firebase_auth.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.firebase_auth.R
import com.example.firebase_auth.dao.PostDao
import com.example.firebase_auth.databinding.FragmentPostBinding

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val postDao = PostDao()


        binding.uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).setType("image/*")
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        binding.submitButton.setOnClickListener {
            if (imageUri != null) {
                postDao.createPost(imageUri!!, binding.postText.text.toString())
                findNavController().navigate(R.id.action_postFragment_to_gridFragment)
                Toast.makeText(context, "Post created ", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Glide.with(this).load(imageUri).centerCrop().into(binding.imageView)

        }
    }


}