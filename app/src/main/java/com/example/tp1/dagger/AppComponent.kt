package com.example.tp1.dagger

import com.example.tp1.MapsActivity
import com.example.tp1.viewmodel.FirestoreViewModel
import dagger.Component

@Component
interface AppComponent {
    fun injectFirestoreRepository(firestoreViewModel: FirestoreViewModel)
    fun injectFirestoreViewModel(mapsActivity: MapsActivity)
}