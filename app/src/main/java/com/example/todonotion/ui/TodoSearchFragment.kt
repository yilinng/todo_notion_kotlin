package com.example.todonotion.ui

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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todonotion.BaseApplication
import com.example.todonotion.R
import com.example.todonotion.data.Keyword.Keyword
import com.example.todonotion.databinding.FragmentSearchTodoBinding
import com.example.todonotion.overview.KeyViewModel
import com.example.todonotion.overview.KeyViewModelFactory
import com.example.todonotion.overview.OverViewModel
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.overview.auth.TokenViewModelFactory
import com.example.todonotion.ui.adapter.KeyTodoAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

//https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
//https://www.reddit.com/r/androiddev/comments/rrspel/rule_of_thumb_for_when_to_use_oncreateview/?rdt=39101
class TodoSearchFragment : Fragment() {
    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    private val keyViewModel: KeyViewModel by activityViewModels {
        KeyViewModelFactory(
            (activity?.application as BaseApplication).database.keyDao()
        )
    }

    private val tokenViewModel: TokenViewModel by activityViewModels {
        TokenViewModelFactory(
            (activity?.application as BaseApplication).database.tokenDao()
        )
    }

    //data from network
    private val overViewModel: OverViewModel by activityViewModels()

    private var _binding: FragmentSearchTodoBinding? = null
    private val binding get() = _binding!!

