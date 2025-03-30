package com.example.lucas_alves_calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    EditText entrada1, entrada2;
    TextView resultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entrada1 = findViewById(R.id.editTextValor1);
        entrada2 = findViewById(R.id.editTextValor2);
        resultado = findViewById(R.id.resultado);

        Button btnSoma = findViewById(R.id.buttonsomar);
        Button btnSubtrair = findViewById(R.id.buttonsubtrair);
        Button btnDivisao = findViewById(R.id.buttondividir);
        Button btnMult = findViewById(R.id.buttonmultiplicar);

        btnSoma.setOnClickListener(v -> {
            double num1 = Double.parseDouble(entrada1.getText().toString());
            double num2 = Double.parseDouble(entrada2.getText().toString());
            double res = num1 + num2;
            resultado.setText("Resultado: " + res);
        });

        btnSubtrair.setOnClickListener(v -> {
            double num1 = Double.parseDouble(entrada1.getText().toString());
            double num2 = Double.parseDouble(entrada2.getText().toString());
            double res = num1 - num2;
            resultado.setText("Resultado: " + res);

        });

        btnMult.setOnClickListener(v -> {
            double num1 = Double.parseDouble(entrada1.getText().toString());
            double num2 = Double.parseDouble(entrada2.getText().toString());
            double res = num1 * num2;
            resultado.setText("Resultado: " + res);
        });

        btnDivisao.setOnClickListener(v -> {
            double num1 = Double.parseDouble(entrada1.getText().toString());
            double num2 = Double.parseDouble(entrada2.getText().toString());
            if (num2 != 0) {
                double res = num1 / num2;
                resultado.setText("Resultado: " + res);
            } else {
                resultado.setText("Erro: Divisão por zero!");
            }
        });
    }
}
