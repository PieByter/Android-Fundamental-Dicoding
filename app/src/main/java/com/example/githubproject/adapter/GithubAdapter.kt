package com.example.githubproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubproject.databinding.ItemUserBinding
import com.example.githubproject.data.remote.response.ItemsItem

class GithubAdapter :
    ListAdapter<ItemsItem, GithubAdapter.ViewHolder>(DiffCallback()) {

    private var onItemClickListener: ((ItemsItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (ItemsItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemsItem) {
            binding.apply {
                tvUsername.text = item.login
                Glide.with(itemView.context).load(item.avatarUrl).into(rvAvatar)

                root.setOnClickListener {
                    onItemClickListener?.invoke(item)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ItemsItem>() {
        override fun areItemsTheSame(oldItem: ItemsItem, newItem: ItemsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ItemsItem, newItem: ItemsItem): Boolean {
            return oldItem == newItem
        }
    }
}
