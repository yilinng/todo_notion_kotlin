package com.example.todonotion.ui.todoSearch

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import android.widget.Toast

import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.todonotion.R
import com.example.todonotion.data.keyword.Keyword
import com.example.todonotion.databinding.FragmentSearchTodoBinding

import com.example.todonotion.ui.adapter.KeyTodoAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.todonotion.BaseApplication
import com.example.todonotion.ui.KeywordsFilterType
import com.example.todonotion.ui.todoSearchResult.TodoSearchResultFragment
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


//https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
//https://www.reddit.com/r/androiddev/comments/rrspel/rule_of_thumb_for_when_to_use_oncreateview/?rdt=39101
class TodoSearchFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val todoSearchViewModel: TodoSearchViewModel by activityViewModels {
        viewModelFactory
    }

    private var _binding: FragmentSearchTodoBinding? = null
    private val binding get() = _binding!!

    private val args: TodoSearchFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as BaseApplication).appComponent.todoSearchComponent()
            .create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchTodoBinding.inflate(inflater, container, false)
        // TODO: call the view model method that calls the todo api
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //when click on an item in keyword list, have to run filter function
        val adapter = KeyTodoAdapter({ keyword, string ->
            //when click close button
            if (string == "close") {
                //todoSearchViewModel.storeWordAction(keyword)
                todoSearchViewModel.deleteKey(keyword)
            } else {
                // todoSearchViewModel.onKeyWordClicked(keyword)
                filteredList(keyword.keyName)
            }
        }) {

        }
        binding.recyclerKeyView.layoutManager =
            FlexboxLayoutManager(this.context)//LinearLayoutManager(this.context)
        binding.recyclerKeyView.adapter = adapter

        /*
         * Attach an observer on the filteredKeyWords list to update the UI automatically when the data changes.
         * https://stackoverflow.com/questions/58971518/how-to-use-recyclerview-itemanimator-with-recyclerview
         * https://stackoverflow.com/questions/55106938/change-style-of-recyclerview-item-onclick
         */
        // Attach an observer on the allKeys list to update the UI automatically when the data changes.
        todoSearchViewModel.allKeys.observe(this.viewLifecycleOwner) {
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    showLoadingProgress()
                    delay(1000)
                    hideLoadingProgress()
                    binding.recentList.isVisible = it.isNotEmpty()
                    adapter.submitList(it)
                }
            }
        }

        //https://stackoverflow.com/questions/24794377/how-do-i-capture-searchviews-clear-button-click
        binding.actionSearch.setOnCloseListener { // Do your stuff
            Log.d("searchClose", "work...")
            false
        }
        setupSnack()
        observeSnackBarText()
        searchEvent()
        //click delete btn
        clickDeleteKeyWord()
        // menuEvent()
        refreshPage()
    }

    private fun observeSnackBarText() {
        setupSnack()
    }


    private fun searchEvent() {
        binding.actionSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //add keyword to database
                addNewKeyWord(query)
                //filter list by keyword
                filteredList(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                //filter keyword list by nextText
                todoSearchViewModel.filterByKeyWords(newText)
                return false
            }
        })
    }

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
                menuInflater.inflate(R.menu.actionbar_menu, menu)
            }

            //https://stackoverflow.com/questions/10770055/use-toast-inside-fragment
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.menu_filter -> {
                        Toast.makeText(activity, "menu filter Clicked", Toast.LENGTH_SHORT).show()
                        todoSearchViewModel.noKeywordIconRes.value?.let {
                            binding.noKeywordsIcon.setImageResource(
                                it
                            )
                        }
                        showFilteringPopUpMenu()
                        true
                    }

                    R.id.refresh -> {
                        Toast.makeText(activity, "menu refresh Clicked", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.menu_clear -> {
                        Toast.makeText(activity, "menu clear Clicked", Toast.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
                navigate(R.id.todoSearchFragment)
            }
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun setupSnack() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return

        val item =
            Snackbar.make(view, todoSearchViewModel.snackbarText.toString(), Snackbar.LENGTH_SHORT)
        arguments.let {
            todoSearchViewModel.showEditResultMessage(args.userMessage)
        }
        // keyViewModel.showEditResultMessage(arguments.getInt())

        item.show()
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_keywords, menu)

            setOnMenuItemClickListener {
                todoSearchViewModel.setFiltering(
                    when (it.itemId) {
                        R.id.active -> KeywordsFilterType.ACTIVE_KEYWORDS
                        R.id.completed -> KeywordsFilterType.COMPLETED_KEYWORDS
                        else -> KeywordsFilterType.ALL_KEYWORDS
                    }
                )
                true
            }
            show()
        }
    }

    private fun filteredList(text: String) {
        val textUpdate = text.replace(" ", "+")
        Log.i("filteredList", textUpdate)
        TodoSearchResultFragment.newInstance(textUpdate)
        val action =
            TodoSearchFragmentDirections.actionTodoSearchFragmentToTodoSearchResultFragment(
                textUpdate
            )
        findNavController().navigate(action)
    }

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     * https://stackoverflow.com/questions/48653099/checking-if-roomdatabase-is-empty-while-using-livedata
     * https://stackoverflow.com/questions/46723729/swift-if-let-statement-equivalent-in-kotlin
     * https://stackoverflow.com/questions/69663119/livedata-value-returns-null
     */
    private fun addNewKeyWord(text: String) {
        //check if value exists in database
        Log.d("keyCount", todoSearchViewModel.filteredKeyCount().toString())
        if (todoSearchViewModel.filteredKeyCount() == 0) {
            Log.d("keyCount0", todoSearchViewModel.filteredKeyCount().toString())
            todoSearchViewModel.addNewKey(text.lowercase())
        }
        //update search input value
        //todoSearchViewModel.storeWordAction(text)

    }

    /*
 * Creates and shows an AlertDialog with final score.
 */
    private fun showClearDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.clear_title))
            .setMessage(getString(R.string.clear_cont))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(getString(R.string.clear)) { _, _ ->
                deleteAll()
            }
            .show()
    }

    /*
    delete all keyword
   */
    private fun deleteAll() {
        for (item in todoSearchViewModel.allKeys.value!!) {
            // checking if the entered string matched with any item of our recycler view.
            todoSearchViewModel.deleteKey(item)
        }
    }


    //https://developer.android.com/guide/topics/ui/controls/button
    //delete keyword when click close icon
    private fun clickDeleteKeyWord() {
        binding.closeBtn.setOnClickListener {
            Log.d("clickDelete", "work")
            showClearDialog()
            // Do something in response to button click
        }
    }

    //https://stackoverflow.com/questions/56463883/toggling-visibility-in-kotlin-isvisible-unresolved-reference

    private fun showLoadingProgress() {
        binding.circleProgressIndicator.isVisible = true
        binding.actionSearch.isVisible = false
        binding.recyclerKeyView.isVisible = false
        binding.recentList.isVisible = false
    }

    private fun hideLoadingProgress() {
        binding.circleProgressIndicator.isVisible = false
        binding.actionSearch.isVisible = true
        binding.recentList.isVisible = true
        binding.recyclerKeyView.isVisible = true
    }


}




