package com.example.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
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
import java.util.Iterator;
import java.util.LinkedList;

public class Drawing2D extends View {

    private LinkedList<Path> drawPaths = new LinkedList<>();

    private LinkedList<Paint> drawPaints = new LinkedList<>();

    public Toolbar toolbarView;

    //  ======= BottomNavigationView =======
    public BottomNavigationView bottomNavigationView;

    private ColorStateList colorStateList;
    //  ======= /BottomNavigationView =======

    private Canvas newCanvas;

    private String someText = "";

    private Paint.Style style = Paint.Style.STROKE;

    private int dColor = Color.BLACK;

    private int brush_width = getResources().getInteger(R.integer.default_size);

    private Bitmap mBitmap;

    public Drawing2D(Context context) {
        super(context);
        setDrawingCacheEnabled(true);
        addNewPath(style);
        toolbarView(context);
        bottomNavigation(context);
    }

    protected void createBitmap() {
        mBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        setDrawingCacheEnabled(true);
    }

    private Paint addNewPaint(Paint.Style style) {
        Paint dPaint = new Paint();
        dPaint.setAntiAlias(true); //сглаживает края
        dPaint.setColor(dColor);
        dPaint.setStyle(style);
        dPaint.setStrokeJoin(Paint.Join.ROUND);
        dPaint.setStrokeCap(Paint.Cap.ROUND);
        dPaint.setStrokeWidth(brush_width);
        drawPaints.add(dPaint);
        return dPaint;
    }

    private Path addNewPath(Paint.Style style) {
        Path dPath = new Path();
        drawPaths.add(dPath);
        addNewPaint(style);
        return dPath;
    }

    private void setNewColor(int color) {
        dColor = color;
    }

    private void clearCanvas() {
        toolbarView.setTitle(R.string.toolbar_title);
        drawPaths.clear();
        drawPaints.clear();
        addNewPath(style);
        setDrawingCacheEnabled(true);
        postInvalidate();
    }

    private void toolbarView(Context context) {
        toolbarView = new Toolbar(context);
        toolbarView.setBackgroundColor(getResources().getColor(R.color.bar_color));
        toolbarView.setTitle(R.string.toolbar_title);
        toolbarView.setTitleTextColor(getResources().getColor(R.color.white));
        toolbarView.setVisibility(View.VISIBLE);
        toolbarView.inflateMenu(R.menu.icons_toolbar);
    }

    protected void cancelAction() {
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

    protected void uploadImage(Context context) {

    }

    protected void saveImage(Context context) {
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
                    Toast.makeText(getContext(), "Wrong name for the picture!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    toolbarView.setTitle(someText);

                    File sdDirectory = context.getExternalFilesDir("/");

                    if (sdDirectory.exists()) {
                        File image = new File(sdDirectory, "/" + someText + ".png");
                        FileOutputStream fileOutputStream;

                        try {
                            fileOutputStream = new FileOutputStream(image);
                            mBitmap = Bitmap.createBitmap(getDrawingCache());
                            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            setDrawingCacheEnabled(false);

                            fileOutputStream.flush(); //очищает любые выходные буферы,
                                                        // завершая операцию вывода
                            fileOutputStream.close(); //закрывает выходной поток.
                            // Последующие попытки записи в этот поток будут возбуждать IOException

                            createBitmap();
                            Toast.makeText(getContext(),"Saved successfully",
                                    Toast.LENGTH_SHORT).show();
                        } catch (FileNotFoundException e) {
//                            some code
                        } catch (IOException e) {
//                            some code
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


    private void bottomNavigation(Context context) {

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
                            case R.id.ic_pencil:
                                style = Paint.Style.STROKE;
                                brush_width = getResources().getInteger(R.integer.default_size);
                                dColor = Color.BLACK;
                                return true;
                            case R.id.ic_paintbrush:
                                style = Paint.Style.FILL;
                                brush_width = getResources().getInteger(R.integer.default_size);
                                dColor = Color.BLACK;
                                return true;
                            case R.id.ic_eraser:
                                style = Paint.Style.STROKE;
                                brush_width = getResources().getInteger(R.integer.eraser_size);
                                dColor = Color.WHITE;
                                return true;
                            case R.id.ic_picker:
                                colorPicker(context);
                                break;
                            case R.id.ic_casting:
                                clearCanvas();
                                break;
                        }
                        return false;
                    }
                });
    }

    private void colorPicker(Context context) {
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Path dPath;
        float positionX = event.getX();
        float positionY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Path new_path = addNewPath(style);
                new_path.moveTo(positionX, positionY);
                return true;
            case MotionEvent.ACTION_MOVE:
                dPath = drawPaths.get(drawPaths.size() - 1);
                dPath.lineTo(positionX, positionY);
                break;
            case MotionEvent.ACTION_UP:
//                some code
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
    }

}
