package com.example.eventmanagement.auth.ui.fragments

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
import com.example.eventmanagement.databinding.FragmentForgetPasswordBinding
import com.example.eventmanagement.databinding.FragmentLoginBinding
import kotlin.getValue


class ForgetPasswordFragment : Fragment() {

    lateinit var binding: FragmentForgetPasswordBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentForgetPasswordBinding.inflate(inflater)

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
        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            viewModel.resetPassword(email)
        }

        binding.tvBackToLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeAuthState() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                AuthState.Loading -> {
                    binding.btnResetPassword.isEnabled = false
                    binding.btnResetPassword.text = "SENDING..."
                }

                AuthState.PasswordResetSuccess -> {
                    binding.btnResetPassword.isEnabled = true
                    binding.btnResetPassword.text =
                        "SEND RESET LINK"
                    Toast.makeText(
                        requireContext(),
                        "Password reset link sent to your email",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.etEmail.text?.clear()
                }

                is AuthState.Error -> {
                    binding.btnResetPassword.isEnabled = true
                    binding.btnResetPassword.text =
                        "SEND RESET LINK"
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }

                else -> {
                    Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}


