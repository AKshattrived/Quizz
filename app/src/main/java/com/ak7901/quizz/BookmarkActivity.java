package com.ak7901.quizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.ak7901.quizz.QuestionsActivity.FILE_NAME;
import static com.ak7901.quizz.QuestionsActivity.KEY_NAME;

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<QuestionModel>bookmarksList;

    private SharedPreferences preferences;//to retrieve bookmarked question list from shared preferences
    private SharedPreferences.Editor editor;//to store bookmarked question list in shared preferences
    private Gson gson;//to convert json to question model and vice versa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        //Setting toolbar as support action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bookmarks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //accessing recycler view
        recyclerView = findViewById(R.id.rv_bookmarks);

        //initializing shared preferences variables and Gson variable
        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();//method calling to retrieve bookmarked questions from shared preference

        //setting linear layout manager for recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //setting adapter for recyclerview
        BookmarksAdapter adapter = new BookmarksAdapter(bookmarksList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();//method calling to store bookmark questions list offline in shared preferences
    }

    //For enabled back function in support action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //to retrieve bookmarked questions list from shared preference and store it in bookmark list
    private void getBookmarks(){

        String json = preferences.getString(KEY_NAME,"");

        Type type = new TypeToken<List<QuestionModel>>(){}.getType();

        bookmarksList = gson.fromJson(json,type);

        if (bookmarksList == null){
            bookmarksList = new ArrayList<>();
        }
    }

    //to store bookmarked questions list in shared preference in form of json file
    private void storeBookmarks(){

        String json = gson.toJson(bookmarksList);
        editor.putString(KEY_NAME,json);
        editor.commit();

    }
}
