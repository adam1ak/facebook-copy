package com.example.facebook_copy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PasswordRegister : AppCompatActivity() {
    private var isVisible: Boolean = false
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_register)
        auth = Firebase.auth

        val back: ImageView = findViewById(R.id.back_to_em_button)
        val next: Button = findViewById(R.id.pass_next_button)
        val password: EditText = findViewById(R.id.password_register)
        val passwordVisible: ImageView = findViewById(R.id.passwordVisible)

        val recivedName = intent.getStringExtra("name_pass")
        val recivedSurname = intent.getStringExtra("surname_pass")
        val recivedGender = intent.getStringExtra("gender_pass")
        val recivedEmail = intent.getStringExtra("email_pass")

        back.setOnClickListener {
            val intent = Intent(this, EmailRegister::class.java)
            startActivity(intent)
        }
        passwordVisible.setOnClickListener {
            if(isVisible == false){
                password.transformationMethod = SingleLineTransformationMethod.getInstance()
                isVisible = true
            }else if(isVisible == true){
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                isVisible = false
            }

            password.setSelection(password.text.length)
        }
        next.setOnClickListener {
            val intent = Intent(this, ProfilepicRegister::class.java)
            intent.putExtra("name_pass", recivedName)
            intent.putExtra("surname_pass", recivedSurname)
            intent.putExtra("gender_pass", recivedGender)
            intent.putExtra("email_pass", recivedEmail)
            intent.putExtra("password_pass", password.text.toString())
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