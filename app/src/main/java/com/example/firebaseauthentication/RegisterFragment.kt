package com.example.firebaseauthentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.firebaseauthentication.databinding.FragmentRegisterBinding
import com.google.firebase.auth.userProfileChangeRequest

class RegisterFragment : BaseFragment() {
    private lateinit var binding: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.txtLogin.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.btnRegister.setOnClickListener {

            if (binding.password.text.toString() == binding.passwordConf.text.toString()) {
                processRegister()
            } else {
                Toast.makeText(context, getString(R.string.dontMatchMessage), Toast.LENGTH_SHORT)
                    .show();
            }
        }
    }

    private fun processRegister() {
        val fullName = binding.fullName.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (!validateForm(binding.fullName,binding.email,binding.password,binding.passwordConf)) {
            return
        }

        showProgressDialog(getString(R.string.creatingRegister),getString(R.string.pleaseWait))

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val userUpdateProfile = userProfileChangeRequest {
                        displayName = fullName
                    }
                    task.result.user?.updateProfile(userUpdateProfile)
                        ?.addOnCompleteListener {
                            auth.currentUser!!.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        auth.signOut()
                                        hideProgressDialog()
                                    }
                                }
                            Navigation.findNavController(view = requireView())
                                .navigate(R.id.action_registerFragment_to_loginFragment)

                        }
                        ?.addOnFailureListener { error ->
                            Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT)
                                .show()
                            hideProgressDialog()
                        }
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.authFailed),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                hideProgressDialog()
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
    }
}