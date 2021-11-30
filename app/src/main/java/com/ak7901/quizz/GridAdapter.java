package com.ak7901.quizz;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    private List<String> sets;
    private String category;//to pass category in questions activity

    public GridAdapter(List<String> sets,String category) {

        this.sets = sets;
        this.category = category;
    }

    @Override
    public int getCount() {
        return sets.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        /*
        To inflate set_item layout
        */
        if (convertView ==  null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_item,parent,false);
        }else {
            view = convertView;
        }

        //A click listener to invoke QuestionsActivity.java
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent questionIntent = new Intent(parent.getContext(),QuestionsActivity.class);
                questionIntent.putExtra("category",category);//to pass title of the category
                questionIntent.putExtra("setId",sets.get(position));//to pass set
                parent.getContext().startActivity(questionIntent);
            }
        });

        //setting position number
        ((TextView)view.findViewById(R.id.textview)).setText(String.valueOf(position+1));

        return view;
    }
}
