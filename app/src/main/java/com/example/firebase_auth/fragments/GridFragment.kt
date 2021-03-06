package com.example.firebase_auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebase_auth.R
import com.example.firebase_auth.RecyclerAdapter
import com.example.firebase_auth.databinding.FragmentGridBinding
import com.example.firebase_auth.models.Post
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class GridFragment : Fragment(), Toolbar.OnMenuItemClickListener, ValueEventListener {
    private var _binding: FragmentGridBinding? = null
    private val auth = Firebase.auth
    private val list = ArrayList<Post>()
    private lateinit var adapter: RecyclerAdapter
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        // Inflate the layout for this fragment
        _binding = FragmentGridBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (_binding != null) {
            _binding!!.progressBar.visibility = View.VISIBLE
        }

        val database = FirebaseDatabase.getInstance().getReference("posts")

        database.addValueEventListener(this)
        adapter = RecyclerAdapter(requireContext())
        _binding!!.recyclerview.adapter = adapter



        _binding!!.toolbar.setOnMenuItemClickListener(this)



        _binding!!.createPost.setOnClickListener {
            findNavController().navigate(R.id.action_gridFragment_to_postFragment)
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.progressBar?.visibility = View.VISIBLE
        _binding = null
        list.clear()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.logout -> {
                    auth.signOut()
                    googleSignInClient.signOut()
                    findNavController().navigate(R.id.action_gridFragment_to_loginFragment)

                }
                R.id.favourite -> {
                    Toast.makeText(context, "faourite", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return true
    }

    override fun onDataChange(snapshot: DataSnapshot) {

        list.clear()
        for (i in snapshot.children) {
            val child = i.getValue(Post::class.java)
            if (child != null) {
                list.add(child)
            }
        }
        if (_binding != null) {
            _binding!!.progressBar.visibility = View.GONE
        }
        adapter.updateData(list)


    }

    override fun onCancelled(error: DatabaseError) {
        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
    }


}