package com.example.githubproject.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubproject.data.remote.response.ItemsItem
import com.example.githubproject.databinding.ItemFollowBinding
import com.example.githubproject.ui.DetailActivity

class FollowAdapter(private val context: Context) : RecyclerView.Adapter<FollowAdapter.FollowerFollowingViewHolder>() {

    private var followersFollowing: List<ItemsItem> = listOf()

    inner class FollowerFollowingViewHolder(private val binding: ItemFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemsItem) {
            binding.apply {
                textUsername.text = item.login
                Glide.with(itemView.context)
                    .load(item.avatarUrl)
                    .into(imageAvatar)

                itemView.setOnClickListener {
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("username", item.login)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerFollowingViewHolder {
        val binding = ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowerFollowingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowerFollowingViewHolder, position: Int) {
        val followerFollowing = followersFollowing[position]
        holder.bind(followerFollowing)
    }

    override fun getItemCount(): Int {
        return followersFollowing.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<ItemsItem>) {
        followersFollowing = data
        notifyDataSetChanged()
    }
}
