package com.example.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import androidx.annotation.Nullable;

public class Drawing2D extends View {

    private Path dPath = new Path(); // позволяет рисовать и запоминать весь путь фигуры

    private Paint dPaint = new Paint(); // как рисуем

    public Button button;

    public LayoutParams layoutParams;

    private int dColor;

    private final int default_brush_width;

    public Drawing2D(Context context) {
        super(context);

//        dColor = 0x1400f000;
        dColor = getResources().getColor(R.color.blue_500);
        default_brush_width = getResources().getInteger(R.integer.default_size);
        dPaint.setAntiAlias(true); // сглаживает края
        dPaint.setColor(this.dColor);
        dPaint.setStyle(Paint.Style.STROKE);
        dPaint.setStrokeJoin(Paint.Join.ROUND);
        dPaint.setStrokeCap(Paint.Cap.ROUND);
        dPaint.setStrokeWidth(default_brush_width);
        button = new Button(context);
        button.setText(getResources().getString(R.string.button_set_text));
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(layoutParams);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dPath.reset(); // очищает объект dPath
                postInvalidate(); // уведомляем android об обновлении пользовательского
                                  // представления

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