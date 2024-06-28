package com.example.todonotion.ui.addPost

import android.content.Context
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

import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.example.todonotion.BaseApplication

import com.example.todonotion.R
import com.example.todonotion.data.model.Post
import com.example.todonotion.databinding.FragmentAddPostBinding

import com.example.todonotion.data.model.dto.PostDto
import com.example.todonotion.overview.auth.TokenViewModel

import com.example.todonotion.overview.auth.UserApiStatus
import com.example.todonotion.ui.postDetails.PostDetailFragment

import com.example.todonotion.util.ADD_ACTION
import com.example.todonotion.util.DELETE_ACTION
import com.example.todonotion.util.EDIT_ACTION
import com.example.todonotion.util.makeStatusNotification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.launch
import javax.inject.Inject


class AddPostFragment : Fragment() {

    /*
    private val tokenViewModel: TokenViewModel by activityViewModels {
        AppViewModelProvider.Factory
    }
    private val networkViewModel: AuthNetworkViewModel by activityViewModels {
        AuthNetworkViewModel.Factory
    }
     */

    private val fromArgs: AddPostFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val addPostViewModel: AddPostViewModel by viewModels {
        viewModelFactory
    }

    private val tokenViewModel: TokenViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var lastAccessToken: String

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var post: Post

    private lateinit var args: String

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as BaseApplication).appComponent.addPostComponent().create()
            .inject(this)

        args = arguments?.getString("postId").toString()

        Log.d("addPost_init", args + " " + fromArgs.postId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        // addPostViewModel.getPostAction(args)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = addPostViewModel

        lastAccessToken = ""
        observeTokenUser()
        observePost()
        observerToken()
        observeAccessToken()
        observeStatus()
        editWithDoneBtn()
        refreshPage()
    }

    private fun observePost() {
        addPostViewModel.post.observe(this.viewLifecycleOwner) {
            if (it != null) {
                post = it
                binding.addPostTitle.text = getString(R.string.update_post_title)
                Log.d("addPost_havePost", addPostViewModel.checkUserHavePost(post).toString())
                if (addPostViewModel.checkUserHavePost(post)) {
                    binding.deleteBtn.visibility = View.VISIBLE

                    binding.deleteBtn.setOnClickListener {
                        showLoadingProgress()
                        context?.let { it1 -> showDeleteDialog(it1, post) }

                    }
                    //update post action
                    bindPost(post)
                }
            }
        }
    }

    private fun observerToken() {
        tokenViewModel.tokens.observe(this.viewLifecycleOwner) {
            it.let {
                if (it.isNotEmpty()) {

                    addPostViewModel.setToken(it[0])
                    lastAccessToken = it[0].accessToken

                    if (addPostViewModel.post.value == null) {
                        binding.saveBtn.setOnClickListener {
                            /*
                            Toast.makeText(
                                this.context, "click save button", Toast.LENGTH_SHORT
                            ).show()
                             */
                            showLoadingProgress()
                            addNewPost()
                        }
                    }

                    binding.titleInput.addTextChangedListener {
                        /*
                        Toast.makeText(
                            this.context, "title input change", Toast.LENGTH_SHORT
                        ).show()
                         */
                        cleanIsEmpty()
                        actionIsEmpty()
                    }

                    binding.contextInput.addTextChangedListener {
                        /*
                        Toast.makeText(
                            this.context, "context input change", Toast.LENGTH_SHORT
                        ).show()
                         */
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

    private fun observeStatus() {
        addPostViewModel.status.observe(this.viewLifecycleOwner) {
            if(it == UserApiStatus.LOADING) {
                binding.addPostLabel.isVisible = false
                binding.usernameLabel.isVisible = false
                binding.titleLabel.isVisible = false
                binding.contextLabel.isVisible = false
                binding.bottomList.isVisible = false
            }
            if(it == UserApiStatus.DONE) {
                binding.addPostLabel.isVisible = true
                binding.usernameLabel.isVisible = true
                binding.titleLabel.isVisible = true
                binding.contextLabel.isVisible = true
                binding.bottomList.isVisible = true
                hideLoadingProgress()
            }

            if(it == UserApiStatus.ERROR) {
                binding.addPostLabel.isVisible = false
                binding.usernameLabel.isVisible = false
                binding.titleLabel.isVisible = false
                binding.contextLabel.isVisible = false
                binding.bottomList.isVisible = false
                hideLoadingProgress()
            }
        }
    }

    private fun observeError() {
        addPostViewModel.error.observe(this.viewLifecycleOwner) { items ->
            items.let {
                if (it != null) {
                    if ("Invalid Token" in it.toString()) {
                        addPostViewModel.updateToken()
                    }
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.setTextColor(Color.RED)
                    Log.i("addPost400", it)
                    binding.errorText.text = getString(R.string.signup_server_error)
                    // binding.errorText.text = it.toString()

                }
            }
        }
    }

    private fun observeTokenUser() {
        Log.d("addPost_observeTokenUser_init", tokenViewModel.user.value.toString())
        tokenViewModel.user.observe(this.viewLifecycleOwner) {
            Log.d("addPost_observeTokenUser", it.toString())
            if (it != null) {
                addPostViewModel.setUser(it)
                observeUser()
            } else {
                addPostViewModel.initUser()
            }

        }

    }

    private fun observeUser() {
        addPostViewModel.user.observe(this.viewLifecycleOwner) {
            if (it != null) {
                Log.d("addPost_observeUser", it.toString())

                addPostViewModel.getPostAction(args)
            }
        }
    }

    private fun observeAccessToken() {
        addPostViewModel.token.observe(this.viewLifecycleOwner) {
            if (lastAccessToken != "" && it != null && it.accessToken != lastAccessToken) {
                tokenViewModel.updateToken(it)
            }
        }
    }

    //https://stackoverflow.com/questions/2986387/multi-line-edittext-with-done-action-button
    private fun editWithDoneBtn() {
        binding.contextInput.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
    }

    private fun addNewPost() {
        if (isEntryValid()) {
            addPostViewModel.addPostAction(convertToDataClass())
            //add post
            observeStatus(ADD_ACTION)
            observeError()
        } else {
            actionIsEmpty()
        }
    }

    private fun updatePost(post: Post) {
        if (isEntryValid()) {
            addPostViewModel.editPostAction(post.id, convertToDataClass())
            //add post
            observeStatus(EDIT_ACTION)
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


    //https://github.com/google-developer-training/basic-android-kotlin-compose-training-workmanager/blob/intermediate/app/src/main/java/com/example/bluromatic/workers/WorkerUtils.kt
    //https://developer.android.com/develop/ui/views/notifications/build-notification
    //https://stackoverflow.com/questions/58526610/what-channelid-should-i-pass-to-the-constructor-of-notificationcompat-builder
    private fun observeStatus(action: String) {
        addPostViewModel.status.observe(this.viewLifecycleOwner) {
            if (it == UserApiStatus.DONE) {
                when (action) {
                    "ADD" -> {
                        makeStatusNotification(
                            requireContext().resources.getString(R.string.successfully_add_post),
                            requireContext()
                        )
                    }

                    "EDIT" -> {
                        makeStatusNotification(
                            requireContext().resources.getString(R.string.successfully_edit_post),
                            requireContext()
                        )
                    }

                    else -> {
                        makeStatusNotification(
                            requireContext().resources.getString(R.string.successfully_delete_post),
                            requireContext()
                        )
                    }
                }
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
        return addPostViewModel.isPostEntryValid(
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
        addPostViewModel.deletePostAction(post.id)
        observeStatus(DELETE_ACTION)
        observeError()
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun showDeleteDialog(context: Context, post: Post) {
        MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.delete_post_title))
            .setMessage(getString(R.string.delete_post_cont))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                GlobalScope.launch {
                    deletePost(post)
                }

            }
            .show()
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
                showLoadingProgress()
                updatePost(post)
            }

        }

    }

    private fun hideLoadingProgress() {
        binding.circleProgressIndicator.isVisible = false
    }

    private fun showLoadingProgress() {
        binding.circleProgressIndicator.isVisible = true

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

    companion object {
        @JvmStatic
        fun newIdInstance(id: String) = PostDetailFragment().apply {
            arguments = Bundle().apply {
                putString("postId", id)
            }
        }
    }

}