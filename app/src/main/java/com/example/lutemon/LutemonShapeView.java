package com.example.lutemon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom view that displays a Lutemon's shape
 */
public class LutemonShapeView extends View {
    private Paint paint;
    private Path path;
    private Lutemon lutemon;
    private float size;
    private float centerX;
    private float centerY;

    public LutemonShapeView(Context context) {
        super(context);
        init();
    }

    public LutemonShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        size = Math.min(w, h) * 0.8f; // Use 80% of the smallest dimension
        centerX = w / 2f;
        centerY = h / 2f;
        updatePath();
    }

    /**
     * Sets the Lutemon whose shape to display
     */
    public void setLutemon(Lutemon lutemon) {
        this.lutemon = lutemon;
        if (lutemon != null) {
            paint.setColor(getLutemonColor(lutemon.getColor()));
            updatePath();
            invalidate();
        }
    }

    private void updatePath() {
        if (lutemon == null || size == 0) return;

        path.reset();
        float radius = size / 2;
        int points = lutemon.getShapePointCount();

        // First point
        float angle = 0;
        float pointRadius = radius * lutemon.getShapeRadius(0);
        float startX = centerX + pointRadius * (float) Math.cos(angle);
        float startY = centerY + pointRadius * (float) Math.sin(angle);
        path.moveTo(startX, startY);

        // Remaining points
        for (int i = 1; i < points; i++) {
            angle = (float) (2 * Math.PI * i / points);
            pointRadius = radius * lutemon.getShapeRadius(i);
            float x = centerX + pointRadius * (float) Math.cos(angle);
            float y = centerY + pointRadius * (float) Math.sin(angle);
            path.lineTo(x, y);
        }

        path.close();
    }

    private int getLutemonColor(String color) {
        switch (color.toLowerCase()) {
            case "white": return getContext().getColor(R.color.lutemon_white);
            case "green": return getContext().getColor(R.color.lutemon_green);
            case "pink": return getContext().getColor(R.color.lutemon_pink);
            case "orange": return getContext().getColor(R.color.lutemon_orange);
            case "black": return getContext().getColor(R.color.lutemon_black);
            default: return getContext().getColor(R.color.lutemon_white);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lutemon != null) {
            canvas.drawPath(path, paint);
        }
    }
}