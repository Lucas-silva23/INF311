package com.example.pratica_5;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pratica_5.DBHelper;

public class GestaoCheckinActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private LinearLayout layoutConteudo;
    private LinearLayout layoutDeletar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestao_checkin);

        dbHelper = new DBHelper(this);
        layoutConteudo = findViewById(R.id.layoutConteudo);
        layoutDeletar = findViewById(R.id.layoutDeletar);

        loadCheckinList();
    }

    private void loadCheckinList() {
        Cursor cursor = dbHelper.getAllCheckinsWithCategory();

        if (cursor.moveToFirst()) {
            do {
                final String localName = cursor.getString(cursor.getColumnIndexOrThrow("Local"));

                // Add TextView for location name [cite: 195]
                TextView tvLocal = new TextView(this);
                tvLocal.setText(localName);
                tvLocal.setPadding(8, 8, 8, 8);
                layoutConteudo.addView(tvLocal);

                // Add ImageButton for deleting [cite: 196]
                ImageButton btnDelete = new ImageButton(this);
                btnDelete.setImageResource(android.R.drawable.ic_delete);
                btnDelete.setTag(localName); // Use primary key as tag [cite: 199]

                btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog((String) v.getTag()));
                layoutDeletar.addView(btnDelete);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void showDeleteConfirmationDialog(final String localName) {
        new AlertDialog.Builder(this)
                .setTitle("Exclusão")
                .setMessage("Tem certeza que deseja excluir " + localName + "?") // [cite: 200]
                .setPositiveButton("SIM", (dialog, which) -> {
                    dbHelper.deleteCheckin(localName);
                    Toast.makeText(GestaoCheckinActivity.this, localName + " excluído.", Toast.LENGTH_SHORT).show();
                    // Refresh screen [cite: 201]
                    finish();
                    startActivity(getIntent());
                })
                .setNegativeButton("NÃO", null)
                .show();
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