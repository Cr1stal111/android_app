package com.example.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

//import java.nio.file.Path;

public class Drawing2D extends View {

    private Path dPath; // позволяет рисовать и запоминать путь рисунка

    private Paint cPaint; // что рисуем

    private Paint dPaint; // как рисуем

    private int pColor = 0x0008FA; // первоначальный цвет

    private Canvas dCanvas; // сам холст

    private Bitmap cBitmap; // растровое изображение холста

    private float current_brush_size;  // текущий размер кисточки

    private float last_brush_size; // последний использованный размер кисти

    public Drawing2D(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setValues();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(cBitmap, 0, 0, cPaint);
        canvas.drawPath(dPath, dPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        dCanvas = new Canvas(cBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float positionX = event.getX();
        float positionY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dPath.moveTo(positionX, positionY);
                break;
            case MotionEvent.ACTION_MOVE:
                dPath.lineTo(positionX, positionY);
                break;
            case MotionEvent.ACTION_UP:
                dPath.lineTo(positionX, positionY);
                dCanvas.drawPath(dPath, dPaint);
                dPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    private void setValues() {
        current_brush_size = getResources().getInteger(R.integer.medium_size);

        last_brush_size = current_brush_size;

        dPath = new Path();

        dPaint = new Paint();
        dPaint.setStyle(Paint.Style.STROKE); // стиль для объекта Paint
        dPaint.setColor(pColor); // задаём цвет
        dPaint.setAntiAlias(true); // сглаживаем линии, рисуемые на холсте
        dPaint.setStrokeWidth(current_brush_size); // задаём ширину кисточки
        dPaint.setStrokeJoin(Paint.Join.ROUND); // округляет линии
        dPaint.setStrokeCap(Paint.Cap.ROUND);

        cPaint = new Paint(Paint.DITHER_FLAG);
    }
}
