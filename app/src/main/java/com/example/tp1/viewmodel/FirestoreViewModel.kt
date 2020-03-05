package com.example.tp1.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tp1.dagger.DaggerAppComponent
import com.example.tp1.model.db.StoredPhoto
import com.example.tp1.repository.FirestoreRepository
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class FirestoreViewModel @Inject constructor(): ViewModel() {

    @Inject
    lateinit var firestoreRepository: FirestoreRepository

    var storedPhotos: MutableLiveData<List<StoredPhoto>> = MutableLiveData()
    init {
        DaggerAppComponent.create().injectFirestoreRepository(this)
    }

    fun saveStoredPhoto(storedPhoto: StoredPhoto) {
        firestoreRepository.saveStoredPhoto(storedPhoto).addOnFailureListener{
            Log.e(TAG, "Failed to save photo")
        }
    }

    fun getStoredPhotos(): LiveData<List<StoredPhoto>> {
        firestoreRepository.getStoredPhoto().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e!=null) {
                Log.w(TAG, "Listen failed.", e)
                storedPhotos.value = null
                return@EventListener
            }
            var savedStoredPhotos: MutableList<StoredPhoto> = mutableListOf()
            for (doc in value!!) {
                var storedPhoto = doc.toObject(StoredPhoto::class.java)
                savedStoredPhotos.add(storedPhoto)
            }
            storedPhotos.value = savedStoredPhotos
        })
        return storedPhotos
    }
}