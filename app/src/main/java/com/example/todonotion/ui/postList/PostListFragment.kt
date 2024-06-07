package com.example.todonotion.ui.postList

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup

import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.example.todonotion.BaseApplication
import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentPostListBinding
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.overview.auth.UserApiStatus
import com.example.todonotion.ui.adapter.PostListAdapter

import com.example.todonotion.ui.adapter.PostListener
import com.example.todonotion.ui.postDetails.PostDetailFragment
import com.example.todonotion.ui.postDetails.PostDetailsViewModel
import com.example.todonotion.ui.todoDetails.TodoDetailFragment
import com.example.todonotion.ui.todoList.TodoListFragmentDirections

import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This fragment shows the the status of the Mars photos web services transaction.
 */

class PostListFragment : Fragment() {
    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication

    /*
    private val networkViewModel: AuthNetworkViewModel by activityViewModels {
        AuthNetworkViewModel.Factory
    }

    private val tokenViewModel: TokenViewModel by activityViewModels {
        AppViewModelProvider.Factory
    }
    */

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val postListViewModel: PostListViewModel by viewModels {
        viewModelFactory
    }

    private val tokenViewModel: TokenViewModel by viewModels {
        viewModelFactory
    }

    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as BaseApplication).appComponent.postListComponent().create()
            .inject(this)
    }

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
        binding.viewModel = postListViewModel

        observeStatus()
        observeToken()
        observePosts()
        tabSelect()
        refreshPage()
        addEvent()
        setupNavigation()

        //binding
        binding.recyclerView.adapter = PostListAdapter(PostListener { post ->
            postListViewModel.onPostClicked(post)
        })

    }

    private fun setupNavigation() {
        postListViewModel.post.observe(this.viewLifecycleOwner) {
            if(it != null) {
                val postDetailFragment = PostDetailFragment.newInstance(it.id)
                openPostDetails(it.id)
            }
        }

    }

    private fun openPostDetails(postId: String) {
        val action = PostListFragmentDirections.actionPostListFragmentToPostDetailFragment(postId)
        findNavController().navigate(action)
        postListViewModel.cleanPost()
    }

    private fun addEvent() {
        //click add btn
        binding.addFab.setOnClickListener {
            //clean select post
            postListViewModel.cleanPost()
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
                postListViewModel.selectedTab(tab)
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

    private fun observeStatus() {
        postListViewModel.status.observe(this.viewLifecycleOwner) {
            if(it != UserApiStatus.LOADING) {
                hideLoadingProgress()
            }
        }
    }



    private fun hideLoadingProgress() {
        // binding.loadingImg.isIndeterminate = true
        binding.linearProgressIndicator.isVisible = false
        binding.recyclerView.isVisible = true
    }

    private fun observePosts() {

        postListViewModel.posts.observe(this.viewLifecycleOwner) {

            if (postListViewModel.status.value == UserApiStatus.DONE) {
                //binding
                if (it.isNotEmpty()) {
                    Log.d("getPosts", it.toString())
                    postListViewModel.filteredPost()
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