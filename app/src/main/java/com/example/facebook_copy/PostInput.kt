package com.example.facebook_copy

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import okhttp3.internal.notify
import org.w3c.dom.Text

class PostInput : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var user_name_2: String
    private lateinit var user_surname: String
    private lateinit var user_pfpUrl: String
    private lateinit var imageUpload: ImageView
    private var imageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
        }
    }

    fun generateRandomId(length: Int): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { characters.random() }
            .joinToString("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_input)

        auth = Firebase.auth
        val db = Firebase.firestore
        val publish: Button = findViewById(R.id.public_button)
        val user_name: TextView = findViewById(R.id.user_name_surname)
        val user_pfp: ImageView = findViewById(R.id.user_pfp)
        val post_content: EditText = findViewById(R.id.post_content)
        val back: ImageView = findViewById(R.id.back_to_home)
        val user = auth.currentUser
        val docRef = db.collection("users").document(user!!.uid)
        imageUpload = findViewById(R.id.public_image)

        imageUpload.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot data: ${document.data?.get("profileImage")}"
                    )
                    user_name.text = "${document.data?.get("name").toString()} ${
                        document.data?.get("surname").toString()
                    }"
                    user_surname = "${document.data?.get("surname").toString()}"
                    user_name_2 = "${document.data?.get("name").toString()}"
                    user_pfpUrl = "${document.data?.get("profileImage").toString()}"
                    Picasso.get().load(document.data?.get("profileImage").toString()).into(user_pfp)
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
        post_content.addTextChangedListener{
            if(post_content.text.toString() != ""){
                publish.isEnabled = true
                publish.setBackgroundResource(R.drawable.button_active_create_post)
            }else{
                publish.isEnabled = false
                publish.setBackgroundResource(R.drawable.button_un_active_create_post)
            }
        }
        publish.setOnClickListener{
            if(post_content.text.toString() != "") {
                if (imageUri != null) {
                    val randomId = generateRandomId(25)
                    val storageReference = Firebase.storage.reference
                    val imageRef = storageReference.child("postImages/${randomId}")
                    val uploadTask = imageRef.putFile(imageUri!!)

                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            val post = hashMapOf(
                                "userId" to user.uid,
                                "name" to user_name_2,
                                "surname" to user_surname,
                                "pfpUrl" to user_pfpUrl,
                                "photoUrl" to downloadUrl,
                                "content" to post_content.text.toString()
                            )
                            db.collection("posts")
                                .add(post)
                                .addOnSuccessListener {
                                    post_content.text.clear()
                                    Log.d("Tag", "DocumentSnapshot successfully written!")
                                    val intent = Intent(this, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e -> Log.w("tag", "Error writing document", e) }
                        }
                    }.addOnFailureListener { e ->
                        Log.d("MAIN", e.toString())
                    }
                } else {
                    val post = hashMapOf(
                        "userId" to user.uid,
                        "name" to user_name_2,
                        "surname" to user_surname,
                        "pfpUrl" to user_pfpUrl,
                        "content" to post_content.text.toString()
                    )
                    db.collection("posts")
                        .add(post)
                        .addOnSuccessListener {
                            post_content.text.clear()
                            Log.d("Tag", "DocumentSnapshot successfully written!")
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e -> Log.w("tag", "Error writing document", e) }
                }
            }else{
                Log.d("e", "content can't be empty")
            }
        }
        back.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d("Logged", "No")
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }
        else {
            Log.d("Logged", "Yes")
        }
    }
}