package com.example.facebook_copy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ProfilepicRegister : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profilepic_register)
        auth = Firebase.auth
        imageView = findViewById(R.id.image_upload)

        val db = Firebase.firestore

        val back: ImageView = findViewById(R.id.back_to_pass_button)
        val register: Button = findViewById(R.id.registerFinal_button)

        val recivedName = intent.getStringExtra("name_pass")
        val recivedSurname = intent.getStringExtra("surname_pass")
        val recivedGender = intent.getStringExtra("gender_pass")
        val recivedEmail = intent.getStringExtra("email_pass")
        val recivedPassword = intent.getStringExtra("password_pass")

        imageView.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        back.setOnClickListener {
            val intent = Intent(this, PasswordRegister::class.java)
            startActivity(intent)
        }
        register.setOnClickListener {
            if(recivedName != "" &&
                recivedSurname != "" &&
                recivedGender != "" &&
                recivedEmail != "" &&
                recivedPassword != ""){
            auth.createUserWithEmailAndPassword(recivedEmail.toString(), recivedPassword.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "createUserWithEmail:success")
                        val user = auth.currentUser
                        Log.w("TAG", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Hello:"+user!!.email.toString(),
                            Toast.LENGTH_SHORT,
                        ).show()
                            if (imageUri != null) {
                                val storageReference = Firebase.storage.reference
                                val imageRef = storageReference.child("images/${user.uid}")
                                val uploadTask = imageRef.putFile(imageUri!!)

                                uploadTask.addOnSuccessListener { taskSnapshot ->
                                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                                        val downloadUrl = uri.toString()
                                            val newUser = hashMapOf(
                                                "email" to recivedEmail.toString(),
                                                "name" to recivedName.toString(),
                                                "surname" to recivedSurname.toString(),
                                                "gender" to recivedGender.toString(),
                                                "profileImage" to downloadUrl
                                            )
                                            db.collection("users").document(user.uid)
                                                .set(newUser)
                                                .addOnSuccessListener {
                                                    Log.d("TAG", "DocumentSnapshot successfully written!")
                                                    val intent = Intent(this, HomeActivity::class.java)
                                                    startActivity(intent)
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w("TAG", "Error writing document", e)
                                                }
                                    }
                                }.addOnFailureListener { e ->
                                    Log.d("MAIN", e.toString())
                        }
                    }
                    else{
                        if(recivedGender == "Male"){
                            val newUser = hashMapOf(
                                "email" to user.email,
                                "name" to recivedName.toString(),
                                "surname" to recivedSurname.toString(),
                                "gender" to recivedGender.toString(),
                                "profileImage" to "https://i.imgur.com/nEyeiQS.png"

                            )
                            db.collection("users").document(user.uid)
                                .set(newUser)
                                .addOnSuccessListener {
                                    Log.d("TAG", "DocumentSnapshot successfully written!")
                                    val intent = Intent(this, HomeActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
                        }else if(recivedGender == "Female"){
                            val newUser = hashMapOf(
                                "email" to user.email,
                                "name" to recivedName.toString(),
                                "surname" to recivedSurname.toString(),
                                "gender" to recivedGender.toString(),
                                "profileImage" to "https://i.imgur.com/ylvW5uI.png"

                            )
                            db.collection("users").document(user.uid)
                                .set(newUser)
                                .addOnSuccessListener {
                                    Log.d("TAG", "DocumentSnapshot successfully written!")
                                    val intent = Intent(this, HomeActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
                        }
                     }
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Authentication failed, password or email are invalid",
                            Toast.LENGTH_SHORT,
                        ).show()
         }
        }
      }else{
                Toast.makeText(
                    baseContext,
                    "Can't register, some fields are empty",
                    Toast.LENGTH_SHORT,
                ).show()
            }
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