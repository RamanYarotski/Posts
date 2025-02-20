package com.homeassignment.posts.presentation.posts.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homeassignment.posts.databinding.ItemPostBinding
import com.homeassignment.posts.domain.entity.Post

class PostAdapter(
    private val onClick: (Post) -> Unit
) : ListAdapter<Post, PostAdapter.UserViewHolder>(UserDiffCallback()), Filterable {
    private var originList = arrayListOf<Post>()

    private val searchFilter: Filter = object : Filter() {
        override fun performFiltering(input: CharSequence): FilterResults {
            val filteredList = if (input.isEmpty()) {
                originList
            } else {
                originList.filter { it.title.lowercase().contains(input.toString().lowercase()) }
            }
            return FilterResults().apply { values = filteredList }
        }

        override fun publishResults(input: CharSequence, results: FilterResults) {
            submitList(results.values as ArrayList<Post>)
        }
    }

    class UserViewHolder(
        private val binding: ItemPostBinding,
        private val onClick: (Post) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            with(binding) {
                itemTitle.text = post.title
                root.setOnClickListener { onClick(post) }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getFilter(): Filter {
        return searchFilter
    }

    fun setData(list: List<Post>?) {
        this.originList = ArrayList(list.orEmpty())
        submitList(list)
    }

    class UserDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
