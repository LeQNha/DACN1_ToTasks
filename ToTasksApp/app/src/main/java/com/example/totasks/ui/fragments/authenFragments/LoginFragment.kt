package com.example.totasks.ui.fragments.authenFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.totasks.MainActivity
import com.example.totasks.R
import com.example.totasks.databinding.FragmentLoginBinding
import com.example.totasks.ui.TasksSchedulePrototype
import com.example.totasks.viewmodels.UserViewModel

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel = (activity as MainActivity).userViewModel


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onclickListenerSetUp()

        userViewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                startActivity(Intent(requireContext(), TasksSchedulePrototype::class.java))
                activity?.finish()
            }
        }
    }

    private fun onclickListenerSetUp() {
//        binding.backButton.setOnClickListener {
//        }
        binding.authLoginButton.setOnClickListener {
            if (!binding.emailEditText.text.isEmpty() && !binding.passwordEditText.text.isEmpty()) {
                val authenticateActivity = activity as? MainActivity
                if (authenticateActivity != null) {
                    userViewModel.login(
                        binding.emailEditText.text.toString(),
                        binding.passwordEditText.text.toString(),
                        requireContext(),
                        authenticateActivity
                    )
                }
            } else {
                Toast.makeText(requireContext(), "Empty field!!!", Toast.LENGTH_SHORT).show()
            }
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            startActivity(intent)
        }
        binding.registerNowBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }
}