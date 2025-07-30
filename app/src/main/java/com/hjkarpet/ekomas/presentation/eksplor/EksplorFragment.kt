package com.hjkarpet.ekomas.presentation.eksplor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.hjkarpet.ekomas.databinding.FragmentEksplorBinding
import com.hjkarpet.ekomas.domain.model.PostType
import com.hjkarpet.ekomas.presentation.beranda.PostAdapter
import kotlinx.coroutines.launch

class EksplorFragment : Fragment() {

    private var _binding: FragmentEksplorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EksplorViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEksplorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTabs()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Kita menggunakan kembali PostAdapter yang sudah ada!
        postAdapter = PostAdapter()
        binding.rvFilteredPosts.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.applyFilter(null) // Semua
                    1 -> viewModel.applyFilter(PostType.ARTIKEL) // Artikel
                    2 -> viewModel.applyFilter(PostType.KEGIATAN) // Kegiatan
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.visibility = if (state is EksplorState.Loading) View.VISIBLE else View.GONE
                when (state) {
                    is EksplorState.Success -> {
                        binding.rvFilteredPosts.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = if (state.filteredPosts.isEmpty()) View.VISIBLE else View.GONE
                        postAdapter.submitList(state.filteredPosts)
                    }
                    is EksplorState.Error -> {
                        binding.rvFilteredPosts.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.tvEmpty.text = state.message
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}