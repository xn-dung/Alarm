package com.example.myapplication.Util;

import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps every NumberPicker visually consistent before, during and after a scroll.
 */
public final class NumberPickerStyler {
    private static final float TEXT_SIZE_SP = 27f;

    private NumberPickerStyler() {
    }

    public static void apply(NumberPicker picker) {
        picker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        Runnable refresh = () -> stylePicker(picker);
        List<EditText> inputs = new ArrayList<>();
        collectInputs(picker, inputs);

        for (EditText input : inputs) {
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence text, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable text) {
                    scheduleRefresh(picker, refresh);
                }
            });
        }

        picker.setOnValueChangedListener(
                (view, oldValue, newValue) -> scheduleRefresh(view, refresh)
        );
        picker.setOnScrollListener((view, scrollState) -> {
            if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                scheduleRefresh(view, refresh);
            }
        });

        scheduleRefresh(picker, refresh);
    }

    private static void scheduleRefresh(NumberPicker picker, Runnable refresh) {
        picker.removeCallbacks(refresh);
        picker.post(refresh);
        picker.postDelayed(refresh, 48L);
        picker.postDelayed(refresh, 120L);
    }

    private static void stylePicker(NumberPicker picker) {
        int textColor = ContextCompat.getColor(picker.getContext(), R.color.text_primary);
        float textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                TEXT_SIZE_SP,
                picker.getResources().getDisplayMetrics()
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            picker.setTextColor(textColor);
            picker.setTextSize(textSizePx);
        }

        List<EditText> inputs = new ArrayList<>();
        collectInputs(picker, inputs);
        for (EditText input : inputs) {
            input.setTextColor(textColor);
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP);
            input.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            input.setGravity(Gravity.CENTER);
            input.setIncludeFontPadding(false);
            input.setAlpha(1f);
        }
        picker.invalidate();
    }

    private static void collectInputs(View view, List<EditText> result) {
        if (view instanceof EditText) {
            result.add((EditText) view);
            return;
        }
        if (!(view instanceof ViewGroup)) {
            return;
        }
        ViewGroup group = (ViewGroup) view;
        for (int i = 0; i < group.getChildCount(); i++) {
            collectInputs(group.getChildAt(i), result);
        }
    }
}