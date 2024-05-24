package com.example.todonotion.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup

import androidx.core.view.*
import androidx.fragment.app.Fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todonotion.AppViewModelProvider

import com.example.todonotion.BaseApplication
import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentPostListBinding
import com.example.todonotion.overview.auth.AuthNetworkViewModel
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.overview.auth.UserApiStatus
import com.example.todonotion.ui.adapter.PostListAdapter

import com.example.todonotion.ui.adapter.PostListener

import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * This fragment shows the the status of the Mars photos web services transaction.
 */

class PostListFragment : Fragment() {
    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    private val networkViewModel: AuthNetworkViewModel by activityViewModels {
        AuthNetworkViewModel.Factory
    }

    private val tokenViewModel: TokenViewModel by activityViewModels {
        AppViewModelProvider.Factory
    }

    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: call the view model method that calls the post api
        binding.lifecycleOwner = this
        binding.viewModel = networkViewModel

        observeToken()
        observePosts()
        tabSelect()
        refreshPage()
        addEvent()

        //binding
        binding.recyclerView.adapter = PostListAdapter(PostListener { post ->
            networkViewModel.onPostClicked(post)
            findNavController().navigate(R.id.action_postListFragment_to_postDetailFragment)
        })

    }

    private fun addEvent() {
        //click add btn
        binding.addFab.setOnClickListener {
            //clean select post
            networkViewModel.cleanPost()
            //redirect to add post page
            val action = PostListFragmentDirections.actionPostListFragmentToAddPostFragment()
            findNavController()
                .navigate(action)

        }
    }

    private fun tabSelect() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab select
                networkViewModel.selectedTab(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
    }

    private fun refreshPage() {
        //https://developer.android.com/develop/ui/views/touch-and-input/swipe/respond-refresh-request
        //refresh page
        binding.refreshLayout.setOnRefreshListener {
            Log.d("onRefresh", "onRefresh called from SwipeRefreshLayout")

            //https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
            val navController = findNavController()
            navController.run {
                popBackStack()
                navigate(R.id.postListFragment)
            }
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun observeToken() {
        tokenViewModel.tokens.observe(this.viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.addFab.visibility = GONE
                binding.tabLayout.visibility = GONE
            } else {
                binding.addFab.visibility = View.VISIBLE
                binding.tabLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun showLoadingProgress() {
        //binding.loadingImg.isIndeterminate = false
        binding.linearProgressIndicator.isVisible = true
        binding.recyclerView.isVisible = false
    }

    private fun hideLoadingProgress() {
        // binding.loadingImg.isIndeterminate = true
        binding.linearProgressIndicator.isVisible = false
        binding.recyclerView.isVisible = true
    }

    private fun observePosts() {
        lifecycleScope.launch {
            showLoadingProgress()
            delay(3000)
            hideLoadingProgress()
        }

        networkViewModel.posts.observe(this.viewLifecycleOwner) {

            if (networkViewModel.status.value == UserApiStatus.DONE) {
                //binding
                if (it.isNotEmpty()) {
                    Log.d("getPosts", it.toString())
                    networkViewModel.filteredPost()
                    binding.postsErrorText.visibility = GONE
                    binding.postEmptyImage.visibility = GONE

                } else {
                    binding.postsErrorText.visibility = View.VISIBLE
                    binding.postEmptyImage.visibility = View.VISIBLE
                }
            }

        }
    }


}