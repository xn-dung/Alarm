package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.Model.Alarm;
import com.example.myapplication.R;
import com.example.myapplication.Service.AlarmService;
import java.util.Random;

public class AlarmActivity extends AppCompatActivity {
    private int mathResult;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        alarm = (Alarm) getIntent().getSerializableExtra("ALARM_OBJECT");
        
        TextView tvLabel = findViewById(R.id.tv_alarm_label);
        if (alarm != null) {
            tvLabel.setText(alarm.getLabel());
        }

        generateMathProblem();

        Button btnDismiss = findViewById(R.id.btn_dismiss);
        EditText etAnswer = findViewById(R.id.et_math_answer);

        btnDismiss.setOnClickListener(v -> {
            String answerStr = etAnswer.getText().toString();
            if (!answerStr.isEmpty()) {
                int userAnswer = Integer.parseInt(answerStr);
                if (userAnswer == mathResult) {
                    stopAlarm();
                } else {
                    Toast.makeText(this, "Wrong answer! Try again.", Toast.LENGTH_SHORT).show();
                    generateMathProblem();
                    etAnswer.setText("");
                }
            } else {
                Toast.makeText(this, "Please solve the math problem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateMathProblem() {
        Random random = new Random();
        int a = random.nextInt(50) + 1;
        int b = random.nextInt(50) + 1;
        mathResult = a + b;
        TextView tvQuestion = findViewById(R.id.tv_math_question);
        tvQuestion.setText(a + " + " + b + " = ?");
    }

    private void stopAlarm() {
        Intent serviceIntent = new Intent(this, AlarmService.class);
        stopService(serviceIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button during alarm ringing
    }
}