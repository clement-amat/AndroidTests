package com.example.tp1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.tp1.helpers.StorageHelper
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    public lateinit var sensorButton: Button;
    public lateinit var cameraButton: Button;
    public lateinit var mapBtn: Button;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorButton = findViewById(R.id.btn);
        sensorButton.setOnClickListener(View.OnClickListener {
                v -> startActivity(Intent(this, SensorsListActivity::class.java))
        });

        cameraButton = findViewById(R.id.btn3);
        cameraButton.setOnClickListener { v -> startActivity(Intent(this, CameraActivity::class.java))}

        mapBtn = findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener {
            v ->
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }


}
