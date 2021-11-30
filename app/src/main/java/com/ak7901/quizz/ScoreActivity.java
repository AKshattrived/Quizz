package com.ak7901.quizz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    private TextView scored,total;//to access score and total
    private Button doneBtn;//to access done btn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scored = findViewById(R.id.scored);
        total = findViewById(R.id.total);
        doneBtn = findViewById(R.id.done_btn);

        //Retrieving score and total from intent passed in questions activity
        scored.setText(String.valueOf(getIntent().getIntExtra("score",0)));
        total.setText("OUT OF "+String.valueOf(getIntent().getIntExtra("total",0)));

        //finish activity when clicked on done button
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}