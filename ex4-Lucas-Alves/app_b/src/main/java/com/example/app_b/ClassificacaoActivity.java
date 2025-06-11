package com.example.app_b;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.app_b.R;

public class ClassificacaoActivity extends Activity {

    private float valorLuz, valorProximidade;
    private TextView txtLuz, txtProximidade;
    private final String ACAO_RETORNO = "com.example.RETORNO_CLASSIFICACAO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classificacao);

        txtLuz = findViewById(R.id.txtLuzRecebida);
        txtProximidade = findViewById(R.id.txtProximidadeRecebida);
        Button btnDevolver = findViewById(R.id.btnDevolverClassificacao);

        // Recupera os valores enviados pelo App A
        Intent intent = getIntent();
        valorLuz = intent.getFloatExtra("luz", -1);
        valorProximidade = intent.getFloatExtra("proximidade", -1);

        txtLuz.setText("Luz: " + valorLuz + " lx");
        txtProximidade.setText("Proximidade: " + valorProximidade + " cm");

        btnDevolver.setOnClickListener(v -> devolverClassificacao());
    }

    private void devolverClassificacao() {
        boolean luzBaixa = valorLuz < 20.0;
        boolean longe = valorProximidade > 3.0;

        Intent resposta = new Intent(ACAO_RETORNO);
        resposta.putExtra("luz_baixa", luzBaixa);
        resposta.putExtra("longe", longe);
        sendBroadcast(resposta);

        finish(); // Fecha a activity
    }
}
