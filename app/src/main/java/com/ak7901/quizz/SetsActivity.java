package com.ak7901.quizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.GridView;

import java.util.List;

public class SetsActivity extends AppCompatActivity {

    private GridView gridView;//To access grid view from activity_sets.xml
    private List<String> sets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        //Setting toolbar as support action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        //setting values in grid view with the help of grid adapter
        gridView = findViewById(R.id.gridview);

        sets = CatagoriesActivity.list.get(getIntent().getIntExtra("position", 0)).getSets();

        //To pass title and sets to adapter to pass in questions activity
        GridAdapter adapter = new GridAdapter(sets,getIntent().getStringExtra("title"));
        gridView.setAdapter(adapter);
    }

    //For enabled back function in support action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}