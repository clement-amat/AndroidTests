package com.example.tp1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class LoadImageFromFirebase : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_image_from_firebase)
        loadWithGlide()
    }

    fun loadWithGlide() {

        val storageReference = FirebaseStorage.getInstance().reference.child("images/0.jpg").downloadUrl
        val imageView = findViewById<ImageView>(R.id.imgtodisplay)
        val vs = "gs://bucket/images/0.jpg"
        Glide.with(this /* context */)
            .load(vs)
            .into(imageView)
    }
}
