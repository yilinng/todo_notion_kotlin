package com.example.todonotion.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.fragment.app.Fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentTodoListBinding
import com.example.todonotion.overview.OverViewModel
import com.example.todonotion.ui.adapter.TodoListAdapter

import com.example.todonotion.ui.adapter.TodoListener
import com.example.todonotion.BaseApplication
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.overview.auth.TokenViewModelFactory

import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * This fragment shows the the status of the Mars photos web services transaction.
 */

class TodoListFragment : Fragment() {
    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    private val viewModel: OverViewModel by activityViewModels()

    private val tokenViewModel: TokenViewModel by activityViewModels {
        TokenViewModelFactory(
            (activity?.application as BaseApplication).database.tokenDao()
        )
    }

    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    //https://stackoverflow.com/questions/61023968/what-do-i-use-now-that-handler-is-deprecated
    private fun observeToken() {
        tokenViewModel.tokens.observe(this.viewLifecycleOwner) {
            lifecycleScope.launch {
                showLoadingProgress()
                delay(3000)
                hideLoadingProgress()
            }
        }
    }

    private fun showLoadingProgress(){
        //binding.loadingImg.isIndeterminate = false
        binding.linearProgressIndicator.isVisible = true
        binding.recyclerView.isVisible = false
    }

    private fun hideLoadingProgress(){
       // binding.loadingImg.isIndeterminate = true
        binding.linearProgressIndicator.isVisible = false
        binding.recyclerView.isVisible = true
    }


    //reload fragment
    //https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
    /*
    private fun reload() {
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.run {
            popBackStack()
            navigate(R.id.todoListFragment)
        }
    }

     */


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: call the view model method that calls the todo api
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        observeToken()

        binding.recyclerView.adapter = TodoListAdapter(TodoListener { todo ->
            viewModel.onTodoClicked(todo)
            findNavController()
                .navigate(R.id.action_todoListFragment_to_todoDetailFragment)
        })


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab select
                viewModel.selectedTab(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
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
                navigate(R.id.todoListFragment)
            }
            binding.refreshLayout.isRefreshing = false
        }

    }


}
