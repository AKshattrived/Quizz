package com.ak7901.quizz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button startbtn,bookmarkBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startbtn = findViewById(R.id.start_btn);
        bookmarkBtn= findViewById(R.id.bookmarks_btn);

        /*
        To invoke Catagories Activity when clicked on Start Quizz Button.
        */
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent catagoryIntent = new Intent(MainActivity.this,CatagoriesActivity.class);
                startActivity(catagoryIntent);
            }
        });

        /*
        To invoke Bookmarks Activity when clicked on Bookmarks Button.
        */
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent BookmarksIntent = new Intent(MainActivity.this,BookmarkActivity.class);
                startActivity(BookmarksIntent);
            }
        });
    }
}