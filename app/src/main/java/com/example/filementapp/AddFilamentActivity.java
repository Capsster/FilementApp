package com.example.filementapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

public class AddFilamentActivity extends AppCompatActivity {

    private FilamentDao dao;
    private Filament editing = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_filament);

        View root = findViewById(R.id.addRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        dao = AppDatabase.getInstance(this).filamentDao();

        EditText brandInput = findViewById(R.id.brandInput);
        EditText materialInput = findViewById(R.id.materialInput);
        EditText colorInput = findViewById(R.id.colorInput);
        EditText totalInput = findViewById(R.id.totalInput);
        EditText remainingInput = findViewById(R.id.remainingInput);
        EditText priceInput = findViewById(R.id.priceInput);
        EditText notesInput = findViewById(R.id.notesInput);
        EditText dateInput = findViewById(R.id.dateInput);
        Button saveButton = findViewById(R.id.saveButton);

        dateInput.setOnClickListener(v -> openDatePicker(dateInput));

        int id = getIntent().getIntExtra("filamentId", -1);
        if (id != -1) {
            editing = dao.getById(id);
            brandInput.setText(editing.getBrand());
            materialInput.setText(editing.getMaterial());
            colorInput.setText(editing.getColor());
            totalInput.setText(String.valueOf(editing.getTotalWeightG()));
            remainingInput.setText(String.valueOf(editing.getRemainingWeightG()));
            priceInput.setText(String.valueOf(editing.getPriceDkk()));
            notesInput.setText(editing.getNotes());
            dateInput.setText(editing.getPurchaseDate());
            saveButton.setText("Opdater spole");
            setTitle("Rediger spole");
        }

        saveButton.setOnClickListener(v -> {
            String brand = brandInput.getText().toString().trim();
            String material = materialInput.getText().toString().trim();
            String color = colorInput.getText().toString().trim();
            String totalStr = totalInput.getText().toString().trim();
            String remainingStr = remainingInput.getText().toString().trim();
            String priceStr = priceInput.getText().toString().trim();
            String notes = notesInput.getText().toString().trim();
            String purchaseDate = dateInput.getText().toString().trim();

            if (TextUtils.isEmpty(brand) || TextUtils.isEmpty(material)
                    || TextUtils.isEmpty(totalStr) || TextUtils.isEmpty(remainingStr)) {
                Toast.makeText(this,
                        "Udfyld mindst mærke, materiale, total og gram tilbage",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int total = Integer.parseInt(totalStr);
            int remaining = Integer.parseInt(remainingStr);
            double price = TextUtils.isEmpty(priceStr)
                    ? 0 : Double.parseDouble(priceStr.replace(',', '.'));

            if (editing == null) {
                dao.insert(new Filament(brand, material, color, total, remaining,
                        price, notes, purchaseDate));
                Toast.makeText(this, "Spole gemt", Toast.LENGTH_SHORT).show();
            } else {
                editing.setBrand(brand);
                editing.setMaterial(material);
                editing.setColor(color);
                editing.setTotalWeightG(total);
                editing.setRemainingWeightG(remaining);
                editing.setPriceDkk(price);
                editing.setNotes(notes);
                editing.setPurchaseDate(purchaseDate);
                dao.update(editing);
                Toast.makeText(this, "Spole opdateret", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }

    private void openDatePicker(EditText target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    year, month + 1, day);
            target.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}