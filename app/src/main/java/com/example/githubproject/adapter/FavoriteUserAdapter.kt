package com.example.githubproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubproject.R
import com.example.githubproject.data.local.entity.FavoriteUser

class FavoriteUserAdapter :
    ListAdapter<FavoriteUser, FavoriteUserAdapter.FavoriteUserViewHolder>(DiffCallback()) {

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return FavoriteUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteUserViewHolder, position: Int) {
        val favoriteUser = getItem(position)
        holder.bind(favoriteUser)
    }

    inner class FavoriteUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val rvAvatar: ImageView = itemView.findViewById(R.id.rvAvatar)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(getItem(position).username)
                }
            }
        }

        fun bind(favoriteUser: FavoriteUser) {
            tvUsername.text = favoriteUser.username
            Glide.with(itemView.context)
                .load(favoriteUser.avatarUrl)
                .into(rvAvatar)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(username: String)
    }

    private class DiffCallback : DiffUtil.ItemCallback<FavoriteUser>() {
        override fun areItemsTheSame(oldItem: FavoriteUser, newItem: FavoriteUser): Boolean {
            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: FavoriteUser, newItem: FavoriteUser): Boolean {
            return oldItem == newItem
        }
    }
}
