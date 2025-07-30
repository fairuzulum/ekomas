package com.hjkarpet.ekomas.presentation.beranda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjkarpet.ekomas.databinding.FragmentBerandaBinding
import kotlinx.coroutines.launch

class BerandaFragment : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BerandaViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter()
        binding.rvPosts.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
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
                        binding.tvEmpty.text = state.message
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mencegah memory leak
    }
}