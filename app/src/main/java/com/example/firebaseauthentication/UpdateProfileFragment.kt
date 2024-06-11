package com.example.firebaseauthentication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.firebaseauthentication.databinding.FragmentUpdateProfileBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest

class UpdateProfileFragment : BaseFragment() {
    private lateinit var binding: FragmentUpdateProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)

        binding.btnUpdate.setOnClickListener {
            update()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth.currentUser?.let { user ->
            binding.fullName.setText(user.displayName)
            binding.email.setText(user.email)
        }
    }

    private fun update() {
        val user = auth.currentUser
        if (user == null || !validateForm(binding.fullName, binding.email)) {
            return
        }

        val profileUpdates = userProfileChangeRequest {
            displayName = binding.fullName.text.toString()
        }

        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updatePassword(user)
            } else {
                Toast.makeText(context, getString(R.string.failedFailedUpdateProfile), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePassword(user: FirebaseUser) {
        val newPassword = binding.password.text.toString()
        if (newPassword.isNotEmpty()) {
            user.updatePassword(newPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navigateToHome()
                } else {
                    Toast.makeText(context, getString(R.string.failedFailedUpdatePassword), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_updateProfileFragment_to_homepageFragment)
    }
}
