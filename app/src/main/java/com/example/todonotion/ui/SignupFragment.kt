package com.example.todonotion.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.View.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todonotion.AppViewModelProvider

import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentSignupBinding
import com.example.todonotion.model.Signup
import com.example.todonotion.overview.auth.AuthNetworkViewModel
import com.example.todonotion.overview.auth.TokenViewModel

import com.example.todonotion.overview.auth.UserApiStatus
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SignupFragment : Fragment() {
    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    /*
    private val viewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(
            (activity?.application as BaseApplication).database.userDao()
        )
    }
    */
    private val tokenViewModel: TokenViewModel by activityViewModels {
        AppViewModelProvider.Factory
    }
    //data from network
    private val networkViewModel: AuthNetworkViewModel by activityViewModels{
        AuthNetworkViewModel.Factory
    }

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        //  val binding = FragmentTodoListBinding.inflate(inflater)
        // TODO: call the view model method that calls the todo api
        //   binding.lifecycleOwner = this
        //   binding.viewModel = viewModel

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshPage()

        //redirect to login page
        binding.loginBtn.setOnClickListener {
            this.findNavController()
                .navigate(R.id.action_signupFragment_to_loginFragment)
        }

        //when click signup button
        binding.signupBtn.setOnClickListener {
            addNewUser()
        }


        binding.nameInput.addTextChangedListener {
            Toast.makeText(
                this.context, "name input change", Toast.LENGTH_SHORT
            ).show()
            cleanIsEmpty()
        }

        binding.emailInput.addTextChangedListener {
            Toast.makeText(
                this.context, "email input change", Toast.LENGTH_SHORT
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

    private fun refreshPage() {
        //https://developer.android.com/develop/ui/views/touch-and-input/swipe/respond-refresh-request
        //refresh page
        binding.refreshLayout.setOnRefreshListener {
            Log.d("onRefresh", "onRefresh called from SwipeRefreshLayout")

            //https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
            val navController = findNavController()
            navController.run {
                popBackStack()
                navigate(R.id.signupFragment)
            }
            binding.refreshLayout.isRefreshing = false
        }
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return networkViewModel.isSignupEntryValid(
            binding.nameInput.text.toString(),
            binding.emailInput.text.toString(),
            binding.passwordInput.text.toString(),
        )
    }

    private fun addNewUser() {
        if (isEntryValid()) {
            networkViewModel.signupAction(convertToDataClass())
            //signup
            observeStatus()
            observeError()
            observeUserToken()
        } else {
            actionIsEmpty()
        }
    }

    private fun actionIsEmpty() {
        if (binding.nameInput.text.toString().isEmpty()) {
            binding.nameLabel.error = getString(R.string.signup_name)
        }
        if (binding.emailInput.text.toString().isEmpty()) {
            binding.emailLabel.error = getString(R.string.signup_email)
        }
        if (binding.passwordInput.text.toString().isEmpty()) {
            binding.passwordLabel.error = getString(R.string.login_password)
        }
    }

    private fun cleanIsEmpty() {
        if (binding.nameInput.text.toString().isNotEmpty()) {
            binding.nameLabel.error = null
        }
        if (binding.emailInput.text.toString().isNotEmpty()) {
            binding.emailLabel.error = null
        }
        if (binding.passwordInput.text.toString().isNotEmpty()) {
            binding.passwordInput.error = null
        }
    }

    private fun observeStatus() {
        networkViewModel.status.observe(this.viewLifecycleOwner) {
            if(it == UserApiStatus.DONE){
                binding.errorText.visibility = GONE
                showSignUpDialog()
            }
        }
    }

    private fun observeError() {
        networkViewModel.error.observe(this.viewLifecycleOwner) { items ->
            items.let {
                if (it != null) {
                    binding.errorText.visibility = VISIBLE
                    binding.errorText.setTextColor(Color.RED)
                    if (it.contains("email already exist!")) {
                        Log.i("signupUser400", it)
                        binding.errorText.text = getString(R.string.signup_email_error)
                    }else if(it.contains("password") && it.contains("length")){
                        binding.errorText.text = getString(R.string.password_length_error)
                    }
                    else {
                        Log.i("signupUser400", it)
                        binding.errorText.text = getString(R.string.signup_server_error)
                    }
                }
            }
        }
    }
    private fun observeUserToken() {
        networkViewModel.token.observe(this.viewLifecycleOwner) {
            if (it != null) {
                tokenViewModel.addNewToken(it)
            }
        }
    }

    private fun convertToDataClass(): Signup {
        return Signup(
            name = binding.nameInput.text.toString(),
            email = binding.emailInput.text.toString(),
            password = binding.passwordInput.text.toString()
        )
    }

    private fun showSignUpDialog() {
        this.context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.signup_success_msg1))
                .setMessage(getString(R.string.signup_success_msg2))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.signup_success_dialogs_btn)) { _, _ ->
                    this.findNavController()
                        .navigate(R.id.action_signupFragment_to_todoListFragment)
                }
                .show()
        }
    }

}