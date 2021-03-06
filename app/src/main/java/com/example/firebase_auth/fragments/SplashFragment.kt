package com.example.firebase_auth.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebase_auth.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        if (auth.currentUser == null) {
            Handler().postDelayed({
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }, 100)
        } else {
            Handler().postDelayed({
                findNavController().navigate(R.id.action_splashFragment_to_gridFragment)
            }, 100)
        }
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

}