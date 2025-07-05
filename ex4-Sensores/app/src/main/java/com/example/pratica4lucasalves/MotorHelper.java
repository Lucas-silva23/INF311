package com.example.pratica4lucasalves;  //TODO: atualizar para o nome do seu pacote!

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

public class MotorHelper {
    private Context ctx;

    public MotorHelper(Context ctx){
        this.ctx = ctx;
    }

    public void iniciarVibracao() {
        Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            Log.d("MotorHelper", "Iniciando vibração");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibrationEffect effect = VibrationEffect.createWaveform(new long[]{0, 500, 500}, 0);
                v.vibrate(effect);
            } else {
                long[] pattern = {0, 500, 500};
                v.vibrate(pattern, 0);
            }
        } else {
            Log.d("MotorHelper", "Vibrator nulo ou não suportado");
        }
    }

    public void pararVibracao() {
        Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.cancel(); // interrompe a vibração
        }
    }
}