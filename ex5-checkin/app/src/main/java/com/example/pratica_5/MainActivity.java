package com.example.pratica_5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.pratica_5.DBHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private DBHelper dbHelper;
    private AutoCompleteTextView autoCompleteLocal;
    private Spinner spinnerCategoria;
    private TextView tvLatitude, tvLongitude;
    private Button btnCheckin;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        autoCompleteLocal = findViewById(R.id.autoCompleteTextViewLocal);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        tvLatitude = findViewById(R.id.textViewLatitude);
        tvLongitude = findViewById(R.id.textViewLongitude);
        btnCheckin = findViewById(R.id.buttonCheckin);

        setupAutoComplete();
        setupSpinner();
        setupLocationUpdates();

        btnCheckin.setOnClickListener(v -> handleCheckin());

        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void setupLocationUpdates() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    currentLocation = location;
                    tvLatitude.setText("Latitude: " + currentLocation.getLatitude());
                    tvLongitude.setText("Longitude: " + currentLocation.getLongitude());
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000); // 5 seconds
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void setupAutoComplete() {
        ArrayList<String> locations = dbHelper.getAllLocationNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
        autoCompleteLocal.setAdapter(adapter);
    }

    private void setupSpinner() {
        Cursor cursor = dbHelper.getAllCategories();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,
                cursor,
                new String[]{"nome"},
                new int[]{android.R.id.text1},
                0
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
    }

    private void handleCheckin() {
        String local = autoCompleteLocal.getText().toString().trim();
        if (local.isEmpty()) {
            Toast.makeText(this, "Por favor, digite o nome de um local.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentLocation == null) {
            Toast.makeText(this, "Aguardando obtenção da posição...", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor checkinCursor = dbHelper.getCheckin(local);
        if (checkinCursor != null && checkinCursor.getCount() > 0) {
            // Local exists, update it [cite: 64]
            dbHelper.updateCheckin(local);
            Toast.makeText(this, "Check-in atualizado para " + local, Toast.LENGTH_SHORT).show();
        } else {
            // New local, insert it [cite: 60]
            Cursor selectedCategory = (Cursor) spinnerCategoria.getSelectedItem();
            if (selectedCategory == null) {
                Toast.makeText(this, "Por favor, escolha uma categoria.", Toast.LENGTH_SHORT).show();
                return;
            }
            int catId = selectedCategory.getInt(selectedCategory.getColumnIndexOrThrow("_id"));
            String lat = String.valueOf(currentLocation.getLatitude());
            String lon = String.valueOf(currentLocation.getLongitude());

            dbHelper.insertCheckin(local, catId, lat, lon);
            Toast.makeText(this, "Novo check-in realizado em " + local, Toast.LENGTH_SHORT).show();
        }
        if (checkinCursor != null) {
            checkinCursor.close();
        }

        // Refresh screen [cite: 58]
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mapa_checkin) {
            if (currentLocation == null) {
                Toast.makeText(this, "Posição atual não disponível. Não é possível abrir o mapa.", Toast.LENGTH_LONG).show();
                return true;
            }
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("latitude", currentLocation.getLatitude());
            intent.putExtra("longitude", currentLocation.getLongitude());
            startActivity(intent);
            return true;
        } else if (id == R.id.gestao_checkin) {
            startActivity(new Intent(this, GestaoCheckinActivity.class));
            return true;
        } else if (id == R.id.relatorio) {
            startActivity(new Intent(this, RelatorioActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}