package com.example.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.ViewUtils;

import java.util.Iterator;
import java.util.LinkedList;


public class Drawing2D extends View {

    private LinkedList<Path> drawPaths = new LinkedList<>();

    private LinkedList<Paint> drawPaints = new LinkedList<>();

    public Button button;

    //  ======= BottomNavigationView =======
    public BottomNavigationView bottomNavigationView;

    public ImageView imageViewTools;

    public ImageView imageViewPicker;

    public ImageView imageViewFill;

    public ColorStateList colorStateList;

    //  ======= /BottomNavigationView =======

    private int dColor = Color.BLACK;

    public int default_brush_width = getResources().getInteger(R.integer.default_size);

    public Drawing2D(Context context) {
        super(context);
        addNewPath();


        button = new Button(context);
        button.setText(getResources().getString(R.string.button_set_text));
//        button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(view -> {
            if (drawPaths.size() != 1) {
                drawPaths.remove(drawPaths.size() - 1);
                drawPaints.remove(drawPaints.size() - 1);
                postInvalidate(); // уведомляем android об обновлении пользовательского интерфейса
            } else {
                Toast.makeText(context, "There is nothing to remove!", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        bottomNavigation(context);

    }

    private Paint addNewPaint() {
        Paint dPaint = new Paint();
        dPaint.setSubpixelText(true); // сглаживает
        dPaint.setAntiAlias(true); // края
        dPaint.setColor(this.dColor);
        dPaint.setStyle(Paint.Style.STROKE);
        dPaint.setStrokeJoin(Paint.Join.ROUND);
        dPaint.setStrokeCap(Paint.Cap.ROUND);
        dPaint.setStrokeWidth(default_brush_width);
        drawPaints.add(dPaint);
        return dPaint;
    }

    private Path addNewPath() {
        Path dPath = new Path();
        drawPaths.add(dPath);
        addNewPaint();
        return dPath;
    }

    public void setNewColor(int color) {
        dColor = color;
    }

    public void setNewWidth(int width) {
        default_brush_width = width;
    }

    public void bottomNavigation(Context context) {

        imageViewTools = new ImageView(context);
        imageViewPicker = new ImageView(context);
        imageViewFill = new ImageView(context);
        colorStateList = new ColorStateList(new int[][]{
                {android.R.attr.state_checked},
                {android.R.attr.state_enabled}
        },
                new int[] {
                        Color.rgb(255, 255, 255),
                        Color.rgb(120, 120, 120)
                });

        bottomNavigationView = new BottomNavigationView(context);
        bottomNavigationView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        bottomNavigationView.setBackgroundColor(Color.rgb(43, 43, 43));
                                                                                // бэкграунд панели
        bottomNavigationView.setItemIconTintList(colorStateList); // цвет иконок
        bottomNavigationView.setItemTextColor(colorStateList); // цвет текста под иконками
        bottomNavigationView.inflateMenu(R.menu.icons_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ic_tools:
                                imageViewTools.setVisibility(View.VISIBLE);
                                showAlertDialog(context);
                                imageViewPicker.setVisibility(View.GONE);
                                imageViewFill.setVisibility(View.GONE);
                                return true;
                            case R.id.ic_picker:
                                imageViewTools.setVisibility(View.GONE);
                                imageViewPicker.setVisibility(View.VISIBLE);
                                colorPicker(context);
                                imageViewFill.setVisibility(View.GONE);
                                return true;
                            case R.id.ic_casting:
                                imageViewTools.setVisibility(View.GONE);
                                imageViewPicker.setVisibility(View.GONE);
                                imageViewFill.setVisibility(View.VISIBLE);
                                for (int i = 0; i < drawPaths.size(); i++) {
                                    drawPaths.clear();
                                    drawPaints.clear();
                                    addNewPath();
                                }
                                postInvalidate();
                                return true;
                        }
                        return false;
                    }
                });
    }

    public void colorPicker(Context context) {
        ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose color")
                .initialColor(dColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
//                        some code
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor,
                                        Integer[] allColors) {
                        setNewColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        some code
                    }
                })
                .build()
                .show();
    }

    public static void showAlertDialog(Context context)  {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.title_tools));
        builder.setCancelable(true);

        ImageButton imageView1 = new ImageButton(context);
//        imageView1.setBackground(context.getResources().getDrawable(R.drawable.ic_clear));
        imageView1.setBackground(context.getResources().
                getDrawable(R.drawable.background_color_icons_bottom_navigation));
//        imageView1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_clear));
        imageView1.setScaleType(ImageView.ScaleType.FIT_XY);

        RelativeLayout.LayoutParams layoutParamsImage1 = new RelativeLayout.LayoutParams(
                180, 180);
        layoutParamsImage1.setMargins(50, 50, 0, 0);

                                imageView1.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
        RelativeLayout linearLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        linearLayout.setPadding(50, 50, 0, 0);
        linearLayout.addView(imageView1, layoutParamsImage1);
        builder.setView(linearLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(context,"You choose positive button",
                        Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(context,"You choose negative button",
                        Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Path dPath = drawPaths.get(drawPaths.size() - 1);
        float positionX = event.getX();
        float positionY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Path new_path = addNewPath();
                new_path.moveTo(positionX, positionY);
                return true;
            case MotionEvent.ACTION_MOVE:
                dPath = drawPaths.get(drawPaths.size() - 1);
                dPath.lineTo(positionX, positionY);
                break;
            case MotionEvent.ACTION_UP:
                dPath = drawPaths.get(drawPaths.size() - 1);
                return true;
            default:
                return false;
        }
        postInvalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Iterator<Path> paths = drawPaths.iterator();
        Iterator<Paint> paints = drawPaints.iterator();
        while (paths.hasNext() && paints.hasNext()) {
            Path path = paths.next();
            Paint drawPaint = paints.next();

            canvas.drawPath(path, drawPaint);
        }
    }

}
