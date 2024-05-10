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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todonotion.BaseApplication
import com.example.todonotion.R
import com.example.todonotion.network.Post
import com.example.todonotion.databinding.FragmentAddPostBinding

import com.example.todonotion.network.dto.PostDto
import com.example.todonotion.overview.auth.AuthNetworkViewModel
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.overview.auth.TokenViewModelFactory


class AddPostFragment : Fragment() {

    //private val navigationArgs: PostDetailFragmentArgs by navArgs()

    private val tokenViewModel: TokenViewModel by activityViewModels {
        TokenViewModelFactory(
            (activity?.application as BaseApplication).database.tokenDao()
        )
    }
    private val networkViewModel: AuthNetworkViewModel by activityViewModels()
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

        editWithDoneBtn()

        networkViewModel.post.observe(this.viewLifecycleOwner) {
            if (it != null) {
                post = it
                binding.addPostTitle.text = getString(R.string.update_post_title)
            }
        }

        // val id = navigationArgs.postId

        // TODO: Observe token,
        //  and call the bindPost method

        tokenViewModel.tokens.observe(this.viewLifecycleOwner) {
            it.let {
                if (it.isNotEmpty()) {

                    // networkViewModel.setToken(it[0])

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
                            // Log.d("save_btn", "work....")

                            Toast.makeText(
                                this.context, "click save button", Toast.LENGTH_SHORT
                            ).show()

                            addNewPost()
                        }
                    }


                    //val inputTitle = binding.titleLabel.editText?.text.toString()

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

                    /*
                    binding.titleLabel.editText?.doOnTextChanged { inputTitle, _, _, _ ->
                        Log.d("add_title", inputTitle.toString())
                        /*
                        Toast.makeText(this.context, "input input change", Toast.LENGTH_SHORT
                        ).show()
                        */
                    }

                     */
                }
            }
        }


        /*
        binding.contentLabel.editText?.doOnTextChanged {_, _, _, _ ->
            Toast.makeText(this.context, "content input change", Toast.LENGTH_SHORT
            ).show()

        }
        */
        /*
        binding.contentInput.addTextChangedListener {
            Toast.makeText(this.context, "content input change", Toast.LENGTH_SHORT
            ).show()
            cleanIsEmpty()
            actionIsEmpty()
        }
         */


    }

    //https://stackoverflow.com/questions/2986387/multi-line-edittext-with-done-action-button
    private fun editWithDoneBtn() {
        binding.contextInput.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
    }

    private fun addNewPost() {
        if (isEntryValid()) {
            networkViewModel.addPostAction(convertToDataClass())
            //add post
            observeError()
        } else {
            actionIsEmpty()
        }
    }

    private fun updatePost(post: Post) {
        if (isEntryValid()) {
            networkViewModel.editPostAction(post.id, convertToDataClass())
            //add post
            observeError()
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


    private fun observeError() {
        networkViewModel.error.observe(this.viewLifecycleOwner) {
            if (it == null) {
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
        return  str
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
        observeError()
    }


    private fun bindPost(post: Post) {
        binding.apply {
            usernameInput.setText(post.username, TextView.BufferType.SPANNABLE)
            titleInput.setText(post.title, TextView.BufferType.SPANNABLE)
            contextInput.setText(post.context?.let { convertListToString(it) }, TextView.BufferType.SPANNABLE)

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