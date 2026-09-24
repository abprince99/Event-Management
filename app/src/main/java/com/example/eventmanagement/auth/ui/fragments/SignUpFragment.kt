package com.example.eventmanagement.auth.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.eventmanagement.auth.viewmodel.AuthState
import com.example.eventmanagement.auth.viewmodel.AuthViewModel
import com.example.eventmanagement.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    lateinit var binding : FragmentSignUpBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeAuthState()
    }

    private fun setupListeners() {

        binding.btnRegister.setOnClickListener {

            val email =
                binding.etEmail.text.toString().trim()

            val password =
                binding.etPassword.text.toString()

            val confirmPassword =
                binding.etConfirmPassword.text.toString()

            if (password != confirmPassword) {

                binding.confirmPasswordLayout.error =
                    "Passwords do not match"

                return@setOnClickListener
            }

            binding.confirmPasswordLayout.error = null

            viewModel.register(
                email = email,
                password = password
            )
        }

        binding.tvLogin.setOnClickListener {

            parentFragmentManager.popBackStack()
        }
    }

    private fun observeAuthState() {

        viewModel.authState.observe(viewLifecycleOwner) { state ->

            when (state) {

                AuthState.Loading -> {

                    binding.btnRegister.isEnabled = false
                    binding.btnRegister.text = "CREATING..."
                }

                AuthState.RegisterSuccess -> {

                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "CREATE ACCOUNT"

                    Toast.makeText(
                        requireContext(),
                        "Account created successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    parentFragmentManager.popBackStack()
                }

                AuthState.LoginSuccess -> {
                    // Not used on Register screen
                }

                AuthState.PasswordResetSuccess -> {
                    // Not used on Register screen
                }

                is AuthState.Error -> {

                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "CREATE ACCOUNT"

                    Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}