package com.cookplan.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.cookplan.BaseActivity
import com.cookplan.R
import com.cookplan.main.MainActivity
import com.google.android.material.snackbar.Snackbar

class AuthActivity : BaseActivity(), AuthView {

    private var presenter: AuthPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_auth)
        presenter = AuthPresenterImpl(this, this)
    }

    override fun onStart() {
        super.onStart()
        presenter?.firstAuthSignIn()
    }

//    public fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        super.onActivityResult(requestCode, resultCode, data)
//        presenter?.onActivityResult(requestCode, resultCode, data)
//    }

    override fun showSnackbar(messageRes: Int) {
        Snackbar.make(findViewById(R.id.firebase_auth_root), messageRes, Snackbar.LENGTH_LONG).show()
    }

    override fun signedInWithGoogle() {
        val intent = Intent()
        intent.setClass(this, MainActivity::class.java)
        startActivityWithLeftAnimation(intent)
        finish()
    }

    override fun signedInFailed() {
        showSnackbar(R.string.unknown_sign_in_response)

        val signInButton = findViewById<View>(R.id.sign_in_google_button) as Button
        signInButton.visibility = View.VISIBLE
        signInButton.setOnClickListener { v ->
            presenter?.firstAuthSignIn()
        }
    }

    override fun setError(errorResourceId: Int) {
        showSnackbar(errorResourceId)
    }
}