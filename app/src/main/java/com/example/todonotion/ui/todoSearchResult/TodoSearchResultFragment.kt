package com.example.todonotion.ui.todoSearchResult

import android.content.Context
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import android.view.View
import android.view.ViewGroup


import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider

import androidx.core.view.isVisible


import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.todonotion.BaseApplication

import com.example.todonotion.R
import com.example.todonotion.ui.adapter.KeyTodoAdapter
import com.example.todonotion.databinding.FragmentSearchResultBinding
import com.example.todonotion.overview.TodoApiStatus

import com.example.todonotion.ui.adapter.TodoListAdapter
import com.example.todonotion.ui.adapter.TodoListener
import com.example.todonotion.ui.callback.ListOnBackPressedCallback


import com.example.todonotion.ui.todoDetails.TodoDetailFragment

import com.example.todonotion.ui.todoSearch.TodoSearchFragmentDirections
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import javax.inject.Inject

//https://stackoverflow.com/questions/59239110/how-to-inflate-different-views-in-a-fragment
//https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
//https://www.reddit.com/r/androiddev/comments/rrspel/rule_of_thumb_for_when_to_use_oncreateview/?rdt=39101
class TodoSearchResultFragment : Fragment() {

    private val fromArgs: TodoSearchResultFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val todoSearchResultViewModel: TodoSearchResultViewModel by viewModels {
        viewModelFactory
    }

    private val keywordViewModel: KeywordViewModel by viewModels {
        viewModelFactory
    }

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var args: String

    private lateinit var callback: ListOnBackPressedCallback

