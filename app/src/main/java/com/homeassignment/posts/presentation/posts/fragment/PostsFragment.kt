package com.homeassignment.posts.presentation.posts.fragment

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.homeassignment.posts.R
import com.homeassignment.posts.databinding.FragmentPostsBinding
import com.homeassignment.posts.presentation.posts.PostsEvents
import com.homeassignment.posts.presentation.posts.PostsSharedViewModel
import com.homeassignment.posts.presentation.posts.PostsUiState
import com.homeassignment.posts.utils.mapToString
import kotlinx.coroutines.launch

class PostsFragment : Fragment() {
    private val sharedViewModel: PostsSharedViewModel by activityViewModels()

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private var textWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPostsBinding.inflate(inflater, container, false)

        val adapter = PostAdapter { user ->
            sharedViewModel.selectPost(user)
            findNavController().navigate(R.id.action_post_fragment_to_post_details_fragment)
        }
        with(binding) {
            rvTitles.layoutManager = LinearLayoutManager(context)
            rvTitles.adapter = adapter
        }

        subscribeToUserEvents()
        subscribeToUIState(adapter = adapter)
        subscribeToAppEvents()

        return binding.root
    }

    /** Manages all user events, like clicks, input etc */
    private fun subscribeToUserEvents() {
        textWatcher = binding.etSearchTitle.addTextChangedListener { input ->
            sharedViewModel.updateQuery(input.toString())
        }
    }

    private fun subscribeToUIState(adapter: PostAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.postsUiState.collect { state ->
                    with(binding) {
                        when (state) {
                            is PostsUiState.Success -> {
                                etSearchTitle.visibility = View.VISIBLE
                                rvTitles.visibility = View.VISIBLE

                                tvWelcome.visibility = View.GONE
                                tvErrorState.visibility = View.GONE
                                pbLoading.visibility = View.GONE
                                adapter.setData(state.posts)
                                adapter.filter.filter(state.query)
                            }

                            is PostsUiState.Error -> {
                                tvErrorState.visibility = View.VISIBLE

                                etSearchTitle.visibility = View.GONE
                                rvTitles.visibility = View.GONE
                                tvWelcome.visibility = View.GONE
                                pbLoading.visibility = View.GONE
                            }

                            is PostsUiState.Loading -> {
                                etSearchTitle.visibility = View.VISIBLE
                                rvTitles.visibility = View.VISIBLE
                                pbLoading.visibility = View.VISIBLE

                                tvWelcome.visibility = View.GONE
                                tvErrorState.visibility = View.GONE
                            }

                            is PostsUiState.Init -> {
                                tvWelcome.visibility = View.VISIBLE

                                etSearchTitle.visibility = View.GONE
                                rvTitles.visibility = View.GONE
                                tvErrorState.visibility = View.GONE
                                pbLoading.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun subscribeToAppEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.postsEvents.collect { event ->
                    when (event) {
                        is PostsEvents.ShowToast -> {
                            Toast
                                .makeText(
                                    requireContext(),
                                    event.message.mapToString(requireContext()),
                                    event.duration
                                )
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.etSearchTitle.removeTextChangedListener(textWatcher)
        _binding = null
    }
}
