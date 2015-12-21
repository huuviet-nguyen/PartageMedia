package com.example.viet.multimedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    Button Publier;
    Button Carte;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Publier = (Button) findViewById(R.id.publier);
        Carte = (Button) findViewById(R.id.carte);
        Publier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassPublierActivity();
            }
        });
        Carte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassCarteActivity();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    public void PassPublierActivity(){
        Intent i = new Intent(this, PublierActivity.class);
        startActivity(i);
    }

    private void PassCarteActivity() {
        Intent i = new Intent(this, CarteActivity.class);
        startActivity(i);
    }
}
