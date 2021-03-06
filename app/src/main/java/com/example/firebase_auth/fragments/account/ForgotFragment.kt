package com.example.firebase_auth.fragments.account

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebase_auth.R
import com.example.firebase_auth.databinding.FragmentForgotBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ForgotFragment : Fragment() {
    private var _binding: FragmentForgotBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentForgotBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resetButton.setOnClickListener {
            forgotPassword()
        }
    }

    private fun forgotPassword() {

        val email = binding.EmailAddress.text.toString()
        if (email.isEmpty()) {
            binding.EmailAddress.error = "Email is Required"
            binding.EmailAddress.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EmailAddress.error = "Please provide valid Email"
            binding.EmailAddress.requestFocus()
            return
        }
        binding.progressBar.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            try {
                auth.sendPasswordResetEmail(email).await()
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Check your Email to reset your password",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_forgotFragment_to_loginFragment)
                }
            } catch (err: FirebaseAuthInvalidUserException) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Your Email is not registered", Toast.LENGTH_LONG)
                        .show()
                }
            }

        }
    }


}