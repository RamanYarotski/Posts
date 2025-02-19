package com.homeassignment.posts.presentation.posts.fragment

import android.os.Bundle
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
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
        }

        subscribeToUserEvents(adapter = adapter)
        subscribeToUIState(adapter = adapter)
        subscribeToAppEvents()

        return binding.root
    }

    /** Manages all user events, like clicks, input etc */
    private fun subscribeToUserEvents(adapter: PostAdapter) {
        binding.searchField.addTextChangedListener { input ->
            adapter.filter.filter(input)
            sharedViewModel.updateQuery(input.toString())
        }
    }

    private fun subscribeToUIState(adapter: PostAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sharedViewModel.postsUiState.collect { state ->
                    with(binding) {
                        when (state) {
                            is PostsUiState.Success -> {
                                searchField.visibility = View.VISIBLE
                                recyclerView.visibility = View.VISIBLE

                                welcomeText.visibility = View.GONE
                                errorStateText.visibility = View.GONE
                                progressBar.visibility = View.GONE
                                adapter.setData(state.posts)
                                adapter.filter.filter(state.query)
                            }

                            is PostsUiState.Error -> {
                                errorStateText.visibility = View.VISIBLE

                                searchField.visibility = View.GONE
                                recyclerView.visibility = View.GONE
                                welcomeText.visibility = View.GONE
                                progressBar.visibility = View.GONE
                            }

                            is PostsUiState.Loading -> {
                                searchField.visibility = View.VISIBLE
                                recyclerView.visibility = View.VISIBLE
                                progressBar.visibility = View.VISIBLE

                                welcomeText.visibility = View.GONE
                                errorStateText.visibility = View.GONE
                            }

                            is PostsUiState.Init -> {
                                welcomeText.visibility = View.VISIBLE

                                searchField.visibility = View.GONE
                                recyclerView.visibility = View.GONE
                                errorStateText.visibility = View.GONE
                                progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun subscribeToAppEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
