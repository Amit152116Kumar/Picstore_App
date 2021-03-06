package com.example.firebase_auth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase_auth.databinding.ListItemViewBinding
import com.example.firebase_auth.models.Post

class RecyclerAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var items = listOf<Post>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ListItemViewBinding.bind(itemView)
        val imageView = binding.image
//        val profileName=binding.profileName
//        val profilePic=binding.profilePic


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_view, parent, false)
        )
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(items[position].imagesUri).centerCrop().into(holder.imageView)
//        holder.profileName.text= items[position].createdBy?.userName
//        Glide.with(context).load(items[position].createdBy?.photoUrl).fitCenter().into(holder.profilePic)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(updatedItems: List<Post>) {
        items = updatedItems
        notifyDataSetChanged()
    }
}