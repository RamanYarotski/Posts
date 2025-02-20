package com.homeassignment.posts.presentation.posts.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.homeassignment.posts.databinding.FragmentPostDetailsBinding
import com.homeassignment.posts.presentation.posts.PostsSharedViewModel
import kotlinx.coroutines.launch

class PostDetailsFragment : Fragment() {
    private val sharedViewModel: PostsSharedViewModel by activityViewModels()
    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        subscribeToUiState()
        return binding.root
    }

    private fun subscribeToUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.postDetailsUiState.collect { state ->
                    state?.let {
                        binding.tvPostTitle.text = it.post.title
                        binding.tvPostBody.text = it.post.body
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}