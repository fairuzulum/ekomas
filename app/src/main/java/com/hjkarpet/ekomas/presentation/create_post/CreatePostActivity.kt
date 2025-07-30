package com.hjkarpet.ekomas.presentation.create_post

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.hjkarpet.ekomas.R
import com.hjkarpet.ekomas.databinding.ActivityCreatePostBinding
import com.hjkarpet.ekomas.domain.model.PostType
import kotlinx.coroutines.launch

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private val viewModel: CreatePostViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var selectedPostType: PostType = PostType.ARTIKEL // Default

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivCoverImage.load(it) {
                crossfade(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_publish) {
                publishPost()
                true
            } else {
                false
            }
        }
    }

    private fun setupListeners() {
        binding.btnSelectImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
        binding.ivCoverImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.toggleType.check(R.id.btnArtikel) // Set default
        binding.toggleType.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                selectedPostType = when (checkedId) {
                    R.id.btnKegiatan -> PostType.KEGIATAN
                    else -> PostType.ARTIKEL
                }
            }
        }
    }

    private fun publishPost() {
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        viewModel.publishPost(title, content, selectedPostType, selectedImageUri)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.createPostState.collect { state ->
                binding.progressBar.visibility = if (state is CreatePostState.Loading) View.VISIBLE else View.GONE
                when (state) {
                    is CreatePostState.Success -> {
                        Toast.makeText(this@CreatePostActivity, "Postingan berhasil dipublikasikan!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is CreatePostState.Error -> {
                        Toast.makeText(this@CreatePostActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }
}