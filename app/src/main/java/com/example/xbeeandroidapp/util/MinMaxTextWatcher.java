package com.example.xbeeandroidapp.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.xbeeandroidapp.R;

public class MinMaxTextWatcher implements TextWatcher {

    private final Context context;
    private Button button;
    private final TextView rangeHint;
    private final int min;
    private final int max;

    public MinMaxTextWatcher(Context context, Button button, TextView rangeHint, int min, int max) {
        this.context = context;
        this.button = button;
        this.rangeHint = rangeHint;
        this.min = min;
        this.max = max;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        int n = 0;
        try {
            n = Integer.parseInt(str);
            if(n < min) {
                rangeHint.setText(context.getResources().getString(R.string.min_timeout) + " " + min);
                rangeHint.setVisibility(View.VISIBLE);
                button.setEnabled(false);
                button.setTextColor(context.getResources().getColor(R.color.grey));
            }
            else if(n > max) {
                rangeHint.setText(context.getResources().getString(R.string.max_timeout) + " " + max);
                rangeHint.setVisibility(View.VISIBLE);
                button.setEnabled(false);
                button.setTextColor(context.getResources().getColor(R.color.grey));
            }
            else {
                rangeHint.setVisibility(View.INVISIBLE);
                button.setEnabled(true);
                button.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        }
        catch(NumberFormatException nfe) {
            rangeHint.setText(context.getResources().getString(R.string.min_timeout) + " " + min);
            button.setEnabled(false);
            button.setTextColor(context.getResources().getColor(R.color.grey));
        }
    }
}
