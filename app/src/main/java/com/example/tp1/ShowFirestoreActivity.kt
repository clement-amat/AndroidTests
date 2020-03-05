package com.example.tp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.tp1.dagger.DaggerAppComponent
import com.example.tp1.model.db.StoredPhoto
import com.example.tp1.viewmodel.FirestoreViewModel
import javax.inject.Inject

class ShowFirestoreActivity : AppCompatActivity() {

//    @Inject
//    lateinit var firestoreViewModel: FirestoreViewModel
//
//    var storedPhotos: List<StoredPhoto> = mutableListOf()
//
//    init {
//        DaggerAppComponent.create().injectFirestoreViewModel(this)
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_show_firestore)
//        var txt: TextView = findViewById(R.id.textView)
//        txt.text = "allo"
//        firestoreViewModel.getStoredPhotos().observe(this, Observer {
//            storedPhotos = it
//            txt.text = storedPhotos.size.toString()
//        })
//
//    }
}
