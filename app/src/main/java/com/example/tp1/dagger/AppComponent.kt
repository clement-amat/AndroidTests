package com.example.tp1.dagger

import com.example.tp1.ShowFirestoreActivity
import com.example.tp1.viewmodel.FirestoreRepository
import dagger.Component

@Component
interface AppComponent {
    fun injectFirestoreRepository(showFirestoreActivity:ShowFirestoreActivity)
}