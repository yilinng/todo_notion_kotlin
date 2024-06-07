package com.example.todonotion.ui.login

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast

import androidx.core.widget.addTextChangedListener

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todonotion.BaseApplication
import com.example.todonotion.R
import com.example.todonotion.databinding.FragmentLoginBinding
import com.example.todonotion.model.Login

import com.example.todonotion.overview.auth.TokenViewModel

import com.example.todonotion.overview.auth.UserApiStatus
import javax.inject.Inject


class LoginFragment : Fragment() {

    // TODO: Refactor the creation of the view model to take an instance of
    //  TodoViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    /*
    private val userViewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(
            (activity?.application as BaseApplication).database.userDao()
        )
    }
    */

    /*
    private val tokenViewModel: TokenViewModel by activityViewModels {
        AppViewModelProvider.Factory
    }

    //data from network
    private val networkViewModel: AuthNetworkViewModel by activityViewModels {
        AuthNetworkViewModel.Factory
    }
     */

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val loginViewModel: LoginViewModel by viewModels {
        viewModelFactory
    }

    private val tokenViewModel: TokenViewModel by viewModels {
        viewModelFactory
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as BaseApplication).appComponent.loginComponent().create()
            .inject(this)
    }

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshPage()

        //redirect to signup page
        binding.signupBtn.setOnClickListener {
            this.findNavController()
                .navigate(R.id.action_loginFragment_to_signupFragment)
        }

        //when click login button
        binding.loginBtn.setOnClickListener {
            addNewUser()
        }

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

    private fun refreshPage() {
        //https://developer.android.com/develop/ui/views/touch-and-input/swipe/respond-refresh-request
        //refresh page
        binding.refreshLayout.setOnRefreshListener {
            Log.d("onRefresh", "onRefresh called from SwipeRefreshLayout")

            //https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
            val navController = findNavController()
            navController.run {
                popBackStack()
                navigate(R.id.loginFragment)
            }
            binding.refreshLayout.isRefreshing = false
        }
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return loginViewModel.isLoginEntryValid(
            binding.emailInput.text.toString(),
            binding.passwordInput.text.toString(),
        )
    }

    private fun addNewUser() {
        if (isEntryValid()) {
            loginViewModel.loginAction(convertToDataClass())
            //login
            observeUserToken()
            observeError()
            observeState()
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
        loginViewModel.token.observe(this.viewLifecycleOwner) {
            if (it != null) {
                tokenViewModel.addNewToken(it)
            }
        }
    }

    private fun observeState() {
        loginViewModel.status.observe(this.viewLifecycleOwner) {
            if (it == UserApiStatus.DONE) {
                binding.errorText.visibility = GONE
                binding.errorText.text = ""
                val action = LoginFragmentDirections.actionLoginFragmentToTodoListFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun observeError() {
        loginViewModel.error.observe(this.viewLifecycleOwner) { items ->
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


}