package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Model.Alarm;
import com.example.myapplication.R;
import com.example.myapplication.Service.AlarmService;
import com.example.myapplication.Util.AlarmScheduler;

import java.util.Random;

public class AlarmActivity extends AppCompatActivity {
    
    private int mathResult;
    private Alarm alarm;
    private EditText answer;
    private View math;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_alarm);
        
        alarm = (Alarm) getIntent().getSerializableExtra("ALARM_OBJECT");
        
        TextView tvAlarmLabel = findViewById(R.id.tv_alarm_label);
        tvAlarmLabel.setText(alarm == null ? "Alarm" : alarm.getLabel());
        
        math = findViewById(R.id.math_mission_container);
        answer = findViewById(R.id.et_math_answer);
        
        boolean needsMath = alarm != null && alarm.getDismissMode() == 1;
        math.setVisibility(needsMath ? View.VISIBLE : View.GONE);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // A ringing alarm can only be dismissed or snoozed from its controls.
            }
        });
        
        if (needsMath) {
            generateMathProblem();
        }
        
        findViewById(R.id.btn_snooze).setOnClickListener(v -> snooze());
        
        findViewById(R.id.btn_dismiss).setOnClickListener(v -> {
            if (!needsMath) {
                dismiss();
                return;
            }
            
            try {
                if (Integer.parseInt(answer.getText().toString()) == mathResult) {
                    dismiss();
                } else {
                    Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
                    generateMathProblem();
                    answer.setText("");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Enter the answer", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateMathProblem() {
        Random r = new Random();
        int a = r.nextInt(40) + 10;
        int b = r.nextInt(40) + 10;
        mathResult = a + b;
        
        TextView tvMathQuestion = findViewById(R.id.tv_math_question);
        tvMathQuestion.setText(a + " + " + b + " = ?");
    }

    private void snooze() {
        if (alarm != null) {
            AlarmScheduler.snooze(this, alarm);
        }
        dismiss();
    }

    private void dismiss() {
        stopService(new Intent(this, AlarmService.class));
        finish();
    }

    @SuppressLint({"MissingSuperCall", "GestureBackNavigation"})
    @Override
    public void onBackPressed() {
        // Trống để vô hiệu hóa nút back cứng khi báo thức đang kêu
    }
}
