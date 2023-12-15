package com.example.todonotion.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.todonotion.databinding.FragmentAddTodoBinding
import com.example.todonotion.data.Keyword.Keyword

class AddTodoFragment : Fragment(){

   // private val navigationArgs: AddTodoFragmentArgs by navArgs()

    private var _binding: FragmentAddTodoBinding? = null

    private lateinit var todo: Keyword

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    //private val viewModel: TodoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        val id = navigationArgs.id
        if (id > 0) {

            // TODO: Observe a Todo that is retrieved by id, set the todo variable,
            //  and call the bindTodo method

            binding.deleteBtn.visibility = View.VISIBLE
            binding.deleteBtn.setOnClickListener {
                deleteTodo(todo)
            }
        } else {
            binding.saveBtn.setOnClickListener {
                addTodo()
            }
        }

         */
    }
    /*
    private fun deleteTodo(todo: Todo) {
       // viewModel.deleteTodo(todo)
        findNavController().navigate(
            R.id.action_addTodoFragment_to_todoListFragment
        )
    }

    private fun addTodo() {

        if (isValidEntry()) {
            viewModel.addTodo(
                binding.nameInput.text.toString(),
                binding.locationAddressInput.text.toString(),
                binding.inSeasonCheckbox.isChecked,
                binding.notesInput.text.toString()
            )
            findNavController().navigate(
                R.id.action_addTodoFragment_to_todoListFragment
            )
        }


    }

    private fun updateTodo() {
        if (isValidEntry()) {

            viewModel.updateTodo(
                id = navigationArgs.id,
                name = binding.nameInput.text.toString(),
                address = binding.locationAddressInput.text.toString(),
                inSeason = binding.inSeasonCheckbox.isChecked,
                notes = binding.notesInput.text.toString()
            )

            findNavController().navigate(
                R.id.action_addTodoFragment_to_todoListFragment
            )

        }
    }

    private fun bindTodo(todo: Todo) {
        binding.apply{

            /*
            nameInput.setText(todo.name, TextView.BufferType.SPANNABLE)
            locationAddressInput.setText(todo.address, TextView.BufferType.SPANNABLE)
            inSeasonCheckbox.isChecked = todo.inSeason
            notesInput.setText(todo.notes, TextView.BufferType.SPANNABLE)
            saveBtn.setOnClickListener {
                updateTodo()
            }
            */

        }

    }


    private fun isValidEntry() = viewModel.isValidEntry(
        binding.nameInput.text.toString(),
        binding.locationAddressInput.text.toString()
    )
    */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    //https://stackoverflow.com/questions/68990789/android-remove-back-button-from-action-bar-for-a-particular-fragment
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

     */
}