package com.example.maps_lucas_alves;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "localizacao.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação das tabelas
        db.execSQL("CREATE TABLE Location (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "descricao TEXT," +
                "latitude REAL," +
                "longitude REAL)");

        db.execSQL("CREATE TABLE Logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "msg TEXT," +
                "timestamp TEXT," +
                "id_location INTEGER," +
                "FOREIGN KEY(id_location) REFERENCES Location(id))");

        // Inserção dos pontos fixos
        db.execSQL("INSERT INTO Location (descricao, latitude, longitude) VALUES " +
                "('DPI UFV', -20.764872343354803, -42.868450416861826)," +
                "('São Fidélis', -21.63775729116445, -41.75588193206691)," +
                "('Viçosa', -20.757657102746958, -42.8842727730562)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Logs");
        db.execSQL("DROP TABLE IF EXISTS Location");
        onCreate(db);
    }
}
