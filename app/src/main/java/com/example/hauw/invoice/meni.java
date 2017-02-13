package com.example.hauw.invoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class meni extends ActionBarActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meni);
        //DA DOBIMO ID
        SharedPreferences sp = getSharedPreferences("MyPrefs", 0);
        String idUser = sp.getString("id_user", null);
        Toast.makeText(getBaseContext(), idUser, Toast.LENGTH_SHORT).show();

        Button btnUstvari = (Button)findViewById(R.id.btnUstvari); // deklaracija gumba za nadaljevanje do vpisnega forma
        Button btnPreglej = (Button)findViewById(R.id.btnPreglej);

        btnPreglej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pojdiDoPredracunov = new Intent(meni.this, predracuni.class);
                startActivity(pojdiDoPredracunov);
            }
        });

        btnUstvari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pojdiDoNaslovnika = new Intent(meni.this, naslovnik.class);
                startActivity(pojdiDoNaslovnika);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meni, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
