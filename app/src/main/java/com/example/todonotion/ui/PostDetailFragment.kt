package com.example.todonotion.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentPostDetailBinding

import com.example.todonotion.overview.auth.AuthNetworkViewModel


class PostDetailFragment : Fragment() {

   // private val navigationArgs: PostDetailFragmentArgs by navArgs()
    private val viewModel: AuthNetworkViewModel by activityViewModels()
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // val id = navigationArgs.postId
        // Retrieve the post details using the postId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
        viewModel.post.observe(this.viewLifecycleOwner){

            binding.editFab.setOnClickListener{
                //val action = PostDetailFragmentDirections.actionPostDetailFragmentToAddPostFragment()
                findNavController().navigate(R.id.action_postDetailFragment_to_addPostFragment)
            }
        }
    }


    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}