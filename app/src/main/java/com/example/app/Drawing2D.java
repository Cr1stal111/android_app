package com.example.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
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

import org.apache.http.params.HttpParams;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    protected void createUploadImage(Context context) {
        String uploadFilePath = Environment.getExternalStorageDirectory() + "/Pictures/";
        String uploadFileName = toolbarView.getTitle().toString() + ".png";
        String sourceFileUri = uploadFilePath + uploadFileName;
        String upLoadServerUri = LoginActivity.HOST + "/api/upload";

        new Thread(new Runnable() {
            public void run() {
                uploadImage(uploadFilePath, uploadFileName, sourceFileUri,
                        upLoadServerUri, uploadFileName, "Image from android");
            }
        }).start();
    }

    protected int uploadImage(String uploadFilePath, String uploadFileName,
                          String sourceFileUri, String upLoadServerUri, String title, String body) {
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "qwerty";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            Log.e("uploadFile", "Source File not exist :"
                    + uploadFilePath + "" + uploadFileName);

            return 0;

        } else {
            int serverResponseCode = 0;
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "*/*");
                conn.setRequestProperty("token", LoginActivity.mToken);
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + uploadFileName + "\"");
                dos.writeBytes(lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"title\"");
                dos.writeBytes(lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(title);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"body\"");
                dos.writeBytes(lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(body);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens);
                dos.writeBytes(lineEnd);


                // Responses fro m the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i(MainActivity.class.getSimpleName(), "OUTPUT HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 201) {
                    Log.i(MainActivity.class.getSimpleName(), "OUTPUT File Upload Completed: " + uploadFileName);
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();

                Log.i(MainActivity.class.getSimpleName(), "OUTPUT File Upload error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();
                Log.i(MainActivity.class.getSimpleName(), "OUTPUT File Upload Exception : "
                        + e.getMessage(), e);
            }
            return serverResponseCode;

        }
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

//                    File sdDirectory = context.getExternalFilesDir("/");
                    File sdDirectory = Environment.getExternalStorageDirectory();

                    if (sdDirectory.exists()) {
                        File image = new File(sdDirectory, "/Pictures/" + someText + ".png");
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
