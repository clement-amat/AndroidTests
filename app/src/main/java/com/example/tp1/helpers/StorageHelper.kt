package com.example.tp1.helpers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tp1.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StorageHelper() {

    var storage: FirebaseStorage = FirebaseStorage.getInstance()
    var imagesRef: StorageReference = storage.reference.child("images")

    fun getImagesName() {

        imagesRef.listAll().addOnSuccessListener { listResult ->
            listResult.items.forEach { item ->
                System.out.println(item.toString());
            }
        }
    }
}