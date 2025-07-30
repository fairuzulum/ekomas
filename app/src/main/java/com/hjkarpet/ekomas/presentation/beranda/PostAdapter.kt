package com.hjkarpet.ekomas.presentation.beranda

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hjkarpet.ekomas.R
import com.hjkarpet.ekomas.databinding.ItemPostBinding
import com.hjkarpet.ekomas.domain.model.Post
import com.hjkarpet.ekomas.domain.model.PostType
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                // Set Info Penulis (Masjid)
                tvAuthorName.text = post.authorName
                ivAuthorPhoto.load(post.authorPhotoUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_person) // Placeholder jika gambar sedang loading
                    error(R.drawable.ic_person)       // Gambar jika terjadi error
                }

                // Format dan set tanggal postingan
                post.createdAt?.let { date ->
                    val format = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
                    tvPostDate.text = format.format(date)
                }

                // Set Konten Postingan
                tvPostTitle.text = post.title
                tvPostSnippet.text = post.content
                ivPostImage.load(post.imageUrl) {
                    crossfade(true)
                    placeholder(R.color.background_soft)
                }

                // Set Tipe Postingan (Artikel atau Kegiatan)
                chipPostType.text = when(post.type) {
                    PostType.ARTIKEL -> "Artikel"
                    PostType.KEGIATAN -> "Kegiatan"
                }

                // Set Data Interaksi
                tvLikeCount.text = post.likesCount.toString()
                tvSaveCount.text = post.savesCount.toString()
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}