package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private long backPressedseconds;

    public Drawing2D drawing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        drawing = new Drawing2D(this);

        RelativeLayout.LayoutParams layoutParamsDrawing = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParamsDrawing.setMargins(0, 178, 0, 168);
        Toolbar toolbarView = drawing.toolbarView;
        RelativeLayout.LayoutParams layoutParamsToolbar = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 178);
        toolbarView.setLayoutParams(layoutParamsToolbar);
        toolbarView.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_undo:
                        drawing.cancelAction();
                        break;
                    case R.id.ic_save:
                        requestPermission();
                        break;
                }
                return true;
            }
        });

        BottomNavigationView bottomNavigationView = drawing.bottomNavigationView;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.addView(drawing, layoutParamsDrawing);
        relativeLayout.addView(toolbarView);
        relativeLayout.addView(bottomNavigationView, layoutParams);
        setContentView(relativeLayout);
        drawing.createBitmap();
    }

    public void requestPermission() {
        int permissionStatus = ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            drawing.saveImage(this);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (backPressedseconds + 1500 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), R.string.Exit_toast, Toast.LENGTH_SHORT).show();
        }

        backPressedseconds = System.currentTimeMillis();
    }

}
