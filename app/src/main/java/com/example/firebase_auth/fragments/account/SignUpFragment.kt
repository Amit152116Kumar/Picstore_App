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
import com.example.firebase_auth.dao.UserDao
import com.example.firebase_auth.databinding.FragmentSignUpBinding
import com.example.firebase_auth.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.register.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val fullName = binding.fullName.text.toString()
        val username = binding.username.text.toString()
        val email = binding.EmailAddress.text.toString()
        val password = binding.Password.text.toString()

        if (fullName.isEmpty()) {
            binding.fullName.error = "Full Name is Required"
            binding.fullName.requestFocus()
            return
        }
        if (username.isEmpty()) {
            binding.fullName.error = null
            binding.username.error = "Username is Required"
            binding.username.requestFocus()
            return
        }
        if (username.length !in 11 downTo 3) {
            binding.username.error = "Username length should be in range 4-10"
            binding.username.requestFocus()
        }
        val regex = Regex("^[a-z0-9A-Z_.]{3,10}\$")
        if (!username.contains(regex)) {
            binding.username.error = "Username can only contain a-z A-Z 0-9 _."
            binding.username.requestFocus()
            return
        }
        if (email.isEmpty()) {
            binding.username.error = null
            binding.EmailAddress.error = "Email is Required"
            binding.EmailAddress.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EmailAddress.error = "Please provide valid Email"
            binding.EmailAddress.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.EmailAddress.error = null
            binding.Password.error = "Password is Required"
            binding.Password.requestFocus()
            return
        }
        if (password.length < 6) {
            binding.Password.error = "Minimum 6 characters"
            binding.Password.requestFocus()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password).await()
            val userDao = UserDao()
            val user = User(username, null, fullName, email)
            userDao.addUser(user, auth.currentUser!!.uid)
            auth.currentUser!!.sendEmailVerification()
            Toast.makeText(context, "Check your Email to verify your Account", Toast.LENGTH_LONG)
                .show()

            withContext(Dispatchers.Main) {
                findNavController().navigate(R.id.action_signUpFragment_to_gridFragment)
            }
        }

    }
}