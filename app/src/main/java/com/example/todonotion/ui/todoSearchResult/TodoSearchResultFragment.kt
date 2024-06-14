package com.example.todonotion.ui.todoSearchResult

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.todonotion.BaseApplication

import com.example.todonotion.R

import com.example.todonotion.databinding.FragmentSearchResultBinding
import com.example.todonotion.overview.TodoApiStatus

import com.example.todonotion.ui.adapter.TodoListAdapter
import com.example.todonotion.ui.adapter.TodoListener
import com.example.todonotion.ui.callback.ListOnBackPressedCallback
import com.example.todonotion.ui.postDetails.PostDetailFragment
import com.example.todonotion.ui.postDetails.PostDetailFragmentArgs
import com.example.todonotion.ui.todoDetails.TodoDetailFragment
import com.example.todonotion.ui.todoList.TodoListFragmentDirections
import com.example.todonotion.ui.todoSearch.TodoSearchFragmentDirections
import com.example.todonotion.ui.todoSearch.TodoSearchViewModel
import javax.inject.Inject


//https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
//https://www.reddit.com/r/androiddev/comments/rrspel/rule_of_thumb_for_when_to_use_oncreateview/?rdt=39101
class TodoSearchResultFragment : Fragment() {

    private val fromArgs: TodoSearchResultFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val todoSearchResultViewModel: TodoSearchResultViewModel by viewModels {
        viewModelFactory
    }

    private val keywordViewModel: KeywordViewModel by activityViewModels {
        viewModelFactory
    }
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var args: String

    private lateinit var callback: ListOnBackPressedCallback


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as BaseApplication).appComponent.todoSearchResultComponent().create()
            .inject(this)

        args = arguments?.getString("keyword").toString()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater)
        // TODO: call the view model method that calls the todo api
        // Inflate the layout for this fragment
        Log.d("todoSearchResult_arg", fromArgs.keyword + " " + args)
        todoSearchResultViewModel.getTodoPhotosByKeyWord(fromArgs.keyword)
        todoSearchResultViewModel.setKeyword(fromArgs.keyword)
        return binding.root
    }


    private fun redirectPage(){
        val navController = findNavController()
        navController.run {
            popBackStack()
            navigate(R.id.todoSearchFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = todoSearchResultViewModel

        val slidingPaneLayout = binding.slidingPaneLayout
        //https://medium.com/@Wingnut/tabbed-slidingpanelayout-primary-detail-using-the-navigation-component-library-%EF%B8%8F-6517a2c1e554
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        callback = ListOnBackPressedCallback(binding.slidingPaneLayout)
        // Connect the SlidingPaneLayout to the system back button.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        val todoAdapter = TodoListAdapter(TodoListener { todo ->
            todoSearchResultViewModel.onTodoClicked(todo)
            // Slide the detail pane into view. If both panes are visible,
            // this has no visible effect.
            binding.slidingPaneLayout.openPane()
        })

        //relate todolist
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = todoAdapter

        //observeStatus()
        //when searchbar input value change.
        searchAction()
        refreshPage()
        setupNavigationToDetails()
        observeStatus()

        /*
        search view set query ->keyword
        *https://stackoverflow.com/questions/27978283/search-view-close-icon-not-visible-when-expanded
        *https://stackoverflow.com/questions/14426769/how-to-change-android-searchview-text
        */
        binding.actionSearch.setIconifiedByDefault(true)
        binding.actionSearch.onActionViewExpanded()
        binding.actionSearch.setQuery(keywordViewModel.storeKeyword.value, false)
        binding.actionSearch.clearFocus()

    }

    private fun setupNavigationToDetails() {
        todoSearchResultViewModel.todo.observe(this.viewLifecycleOwner) {
            if (it != null) {
                TodoDetailFragment.newInstance(it.id)
                openTodoDetails(it.id)
            }
        }
    }

    private fun openTodoDetails(todoId: String) {
        val action = TodoSearchResultFragmentDirections.actionTodoSearchResultFragmentToTodoDetailFragment(todoId)
        findNavController().navigate(action)
        todoSearchResultViewModel.initTodo()
    }

    private fun searchAction() {
        binding.actionSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //Log.d("searchKey1", binding.actionSearch.query.toString())
                //redirect to last page
               // redirectPage()
                return false
            }
        })
    }

    private fun refreshPage() {
        //https://developer.android.com/develop/ui/views/touch-and-input/swipe/respond-refresh-request
        binding.refreshLayout.setOnRefreshListener{
            Log.d("onRefresh", "onRefresh called from SwipeRefreshLayout")

            //https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
            val navController = findNavController()
            val action = TodoSearchFragmentDirections.actionTodoSearchFragmentToTodoSearchResultFragment(fromArgs.keyword)
            navController.run {
                popBackStack()
                navigate(action)
            }
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun observeStatus() {
        todoSearchResultViewModel.status.observe(this.viewLifecycleOwner) {
            if(it == TodoApiStatus.LOADING || it == TodoApiStatus.ERROR) {
                binding.actionSearch.isVisible = false
                binding.recyclerView.isVisible = false
                binding.detailContainer.isVisible = false
            }else{
                binding.actionSearch.isVisible = true
                binding.recyclerView.isVisible = true
                binding.detailContainer.isVisible = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        callback.onTabResumed()
    }

    override fun onPause() {
        super.onPause()
        callback.onTabPaused()
    }

    companion object {
        @JvmStatic
        fun newInstance(keyword: String) = TodoSearchResultFragment().apply {
            arguments = Bundle().apply {
                putString("keyword", keyword)
            }
        }
    }


}


