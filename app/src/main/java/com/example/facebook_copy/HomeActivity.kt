package com.example.facebook_copy

import android.R.attr.button
import android.R.attr.scaleX
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var signout: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = Firebase.auth
        signout = findViewById(R.id.button_signout)
        val db = Firebase.firestore

        val profilePicUrl: ImageView = findViewById(R.id.profilePicUrl)
        val postText: EditText = findViewById(R.id.userpost_text)
        val user = auth.currentUser
        val docRef = db.collection("users").document(user!!.uid)
        val postRef = db.collection("posts")
        val postLayout: LinearLayout = findViewById(R.id.postLayout)
        postText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val intent = Intent(this, PostInput::class.java)
                startActivity(intent)
                finish()
            }
        }

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data?.get("profileImage")}")
                    postText.setHint("O czym myślisz, ${document.data?.get("name")}?").toString()
                    Picasso.get().load(document.data?.get("profileImage").toString()).into(profilePicUrl)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val photoUrl = document.getString("photoUrl")
                    val name = document.getString("name")
                    val surname = document.getString("surname")
                    val content = document.getString("content")

                    val postItemView = layoutInflater.inflate(R.layout.post_item, null) as LinearLayout

                    val postImageView = postItemView.findViewById<ImageView>(R.id.postImageView)
                    val postUserName = postItemView.findViewById<TextView>(R.id.postUserName)
                    val postUserSurname = postItemView.findViewById<TextView>(R.id.postUserSurname)
                    val postUserPfp = postItemView.findViewById<ImageView>(R.id.postUserPfp)
                    val postContentTextView = postItemView.findViewById<TextView>(R.id.postContentTextView)

                    if (photoUrl != null) {
                        Picasso.get().load(document.data?.get("photoUrl").toString()).into(postImageView)
                    } else {
                        postImageView.visibility = View.GONE
                    }


                    Picasso.get().load(document.data?.get("pfpUrl").toString()).into(postUserPfp)
                    postUserName.text = name
                    postUserSurname.text = surname
                    postContentTextView.text = content

                    postLayout.addView(postItemView)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

       signout.setOnClickListener{
           logoutAnimationIncrease()
           val message: String? = "Czy na pewno chcesz się wylogować?"
           showCustomDialogBox(message)
        }
    }



    private  fun showCustomDialogBox(message: String?){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_confirm_logout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialMessage : TextView = dialog.findViewById(R.id.dialMessage)
        val btnYes : Button = dialog.findViewById(R.id.btnYes)
        val btnNo : Button = dialog.findViewById(R.id.btnNo)

        dialMessage.text = message

        val fadeInAnimator = ObjectAnimator.ofFloat(dialog.window?.decorView, View.ALPHA, 0f, 2f)
        fadeInAnimator.duration = 500

        btnYes.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnNo.setOnClickListener{
            dialog.dismiss()
            logoutAnimationDecrease()
        }
        dialog.setOnShowListener {
            fadeInAnimator.start()
        }
        dialog.show()
    }
    private fun logoutAnimationIncrease() {
        val scaleXAnimator = ObjectAnimator.ofFloat(signout, "scaleX", 1.0f, 1.2f)
        scaleXAnimator.duration = 200 // Adjust the duration as needed

        val scaleYAnimator = ObjectAnimator.ofFloat(signout, "scaleY", 1.0f, 1.2f)
        scaleYAnimator.duration = 200 // Adjust the duration as needed

        scaleXAnimator.start()
        scaleYAnimator.start()
    }
    private fun logoutAnimationDecrease() {
        val scaleXAnimator = ObjectAnimator.ofFloat(signout, "scaleX", 1.2f, 1.0f)
        scaleXAnimator.duration = 200 // Adjust the duration as needed

        val scaleYAnimator = ObjectAnimator.ofFloat(signout, "scaleY", 1.2f, 1.0f)
        scaleYAnimator.duration = 200 // Adjust the duration as needed

        scaleXAnimator.start()
        scaleYAnimator.start()
    }

}