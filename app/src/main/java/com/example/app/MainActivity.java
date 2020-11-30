package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private long backPressedseconds;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Drawing2D drawing = new Drawing2D(this);
        RelativeLayout.LayoutParams layoutParamsDrawing = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParamsDrawing.setMargins(0, 178, 0, 0);
        Toolbar toolbarView = new Toolbar(this);
        RelativeLayout.LayoutParams layoutParamsToolbar = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 178);
        layoutParamsToolbar.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        toolbarView.setLayoutParams(layoutParamsToolbar);
        toolbarView.setBackgroundColor(getResources().getColor(R.color.bar_color));
//        toolbarView.setMinimumHeight(R.attr.actionBarSize);
//        toolbarView.setPopupTheme(R.style.AppTheme);
//        toolbarView.inflateMenu(R.menu.icons_bottom_navigation);
        toolbarView.setTitle(R.string.toolbar_title);
        toolbarView.setTitleTextColor(getResources().getColor(R.color.white));
        toolbarView.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbarView);

        Button button = drawing.button;
        RelativeLayout.LayoutParams layoutParamsButton = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParamsButton.setMargins(0, 188, 0, 0);

        BottomNavigationView bottomNavigationView = drawing.bottomNavigationView;
        
        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.addView(drawing, layoutParamsDrawing);
        relativeLayout.addView(toolbarView, 0);
        relativeLayout.addView(button, layoutParamsButton);
        relativeLayout.addView(bottomNavigationView, layoutParams);

        setContentView(relativeLayout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (backPressedseconds + 1500 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getApplicationContext(), R.string.Exit_toast, Toast.LENGTH_SHORT).show();
        }

        backPressedseconds = System.currentTimeMillis();
    }

}
