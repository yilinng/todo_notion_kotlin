package com.example.todonotion.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todonotion.BaseApplication
import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentSignupBinding
import com.example.todonotion.network.Signup
import com.example.todonotion.overview.auth.AuthNetworkViewModel
import com.example.todonotion.overview.auth.AuthViewModel
import com.example.todonotion.overview.auth.AuthViewModelFactory

class SignupFragment: Fragment() {
    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    private val viewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(
            (activity?.application as BaseApplication).database.userDao()
        )
    }

    //data from network
    private val networkViewModel: AuthNetworkViewModel by activityViewModels()
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        //  val binding = FragmentTodoListBinding.inflate(inflater)
        // TODO: call the view model method that calls the todo api
        //   binding.lifecycleOwner = this
        //   binding.viewModel = viewModel

        // Inflate the layout for this fragment
        return binding.root
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return networkViewModel.isSignupEntryValid(
            binding.usernameInput.text.toString(),
            binding.emailInput.text.toString(),
            binding.passwordInput.text.toString(),
        )
    }


    /**
     * Binds views with the passed in [signup] information.
     */
    private fun bind(signup: Signup) {
      //  val price = "%.2f".format(item.itemPrice)
        binding.apply {
            usernameInput.setText(signup.username, TextView.BufferType.SPANNABLE)
            emailInput.setText(signup.email, TextView.BufferType.SPANNABLE)
            passwordInput.setText(signup.password, TextView.BufferType.SPANNABLE)
           // signupBtn.setOnClickListener { updateItem() }
        }
    }

    private fun addNewUser(){
        if(isEntryValid()) {
           networkViewModel.signupAction(convertToDataClass())
            val action = SignupFragmentDirections.actionSignupFragmentToTodoListFragment()
            findNavController().navigate(action)
        }
    }

    private fun convertToDataClass():Signup{
       return Signup(
            username = binding.usernameInput.text.toString(),
            binding.emailInput.text.toString(),
            binding.passwordInput.text.toString()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //networkViewModel.loginAction()
        //redirect to login page
        binding.loginBtn.setOnClickListener{
            this.findNavController()
                .navigate(R.id.action_signupFragment_to_loginFragment)
        }

        //when click signup button
        binding.signupBtn.setOnClickListener{
           addNewUser()
        }

    }

}