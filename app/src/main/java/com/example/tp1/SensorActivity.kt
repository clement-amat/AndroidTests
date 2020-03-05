package com.example.tp1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_sensor.*
import java.util.*

class SensorActivity : AppCompatActivity(), SensorEventListener {

    lateinit var tvName: TextView;
    lateinit var tvType: TextView;
    lateinit var tvResolution: TextView;
    lateinit var tvPower: TextView;
    lateinit var tvValue: TextView;

    lateinit var sensorManager: SensorManager;

    public var sensorId: Int = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)
        tvName = findViewById(R.id.tvName);
        tvType = findViewById(R.id.tvType);
        tvPower = findViewById(R.id.tvPower);
        tvValue = findViewById(R.id.tvValue);
        tvResolution = findViewById(R.id.tvResolution)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager;

        sensorId = intent.getIntExtra("sensorId", Sensor.TYPE_MAGNETIC_FIELD);
    }


    override fun onResume() {
        super.onResume();

        val sensor: Sensor = sensorManager.getDefaultSensor(sensorId); // TODO might be null

        tvName.text = sensor.name;
        tvPower.text = sensor.power.toString();
        tvResolution.text = sensor.resolution.toString();
        tvType.text = sensor.type.toString();

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    override fun onStop() {
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    override fun onSensorChanged(event: SensorEvent?) {
        tvValue.text = Arrays.toString(event?.values)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}
