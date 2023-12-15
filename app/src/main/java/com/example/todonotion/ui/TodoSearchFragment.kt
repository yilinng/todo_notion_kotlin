package com.example.todonotion.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
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
         var textUpdate = text.replace(" ", "+")
        Log.i("filteredList", textUpdate)
        //  val text = keyViewModel.word.value
        if (text != null) {
           // overViewModel.filterByTags
            overViewModel.getTodoPhotosByKeyWord(textUpdate)
        }
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
            defaultLoadingProgress()

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

    private fun setLoadingProgress(){
        binding.loadingImg.isIndeterminate = false
        binding.loadingImg.isVisible = true
    }

    private fun defaultLoadingProgress(){
        binding.loadingImg.isIndeterminate = true
        binding.loadingImg.isVisible = false
    }


    //reload fragment
    //https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
    /*
    private fun reload() {
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.run {
            popBackStack()
            navigate(R.id.todoSearchFragment)
        }
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
                setLoadingProgress()
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
        val adapter = KeyTodoAdapter {
            filteredList(it.keyName)
           // keyViewModel.onKeyWordClicked(it)
              keyViewModel.storeWordAction(it.keyName)
            val action =
                TodoSearchFragmentDirections.actionTodoSearchFragmentToTodoSearchResultFragment()
            this.findNavController().navigate(action)
        }

        binding.recyclerKeyView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerKeyView.adapter = adapter

        // Attach an observer on the allKeys list to update the UI automatically when the data
        // changes.
        keyViewModel.allKeys.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
                changeVisibility(it)
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

    }


}


