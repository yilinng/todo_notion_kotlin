package com.example.todonotion.ui.postDetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todonotion.BaseApplication

import com.example.todonotion.MainActivity
import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentPostDetailBinding

import com.example.todonotion.overview.auth.AuthNetworkViewModel
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.ui.adapter.ContextAdapter
import com.example.todonotion.ui.todoDetails.TodoDetailFragment
import javax.inject.Inject


//https://stackoverflow.com/questions/17436298/how-to-pass-a-variable-from-activity-to-fragment-and-pass-it-back
class PostDetailFragment : Fragment() {

    // private val args: PostDetailFragmentArgs by navArgs()
    /*
    private val viewModel: AuthNetworkViewModel by activityViewModels {
        AuthNetworkViewModel.Factory
    }
     */

    private lateinit var args: String

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val postDetailsViewModel: PostDetailsViewModel by viewModels {
        viewModelFactory
    }

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as BaseApplication).appComponent.postDetailsComponent()
            .create()
            .inject(this)

        args = arguments?.getString("postId").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        postDetailsViewModel.getPostAction(args)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // val id = navigationArgs.postId
        // Retrieve the post details using the postId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
        val context = postDetailsViewModel.post.value?.context ?: listOf()
        binding.recyclerView.adapter = ContextAdapter(context)
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
        postDetailsViewModel.post.observe(this.viewLifecycleOwner) {
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
        postDetailsViewModel.user.observe(this.viewLifecycleOwner) {
            if (it!!.todos!!.isNotEmpty() && it.todos!!.contains(postDetailsViewModel.post.value!!.id)) {
                binding.editFab.visibility = View.VISIBLE
            } else {
                binding.editFab.visibility = View.GONE
            }
        }

    }

    //https://stackoverflow.com/questions/15560904/setting-custom-actionbar-title-from-fragment
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).supportActionBar?.title =
            postDetailsViewModel.post.value?.title

    }


    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String) = PostDetailFragment().apply {
            arguments = Bundle().apply {
                putString("postId", id)
            }
        }
    }
}