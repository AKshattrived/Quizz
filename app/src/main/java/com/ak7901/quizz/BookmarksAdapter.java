package com.ak7901.quizz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
/*
bookmarks Adapter containing View Holder to set data in bookmark_item.xml and activity_bookmark.xml recyclerview
*/
public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.viewholder> {

    private List<QuestionModel> list;//A question model list to carry bookmarked questions

    public BookmarksAdapter(List<QuestionModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
        To inflate bookmark_item layout and pass it to viewholder
        */
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        //setData call to set data
        holder.setData(list.get(position).getQuestion(),list.get(position).getAnswer(),position);
    }

    @Override
    public int getItemCount() {
        //number of items to be shown
        return list.size();
    }

    class viewholder extends RecyclerView.ViewHolder{

        private TextView question,answer;//to access question and answer text view from bookmark_item.xml
        private ImageButton deleteBtn;//to access delete button from bookmark_item.xml

        public viewholder(@NonNull View itemView) {
            super(itemView);

            //accessing question,answer text view and delete button
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }

        //it will set question and answer in their respective text views and carries functionality of delete btn
        //position as parameter to delete the question of the passed position
        private void setData(String question, String answer,int position){
            this.question.setText(question);
            this.answer.setText(answer);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    notifyItemRemoved(position);
                }
            });

        }
    }
}
