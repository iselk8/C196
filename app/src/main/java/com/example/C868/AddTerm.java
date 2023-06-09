package com.example.c196;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.c196.Entities.TermModel;
import com.example.c196.Repositories.TermRepository;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;

public class AddTerm extends AppCompatActivity {
    String username;
    ImageView backBtn;
    TextInputLayout addTermTitle;
    TextView dateError;
    DatePicker addTermStartDate, addTermEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        //Hooks
        addTermTitle = (TextInputLayout) findViewById(R.id.add_term_title);
        addTermStartDate = (DatePicker) findViewById(R.id.add_term_start_date);
        addTermEndDate = (DatePicker) findViewById(R.id.add_term_end_date);
        dateError = (TextView) findViewById(R.id.add_term_date_error);
        dateError.setAlpha(0.0f);
        dateError.setTextSize(TypedValue.COMPLEX_UNIT_SP, 1);
        backBtn = findViewById(R.id.add_term_back_button);
        backBtn.setClickable(true);

        //Reception of the values sent by the previous intent
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            username = extras.getString("username");
    }

    public void backBtnPressed(View v){ back(); }

    @Override
    public void onBackPressed(){
        back();
    }

    private void back() {
        Intent intent = new Intent(this, DisplayData.class);
        intent.putExtra("username", username);
        intent.putExtra("data type", "term");
        startActivity(intent);
        super.finish();
    }

    public void addTerm(View v) {
        //Verifying that android version is equals or higher than Android 8.0 before using the LocalDate class
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            //Text input and date validation through the Utility class
            // If one of the input field  is invalid, the operation will fail and display the appropriate error message
            if (!Utility.isValidTitle(addTermTitle) | !Utility.isValidDateCombination(addTermStartDate, addTermEndDate, dateError)) {
                Toast.makeText(this, "Operation failed", Toast.LENGTH_LONG).show();
                return;
            } else {

                //Reading values from the TextInputLayout and the DatePickers and storing them into variables
                String termTitle = addTermTitle.getEditText().getText().toString().toLowerCase();
                int startYear = addTermStartDate.getYear();
                int startMonth = addTermStartDate.getMonth()+1;
                int startDayOfMonth = addTermStartDate.getDayOfMonth();
                int endYear = addTermEndDate.getYear();
                int endMonth = addTermEndDate.getMonth()+1;
                int endDayOfMonth = addTermEndDate.getDayOfMonth();

                //Creating LocalDate objects with the input from the date picker inputs
                LocalDate startDate = LocalDate.of(startYear, startMonth, startDayOfMonth);
                LocalDate endDate = LocalDate.of(endYear, endMonth, endDayOfMonth);

                //Creating a term repository that will be used to insert a new term into the database
                TermRepository termRepo = new TermRepository(getApplication());

                //Creating a term model used to insert the data into a database row.
                //NOTE: We are using the constructor without the termId because it will be auto generated by the database as a primary key
                TermModel term = new TermModel(termTitle, startDate.toString(), endDate.toString(), username);

                //Verifies if the term title already exists for that username before inserting the term
                if(!termRepo.isTaken(username, termTitle)) {
                    termRepo.insertTerm(term);
                    Toast.makeText(this, "Term added", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, DisplayData.class);
                    intent.putExtra("username", username);
                    intent.putExtra("data type", "term");
                    startActivity(intent);
                    super.finish();
                }else{
                    //If term title is taken for this username
                    Utility.setTitleTakenTextInputLayout(addTermTitle, "Term");
                }
            }
        }

    }
}