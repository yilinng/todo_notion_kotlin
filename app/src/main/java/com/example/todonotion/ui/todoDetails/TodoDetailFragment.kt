package com.example.todonotion.ui.todoDetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.todonotion.databinding.FragmentTodoDetailBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todonotion.BaseApplication
import com.example.todonotion.R
import javax.inject.Inject

//https://stackoverflow.com/questions/17436298/how-to-pass-a-variable-from-activity-to-fragment-and-pass-it-back
//https://stackoverflow.com/questions/7220404/what-is-the-trick-with-0dip-layout-height-or-layouth-width
class TodoDetailFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val todoDetailsViewModel: TodoDetailsViewModel by viewModels {
        viewModelFactory
    }

   // private val args: TodoDetailFragmentArgs by navArgs()

    private lateinit var args: String
    /*
       private val viewModel: TodoViewModel by activityViewModels{
           TodoViewModel.Factory
       }

    */
    private var _binding: FragmentTodoDetailBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as BaseApplication).appComponent.todoDetailsComponent().create()
            .inject(this)

       args = arguments?.getString("todoId").toString()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoDetailBinding.inflate(inflater, container, false)
      //  val binding = FragmentTodoDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = todoDetailsViewModel

        todoDetailsViewModel.getTodoPhoto(args)

      //  todoDetailsViewModel.getTodoPhoto(args.todoId)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        //https://developer.android.com/develop/ui/views/touch-and-input/swipe/respond-refresh-request
        //refresh page
        binding.refreshLayout.setOnRefreshListener {
            Log.d("onRefresh", "onRefresh called from SwipeRefreshLayout")

            //https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
            val navController = findNavController()
            navController.run {
                popBackStack()
                navigate(R.id.todoDetailFragment)
            }
            binding.refreshLayout.isRefreshing = false
        }
    }

    /*
    //https://stackoverflow.com/questions/15560904/setting-custom-actionbar-title-from-fragment
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).supportActionBar?.title = viewModel.todo.value?.tags
    }
    */

    companion object {
        @JvmStatic
        fun newInstance(id: String) = TodoDetailFragment().apply {
            arguments = Bundle().apply {
                putString("todoId", id)
            }
        }
    }
}