    private var findSearchView: SearchView? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as BaseApplication).appComponent.todoSearchResultComponent()
            .create()
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


    private fun redirectPage() {
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

        //when click on an item in keyword list, have to run filter function
        val keyAdapter = KeyTodoAdapter({ keyword, string ->
            //when click close button
            if (string == "close") {
                Log.d("searchResult_close", keyword.keyName)
                //todoSearchViewModel.storeWordAction(keyword)
                //  keywordViewModel.deleteKey(keyword)
            } else {
                // todoSearchViewModel.onKeyWordClicked(keyword)
                Log.d("searchResult_key", keyword.keyName)
                filteredList(keyword.keyName)
            }
        }) {

        }


        binding.layoutSearch.recyclerKeyView.layoutManager =
            FlexboxLayoutManager(this.context)//LinearLayoutManager(this.context)
        binding.layoutSearch.recyclerKeyView.adapter = keyAdapter

        /*
       * Attach an observer on the filteredKeyWords list to update the UI automatically when the data changes.
       * https://stackoverflow.com/questions/58971518/how-to-use-recyclerview-itemanimator-with-recyclerview
       * https://stackoverflow.com/questions/55106938/change-style-of-recyclerview-item-onclick
       */
        // Attach an observer on the allKeys list to update the UI automatically when the data changes.
        keywordViewModel.allKeys.observe(this.viewLifecycleOwner) {
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    //showLoadingProgress()
                    //delay(1000)
                    //hideLoadingProgress()
                    if (it.isEmpty()) {
                        binding.layoutSearch.recentList.visibility = View.GONE
                    }
                    // binding.layoutSearch.recentList.isVisible = it.isNotEmpty()
                    keyAdapter.submitList(it)
                }
            }
        }


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
        setupNavigationToDetails()
        observeStatus()
        defaultSearchView()
        menuEvent()
    }

    //https://stackoverflow.com/questions/70054084/listener-for-clicking-on-any-area-of-the-searchview

    private fun filteredList(text: String) {
        findSearchView?.clearFocus()
        if (args != text) {
            val textUpdate = text.replace(" ", "+")
            Log.i("filteredList", textUpdate)
            newInstance(textUpdate)
            val action =
                TodoSearchResultFragmentDirections.actionTodoSearchResultFragmentToTodoSearchResultFragment(
                    textUpdate
                )
            findNavController().navigate(action)
        }

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
        val action =
            TodoSearchResultFragmentDirections.actionTodoSearchResultFragmentToTodoDetailFragment(
                todoId
            )
        findNavController().navigate(action)
        todoSearchResultViewModel.initTodo()
    }

    private fun defaultSearchView() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.layoutSearch.recentList.visibility = View.GONE
        binding.layoutSearch.recyclerKeyView.visibility = View.GONE
        binding.layoutSearch.keyList.visibility = View.GONE
    }

    private fun onSearchClickViewExpanded() {
        binding.recyclerView.visibility = View.GONE
        binding.layoutSearch.recentList.visibility = View.VISIBLE
        binding.layoutSearch.recyclerKeyView.visibility = View.VISIBLE
        binding.layoutSearch.keyList.visibility = View.VISIBLE

        findSearchView!!.onActionViewExpanded()
        findSearchView!!.setQuery(args, false)

    }

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     * https://stackoverflow.com/questions/48653099/checking-if-roomdatabase-is-empty-while-using-livedata
     * https://stackoverflow.com/questions/46723729/swift-if-let-statement-equivalent-in-kotlin
     * https://stackoverflow.com/questions/69663119/livedata-value-returns-null
     */
    private fun addNewKeyWord(text: String) {
        //check if value exists in database
        Log.d("keyCount", keywordViewModel.filteredKeyCount().toString())
        if (keywordViewModel.filteredKeyCount() == 0) {
            Log.d("keyCount0", keywordViewModel.filteredKeyCount().toString())
            keywordViewModel.addNewKey(text.lowercase())
        }
        //update search input value
        //todoSearchViewModel.storeWordAction(text)

    }


    /*
    private fun refreshPage() {
        //https://developer.android.com/develop/ui/views/touch-and-input/swipe/respond-refresh-request
        binding.refreshLayout.setOnRefreshListener {
            Log.d("onRefresh", "onRefresh called from SwipeRefreshLayout")

            //https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
            val navController = findNavController()
            val action =
                TodoSearchFragmentDirections.actionTodoSearchFragmentToTodoSearchResultFragment(
                    fromArgs.keyword
                )
            navController.run {
                popBackStack()
                navigate(action)
            }
            binding.refreshLayout.isRefreshing = false
        }
    }
    */
    private fun observeStatus() {
        todoSearchResultViewModel.status.observe(this.viewLifecycleOwner) {
            if (it == TodoApiStatus.LOADING || it == TodoApiStatus.ERROR) {
                // binding.actionSearch.isVisible = false
                binding.recyclerView.isVisible = false
                binding.detailContainer.isVisible = false
            } else {
                // binding.actionSearch.isVisible = true
                binding.recyclerView.isVisible = true
                binding.detailContainer.isVisible = true
            }
        }
    }

    private fun showLoadingProgress() {
        /*
        binding.layoutSearch.circleProgressIndicator.visibility = View.VISIBLE

        binding.layoutSearch.recyclerKeyView.visibility = View.GONE
        binding.layoutSearch.recentList.visibility = View.GONE

         */
    }

    private fun hideLoadingProgress() {
        /*
        binding.layoutSearch.circleProgressIndicator.visibility = View.GONE

        binding.layoutSearch.recentList.visibility = View.VISIBLE
        binding.layoutSearch.recyclerKeyView.visibility = View.VISIBLE

         */
    }

    //https://developer.android.com/develop/ui/views/components/appbar/action-views
    private fun menuEvent() {
        //https://stackoverflow.com/questions/71917856/sethasoptionsmenuboolean-unit-is-deprecated-deprecated-in-java
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.action_search_menu, menu)

                // Configure the search info and add any event listeners.
                // Define the listener.
                val expandListener = object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        // Do something when the action item collapses.
                        Log.d("searchResult_collapse", "work")

                        return true // Return true to collapse the action view.
                    }

                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        Log.d("searchResult_Expand", "work")
                        // Do something when it expands.
                        return true // Return true to expand the action view.
                    }

                }

                // Get the MenuItem for the action item.
                val actionMenuItem = menu.findItem(R.id.item_search)

                // Assign the listener to that action item.
                actionMenuItem?.setOnActionExpandListener(expandListener)

                findSearchView = actionMenuItem?.actionView as SearchView

                findSearchView!!.isIconified = true
                findSearchView!!.setIconifiedByDefault(false)
                findSearchView!!.onActionViewExpanded()
                findSearchView!!.setQuery(args, false)
                findSearchView!!.clearFocus()

                searchEvent(findSearchView!!)
                findSearchView!!.setOnQueryTextFocusChangeListener { v, hasFocus -> if (hasFocus) onSearchClickViewExpanded() }


            }

            //https://stackoverflow.com/questions/10770055/use-toast-inside-fragment
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.item_search -> true
                    //up button
                    android.R.id.home -> {
                        Log.d("searchResult_up_button", "work")
                        findSearchView!!.clearFocus()
                        findSearchView!!.setIconifiedByDefault(false)
                        if(binding.layoutSearch.keyList.visibility == View.GONE) {
                            //https://stackoverflow.com/questions/10863572/programmatically-go-back-to-the-previous-fragment-in-the-backstack
                            findNavController().popBackStack()
                        }
                        defaultSearchView()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun searchEvent(findSearchView: SearchView) {

        findSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                //add keyword to database
                addNewKeyWord(query)
                //filter list by keyword
                filteredList(query)
                Log.d("searchResult_onQueryTextSubmit", query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                //filter keyword list by nextText
                //  keywordViewModel.filterByKeyWords(newText)
                Log.d("searchResult_onQueryTextChange", newText)
                keywordViewModel.filterByKeyWords(newText)
                /*
                if(newText.isEmpty()) {
                    Log.d("searchResult_empty", newText)
                    onSearchClickViewExpanded()
                }
                */
                return false
            }

        })

    }


    override fun onResume() {
        super.onResume()
        callback.onTabResumed()
    }

    override fun onPause() {
        super.onPause()
        callback.onTabPaused()
    }


    //https://stackoverflow.com/questions/37401287/how-to-add-a-listener-for-the-up-button-in-actionbar-on-android

    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Log.d("todoSearchResult_up", "Toolbar Back BTN Click")
                false
            }
            else -> {
                Log.d("todoSearchResult_other", item.itemId.toString())
                super.onOptionsItemSelected(item)
            }
        }
    }
    */

    companion object {
        @JvmStatic
        fun newInstance(keyword: String) = TodoSearchResultFragment().apply {
            arguments = Bundle().apply {
                putString("keyword", keyword)
            }
        }
    }


}


