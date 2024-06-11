package com.example.firebaseauthentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.firebaseauthentication.databinding.FragmentHomepageBinding

class HomepageFragment : BaseFragment() {
    private lateinit var binding: FragmentHomepageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firebaseUser = auth.currentUser

        firebaseUser?.let {
            binding.textFullName.text = it.displayName
            binding.textEmail.text = it.email
        } ?: run {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_homepageFragment_to_loginFragment)
        }
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            showProgressDialog(
                getString(R.string.titleLogOut),
                getString( R.string.pleaseWait)
            )
            Navigation.findNavController(view).navigate(R.id.action_homepageFragment_to_loginFragment)
        }

        binding.btnProfileUpdate.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_homepageFragment_to_updateProfileFragment)
        }
    }

}
