package com.hjkarpet.ekomas

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjkarpet.ekomas.databinding.ActivityMainBinding
import com.hjkarpet.ekomas.presentation.beranda.BerandaState
import com.hjkarpet.ekomas.presentation.beranda.BerandaViewModel
import com.hjkarpet.ekomas.presentation.beranda.PostAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: BerandaViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupBottomNavigation()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter()
        binding.rvPosts.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    // Anda sudah di sini
                    true
                }
                R.id.nav_eksplor, R.id.nav_analitik, R.id.nav_profil -> {
                    // Navigasi ke halaman lain akan diimplementasikan nanti
                    Toast.makeText(this, "${item.title} diklik", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is BerandaState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.rvPosts.visibility = View.GONE
                        binding.tvEmpty.visibility = View.GONE
                    }
                    is BerandaState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (state.posts.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.rvPosts.visibility = View.GONE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                            binding.rvPosts.visibility = View.VISIBLE
                            postAdapter.submitList(state.posts)
                        }
                    }
                    is BerandaState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.tvEmpty.text = state.message // Tampilkan pesan error
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}