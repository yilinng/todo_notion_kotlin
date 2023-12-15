package com.example.todonotion.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todonotion.BaseApplication
import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentLoginBinding
import com.example.todonotion.network.Login
import com.example.todonotion.overview.auth.AuthViewModelFactory
import com.example.todonotion.overview.auth.AuthNetworkViewModel
import com.example.todonotion.overview.auth.AuthViewModel
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.overview.auth.TokenViewModelFactory


class LoginFragment : Fragment() {

    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    private val userViewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(
            (activity?.application as BaseApplication).database.userDao()
        )
    }

    private val tokenViewModel: TokenViewModel by activityViewModels {
        TokenViewModelFactory(
            (activity?.application as BaseApplication).database.tokenDao()
        )
    }

    //data from network
    private val networkViewModel: AuthNetworkViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        //  val binding = FragmentTodoListBinding.inflate(inflater)
        // TODO: call the view model method that calls the todo api
        // binding.lifecycleOwner = this
        // binding.networkViewModel = networkViewModel

        // Inflate the layout for this fragment
        return binding.root
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return networkViewModel.isLoginEntryValid(
            binding.usernameOremailInput.text.toString(),
            binding.passwordInput.text.toString(),
        )
    }

    private fun addNewUser() {
        if (isEntryValid()) {
            networkViewModel.loginAction(convertToDataClass())
            //login
            observeUserToken()
        }
    }


    private fun observeUserToken() {
        networkViewModel.token.observe(this.viewLifecycleOwner) { items ->
            items.let {
                tokenViewModel.addNewToken(it)
                binding.errorText.visibility = VISIBLE
                binding.errorText.setTextColor(Color.GREEN)
                binding.errorText.text = it.toString()
                val action = LoginFragmentDirections.actionLoginFragmentToTodoListFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun observeToken() {
        tokenViewModel.tokens.observe(this.viewLifecycleOwner) {
            //  binding.tokenText.visibility = VISIBLE
            //  binding.tokenText.text = it.toString()
            //observe token change
            binding.errorText.visibility = INVISIBLE
            binding.errorText.text = ""
        }
    }



    private fun convertToDataClass(): Login {
        return Login(
            binding.usernameOremailInput.text.toString(),
            binding.passwordInput.text.toString()
        )
    }

    //test get todos
    /*
    private fun getPosts() {
        networkViewModel.getTodoAction()
    }
    */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //networkViewModel.loginAction()
        //redirect to signup page
        binding.signupBtn.setOnClickListener {
            this.findNavController()
                .navigate(R.id.action_loginFragment_to_signupFragment)
        }

        //when click login button
        binding.loginBtn.setOnClickListener {
            addNewUser()
        }

        //error message
        networkViewModel.error.observe(this.viewLifecycleOwner) {
            binding.errorText.visibility = VISIBLE
            binding.errorText.text = R.string.login_error_text.toString()
        }

        observeToken()

    }

}