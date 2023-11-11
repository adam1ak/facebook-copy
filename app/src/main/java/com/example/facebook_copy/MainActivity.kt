package com.example.facebook_copy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth

        val registerButton : Button = findViewById(R.id.register_button)
        val loginButton : Button = findViewById(R.id.login_button)
        val user_email: EditText = findViewById(R.id.email_input)
        val user_password: EditText = findViewById(R.id.password_input)

        loginButton.setOnClickListener{
            auth.signInWithEmailAndPassword(user_email.text.toString(), user_password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else { Log.e("TAG", "signInWithEmail:failure", task.exception) }
                }
        }

        registerButton.setOnClickListener{
            val intent = Intent(this, NameRegister::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(
                baseContext,
                "Niezalogowany",
                Toast.LENGTH_SHORT,
            ).show()
        }
        else {
            Toast.makeText(
                baseContext,
                "Zalogowany",
                Toast.LENGTH_SHORT,
            ).show()
            val intent = Intent(baseContext, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}