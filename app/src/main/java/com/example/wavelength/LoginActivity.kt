package com.example.wavelength

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.wavelength.databinding.ActivityLoginBinding
import com.firebase.client.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("666666","oncreate")
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        Firebase.setAndroidContext(this)
        auth = FirebaseAuth.getInstance()
        binding.btLogin.setOnClickListener { login() }
        binding.btSignup.setOnClickListener { createUser() }
        supportActionBar?.hide()
        window.statusBarColor = resources.getColor(R.color.purple_off_white, theme)

        setContentView(binding.root)
    }

    private fun createUser() {
        auth.createUserWithEmailAndPassword(binding.etUsername.text.toString(), binding.etPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser!!
                    startActivity()
                } else {
                    Toast.makeText(this, "Authentication failed: " + task.exception, Toast.LENGTH_SHORT).show()
                }
            }
    }

    //login
    private fun login() {
        Log.i("test666666",(auth==null).toString())
        auth.signInWithEmailAndPassword(binding.etUsername.text.toString(), binding.etPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                Log.i("isSuccess6","if------")
                if (task.isSuccessful) {
                    Log.i("isSuccess","if------")
                    user = auth.currentUser!!
                    startActivity()
                } else {
                    Toast.makeText(this, "Authentication failed: " + task.exception, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun startActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "Welcome, ${user.email}!", Toast.LENGTH_SHORT).show()
    }
}