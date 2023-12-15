package com.example.todonotion.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.fragment.app.activityViewModels
import com.example.todonotion.databinding.FragmentTodoDetailBinding
import com.example.todonotion.network.Todo
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentTodoListBinding
import com.example.todonotion.overview.OverViewModel

//https://stackoverflow.com/questions/7220404/what-is-the-trick-with-0dip-layout-height-or-layouth-width
class TodoDetailFragment : Fragment() {

    private val viewModel: OverViewModel by activityViewModels()
    private var _binding: FragmentTodoDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodoDetailBinding.inflate(inflater, container, false)
      //  val binding = FragmentTodoDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}


