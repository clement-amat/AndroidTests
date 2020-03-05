package com.example.tp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tp1.viewmodel.FirestoreRepository
import javax.inject.Inject

class ShowFirestoreActivity : AppCompatActivity() {


    @Inject
    lateinit var firestoreRepository: FirestoreRepository

    init {
        
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_firestore)
    }
}
