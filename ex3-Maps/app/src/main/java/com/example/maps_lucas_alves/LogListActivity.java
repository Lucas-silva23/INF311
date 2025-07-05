package com.example.maps_lucas_alves;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogListActivity extends ListActivity {
    private DatabaseHelper dbHelper;
    private ArrayList<String> logsList;
    private ArrayList<LogItem> logItems; // Guarda dados completos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Meus Logs");

        dbHelper = new DatabaseHelper(this);
        logsList = new ArrayList<>();
        logItems = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Log.d("LogListActivity", "Iniciando consulta ao banco de dados...");

        Cursor cursor = db.rawQuery("SELECT Logs.id, Logs.msg, Logs.timestamp, Location.latitude, Location.longitude " +
                "FROM Logs INNER JOIN Location ON Logs.id_location = Location.id", null);

        if (cursor.getCount() == 0) {
            Log.d("LogListActivity", "Nenhum log encontrado");
            Toast.makeText(this, "Nenhum log encontrado", Toast.LENGTH_SHORT).show();
        }

        while (cursor.moveToNext()) {
            String msg = cursor.getString(cursor.getColumnIndexOrThrow("msg"));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
            double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

            String itemText = msg + " - " + timestamp;
            logsList.add(itemText);
            logItems.add(new LogItem(msg, timestamp, lat, lng));
        }
        cursor.close();

        Log.d("LogListActivity", "Encontrados " + logsList.size() + " registros.");

        if (logsList.isEmpty()) {
            Toast.makeText(this, "Nenhum log encontrado", Toast.LENGTH_SHORT).show();
        }

        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 0; i < logsList.size(); i++) {
            Map<String, String> item = new HashMap<>();
            item.put("log_text", logsList.get(i));
            data.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                data,
                R.layout.item_log,
                new String[]{"log_text"},
                new int[]{R.id.textLog}
        );
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        LogItem item = logItems.get(position);

        String mensagem = "Lat: " + item.latitude + ", Long: " + item.longitude;
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    private static class LogItem {
        String msg;
        String timestamp;
        double latitude;
        double longitude;

        LogItem(String msg, String timestamp, double latitude, double longitude) {
            this.msg = msg;
            this.timestamp = timestamp;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}