package com.example.orbitsimulator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class SpaceView extends View {

    private Paint sunPaint;
    private List<Paint> planetPaints;
    private List<Float> planetX, planetY;
    private int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.parseColor("#FF9800"), Color.parseColor("#9C27B0")};

    private OnPlanetLongClickListener onPlanetLongClickListener;
    private static final int PLANET_RADIUS = 20;

    public SpaceView(Context context) {
        super(context);
        init();
    }

    public SpaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        sunPaint = new Paint();
        sunPaint.setColor(Color.YELLOW);
        sunPaint.setStyle(Paint.Style.FILL);

        planetPaints = new ArrayList<>();
        planetX = new ArrayList<>();
        planetY = new ArrayList<>();
    }

    public void addPlanet(float x, float y) {
        planetX.add(x);
        planetY.add(y);
        Paint paint = new Paint();
        paint.setColor(colors[planetPaints.size() % colors.length]);
        paint.setStyle(Paint.Style.FILL);
        planetPaints.add(paint);
    }

    public void removeLastPlanet() {
        if (planetX.size() > 0) {
            planetX.remove(planetX.size() - 1);
            planetY.remove(planetY.size() - 1);
            planetPaints.remove(planetPaints.size() - 1);
        }
    }

    // НОВЫЙ МЕТОД — ОЧИСТКА ВСЕХ ПЛАНЕТ
    public void clearPlanets() {
        planetX.clear();
        planetY.clear();
        planetPaints.clear();
    }

    public int getPlanetCount() {
        return planetX.size();
    }

    public List<Float> getPlanetX() { return planetX; }
    public List<Float> getPlanetY() { return planetY; }

    public void setPlanetPosition(int index, float x, float y) {
        if (index < planetX.size()) {
            planetX.set(index, x);
            planetY.set(index, y);
        }
    }

    public void setPlanetColor(int index, int color) {
        if (index < planetPaints.size()) {
            planetPaints.get(index).setColor(color);
        }
    }

    public interface OnPlanetLongClickListener {
        void onPlanetLongClick(int index);
    }

    public void setOnPlanetLongClickListener(OnPlanetLongClickListener listener) {
        this.onPlanetLongClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && onPlanetLongClickListener != null) {
            float touchX = event.getX();
            float touchY = event.getY();

            for (int i = 0; i < planetX.size(); i++) {
                float dx = touchX - planetX.get(i);
                float dy = touchY - planetY.get(i);
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance <= PLANET_RADIUS + 10) {
                    onPlanetLongClickListener.onPlanetLongClick(i);
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        // Солнце
        canvas.drawCircle(w / 2f, h / 2f, 35, sunPaint);

        // Сияние Солнца
        Paint sunGlow = new Paint();
        sunGlow.setColor(Color.parseColor("#FFFF00"));
        sunGlow.setAlpha(50);
        canvas.drawCircle(w / 2f, h / 2f, 55, sunGlow);

        // Планеты
        for (int i = 0; i < planetX.size(); i++) {
            if (planetX.get(i) != 0 && planetY.get(i) != 0) {
                canvas.drawCircle(planetX.get(i), planetY.get(i), PLANET_RADIUS, planetPaints.get(i));

                // Обводка планеты
                Paint strokePaint = new Paint();
                strokePaint.setColor(Color.WHITE);
                strokePaint.setStyle(Paint.Style.STROKE);
                strokePaint.setStrokeWidth(2);
                canvas.drawCircle(planetX.get(i), planetY.get(i), PLANET_RADIUS, strokePaint);
            }
        }
    }
}