    private val args: TodoSearchFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchTodoBinding.inflate(inflater, container, false)
        // TODO: call the view model method that calls the todo api
        // Inflate the layout for this fragment
        return binding.root
    }




    private fun filteredList(text: String) {
         val textUpdate = text.replace(" ", "+")
        Log.i("filteredList", textUpdate)
        //  val text = keyViewModel.word.value
        overViewModel.getTodoPhotosByKeyWord(textUpdate)
    }

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     * https://stackoverflow.com/questions/48653099/checking-if-roomdatabase-is-empty-while-using-livedata
     * https://stackoverflow.com/questions/46723729/swift-if-let-statement-equivalent-in-kotlin
     * https://stackoverflow.com/questions/69663119/livedata-value-returns-null
     */
    private fun addNewKeyWord(text: String) {
        //check if value exists in database
        Log.d("keyCount", keyViewModel.filteredKeyCount().toString())
        if (keyViewModel.filteredKeyCount() == 0) {
            Log.d("keyCount0", keyViewModel.filteredKeyCount().toString())
            keyViewModel.addNewKey(text.lowercase())
        }
        //update search input value
        keyViewModel.storeWordAction(text)

        val action =
            TodoSearchFragmentDirections.actionTodoSearchFragmentToTodoSearchResultFragment()
        findNavController().navigate(action)

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
        for (item in keyViewModel.allKeys.value!!) {
            // checking if the entered string matched with any item of our recycler view.
            keyViewModel.deleteKey(item)
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
    private fun changeVisibility(keywords: List<Keyword>){
        //change keyword list to INVISIBLE when keyword list is empty
        if (keywords.isEmpty()) {
            //change keyword list hide
            binding.recyclerKeyView.visibility = View.INVISIBLE
            //change recent list hide
            binding.recentList.visibility = View.INVISIBLE
        } else {
            //change keyword list show
            binding.recyclerKeyView.visibility = View.VISIBLE
            //change recent list show
            binding.recentList.visibility = View.VISIBLE
            // binding.recyclerKeyView.setBackgroundResource(com.google.android.material.R.drawable.abc_list_divider_material)
        }
    }

    private fun observeToken() {
        tokenViewModel.tokens.observe(this.viewLifecycleOwner) {
            //  binding.tokenText.visibility = VISIBLE
            //  binding.tokenText.text = it.toString()
            //observe token change
        //    defaultLoadingProgress()

            /*
            if (it.isNotEmpty()) {
                binding.bottomNavigation.findViewById<View>(R.id.logout).isVisible = true
                binding.bottomNavigation.findViewById<View>(R.id.login).isVisible = false
            } else {
                binding.bottomNavigation.findViewById<View>(R.id.logout).isVisible = false
                binding.bottomNavigation.findViewById<View>(R.id.login).isVisible = true
            }
             */
        }
    }

    /*
    private fun setLoadingProgress(){
        binding.loadingImg.isIndeterminate = false
        binding.loadingImg.isVisible = true
    }

    private fun defaultLoadingProgress(){
        binding.loadingImg.isIndeterminate = true
        binding.loadingImg.isVisible = false
    }
    */


    /*
 * Creates and shows an AlertDialog with logout fun.
 */
    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.logout_cont))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                //logout action, clear token
              //  setLoadingProgress()
                deleteToken()
            }
            .show()
    }

    /*
    delete token
    */
    private fun deleteToken() {
        for (item in tokenViewModel.tokens.value!!) {
            // checking if the entered string matched with any item of our recycler view.
            tokenViewModel.deleteToken(item)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //when click on an item in keyword list, have to run filter function
        val adapter = KeyTodoAdapter{ keyword ->
            filteredList(keyword.keyName)
            keyViewModel.onKeyWordClicked(keyword)
            keyViewModel.storeWordAction(keyword.keyName)

            val action =
                TodoSearchFragmentDirections.actionTodoSearchFragmentToTodoSearchResultFragment()
           // this.findNavController().navigate(action)
        }

        binding.recyclerKeyView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerKeyView.adapter = adapter

        setupSnackbar()



        // Attach an observer on the allKeys list to update the UI automatically when the data changes.
        keyViewModel.allKeys.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
                changeVisibility(it)
            }
        }

        //Attach an observer on the allKeys list to update the UI automatically when the data change
        //https://developer.android.com/codelabs/basic-android-kotlin-training-intro-room-flow?hl=zh-tw&continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-kotlin-unit-5-pathway-1%3Fhl%3Dzh-tw%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-training-intro-room-flow#8
        lifecycle.coroutineScope.launch {
            keyViewModel.fullKeyword().collect() {
                adapter.submitList(it)
                Log.d("keywordClick", it.toString())
            }
        }

        observeToken()

        /*
        * Attach an observer on the filteredKeyWords list to update the UI automatically when the data changes.
        * https://stackoverflow.com/questions/58971518/how-to-use-recyclerview-itemanimator-with-recyclerview
        * https://stackoverflow.com/questions/55106938/change-style-of-recyclerview-item-onclick
        */
        keyViewModel.filteredKeywords.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
                changeVisibility(it)
            }
        }

        keyViewModel.snackbarText.observe(this.viewLifecycleOwner) {
            setupSnackbar()
        }

        /*
        search view set query ->keyword
        *https://stackoverflow.com/questions/27978283/search-view-close-icon-not-visible-when-expanded
        *https://stackoverflow.com/questions/14426769/how-to-change-android-searchview-text
        */
        /*
        binding.actionSearch.setIconifiedByDefault(true)
        binding.actionSearch.onActionViewExpanded()
        binding.actionSearch.setQuery(keyViewModel.storeKeyword.value, false)
        binding.actionSearch.clearFocus()
        */
        //when searchbar input value change, search keys have to store database.
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
                keyViewModel.filterByKeyWords(newText)
                //clean store keyword
                /*
                if(newText.isEmpty()) {
                    keyViewModel.storeWordAction(newText)
                }
                 */
                return false
            }
        })


        //https://stackoverflow.com/questions/24794377/how-do-i-capture-searchviews-clear-button-click
        binding.actionSearch.setOnCloseListener { // Do your stuff
            Log.d("searchClose", "work...")
            false
        }

        //click delete btn
        clickDeleteKeyWord()



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
                        keyViewModel.noKeywordIconRes.value?.let {
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


    private fun setupSnackbar() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return

        val mySnackbar = Snackbar.make(view, keyViewModel.snackbarText.toString(), Snackbar.LENGTH_SHORT)

        arguments.let {
            keyViewModel.showEditResultMessage(args.userMessage)
        }
       // keyViewModel.showEditResultMessage(arguments.getInt())

        mySnackbar.show()
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_keywords, menu)

            setOnMenuItemClickListener {
                keyViewModel.setFiltering(
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


}




