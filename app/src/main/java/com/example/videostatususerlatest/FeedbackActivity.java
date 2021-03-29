package com.example.videostatususerlatest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    EditText name, feedback;
    Button submitBtn;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        name = (EditText) findViewById(R.id.Name);
        feedback = (EditText) findViewById(R.id.feedback);
        submitBtn = (Button) findViewById((R.id.submitBtn));

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = name.getText().toString();
                String feedBackTxt = feedback.getText().toString();
                //Get reference of the Real time database.
                reference = FirebaseDatabase.getInstance().getReference().child("Feedback:" + userName);
                //Push the values to the database.
                reference.push().setValue(feedBackTxt);

                name.setText("");
                feedback.setText("");

                Toast.makeText(FeedbackActivity.this, "Thank you for providing the feedback.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
