package com.example.imc_calculadora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText inputNome, inputIdade, inputPeso, inputAltura;
    Button btnRelatorio;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputNome = findViewById(R.id.inputNome);
        inputIdade = findViewById(R.id.inputIdade);
        inputPeso = findViewById(R.id.inputPeso);
        inputAltura = findViewById(R.id.inputAltura);
        btnRelatorio = findViewById(R.id.btnRelatorio);

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // A ResultActivity retornou com sucesso (o usuário clicou em "Voltar")
                        // Limpe os campos
                        inputNome.setText("");
                        inputIdade.setText("");
                        inputPeso.setText("");
                        inputAltura.setText("");

                        // Opcional: exibir uma mensagem para o usuário
                        Toast.makeText(this, "Campos limpos.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnRelatorio.setOnClickListener(v -> {
            String nome = inputNome.getText().toString().trim();
            String idadeStr = inputIdade.getText().toString().trim();
            String pesoStr = inputPeso.getText().toString().trim();
            String alturaStr = inputAltura.getText().toString().trim();

            if (nome.isEmpty() || idadeStr.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int idade = Integer.parseInt(idadeStr);
                float peso = Float.parseFloat(pesoStr);
                float altura = Float.parseFloat(alturaStr);

                // Verificação de altura (importante para evitar divisão por zero)
                if (altura == 0) {
                    Toast.makeText(this, "Altura não pode ser zero.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent it = new Intent(MainActivity.this, ResultActivity.class);
                it.putExtra("nome", nome);
                it.putExtra("idade", idade);
                it.putExtra("peso", peso);
                it.putExtra("altura", altura);
                resultLauncher.launch(it); // Usa o launcher para iniciar a Activity

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Erro ao converter valores numéricos. Verifique os campos.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}