package com.example.totasks.ui.fragments.authenFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.totasks.R
import com.example.totasks.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private lateinit var binding: FragmentWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickListenerSetUp()
    }

    private fun onClickListenerSetUp(){
        binding.loginButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }
        binding.signUpButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_welcomeFragment_to_signUpFragment)
        }
    }
}