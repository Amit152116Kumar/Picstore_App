package com.example.firebase_auth.fragments.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebase_auth.R
import com.example.firebase_auth.dao.UserDao
import com.example.firebase_auth.databinding.FragmentLoginBinding
import com.example.firebase_auth.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class LoginFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val RC_SIGN_IN = 120
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mAuth = Firebase.auth
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.signInGoogle.setOnClickListener {
            signIn()
        }
        binding.createAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
        }

        binding.login.setOnClickListener {
            userLogin()
        }


    }

    private fun userLogin() {
        val email = binding.EmailAddress.text.toString()
        val password = binding.password.text.toString()

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
        if (password.isEmpty()) {
            binding.EmailAddress.error = null
            binding.password.error = "Password is Required"
            binding.password.requestFocus()
            return
        }
        if (password.length < 6) {
            binding.password.error = "Minimum 6 characters"
            binding.password.requestFocus()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        GlobalScope.launch {
            try {
                mAuth.signInWithEmailAndPassword(email, password).await()
                if (mAuth.currentUser!!.isEmailVerified) {
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_loginFragment_to_gridFragment)
                    }
                } else {
                    mAuth.currentUser!!.sendEmailVerification()
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            context,
                            "Your account is not verified. Please check your email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    mAuth.signOut()
                }
            } catch (err: FirebaseAuthInvalidCredentialsException) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Failed to Login!\nPlease check your Credentials",
                        Toast.LENGTH_LONG
                    ).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("TAG", "Google sign in failed", e)
                    // ...
                }

            } else
                Log.w("TAG", exception.toString())

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        GlobalScope.launch(Dispatchers.IO) {
            val auth = mAuth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main) {
                updateUI(firebaseUser)
            }
        }
//        val auth = mAuth.signInWithCredential(credential)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("TAG", "signInWithCredential:success")
//                    val user = mAuth.currentUser
//                    findNavController().navigate(R.id.action_loginFragment_to_gridFragment)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w("TAG", "signInWithCredential:failure", task.exception)
//                    // ...
//                    view?.let {
//                        Snackbar.make(it, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
//                    }
//                }
//
//            }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            val user = User(
                firebaseUser.displayName!!, firebaseUser.photoUrl.toString(),
                firebaseUser.displayName!!, firebaseUser.email!!
            )
            val userDao = UserDao()
            GlobalScope.launch(Dispatchers.IO) {
                userDao.addUser(user, firebaseUser.uid)
            }
            findNavController().navigate(R.id.action_loginFragment_to_gridFragment)
        }
    }


}