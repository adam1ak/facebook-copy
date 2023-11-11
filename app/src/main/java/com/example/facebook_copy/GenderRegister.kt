package com.example.facebook_copy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GenderRegister : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender_register)
        auth = Firebase.auth

        val back: ImageView = findViewById(R.id.back_to_birth_button)
        val next: Button = findViewById(R.id.gen_next_button)
        val maleSex: RadioButton = findViewById(R.id.male_radio)
        val femaleSex: RadioButton = findViewById(R.id.female_radio)
        var gender = "Choose your gender"

        val recivedName = intent.getStringExtra("name_pass")
        val recivedSurname = intent.getStringExtra("surname_pass")

        maleSex.setOnClickListener {
            gender = "Male"
        }
        femaleSex.setOnClickListener {
            gender = "Female"
        }
        back.setOnClickListener {
            val intent = Intent(this, NameRegister::class.java)
            startActivity(intent)
        }
        next.setOnClickListener {
            val intent = Intent(this, EmailRegister::class.java)
            intent.putExtra("name_pass", recivedName)
            intent.putExtra("surname_pass", recivedSurname)
            intent.putExtra("gender_pass", gender)
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