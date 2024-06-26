package com.example.todonotion.ui.todoList

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels


import androidx.fragment.app.viewModels

import androidx.lifecycle.ViewModelProvider

import androidx.navigation.fragment.findNavController

import androidx.slidingpanelayout.widget.SlidingPaneLayout

import com.example.todonotion.BaseApplication

import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentTodoListBinding

import com.example.todonotion.ui.adapter.TodoListAdapter

import com.example.todonotion.ui.adapter.TodoListener


import com.example.todonotion.ui.callback.ListOnBackPressedCallback

import com.google.android.material.tabs.TabLayout

import javax.inject.Inject

import com.example.todonotion.overview.TodoApiStatus
import com.example.todonotion.overview.auth.AuthNetworkViewModel

import com.example.todonotion.ui.todoDetails.TodoDetailFragment


/**
 * This fragment shows the the status of the Mars photos web services transaction.
 * https://github.com/google-developer-training/android-basics-kotlin-bus-schedule-app/tree/main/app/src/main/java/com/example/busschedule
 */

class TodoListFragment : Fragment() {
    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val todoListViewModel: TodoListViewModel by viewModels {
        viewModelFactory
    }

    private val authNetworkViewModel: AuthNetworkViewModel by activityViewModels {
        viewModelFactory
    }

    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!

    private lateinit var callback: ListOnBackPressedCallback


    //https://stackoverflow.com/questions/17436298/how-to-pass-a-variable-from-activity-to-fragment-and-pass-it-back
    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as BaseApplication).appComponent.todoListComponent().create()
            .inject(this)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: call the view model method that calls the todo api
        binding.lifecycleOwner = this
        binding.viewModel = todoListViewModel

        /*
        Log.d("todoList_arg", arguments.toString())

        val message = requireArguments().getString("mText")

        if (message != null) {
            if(message.isNotEmpty()) {
                todoListViewModel.getTodoPhotosByKeyWord(message)
            }
        }
        */
        val slidingPaneLayout = binding.slidingPaneLayout
        //https://medium.com/@Wingnut/tabbed-slidingpanelayout-primary-detail-using-the-navigation-component-library-%EF%B8%8F-6517a2c1e554
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        callback = ListOnBackPressedCallback(binding.slidingPaneLayout)
        // Connect the SlidingPaneLayout to the system back button.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        tabSelect()
        refreshPage()
        setupNavigation()
        observeStatus()
        observeFilteredKeyword()

        binding.recyclerView.adapter = TodoListAdapter(TodoListener { todo ->
            todoListViewModel.onTodoClicked(todo)
            // Slide the detail pane into view. If both panes are visible,
            // this has no visible effect.
            binding.slidingPaneLayout.openPane()
            //https://developer.android.com/codelabs/basic-android-kotlin-training-adaptive-layouts?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-kotlin-unit-3-pathway-5#8
            /*
            findNavController()
                .navigate(R.id.action_todoListFragment_to_todoDetailFragment)
             */
        })
    }

    private fun observeFilteredKeyword() {
        authNetworkViewModel.filteredKeyword.observe(this.viewLifecycleOwner) {
            if(it != null) {
                todoListViewModel.getTodoPhotosByKeyWord(it)
            }
        }
    }


    private fun setupNavigation() {
        todoListViewModel.todo.observe(this.viewLifecycleOwner) {
            if (it != null) {
                TodoDetailFragment.newInstance(it.id)
                openTodoDetails(it.id)
            }
        }

    }

    private fun observeStatus() {
        todoListViewModel.status.observe(this.viewLifecycleOwner) {
            if (it != TodoApiStatus.LOADING) {
                hideLoadingProgress()
            }
        }
    }


    private fun openTodoDetails(todoId: String) {
        val action = TodoListFragmentDirections.actionTodoListFragmentToTodoDetailFragment(todoId)
        findNavController().navigate(action)
        todoListViewModel.initTodo()
    }

    private fun showLoadingProgress() {
        binding.linearProgressIndicator.isVisible = true
        binding.recyclerView.isVisible = false
    }

    private fun hideLoadingProgress() {
        // binding.loadingImg.isIndeterminate = true
        binding.linearProgressIndicator.isVisible = false
        binding.recyclerView.isVisible = true
    }

    private fun tabSelect() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab select
                todoListViewModel.selectedTab(tab)
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
                navigate(R.id.todoListFragment)
            }
            binding.refreshLayout.isRefreshing = false
        }
    }
    /*
    //https://stackoverflow.com/questions/15560904/setting-custom-actionbar-title-from-fragment
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).supportActionBar?.title = getString(R.string.home_label)
    }
    */

    override fun onResume() {
        super.onResume()
        callback.onTabResumed()
    }

    override fun onPause() {
        super.onPause()
        callback.onTabPaused()
    }


}

/**
 * Callback providing custom back navigation.
 * https://developer.android.com/codelabs/basic-android-kotlin-training-adaptive-layouts?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-kotlin-unit-3-pathway-5%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-training-adaptive-layouts#8
 */

