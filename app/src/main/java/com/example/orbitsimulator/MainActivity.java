package com.example.orbitsimulator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SpaceView spaceView;
    private Button btnSimulate, btnStop, btnMinusPlanet, btnPlusPlanet, btnExitToMenu;
    private TextView tvPlanetCount, tvInfo;
    private SeekBar speedSeekBar;
    private TextView tvSpeedValue;
    private TextView tvSunMass;
    private boolean isSimulating = false;
    private Handler handler = new Handler();

    private List<Float> planetVx, planetVy, planetMass;
    private int planetCount = 0;

    private float sunMass = 1000f;
    private static final float DEFAULT_G = 0.8f;
    private float G = DEFAULT_G;

    private float simulationSpeed = 0.5f;
    private static final float BASE_DELAY_MS = 20f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Загружаем сохранённое значение гравитационной постоянной
        SharedPreferences prefs = getSharedPreferences("SimulatorPrefs", MODE_PRIVATE);
        G = prefs.getFloat("G_VALUE", DEFAULT_G);

        spaceView = findViewById(R.id.spaceView);
        btnSimulate = findViewById(R.id.btnSimulate);
        btnStop = findViewById(R.id.btnStop);
        btnMinusPlanet = findViewById(R.id.btnMinusPlanet);
        btnPlusPlanet = findViewById(R.id.btnPlusPlanet);
        btnExitToMenu = findViewById(R.id.btnExitToMenu);
        tvPlanetCount = findViewById(R.id.tvPlanetCount);
        tvInfo = findViewById(R.id.tvInfo);
        speedSeekBar = findViewById(R.id.speedSeekBar);
        tvSpeedValue = findViewById(R.id.tvSpeedValue);
        tvSunMass = findViewById(R.id.tvSunMass);

        planetVx = new ArrayList<>();
        planetVy = new ArrayList<>();
        planetMass = new ArrayList<>();

        // Масса Солнца
        tvSunMass.setText(String.format("%.0f", sunMass));

        Button btnSunMassMinus = findViewById(R.id.btnSunMassMinus);
        Button btnSunMassPlus = findViewById(R.id.btnSunMassPlus);

        btnSunMassMinus.setOnClickListener(v -> {
            sunMass = Math.max(100f, sunMass - 100f);
            tvSunMass.setText(String.format("%.0f", sunMass));
            tvInfo.setText("☀️ Масса Солнца: " + String.format("%.0f", sunMass));
            if (isSimulating) {
                stopSimulation();
                startSimulation();
            }
        });

        btnSunMassPlus.setOnClickListener(v -> {
            sunMass = Math.min(5000f, sunMass + 100f);
            tvSunMass.setText(String.format("%.0f", sunMass));
            tvInfo.setText("☀️ Масса Солнца: " + String.format("%.0f", sunMass));
            if (isSimulating) {
                stopSimulation();
                startSimulation();
            }
        });

        // Ползунок скорости
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                simulationSpeed = 0.1f + (progress / 100f) * 2.9f;
                tvSpeedValue.setText(String.format("%.1fx", simulationSpeed));
                if (isSimulating) {
                    stopSimulation();
                    startSimulation();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnPlusPlanet.setOnClickListener(v -> addRandomPlanet());
        btnMinusPlanet.setOnClickListener(v -> removePlanet());
        btnSimulate.setOnClickListener(v -> startSimulation());
        btnStop.setOnClickListener(v -> stopSimulation());

        // Кнопка выхода в главное меню
        btnExitToMenu.setOnClickListener(v -> finish());

        spaceView.setOnPlanetLongClickListener(this::showEditDialog);
    }

    private void addRandomPlanet() {
        if (planetCount >= 8) {
            Toast.makeText(this, "Максимум 8 планет", Toast.LENGTH_SHORT).show();
            return;
        }

        int w = spaceView.getWidth();
        int h = spaceView.getHeight();
        if (w == 0 || h == 0) {
            w = 1000;
            h = 1800;
        }

        Random rand = new Random();
        float angle = (float) (rand.nextFloat() * 2 * Math.PI);
        float distance = 150 + rand.nextFloat() * 250;
        float x = w / 2f + (float) (Math.cos(angle) * distance);
        float y = h / 2f + (float) (Math.sin(angle) * distance);

        // Правильная скорость для круговой орбиты
        float orbitalSpeed = (float) Math.sqrt(G * sunMass / distance);
        float vx = (float) (-Math.sin(angle) * orbitalSpeed);
        float vy = (float) (Math.cos(angle) * orbitalSpeed);

        float mass = 5 + rand.nextFloat() * 15;

        addPlanetWithParams(x, y, mass, vx, vy);
        tvInfo.setText("✅ Добавлена планета " + planetCount);
    }

    private void addPlanetWithParams(float x, float y, float mass, float vx, float vy) {
        spaceView.addPlanet(x, y);
        planetVx.add(vx);
        planetVy.add(vy);
        planetMass.add(mass);
        planetCount++;
        tvPlanetCount.setText(String.valueOf(planetCount));
    }

    private void removePlanet() {
        if (planetCount > 0) {
            spaceView.removeLastPlanet();
            planetVx.remove(planetCount - 1);
            planetVy.remove(planetCount - 1);
            planetMass.remove(planetCount - 1);
            planetCount--;
            tvPlanetCount.setText(String.valueOf(planetCount));
            tvInfo.setText("🗑 Планета удалена");
        } else {
            Toast.makeText(this, "Нет планет для удаления", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDialog(int index) {
        List<Float> planetX = spaceView.getPlanetX();
        List<Float> planetY = spaceView.getPlanetY();

        PlanetEditDialog.show(this, index,
                planetX.get(index),
                planetY.get(index),
                planetMass.get(index),
                planetVx.get(index),
                planetVy.get(index),
                (idx, newX, newY, newMass, newVx, newVy) -> {
                    spaceView.setPlanetPosition(idx, newX, newY);
                    planetMass.set(idx, newMass);
                    planetVx.set(idx, newVx);
                    planetVy.set(idx, newVy);

                    tvInfo.setText("✏️ Планета " + (idx + 1) + " изменена");

                    spaceView.setPlanetColor(idx, android.graphics.Color.parseColor("#FF9800"));
                    new Handler().postDelayed(() ->
                            spaceView.setPlanetColor(idx, getPlanetColor(idx)), 500);
                });
    }

    private int getPlanetColor(int index) {
        int[] colors = {android.graphics.Color.RED, android.graphics.Color.BLUE,
                android.graphics.Color.GREEN, android.graphics.Color.YELLOW,
                android.graphics.Color.MAGENTA, android.graphics.Color.CYAN,
                android.graphics.Color.parseColor("#FF9800"), android.graphics.Color.parseColor("#9C27B0")};
        return colors[index % colors.length];
    }

    private void startSimulation() {
        if (planetCount == 0) {
            Toast.makeText(this, "Сначала добавьте планеты", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isSimulating) return;
        isSimulating = true;
        tvInfo.setText("🚀 СИМУЛЯЦИЯ ЗАПУЩЕНА");
        simulationLoop();
    }

    private void stopSimulation() {
        isSimulating = false;
        tvInfo.setText("⏹ СИМУЛЯЦИЯ ОСТАНОВЛЕНА");
    }

    private void simulationLoop() {
        if (!isSimulating) return;

        updatePhysics();
        spaceView.invalidate();

        long delay = (long) (BASE_DELAY_MS / simulationSpeed);
        handler.postDelayed(this::simulationLoop, delay);
    }

    private void updatePhysics() {
        float sunX = spaceView.getWidth() / 2f;
        float sunY = spaceView.getHeight() / 2f;

        List<Float> planetX = spaceView.getPlanetX();
        List<Float> planetY = spaceView.getPlanetY();

        int n = planetCount;
        if (n == 0) return;

        float[] ax = new float[n];
        float[] ay = new float[n];

        // Влияние Солнца
        for (int i = 0; i < n; i++) {
            float dx = sunX - planetX.get(i);
            float dy = sunY - planetY.get(i);
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < 15) dist = 15;

            float force = G * sunMass * planetMass.get(i) / (dist * dist);
            ax[i] += force / planetMass.get(i) * (dx / dist);
            ay[i] += force / planetMass.get(i) * (dy / dist);
        }

        //  Влияние планет друг на друга
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                float dx = planetX.get(j) - planetX.get(i);
                float dy = planetY.get(j) - planetY.get(i);
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist < 15) dist = 15;

                float force = G * planetMass.get(i) * planetMass.get(j) / (dist * dist);

                ax[i] += force / planetMass.get(i) * (dx / dist);
                ay[i] += force / planetMass.get(i) * (dy / dist);

                ax[j] -= force / planetMass.get(j) * (dx / dist);
                ay[j] -= force / planetMass.get(j) * (dy / dist);
            }
        }

        // Обновление скоростей и позиций
        for (int i = 0; i < n; i++) {
            planetVx.set(i, planetVx.get(i) + ax[i]);
            planetVy.set(i, planetVy.get(i) + ay[i]);

            float newX = planetX.get(i) + planetVx.get(i);
            float newY = planetY.get(i) + planetVy.get(i);
            spaceView.setPlanetPosition(i, newX, newY);
        }
    }
}