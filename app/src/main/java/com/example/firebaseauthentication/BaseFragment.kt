package com.example.firebaseauthentication

import android.app.ProgressDialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

open class BaseFragment : Fragment() {
    private val progressDialog by lazy {
        ProgressDialog(requireContext()).apply {
            setCancelable(false)
        }
    }

    protected val auth: FirebaseAuth by lazy { Firebase.auth }

    protected fun showProgressDialog(title: String, message: String) {
        progressDialog.apply {
            setTitle(title)
            setMessage(message)
            show()
        }
    }

    protected fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun hideKeyboard(view: View) {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

    protected fun validateForm(vararg textViews: TextView): Boolean {
        var valid = true
        for (textView in textViews) {
            if (TextUtils.isEmpty(textView.text.toString())) {
                textView.error = R.string.required.toString()
                valid = false
            } else {
                textView.error = null
            }
        }
        if (!valid) {
            Toast.makeText(context, R.string.fillAllFields.toString(), Toast.LENGTH_SHORT).show()
        }
        return valid
    }
}