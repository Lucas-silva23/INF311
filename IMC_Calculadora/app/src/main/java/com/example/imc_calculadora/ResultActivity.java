package com.example.imc_calculadora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Recuperar dados da intent
        Intent intent = getIntent();
        String nome = intent.getStringExtra("nome");
        int idade = intent.getIntExtra("idade", 0);
        float peso = intent.getFloatExtra("peso", 0f);
        float altura = intent.getFloatExtra("altura", 1f);

        // Calcular IMC
        float imc = peso / (altura * altura);
        String classificacao = getClassificacaoIMC(imc);

        // Atualizar TextView com os dados
        TextView resultado = findViewById(R.id.textResultado);
        if (resultado != null) {
            String resultadoFormatado = String.format(Locale.getDefault(), getString(R.string.resultado_imc_formatado), nome, idade, peso, altura, imc, classificacao);
            resultado.setText(resultadoFormatado);
        }

        // Botão de voltar
        Button voltar = findViewById(R.id.btnVoltar);
        if (voltar != null) {
            voltar.setOnClickListener(v -> {
                // Volta para a tela anterior
                Intent it = new Intent();  // Pode ser um Intent vazio
                setResult(RESULT_OK, it);  // Define o resultado como RESULT_OK
                finish(); // encerra essa tela
            });
        }
    }

    private String getClassificacaoIMC(float imc) {
        if (imc < 18.5) return "Abaixo do Peso";
        else if (imc < 25) return "Saudável";
        else if (imc < 30) return "Sobrepeso";
        else if (imc < 35) return "Obesidade Grau I";
        else if (imc < 40) return "Obesidade Grau II (severa)";
        else return "Obesidade Grau III (mórbida)";
    }
}