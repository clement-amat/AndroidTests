package com.example.tp1

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

class SensorsListActivity : AppCompatActivity() {

    lateinit var listView: ListView;
    lateinit var sensorManager: SensorManager;
    lateinit var sensors: List<Sensor>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensors_list)

        listView = findViewById(R.id.list_view);

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        val sensorsAdapter = ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, sensors.map { s -> s.name})
        listView.adapter = sensorsAdapter;

        listView.isClickable = true;
        listView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, SensorActivity::class.java);
            intent.putExtra("sensorId", sensors[position].type);
            startActivity(intent);
        });
    }
}
