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

class NameRegister : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_register)
        auth = Firebase.auth

        val back: ImageView = findViewById(R.id.back_to_main_button)
        val next: Button = findViewById(R.id.nam_next_button)
        val name: EditText = findViewById(R.id.name_input)
        val surname: EditText = findViewById(R.id.surname_input)

        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        next.setOnClickListener {
            val intent = Intent(this, GenderRegister::class.java)
            intent.putExtra("name_pass", name.text.toString())
            intent.putExtra("surname_pass", surname.text.toString())
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
