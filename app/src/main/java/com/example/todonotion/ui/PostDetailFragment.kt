package com.example.todonotion.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.example.todonotion.MainActivity
import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentPostDetailBinding

import com.example.todonotion.overview.auth.AuthNetworkViewModel
import com.example.todonotion.ui.adapter.ContextAdapter


class PostDetailFragment : Fragment() {

    // private val navigationArgs: PostDetailFragmentArgs by navArgs()
    private val viewModel: AuthNetworkViewModel by activityViewModels {
        AuthNetworkViewModel.Factory
    }
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // val id = navigationArgs.postId
        // Retrieve the post details using the postId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
        binding.recyclerView.adapter = ContextAdapter(viewModel.post.value?.context!!)
        observeUser()
        observePost()
        refreshPage()
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
                navigate(R.id.postDetailFragment)
            }
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun observePost() {
        viewModel.post.observe(this.viewLifecycleOwner) {
            if (it != null) {
                binding.todoTitle.text = it.title
                binding.todoUser.text = it.username
                binding.todoDate.text = it.updateDate!!.substring(0, 10)

                binding.editFab.setOnClickListener {
                    //val action = PostDetailFragmentDirections.actionPostDetailFragmentToAddPostFragment()
                    findNavController().navigate(R.id.action_postDetailFragment_to_addPostFragment)
                }
            }
        }
    }

    private fun observeUser() {
        viewModel.user.observe(this.viewLifecycleOwner) {
            if (it!!.todos!!.isNotEmpty() && it.todos!!.contains(viewModel.post.value!!.id)) {
                binding.editFab.visibility = View.VISIBLE
            } else {
                binding.editFab.visibility = View.GONE
            }
        }

    }

    //https://stackoverflow.com/questions/15560904/setting-custom-actionbar-title-from-fragment
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).supportActionBar?.title = viewModel.post.value?.title

    }


    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}