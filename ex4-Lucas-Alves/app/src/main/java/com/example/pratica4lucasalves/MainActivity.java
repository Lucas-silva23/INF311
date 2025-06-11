package com.example.pratica4lucasalves;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.os.Build;


public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proxSensor, luzSensor;
    private float ultimaProximidade = -1, ultimaLuz = -1;

    private TextView txtProximidade, txtLuz;
    private Switch switchLanterna, switchMotor;
    private LanternaHelper lanternaHelper;
    private MotorHelper motorHelper;

    private final String ACAO_CLASSIFICACAO = "com.example.ACTION_CLASSIFICAR";

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchLanterna = findViewById(R.id.switchLanterna);
        switchMotor = findViewById(R.id.switchMotor);
        switchLanterna.setEnabled(true); // permite interação
        switchMotor.setEnabled(true);

        Button btnClassificar = findViewById(R.id.btnClassificar);
        btnClassificar.setOnClickListener(v -> enviarLeituras());

        lanternaHelper = new LanternaHelper(this);
        motorHelper = new MotorHelper(this);

        // Listeners para switches
        switchLanterna.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                lanternaHelper.ligar();
            } else {
                lanternaHelper.desligar();
            }
        });

        switchMotor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                motorHelper.iniciarVibracao();
            } else {
                motorHelper.pararVibracao();
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        luzSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(classificacaoReceiver,
                    new IntentFilter("com.example.RETORNO_CLASSIFICACAO"),
                    Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(classificacaoReceiver,
                    new IntentFilter("com.example.RETORNO_CLASSIFICACAO"));
        }
    }

    private void enviarLeituras() {
        Intent intent = new Intent(ACAO_CLASSIFICACAO);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("proximidade", ultimaProximidade);
        intent.putExtra("luz", ultimaLuz);
        sendBroadcast(intent);
    }

    private final BroadcastReceiver classificacaoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean luzBaixa = intent.getBooleanExtra("luz_baixa", false);
            boolean longe = intent.getBooleanExtra("longe", false);

            // Atualiza lanterna
            if (luzBaixa) {
                lanternaHelper.ligar();
                switchLanterna.setChecked(true);
            } else {
                lanternaHelper.desligar();
                switchLanterna.setChecked(false);
            }

            // Atualiza motor
            if (longe) {
                motorHelper.iniciarVibracao();
                switchMotor.setChecked(true);
            } else {
                motorHelper.pararVibracao();
                switchMotor.setChecked(false);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, luzSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        lanternaHelper.desligar();
        motorHelper.pararVibracao();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            ultimaProximidade = event.values[0];

        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            ultimaLuz = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
