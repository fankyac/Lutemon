package com.example.lutemon;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Custom view that displays the battle arena with fighters
 */
public class BattleArenaView extends View {
    private Paint fighter1Paint;
    private Paint fighter2Paint;
    private Paint healthPaint;
    private Paint healthBgPaint;
    private Paint winnerPaint;
    private Paint namePaint;
    private Path fighter1Path;
    private Path fighter2Path;
    private float fighter1X, fighter1Y;
    private float fighter2X, fighter2Y;
    private int fighter1Health = 100;
    private int fighter2Health = 100;
    private float shakeIntensity = 0f;
    private String winnerMessage = null;
    private float winnerAlpha = 0f;
    private String fighter1Name = "";
    private String fighter2Name = "";
    private Lutemon fighter1;
    private Lutemon fighter2;

    // Size constants
    private static final float FIGHTER_SIZE = 150f;
    private static final float HEALTH_BAR_WIDTH = 120f;
    private static final float HEALTH_BAR_HEIGHT = 12f;
    private static final float MARGIN = 20f;
    private static final float NAME_OFFSET = 30f;

    public BattleArenaView(Context context) {
        super(context);
        init();
    }

    public BattleArenaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialize paints
        fighter1Paint = new Paint();
        fighter1Paint.setStyle(Paint.Style.FILL);
        fighter1Paint.setAntiAlias(true);

        fighter2Paint = new Paint();
        fighter2Paint.setStyle(Paint.Style.FILL);
        fighter2Paint.setAntiAlias(true);

        healthPaint = new Paint();
        healthPaint.setColor(0xFF4CAF50);
        healthPaint.setStyle(Paint.Style.FILL);

        healthBgPaint = new Paint();
        healthBgPaint.setColor(0xFFE0E0E0);
        healthBgPaint.setStyle(Paint.Style.FILL);

        winnerPaint = new Paint();
        winnerPaint.setColor(0xFFFFD700); // Gold color
        winnerPaint.setTextSize(60);
        winnerPaint.setTextAlign(Paint.Align.CENTER);
        winnerPaint.setAntiAlias(true);

        namePaint = new Paint();
        namePaint.setColor(0xFF000000);
        namePaint.setTextSize(36);
        namePaint.setTextAlign(Paint.Align.CENTER);
        namePaint.setAntiAlias(true);

        // Initialize paths
        fighter1Path = new Path();
        fighter2Path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Position fighters
        fighter1X = w / 3f;
        fighter1Y = 2 * h / 3f;
        fighter2X = 2 * w / 3f;
        fighter2Y = h / 3f;

        updateFighterPaths();
    }

    private void updateFighterPaths() {
        if (fighter1 == null || fighter2 == null) return;

        updatePath(fighter1Path, fighter1, true);
        updatePath(fighter2Path, fighter2, false);
    }

    private void updatePath(Path path, Lutemon lutemon, boolean isFirst) {
        path.reset();
        float centerX = isFirst ? fighter1X : fighter2X;
        float centerY = isFirst ? fighter1Y : fighter2Y;
        float radius = FIGHTER_SIZE / 2;
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

    public void setFighters(Lutemon fighter1, Lutemon fighter2) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        fighter1Paint.setColor(getLutemonColor(fighter1.getColor()));
        fighter2Paint.setColor(getLutemonColor(fighter2.getColor()));
        fighter1Name = fighter1.getName();
        fighter2Name = fighter2.getName();
        updateFighterPaths();
        invalidate();
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

    public void updateHealth(int health1Percent, int health2Percent) {
        fighter1Health = health1Percent;
        fighter2Health = health2Percent;
        invalidate();
    }

    public void showDamageEffect(boolean isFighter1Attacked) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            // Shake effect peaks at middle of animation
            shakeIntensity = (float) Math.sin(progress * Math.PI) * 20;
            if (!isFighter1Attacked) {
                shakeIntensity *= -1; // Reverse shake for fighter 2
            }
            invalidate();
        });
        animator.start();
    }

    public void showWinner(String name) {
        winnerMessage = "WINNER: " + name;
        
        // Fade in animation
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            winnerAlpha = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw winner message if present
        if (winnerMessage != null) {
            winnerPaint.setAlpha((int)(winnerAlpha * 255));
            canvas.drawText(winnerMessage, getWidth()/2f, getHeight()/2f, winnerPaint);
        }

        // Draw fighter 1 (bottom left)
        canvas.save();
        canvas.translate(shakeIntensity * (fighter1Health < 50 ? 2 : 1), 0);
        canvas.drawPath(fighter1Path, fighter1Paint);
        drawHealthBar(canvas, fighter1Health, fighter1X - HEALTH_BAR_WIDTH/2, 
            fighter1Y - FIGHTER_SIZE);
        canvas.drawText(fighter1Name, fighter1X, fighter1Y + FIGHTER_SIZE/2 + NAME_OFFSET, 
            namePaint);
        canvas.restore();

        // Draw fighter 2 (top right)
        canvas.save();
        canvas.translate(shakeIntensity * (fighter2Health < 50 ? 2 : 1), 0);
        canvas.drawPath(fighter2Path, fighter2Paint);
        drawHealthBar(canvas, fighter2Health, fighter2X - HEALTH_BAR_WIDTH/2, 
            fighter2Y - FIGHTER_SIZE);
        canvas.drawText(fighter2Name, fighter2X, fighter2Y + FIGHTER_SIZE/2 + NAME_OFFSET, 
            namePaint);
        canvas.restore();
    }

    private void drawHealthBar(Canvas canvas, int healthPercent, float x, float y) {
        // Background
        canvas.drawRect(x, y, x + HEALTH_BAR_WIDTH, y + HEALTH_BAR_HEIGHT, healthBgPaint);
        // Health level
        canvas.drawRect(x, y, x + HEALTH_BAR_WIDTH * healthPercent / 100f, 
            y + HEALTH_BAR_HEIGHT, healthPaint);
    }
}