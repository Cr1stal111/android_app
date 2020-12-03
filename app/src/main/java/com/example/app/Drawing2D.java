package com.example.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class Drawing2D extends View {

    private LinkedList<Path> drawPaths = new LinkedList<>();

    private LinkedList<Paint> drawPaints = new LinkedList<>();

    public Toolbar toolbarView;

    //  ======= BottomNavigationView =======
    public BottomNavigationView bottomNavigationView;

    public ImageView imageViewTools;

    public ImageView imageViewPicker;

    public ImageView imageViewFill;

    public ColorStateList colorStateList;
    //  ======= /BottomNavigationView =======

    private Canvas newCanvas;

    private String someText = "";

    private int dColor = Color.BLACK;

    public int default_brush_width = getResources().getInteger(R.integer.default_size);

    private Bitmap mBitmap;

    public Drawing2D(Context context) {
        super(context);
        setFocusableInTouchMode(true);
        setDrawingCacheEnabled(true);
        addNewPath();
        toolbarView(context);
        bottomNavigation(context);
    }

    public void createBitmap() {
        mBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
    }

    private Paint addNewPaint() {
        Paint dPaint = new Paint();
                                        // сглаживает
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

    public void toolbarView(Context context) {
        toolbarView = new Toolbar(context);
        toolbarView.setBackgroundColor(getResources().getColor(R.color.bar_color));
//        toolbarView.setMinimumHeight(R.attr.actionBarSize);
//        toolbarView.setPopupTheme(R.style.AppTheme);
        toolbarView.setTitle(R.string.toolbar_title);
        toolbarView.setTitleTextColor(getResources().getColor(R.color.white));
        toolbarView.setVisibility(View.VISIBLE);
        toolbarView.inflateMenu(R.menu.icons_toolbar);

    }

    public void cancelAction() {
        setDrawingCacheEnabled(false);
        if (drawPaths.size() != 1) {
            drawPaths.remove(drawPaths.size() - 1);
            drawPaints.remove(drawPaints.size() - 1);
            postInvalidate();
        } else {
            Toast.makeText(getContext(), "There is nothing to remove!",
                    Toast.LENGTH_SHORT).show();
        }
        setDrawingCacheEnabled(true);
    }

    public void saveImage(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getResources().getString(R.string.save_title));
        builder.setCancelable(true);

        EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                someText = input.getText().toString();
                if (someText.equals("") || someText.contains(" ")) {
                    Toast.makeText(getContext(), "Empty name picture!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    toolbarView.setTitle(someText);
                    Toast.makeText(getContext(),"Saved successfully",
                            Toast.LENGTH_SHORT).show();

                    File sdDirectory = context.getExternalFilesDir("/");

                    if (sdDirectory.exists()) {
                        File image = new File(sdDirectory, "/" + someText + ".png");
                        FileOutputStream fileOutputStream;

                        //Попытка сохранения
                        try {

                            fileOutputStream = new FileOutputStream(image);
                            mBitmap = Bitmap.createBitmap(getDrawingCache());
                            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            setDrawingCacheEnabled(false);

                            fileOutputStream.flush();
                            fileOutputStream.close();

                        } catch (FileNotFoundException e) {

                        } catch (IOException e) {

                        }
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
                                toolbarView.setTitle(R.string.toolbar_title);
                                drawPaths.clear();
                                drawPaints.clear();
                                addNewPath();
                                setDrawingCacheEnabled(true);
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
        newCanvas = canvas;
        newCanvas.drawColor(getResources().getColor(R.color.white));
        Iterator<Path> paths = drawPaths.iterator();
        Iterator<Paint> paints = drawPaints.iterator();
        while (paths.hasNext() && paints.hasNext()) {
            Path path = paths.next();
            Paint drawPaint = paints.next();

            newCanvas.drawPath(path, drawPaint);
        }
//        newCanvas = new Canvas(mBitmap);
    }

}
