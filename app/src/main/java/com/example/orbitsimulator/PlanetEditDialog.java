package com.example.orbitsimulator;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PlanetEditDialog {

    public interface OnPlanetEditedListener {
        void onPlanetEdited(int index, float x, float y, float mass, float vx, float vy);
    }

    public static void show(Context context, int planetIndex,
                            float currentX, float currentY,
                            float currentMass, float currentVx, float currentVy,
                            OnPlanetEditedListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_planet, null);

        EditText etX = view.findViewById(R.id.etX);
        EditText etY = view.findViewById(R.id.etY);
        EditText etMass = view.findViewById(R.id.etMass);
        EditText etVx = view.findViewById(R.id.etVx);
        EditText etVy = view.findViewById(R.id.etVy);

        etX.setText(String.format("%.1f", currentX));
        etY.setText(String.format("%.1f", currentY));
        etMass.setText(String.format("%.1f", currentMass));
        etVx.setText(String.format("%.1f", currentVx));
        etVy.setText(String.format("%.1f", currentVy));

        builder.setTitle("🪐 Редактирование планеты " + (planetIndex + 1))
                .setView(view)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    try {
                        float newX = Float.parseFloat(etX.getText().toString());
                        float newY = Float.parseFloat(etY.getText().toString());
                        float newMass = Float.parseFloat(etMass.getText().toString());
                        float newVx = Float.parseFloat(etVx.getText().toString());
                        float newVy = Float.parseFloat(etVy.getText().toString());

                        if (newMass <= 0) {
                            Toast.makeText(context, "Масса должна быть больше 0", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (newMass > 50) {
                            Toast.makeText(context, "Масса не должна превышать 50", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (Math.abs(newVx) > 15 || Math.abs(newVy) > 15) {
                            Toast.makeText(context, "Скорость должна быть в пределах -15..15", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        listener.onPlanetEdited(planetIndex, newX, newY, newMass, newVx, newVy);
                        Toast.makeText(context, "✅ Параметры планеты обновлены", Toast.LENGTH_SHORT).show();

                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "❌ Введите корректные числа", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}
