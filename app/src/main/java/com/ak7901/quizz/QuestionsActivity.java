package com.ak7901.quizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    public static final String FILE_NAME = "QUIZZ";
    public static final String KEY_NAME = "QUESTIONS";

    //Retrieves the instance from firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    private TextView question,noIndicator;//to access question and question index text view
    private FloatingActionButton bookmarkBtn;//to access bookmark button
    private LinearLayout optionsContainer;//to access options container
    private Button shareBtn,nextBtn;//to access share and next button
    private int count=0;//used to play 4 options animation in playAnim method
    List<QuestionModel> list;//Question Model list
    private int position=0;//question position
    private int score=0;//to store score
    private String setId;//to retrieve setNO from intent
    private Dialog loadingDialog;

    private List<QuestionModel>bookmarksList;//to store bookmarked questions

    private SharedPreferences preferences;//to retrieve bookmarked question list from shared preferences
    private SharedPreferences.Editor editor;//to store bookmarked question list in shared preferences
    private Gson gson;//to convert json to question model and vice versa
    private int matchedQuestionPosition;//to save matched questions position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);


        //Setting toolbar as support action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Accessing all the components
        question = findViewById(R.id.question);
        noIndicator = findViewById(R.id.no_indicator);
        bookmarkBtn = findViewById(R.id.bookmark_btn);
        optionsContainer = findViewById(R.id.options_container);
        shareBtn = findViewById(R.id.share_btn);
        nextBtn = findViewById(R.id.next_btn);

        //initializing shared preferences variables and Gson variable
        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();//method calling to retrieve bookmarked questions from shared preference

        //bookmarks btn click listener to store/remove question in/from list
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelMatch()){
                    //if question is already bookmarked then remove bookmark
                    bookmarksList.remove(matchedQuestionPosition);
                    //set bordered bookmark button
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                }else {
                    //if question is not bookmarked then bookmark it
                    bookmarksList.add(list.get(position));
                    //set filled bookmark button
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });

        //Retrieving category and setNO from intent passed in gridAdapter
        setId = getIntent().getStringExtra("setId");

        //Initializing loadingDialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);//setting loading.xml layout as loading dialog box
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));//set background rounded_corners
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);//can be later changed if we want loading dialog box not to be cancelable

        list = new ArrayList<>();

        //show loading dialog box
        loadingDialog.show();

        //Query to retrieve data for question from firebase database
        myRef.child("SETS").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Retrieving all the questions at once with for loop and store it in a list
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    String id = snapshot1.getKey();
                    String question = snapshot1.child("question").getValue().toString();
                    String a = snapshot1.child("optionA").getValue().toString();
                    String b = snapshot1.child("optionB").getValue().toString();
                    String c = snapshot1.child("optionC").getValue().toString();
                    String d = snapshot1.child("optionD").getValue().toString();
                    String correctANS = snapshot1.child("correctANS").getValue().toString();

                    list.add(new QuestionModel(id,question,a,b,c,d,correctANS,setId));
                }

                //There should be questions in it,otherwise finish activity
                if (list.size() > 0){

                    //to check whether option is selected or not
                    for (int i=0 ; i < 4 ; i++){
                        optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkAnswer((Button) v);//Calling of check answer method
                            }
                        });
                    }
                    playAnim(question,0,list.get(position).getQuestion());//to set first question by default

                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //disable next button for next question
                            nextBtn.setEnabled(false);
                            nextBtn.setAlpha(0.6f);
                            //enable options button
                            enableOption(true);
                            position++;
                            //go to score activity if last question
                            if (position == list.size()){
                                Intent scoreIntent = new Intent(QuestionsActivity.this,ScoreActivity.class);
                                scoreIntent.putExtra("score",score);
                                scoreIntent.putExtra("total",list.size());
                                startActivity(scoreIntent);
                                finish();
                                return;
                            }
                            count=0;//default to 0 for next question
                            playAnim(question,0,list.get(position).getQuestion());//play animation for next question
                        }
                    });

                    //click listener for share button
                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //string to store question
                            String body = list.get(position).getQuestion() +"\n" +
                                    "A)"+list.get(position).getA() + "\n" +
                                    "B)"+list.get(position).getB() +"\n" +
                                    "C)"+list.get(position).getC() +"\n" +
                                    "D)"+list.get(position).getC() ;

                            //intent to send string containing question
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("plain/text");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Answer The Question");
                            shareIntent.putExtra(Intent.EXTRA_TEXT,body);
                            startActivity(Intent.createChooser(shareIntent,"Share Via"));
                        }
                    });
                }else {
                    //finish activity if there is no questions in selected set and a toast to inform user
                    finish();
                    Toast.makeText(QuestionsActivity.this, "no questions", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();//dismiss loading dialog box when data retrieved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuestionsActivity.this, error.getMessage() , Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();//dismiss loading dialog box when get error from database
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();//method calling to store bookmark questions list offline in shared preferences
    }

    //To play animation for questions and options
    private void playAnim(final View view, final int value, final String data){

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //It will set 4 options
                if (value == 0 && count < 4) {
                    String option="";
                    if (count == 0){
                        option = list.get(position).getA();
                    }else if (count == 1){
                        option = list.get(position).getB();
                    }else if (count == 2){
                        option = list.get(position).getC();
                    }else if (count == 3){
                        option = list.get(position).getD();
                    }
                    playAnim(optionsContainer.getChildAt(count),0,option);
                    count++;
                }

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //setting data
                if (value == 0){
                    try {
                        ((TextView)view).setText(data);//sets question in text view
                        noIndicator.setText(position+1+"/"+list.size());//sets index of question in text view
                        if(modelMatch()){
                            //if question is already bookmarked then filled bookmark button should be shown
                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                        }else {
                            //if question is not bookmarked then bordered bookmark button should be shown
                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                        }
                    }catch (ClassCastException ex){
                        ((Button)view).setText(data);
                    }
                    view.setTag(data);
                    playAnim(view,1,data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    //Method to check whether selected answer is true or flase
    private void checkAnswer(Button selectedOption){
        enableOption(false);//method call to disable option buttons
        nextBtn.setEnabled(true);//enable next button
        nextBtn.setAlpha(1);//change transparency to 1 to indicate user enbling of next button
        if (selectedOption.getText().toString().equals(list.get(position).getAnswer())){
            //if selected answer is correct
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            score++;
        }else {
            //if selected answer is incorrect
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
            Button correctoption = (Button) optionsContainer.findViewWithTag(list.get(position).getAnswer());
            correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }
    }

    //Method to enable or disable all the options at once
    private void enableOption(boolean enable){
        for (int i=0 ; i < 4 ; i++){
           optionsContainer.getChildAt(i).setEnabled(enable);
           if (enable){
               optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
           }
        }
    }

    //to retrieve bookmarked questions list from shared preference and store it in bookmark list
    private void getBookmarks(){

        String json = preferences.getString(KEY_NAME,"");//retrieve list from json file
        Type type = new TypeToken<List<QuestionModel>>(){}.getType();
        bookmarksList = gson.fromJson(json,type);//convert json to question model type and store it it bookmarksList

        if (bookmarksList == null){
            bookmarksList = new ArrayList<>();
        }
    }

    //to check whether a question is already bookmarked or not
    private boolean modelMatch(){
        boolean matched = false;
        int i=0;
        for (QuestionModel model : bookmarksList){
            if (model.getQuestion().equals(list.get(position).getQuestion())
            && model.getAnswer().equals(list.get(position).getAnswer())
            && model.getSet().equals(list.get(position).getSet())){
                matched = true;
                matchedQuestionPosition = i;
            }
            i++;
        }
        return matched;
    }

    //to store bookmarked questions list in shared preference in form of json file
    private void storeBookmarks(){

        String json = gson.toJson(bookmarksList);//convert question model bookmark list to json
        editor.putString(KEY_NAME,json);//store it in shared preference
        editor.commit();
    }
}