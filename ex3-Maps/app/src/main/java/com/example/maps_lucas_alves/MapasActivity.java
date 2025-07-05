package com.example.maps_lucas_alves;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;

import java.time.Instant;

public class MapasActivity extends FragmentActivity implements OnMapReadyCallback {
    private final LatLng DPI_UFV = new LatLng(-20.764872343354803, -42.868450416861826);
    private final LatLng Sao_Fidelis = new LatLng(-21.63775729116445, -41.75588193206691);
    private final LatLng Vicosa = new LatLng(-20.757657102746958, -42.8842727730562);
    private GoogleMap map;
    private int tag;
    private DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker marcadorAtual;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapas);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Intent it = getIntent();
        tag = it.getIntExtra("tag", -1);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Solicita permissões se não tiver
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_LOCATION_PERMISSION);
        }

        dbHelper = new DatabaseHelper(this);

        // Registra o log com base no tag recebido
        switch (tag) {
            case 0:
                registrarLog("São Fidélis");
                break;
            case 1:
                registrarLog("Viçosa");
                break;
            case 2:
                registrarLog("DPI UFV");
                break;
        }
    }

    private void registrarLog(String descricao) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM Location WHERE descricao = ?", new String[]{descricao});
        int idLocation = -1;
        if (cursor.moveToFirst()) {
            idLocation = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }
        cursor.close();

        if (idLocation != -1) {
            ContentValues values = new ContentValues();
            values.put("msg", descricao);
            values.put("timestamp", Instant.now().toString());
            values.put("id_location", idLocation);
            db.insert("Logs", null, values);
        }
    }

    public void onClickVicosa(View v) {
        goToVicosa();
    }

    public void onClickSaoFidelis(View v) {
        goToSaoFidelis();
    }

    public void onClickDpiUfv(View v) {
        goToDpiUfv();
    }

    private void goToVicosa() {
        if (map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(Vicosa)
                            .tilt(60)
                            .zoom(16)
                            .build());
            map.animateCamera(c);
        }
    }

    private void goToSaoFidelis() {
        if (map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(Sao_Fidelis)
                            .tilt(60)
                            .zoom(16)
                            .build());
            map.animateCamera(c);
        }
    }

    private void goToDpiUfv() {
        if (map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(DPI_UFV)
                            .tilt(60)
                            .zoom(16)
                            .build());
            map.animateCamera(c);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Location", null);

        while (cursor.moveToNext()) {
            String descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"));
            double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

            LatLng ponto = new LatLng(lat, lng);
            map.addMarker(new MarkerOptions()
                    .position(ponto)
                    .title(descricao)
                    .snippet(descricao)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        cursor.close();

        switch (tag) {
            case 0:
                goToSaoFidelis();
                break;
            case 1:
                goToVicosa();
                break;
            case 2:
                goToDpiUfv();
                break;
        }
    }

    public void onClickLocalizacao(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null && map != null) {
                    LatLng atual = new LatLng(location.getLatitude(), location.getLongitude());

                    if (marcadorAtual != null) {
                        marcadorAtual.remove();
                    }

                    marcadorAtual = map.addMarker(new MarkerOptions()
                            .position(atual)
                            .title("Minha localização atual")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(atual, 16);
                    map.animateCamera(update);

                    float[] resultados = new float[1];
                    Location.distanceBetween(
                            atual.latitude, atual.longitude,
                            Vicosa.latitude, Vicosa.longitude,
                            resultados);
                    float distancia = resultados[0];

                    Toast.makeText(MapasActivity.this,
                            "Distância até Viçosa: " + (int) distancia + " metros",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão de localização concedida", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickLogs(View view) {
        Intent intent = new Intent(this, LogListActivity.class);
        startActivity(intent);
    }

}