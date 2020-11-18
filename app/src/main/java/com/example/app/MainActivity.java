package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Drawing2D drawing = new Drawing2D(this);
        setContentView(drawing);
        addContentView(drawing.button, drawing.layoutParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

