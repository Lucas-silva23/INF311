package com.example.maps_lucas_alves;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String menu[] = new String[] {
                "Minha casa na Cidade Natal",
                "Minha Casa Em Viçosa",
                "Meu Departamento",
                "Fechar Aplicação"
        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menu);
        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){

        Intent it = new Intent(getBaseContext(), MapasActivity.class);
        String aux = l.getItemAtPosition(position).toString();

        switch(position){
            case 0:
                it.putExtra("tag", 0);

                Toast.makeText(getBaseContext(),aux, Toast.LENGTH_SHORT).show();
                startActivity(it);
                break;
            case 1:
                it.putExtra("tag", 1);

                Toast.makeText(getBaseContext(),aux, Toast.LENGTH_SHORT).show();
                startActivity(it);
                break;
            case 2:
                it.putExtra("tag", 2);

                Toast.makeText(getBaseContext(),aux, Toast.LENGTH_SHORT).show();
                startActivity(it);
                break;
            case 3:
                finish();
        }
    }

}