package com.example.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long backPressedseconds;

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

    @Override
    public void onBackPressed() {
        if (backPressedseconds + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getApplicationContext(), R.string.Exit_toast, Toast.LENGTH_SHORT).show();
        }

        backPressedseconds = System.currentTimeMillis();
    }
}
