package com.example.orbitsimulator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvGValue;
    private float gValue = 0.8f;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("SimulatorPrefs", MODE_PRIVATE);
        gValue = prefs.getFloat("G_VALUE", 0.8f);

        tvGValue = findViewById(R.id.tvGValue);
        Button btnGMinus = findViewById(R.id.btnGMinus);
        Button btnGPlus = findViewById(R.id.btnGPlus);
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);

        tvGValue.setText(String.format("%.1f", gValue));

        btnGMinus.setOnClickListener(v -> {
            gValue = Math.max(0.1f, gValue - 0.1f);
            tvGValue.setText(String.format("%.1f", gValue));
            prefs.edit().putFloat("G_VALUE", gValue).apply();
        });

        btnGPlus.setOnClickListener(v -> {
            gValue = Math.min(2.0f, gValue + 0.1f);
            tvGValue.setText(String.format("%.1f", gValue));
            prefs.edit().putFloat("G_VALUE", gValue).apply();
        });

        btnBackToMenu.setOnClickListener(v -> finish());
    }
}