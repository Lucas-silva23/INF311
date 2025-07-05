package com.example.pratica_5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "checkinApp.db";
    private static final int DATABASE_VERSION = 1;

    // Table Categoria
    private static final String TABLE_CATEGORIA = "Categoria";
    private static final String COLUMN_ID_CATEGORIA = "idCategoria";
    private static final String COLUMN_NOME_CATEGORIA = "nome";

    // Table Checkin
    private static final String TABLE_CHECKIN = "Checkin";
    private static final String COLUMN_LOCAL = "Local";
    private static final String COLUMN_QTD_VISITAS = "qtdVisitas";
    private static final String COLUMN_CAT_FK = "cat";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Categoria Table [cite: 14]
        String CREATE_CATEGORIA_TABLE = "CREATE TABLE " + TABLE_CATEGORIA + "("
                + COLUMN_ID_CATEGORIA + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOME_CATEGORIA + " TEXT NOT NULL" + ")";
        db.execSQL(CREATE_CATEGORIA_TABLE);

        // Create Checkin Table [cite: 13]
        String CREATE_CHECKIN_TABLE = "CREATE TABLE " + TABLE_CHECKIN + "("
                + COLUMN_LOCAL + " TEXT PRIMARY KEY,"
                + COLUMN_QTD_VISITAS + " INTEGER NOT NULL,"
                + COLUMN_CAT_FK + " INTEGER NOT NULL,"
                + COLUMN_LATITUDE + " TEXT NOT NULL,"
                + COLUMN_LONGITUDE + " TEXT NOT NULL,"
                + "CONSTRAINT fkey0 FOREIGN KEY (" + COLUMN_CAT_FK + ")"
                + " REFERENCES " + TABLE_CATEGORIA + "(" + COLUMN_ID_CATEGORIA + "))";
        db.execSQL(CREATE_CHECKIN_TABLE);

        // Insert initial categories [cite: 15, 16, 17, 18, 19, 20, 21]
        db.execSQL("INSERT INTO Categoria (nome) VALUES ('Restaurante');");
        db.execSQL("INSERT INTO Categoria (nome) VALUES ('Bar');");
        db.execSQL("INSERT INTO Categoria (nome) VALUES ('Cinema');");
        db.execSQL("INSERT INTO Categoria (nome) VALUES ('Universidade');");
        db.execSQL("INSERT INTO Categoria (nome) VALUES ('Estádio');");
        db.execSQL("INSERT INTO Categoria (nome) VALUES ('Parque');");
        db.execSQL("INSERT INTO Categoria (nome) VALUES ('Outros');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIA);
        onCreate(db);
    }

    // --- CHECKIN CRUD OPERATIONS ---

    // Get a single Checkin by its name
    public Cursor getCheckin(String local) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CHECKIN, null, COLUMN_LOCAL + "=?", new String[]{local}, null, null, null);
    }

    // Insert a new Checkin
    public boolean insertCheckin(String local, int catId, String latitude, String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOCAL, local);
        values.put(COLUMN_QTD_VISITAS, 1);
        values.put(COLUMN_CAT_FK, catId);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        long result = db.insert(TABLE_CHECKIN, null, values);
        return result != -1;
    }

    // Update an existing Checkin (increment visit count)
    public boolean updateCheckin(String local) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CHECKIN + " SET " + COLUMN_QTD_VISITAS + " = " + COLUMN_QTD_VISITAS + " + 1 WHERE " + COLUMN_LOCAL + " = ?", new String[]{local});
        return true;
    }

    // Delete a Checkin
    public void deleteCheckin(String local) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHECKIN, COLUMN_LOCAL + "=?", new String[]{local});
    }

    // Get all checkins with category names
    public Cursor getAllCheckinsWithCategory() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c.*, cat.nome FROM " + TABLE_CHECKIN + " c JOIN "
                + TABLE_CATEGORIA + " cat ON c.cat = cat.idCategoria";
        return db.rawQuery(query, null);
    }

    // Get all checkin location names
    public ArrayList<String> getAllLocationNames() {
        ArrayList<String> locations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_LOCAL + " FROM " + TABLE_CHECKIN, null);
        if (cursor.moveToFirst()) {
            do {
                locations.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return locations;
    }

    // Get all checkins for the report, sorted by visits
    public Cursor getReportData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CHECKIN, new String[]{COLUMN_LOCAL, COLUMN_QTD_VISITAS},
                null, null, null, null, COLUMN_QTD_VISITAS + " DESC");
    }


    // --- CATEGORY CRUD OPERATIONS ---

    // Get all Categories
    public Cursor getAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        // SimpleCursorAdapter requires a column named "_id". We use a SQL alias to rename
        // our primary key 'idCategoria' to '_id' in the result set.
        String query = "SELECT " + COLUMN_ID_CATEGORIA + " as _id, " + COLUMN_NOME_CATEGORIA + " as nome FROM " + TABLE_CATEGORIA + " ORDER BY " + COLUMN_ID_CATEGORIA + " ASC";
        return db.rawQuery(query, null);
    }
}