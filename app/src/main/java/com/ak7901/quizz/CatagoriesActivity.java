package com.ak7901.quizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CatagoriesActivity extends AppCompatActivity {

    //Retrieves the instance from firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private Dialog loadingDialog;

    private RecyclerView recyclerView;//To access recycler view from activity_catagories.xml
    public static List<CategoryModel> list;////A category model list to carry different category and data related to it.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagories);

        //Setting toolbar as support action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing loadingDialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);//setting loading.xml layout as loading dialog box
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));//set background rounded_corners
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(true);//can be later changed if we want loading dialog box to be cancelable

        //Accessing recycler view and setting new vertical linear layout manager for it
        recyclerView = findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //Setting a category adapter and setting it to recycler view
        list = new ArrayList<>();
        CategoryAdapter adapter = new CategoryAdapter(list);
        recyclerView.setAdapter(adapter);

        //show loading dialog box
        loadingDialog.show();

        //Query to retrieve data for categories from firebase
        myRef.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Retrieve all the categories and store it in the list
                for (DataSnapshot snapshot1 : snapshot.getChildren()){

                    List<String> sets = new ArrayList<>();
                    for (DataSnapshot snapshot2 : snapshot1.child("sets").getChildren()){
                        sets.add(snapshot2.getKey());
                    }

                    list.add(new CategoryModel(snapshot1.child("name").getValue().toString(),sets,snapshot1.child("url").getValue().toString(),snapshot1.getKey()));
                }
                adapter.notifyDataSetChanged();//Refresh adapter with new retrieved data
                loadingDialog.dismiss();//dismiss loading dialog box when data retrieved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CatagoriesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();//dismiss loading dialog box when get error from database
                finish();
            }
        });
    }

    //For enabled back function in support action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}