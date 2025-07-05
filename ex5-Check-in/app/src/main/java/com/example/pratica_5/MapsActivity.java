package com.example.pratica_5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.pratica_5.DBHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DBHelper dbHelper;
    private double currentLatitude, currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        dbHelper = new DBHelper(this);

        // Get current location passed from MainActivity [cite: 132]
        Intent intent = getIntent();
        currentLatitude = intent.getDoubleExtra("latitude", 0);
        currentLongitude = intent.getDoubleExtra("longitude", 0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Center map on user's current location [cite: 131]
        LatLng userLocation = new LatLng(currentLatitude, currentLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        loadCheckinMarkers();
    }

    private void loadCheckinMarkers() {
        Cursor cursor = dbHelper.getAllCheckinsWithCategory();
        if (cursor.moveToFirst()) {
            do {
                String local = cursor.getString(cursor.getColumnIndexOrThrow("Local"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                int visitas = cursor.getInt(cursor.getColumnIndexOrThrow("qtdVisitas"));
                double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("latitude")));
                double lon = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("longitude")));

                LatLng checkinPosition = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions()
                        .position(checkinPosition)
                        .title(local) // [cite: 99]
                        .snippet("Categoria: " + categoria + " Visitas: " + visitas)); // [cite: 99]
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.voltar_principal) {
            finish(); // Go back to MainActivity
            return true;
        } else if (id == R.id.gestao_checkin_mapa) {
            startActivity(new Intent(this, GestaoCheckinActivity.class));
            return true;
        } else if (id == R.id.relatorio_mapa) {
            startActivity(new Intent(this, RelatorioActivity.class));
            return true;
        } else if (id == R.id.mapa_normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // [cite: 139]
            return true;
        } else if (id == R.id.mapa_hibrido) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // [cite: 139]
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}