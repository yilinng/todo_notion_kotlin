package com.example.todonotion.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todonotion.BaseApplication
import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentSearchResultBinding
import com.example.todonotion.overview.KeyViewModel
import com.example.todonotion.overview.KeyViewModelFactory
import com.example.todonotion.overview.OverViewModel
import com.example.todonotion.ui.adapter.TodoListAdapter
import com.example.todonotion.ui.adapter.TodoListener


//https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
//https://www.reddit.com/r/androiddev/comments/rrspel/rule_of_thumb_for_when_to_use_oncreateview/?rdt=39101
class TodoSearchResultFragment : Fragment() {
    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    private val keyViewModel: KeyViewModel by activityViewModels {
        KeyViewModelFactory(
            (activity?.application as BaseApplication).database.keyDao()
        )
    }

    //data from network
    private val overViewModel: OverViewModel by activityViewModels()

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchResultBinding.inflate(inflater)
        // TODO: call the view model method that calls the todo api
        binding.lifecycleOwner = this
        binding.viewModel = overViewModel

        // Inflate the layout for this fragment
        return binding.root

    }

    /*
    private fun redirectPage(){
        this.findNavController()
            .navigate(R.id.action_todoSearchResultFragment_to_todoSearchFragment)
    }
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val todoAdapter = TodoListAdapter(TodoListener { todo ->
            overViewModel.onTodoClicked(todo)
            findNavController()
                .navigate(R.id.action_todoSearchResultFragment_to_todoDetailFragment)
        })

        //relate todolist
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = todoAdapter


        // Attach an observer on the filteredTodos list to update the UI automatically when the data
        // changes.
        overViewModel.filteredTodos.observe(this.viewLifecycleOwner) { items ->
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
             //   redirectPage()
                return false
            }
        })

    }


}


