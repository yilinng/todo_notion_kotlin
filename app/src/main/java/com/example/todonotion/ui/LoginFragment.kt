package com.example.todonotion.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

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
            binding.emailInput.text.toString(),
            binding.passwordInput.text.toString(),
        )
    }

    private fun addNewUser() {
        if (isEntryValid()) {
            networkViewModel.loginAction(convertToDataClass())
            //login
            observeUserToken()
            observeError()
        } else {
            actionIsEmpty()
        }
    }

    private fun actionIsEmpty() {
        if (binding.emailInput.text.toString().isEmpty()) {
            binding.emailLabel.error = getString(R.string.login_usernameOrEmail)
        }
        if (binding.passwordInput.text.toString().isEmpty()) {
            binding.passwordLabel.error = getString(R.string.login_password)
        }
    }

    private fun cleanIsEmpty() {
        if (binding.emailInput.text.toString().isNotEmpty()) {
            binding.emailInput.error = null
        }
        if (binding.passwordInput.text.toString().isNotEmpty()) {
            binding.passwordInput.error = null
        }
    }


    private fun observeUserToken() {
        networkViewModel.token.observe(this.viewLifecycleOwner) { items ->
            items.let {
                if (it != null) {
                    tokenViewModel.addNewToken(it)
                }
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

    private fun observeError() {
        networkViewModel.error.observe(this.viewLifecycleOwner) { items ->
            items.let {
                if (it != null) {
                    binding.errorText.visibility = VISIBLE
                    binding.errorText.setTextColor(Color.RED)
                    if ("user is login!" in it.toString()) {
                        binding.errorText.text = getString(R.string.login_user_error)
                    } else {
                        binding.errorText.text = getString(R.string.login_error)
                    }
                }
            }
        }
    }


    private fun convertToDataClass(): Login {
        return Login(
            binding.emailInput.text.toString(),
            binding.passwordInput.text.toString()
        )
    }

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

        observeToken()

        binding.emailInput.addTextChangedListener {
            Toast.makeText(
                this.context, "username or email input change", Toast.LENGTH_SHORT
            ).show()
            cleanIsEmpty()
        }



        binding.passwordInput.addTextChangedListener {
            Toast.makeText(
                this.context, "password input change", Toast.LENGTH_SHORT
            ).show()
            cleanIsEmpty()
        }

    }

}