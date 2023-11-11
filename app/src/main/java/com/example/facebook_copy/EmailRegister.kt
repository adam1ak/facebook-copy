package com.example.facebook_copy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailRegister : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_register)
        auth = Firebase.auth

        val back: ImageView = findViewById(R.id.back_to_gen_button)
        val next: Button = findViewById(R.id.em_next_button)
        val email: EditText = findViewById(R.id.email_register)

        val recivedName = intent.getStringExtra("name_pass")
        val recivedSurname = intent.getStringExtra("surname_pass")
        val recivedGender = intent.getStringExtra("gender_pass")

        back.setOnClickListener {
            val intent = Intent(this, GenderRegister::class.java)
            startActivity(intent)
        }
        next.setOnClickListener {
            val intent = Intent(this, PasswordRegister::class.java)
            intent.putExtra("name_pass", recivedName)
            intent.putExtra("surname_pass", recivedSurname)
            intent.putExtra("gender_pass", recivedGender)
            intent.putExtra("email_pass", email.text.toString())
            startActivity(intent)
        }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d("Logged", "No")
        }
        else {
            Log.d("Logged", "Yes")
            val intent = Intent(baseContext, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}