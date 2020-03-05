package com.example.tp1.viewmodel

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class FirestoreRepository : ViewModel() {

    fun vroom() {
        System.out.println("hello");
    }
}