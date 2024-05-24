package com.example.todonotion.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.todonotion.AppViewModelProvider

import com.example.todonotion.R

import com.example.todonotion.databinding.FragmentSearchResultBinding
import com.example.todonotion.overview.KeyViewModel

import com.example.todonotion.overview.TodoViewModel
import com.example.todonotion.ui.adapter.TodoListAdapter
import com.example.todonotion.ui.adapter.TodoListener
import com.example.todonotion.ui.callback.ListOnBackPressedCallback


//https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
//https://www.reddit.com/r/androiddev/comments/rrspel/rule_of_thumb_for_when_to_use_oncreateview/?rdt=39101
class TodoSearchResultFragment : Fragment() {
    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    private val keyViewModel: KeyViewModel by activityViewModels {
        AppViewModelProvider.Factory
    }

    //data from network
    private val todoViewModel: TodoViewModel by activityViewModels{
        TodoViewModel.Factory
    }

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var callback: ListOnBackPressedCallback


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater)
        // TODO: call the view model method that calls the todo api
        // Inflate the layout for this fragment
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
        binding.viewModel = todoViewModel

        val slidingPaneLayout = binding.slidingPaneLayout
        //https://medium.com/@Wingnut/tabbed-slidingpanelayout-primary-detail-using-the-navigation-component-library-%EF%B8%8F-6517a2c1e554
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        callback = ListOnBackPressedCallback(binding.slidingPaneLayout)
        // Connect the SlidingPaneLayout to the system back button.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        val todoAdapter = TodoListAdapter(TodoListener { todo ->
            todoViewModel.onTodoClicked(todo)
            // Slide the detail pane into view. If both panes are visible,
            // this has no visible effect.
            binding.slidingPaneLayout.openPane()
            //https://developer.android.com/codelabs/basic-android-kotlin-training-adaptive-layouts?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-kotlin-unit-3-pathway-5#8
            /*
            findNavController()
                .navigate(R.id.action_todoSearchResultFragment_to_todoDetailFragment)
             */
        })

        //relate todolist
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = todoAdapter


        // Attach an observer on the filteredTodos list to update the UI automatically when the data
        // changes.
        todoViewModel.filteredTodos.observe(this.viewLifecycleOwner) { items ->
            items.let {
                todoAdapter.submitList(it)
                //todoAdapter.filterList()
            }
        }

        /*
        search view set query ->keyword
        *https://stackoverflow.com/questions/27978283/search-view-close-icon-not-visible-when-expanded
        *https://stackoverflow.com/questions/14426769/how-to-change-android-searchview-text
        */
        binding.actionSearch.setIconifiedByDefault(true)
        binding.actionSearch.onActionViewExpanded()
        binding.actionSearch.setQuery(keyViewModel.storeKeyword.value, false)
        binding.actionSearch.clearFocus()


        //when searchbar input value change.
        binding.actionSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //Log.d("searchKey1", binding.actionSearch.query.toString())
                //redirect to last page
                redirectPage()
                return false
            }
        })

        //https://developer.android.com/develop/ui/views/touch-and-input/swipe/respond-refresh-request
        //refresh page
        binding.refreshLayout.setOnRefreshListener{
            Log.d("onRefresh", "onRefresh called from SwipeRefreshLayout")

            //https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
            val navController = findNavController()
            navController.run {
                popBackStack()
                navigate(R.id.todoSearchResultFragment)
            }
            binding.refreshLayout.isRefreshing = false
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


}


