package com.example.firebaseauthentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.firebaseauthentication.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginBinding
    lateinit var googleSignInClient: GoogleSignInClient

    companion object{
        private const val RC_SIGN_IN = 1001
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            hideProgressDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLogin.setOnClickListener {
            processLogin(it)
        }

        binding.forgetPassword.setOnClickListener {
            var action = LoginFragmentDirections.actionLoginFragmentToPasswordResetFragment(binding.email.text.toString())
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent,RC_SIGN_IN)
        }

    }


    private fun processLogin(view: View) {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (!validateForm(binding.email, binding.password)) return

        showProgressDialog(getString(R.string.titleLogIn),getString(R.string.pleaseWait))

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser!!.isEmailVerified || !auth.currentUser!!.isEmailVerified){
                        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homepageFragment)
                    }
                    else{
                        auth.signOut()
                        Toast.makeText(
                            context,
                            getString(R.string.confirmEmail),
                            Toast.LENGTH_SHORT,
                        ).show()
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }
            catch (e: ApiException){
                e.printStackTrace()
                Toast.makeText(
                    context,
                    e.localizedMessage,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String){
        showProgressDialog(getString(R.string.titleLogIn),getString(R.string.pleaseWait))
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_homepageFragment)
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