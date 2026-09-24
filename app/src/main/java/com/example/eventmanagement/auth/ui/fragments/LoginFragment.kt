package com.example.eventmanagement.auth.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.eventmanagement.R
import com.example.eventmanagement.auth.viewmodel.AuthState
import com.example.eventmanagement.auth.viewmodel.AuthViewModel
import com.example.eventmanagement.events.ui.Dashboard
import com.example.eventmanagement.databinding.FragmentLoginBinding
import kotlin.getValue
import kotlin.jvm.java


class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater)

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

        binding.btnLogin.setOnClickListener {

            val email =
                binding.etEmail.text.toString().trim()

            val password =
                binding.etPassword.text.toString()

            viewModel.login(
                email = email,
                password = password
            )
        }

        binding.tvSignUp.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    SignUpFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        binding.tvForgotPassword.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    ForgetPasswordFragment()
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun observeAuthState() {

        viewModel.authState.observe(viewLifecycleOwner) { state ->

            when (state) {

                AuthState.Loading -> {

                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.text = "LOGGING IN..."
                }

                AuthState.LoginSuccess -> {

                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "LOGIN"

                    Toast.makeText(
                        requireContext(),
                        "Login successful",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(requireContext(), Dashboard::class.java)
                    startActivity(intent)

                    requireActivity().finish()

                    // Dashboard will be opened here later
                }

                AuthState.RegisterSuccess -> {
                    // Not used on Login screen
                }

                AuthState.PasswordResetSuccess -> {
                    // Not used on Login screen
                }

                is AuthState.Error -> {

                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "LOGIN"

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