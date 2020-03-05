package com.example.tp1.repository

import com.example.tp1.model.db.StoredPhoto
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreRepository @Inject constructor() {
    var firestoreDB = FirebaseFirestore.getInstance()


    fun getStoredPhoto(): CollectionReference {
        return firestoreDB.collection("images")
    }

    fun saveStoredPhoto(storedPhoto: StoredPhoto): Task<Void> {
        return firestoreDB.collection("images").document(storedPhoto.title).set(storedPhoto)
    }
}