package com.example.app;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.logging.Level;

public class Drawing2D extends View {

    public Path dPath = new Path(); // позволяет рисовать и запоминать весь путь фигуры

    public Paint dPaint = new Paint(); // как рисуем

    public Button button;
//  ======= BottomNavigationView =======
    public BottomNavigationView bottomNavigationView;

    public ImageView imageViewBrush;

    public ImageView imageViewScissors;

    public ImageView imageViewTools;

    public ColorStateList colorStateList;
    //  ======= /BottomNavigationView =======
    private int dColor;

    private final int default_brush_width;

    public Drawing2D(Context context) {
        super(context);

//        dColor = 0x1400f000;
        dColor = getResources().getColor(R.color.purple_500);
        default_brush_width = getResources().getInteger(R.integer.default_size);
        dPaint.setAntiAlias(true); // сглаживает края
        dPaint.setColor(this.dColor);
        dPaint.setStyle(Paint.Style.STROKE);
        dPaint.setStrokeJoin(Paint.Join.ROUND);
        dPaint.setStrokeCap(Paint.Cap.ROUND);
        dPaint.setStrokeWidth(default_brush_width);


        button = new Button(context);
        button.setText(getResources().getString(R.string.button_set_text));
        button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(view -> {
            dPath.reset(); // очищает объект dPath
            postInvalidate(); // уведомляем android об обновлении пользовательского
                              // представления
        });

        bottomNavigation(context);
    }

    public void bottomNavigation(Context context) {
        imageViewBrush = new ImageView(context);
        imageViewScissors = new ImageView(context);
        imageViewTools = new ImageView(context);

        colorStateList = new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_enabled}
        },
                new int[] {
                        Color.GREEN,
                        Color.BLACK
                });

        bottomNavigationView = new BottomNavigationView(context);
        bottomNavigationView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.cool_blue));
        // бэкграунд панели
        bottomNavigationView.setItemIconTintList(colorStateList);
        // цвет иконок
        bottomNavigationView.setItemTextColor(colorStateList);
        // цвет текста под иконками
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.icons_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_brush:
                        imageViewBrush.setVisibility(View.VISIBLE);
//                            imageViewBrush.setEnabled(true);
                        imageViewScissors.setVisibility(View.GONE);
//                            imageViewScissors.setEnabled(false);
                        imageViewTools.setVisibility(View.GONE);
//                            imageViewTools.setEnabled(false);
                        return true;
                    case R.id.ic_scissors:
                        imageViewBrush.setVisibility(View.GONE);
//                            imageViewBrush.setEnabled(false);
                        imageViewScissors.setVisibility(View.VISIBLE);
//                            imageViewScissors.setEnabled(true);
                        imageViewTools.setVisibility(View.GONE);
//                            imageViewTools.setEnabled(false);
                        return true;
                    case R.id.ic_tools:
                        imageViewBrush.setVisibility(View.GONE);
//                            imageViewBrush.setEnabled(false);
                        imageViewScissors.setVisibility(View.GONE);
//                            imageViewScissors.setEnabled(false);
                        imageViewTools.setVisibility(View.VISIBLE);
//                            imageViewTools.setEnabled(true);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float positionX = event.getX();
        float positionY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dPath.moveTo(positionX, positionY);
                return true;
            case MotionEvent.ACTION_MOVE:
                dPath.lineTo(positionX, positionY);
                break;
            case MotionEvent.ACTION_UP:
                return true;
            default:
                return false;
        }
        postInvalidate();
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(dPath, dPaint);
    }

}