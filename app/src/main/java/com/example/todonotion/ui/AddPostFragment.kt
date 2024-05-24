package com.example.todonotion.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels

import androidx.navigation.fragment.findNavController

import com.example.todonotion.AppViewModelProvider

import com.example.todonotion.R
import com.example.todonotion.model.Post
import com.example.todonotion.databinding.FragmentAddPostBinding

import com.example.todonotion.model.dto.PostDto
import com.example.todonotion.overview.auth.AuthNetworkViewModel
import com.example.todonotion.overview.auth.TokenViewModel

import com.example.todonotion.overview.auth.UserApiStatus


class AddPostFragment : Fragment() {
    private val tokenViewModel: TokenViewModel by activityViewModels {
        AppViewModelProvider.Factory
    }
    private val networkViewModel: AuthNetworkViewModel by activityViewModels {
        AuthNetworkViewModel.Factory
    }
    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var post: Post

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerToken()
        observePost()
        editWithDoneBtn()
        refreshPage()
    }

    private fun observePost() {
        networkViewModel.post.observe(this.viewLifecycleOwner) {
            if (it != null) {
                post = it
                binding.addPostTitle.text = getString(R.string.update_post_title)
            }
        }
    }

    private fun observerToken() {
        tokenViewModel.tokens.observe(this.viewLifecycleOwner) {
            it.let {
                if (it.isNotEmpty()) {
                    if (networkViewModel.post.value != null && networkViewModel.checkUserHavePost(
                            post
                        )
                    ) {
                        binding.deleteBtn.visibility = View.VISIBLE
                        binding.deleteBtn.setOnClickListener {
                            deletePost(post)
                        }
                        //update post action
                        bindPost(post)
                    } else {
                        binding.saveBtn.setOnClickListener {
                            Toast.makeText(
                                this.context, "click save button", Toast.LENGTH_SHORT
                            ).show()
                            addNewPost()
                        }
                    }

                    binding.titleInput.addTextChangedListener {
                        Toast.makeText(
                            this.context, "title input change", Toast.LENGTH_SHORT
                        ).show()
                        cleanIsEmpty()
                        actionIsEmpty()
                    }

                    binding.contextInput.addTextChangedListener {
                        Toast.makeText(
                            this.context, "context input change", Toast.LENGTH_SHORT
                        ).show()
                        cleanIsEmpty()
                        actionIsEmpty()
                    }

                }
            }
        }
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
                navigate(R.id.addPostFragment)
            }
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun observeError() {
        networkViewModel.error.observe(this.viewLifecycleOwner) { items ->
            items.let {
                if (it != null) {
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.setTextColor(Color.RED)
                    Log.i("addPost400", it)
                    binding.errorText.text = getString(R.string.signup_server_error)
                    // binding.errorText.text = it.toString()

                }
            }
        }
    }

    //https://stackoverflow.com/questions/2986387/multi-line-edittext-with-done-action-button
    private fun editWithDoneBtn() {
        binding.contextInput.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
    }

    private fun addNewPost() {
        if (isEntryValid()) {
            networkViewModel.addPostAction(convertToDataClass())
            //add post
            observeStatus()
            observeError()
        } else {
            actionIsEmpty()
        }
    }

    private fun updatePost(post: Post) {
        if (isEntryValid()) {
            networkViewModel.editPostAction(post.id, convertToDataClass())
            //add post
            observeStatus()
        } else {
            actionIsEmpty()
        }
    }

    private fun actionIsEmpty() {
        if (binding.titleInput.text.toString().isEmpty()) {
            binding.titleLabel.error = getString(R.string.edit_title_error)
        }
        if (binding.contextInput.text.toString().isEmpty()) {
            binding.contextLabel.error = getString(R.string.edit_content_error)
        }
    }

    private fun cleanIsEmpty() {
        if (binding.titleInput.text.toString().isNotEmpty()) {
            binding.titleLabel.error = null
        }
        if (binding.contextInput.text.toString().isNotEmpty()) {
            binding.contextLabel.error = null
        }
    }


    private fun observeStatus() {
        networkViewModel.status.observe(this.viewLifecycleOwner) {
            if (it == UserApiStatus.DONE) {
                navToLastPage()
            }
        }
    }

    private fun navToLastPage() {
        val action = AddPostFragmentDirections.actionAddPostFragmentToPostListFragment()
        findNavController().navigate(action)
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return networkViewModel.isPostEntryValid(
            binding.titleInput.text.toString(),
            binding.contextInput.text.toString(),
            binding.usernameInput.text.toString()
        )
    }

    private fun convertStringToList(context: String): List<String> {
        val mutableList: MutableList<String> = ArrayList()
        var last = 0
        var index = 0
        for (letter in context) {
            if (letter == '\n') {
                mutableList.add(context.substring(last, index))
                last = index
            }
            index++
        }
        // last text before newline
        mutableList.add(context.substring(last))
        Log.d("convertStringToList last text...", context.substring(last))
        Log.d("convertStringToList", mutableList.toString())
        return mutableList
    }

    private fun convertListToString(list: List<String>): String {
        var str = ""
        for (letter in list) {
            str = str + letter + '\n'
        }
        return str
    }

    private fun convertToDataClass(): PostDto {
        return PostDto(
            binding.titleInput.text.toString(),
            convertStringToList(binding.contextInput.text.toString()),
            binding.usernameInput.text.toString()
        )
    }


    private fun deletePost(post: Post) {
        networkViewModel.deletePostAction(post.id)
        observeStatus()
        observeError()
    }


    private fun bindPost(post: Post) {
        binding.apply {
            usernameInput.setText(post.username, TextView.BufferType.SPANNABLE)
            titleInput.setText(post.title, TextView.BufferType.SPANNABLE)
            contextInput.setText(
                post.context?.let { convertListToString(it) },
                TextView.BufferType.SPANNABLE
            )

            saveBtn.setOnClickListener {
                updatePost(post)
            }

        }

    }


    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

}