package com.example.firebaseauthentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.firebaseauthentication.databinding.FragmentPasswordResetBinding

class PasswordResetFragment : BaseFragment() {
    private lateinit var binding: FragmentPasswordResetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordResetBinding.inflate(inflater, container, false)
        val bundle: PasswordResetFragmentArgs by navArgs()

        binding.email.setText(bundle.email)

        binding.btnReset.setOnClickListener {
            showProgressDialog(getString(R.string.sending),getString(R.string.resetPasswordMessage))
            auth.sendPasswordResetEmail(binding.email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        hideProgressDialog()
                        Navigation.findNavController(it).navigate(R.id.action_passwordResetFragment_to_loginFragment)
                        Toast.makeText(
                            context,
                            getString(R.string.successResetPasswordMessage),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
                .addOnFailureListener {
                    hideProgressDialog()
                    Toast.makeText(
                        context,
                        getString(R.string.failedResetPasswordMessage),
                        Toast.LENGTH_LONG,
                    ).show()
                }
        }
        return binding.root
    }
}