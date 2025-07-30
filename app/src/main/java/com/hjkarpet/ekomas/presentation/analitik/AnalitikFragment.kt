package com.hjkarpet.ekomas.presentation.analitik

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hjkarpet.ekomas.databinding.FragmentAnalitikBinding
import kotlinx.coroutines.launch

class AnalitikFragment : Fragment() {
    private var _binding: FragmentAnalitikBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnalitikViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnalitikBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.visibility = if (state is AnalitikState.Loading) View.VISIBLE else View.GONE
                binding.gridLayout.visibility = if (state is AnalitikState.Success) View.VISIBLE else View.INVISIBLE

                when(state) {
                    is AnalitikState.Success -> {
                        val data = state.data
                        binding.tvPublishedArticlesCount.text = data.publishedArticles.toString()
                        binding.tvPublishedEventsCount.text = data.publishedEvents.toString()
                        binding.tvDraftsCount.text = data.drafts.toString()
                        // Update juga untuk tvLikedCount dan tvSavedCount
                    }
                    is AnalitikState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
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