package com.example.pratica_5;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pratica_5.DBHelper;

public class RelatorioActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private LinearLayout layoutLocais;
    private LinearLayout layoutVisitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        dbHelper = new DBHelper(this);
        layoutLocais = findViewById(R.id.layoutLocais);
        layoutVisitas = findViewById(R.id.layoutVisitas);

        loadReportData();
    }

    private void loadReportData() {
        // Get data sorted by visit count [cite: 237]
        Cursor cursor = dbHelper.getReportData();

        if (cursor.moveToFirst()) {
            do {
                String localName = cursor.getString(cursor.getColumnIndexOrThrow("Local"));
                int visitCount = cursor.getInt(cursor.getColumnIndexOrThrow("qtdVisitas"));

                // Add TextView for location name
                TextView tvLocal = new TextView(this);
                tvLocal.setText(localName);
                tvLocal.setPadding(8, 8, 8, 8);
                layoutLocais.addView(tvLocal);

                // Add TextView for visit count
                TextView tvVisitas = new TextView(this);
                tvVisitas.setText(String.valueOf(visitCount));
                tvVisitas.setPadding(8, 8, 8, 8);
                layoutVisitas.addView(tvVisitas);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gestao_relatorio_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.voltar) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}