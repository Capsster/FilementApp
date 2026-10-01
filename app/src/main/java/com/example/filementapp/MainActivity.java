package com.example.filementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FilamentDao dao;
    private FilamentAdapter adapter;

    private int currentSort = 0;   // 0=mærke, 1=mindst, 2=mest, 3=nyeste køb
    private final String[] sortOptions = {
            "Mærke (A–Z)",
            "Mindst tilbage først",
            "Mest tilbage først",
            "Nyeste køb først"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.mainRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_sort) {
                showSortDialog();
                return true;
            }
            return false;
        });

        dao = AppDatabase.getInstance(this).filamentDao();

        RecyclerView list = findViewById(R.id.filamentList);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FilamentAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new FilamentAdapter.OnItemClickListener() {
            @Override
            public void onClick(Filament filament) {
                showRegisterPrintDialog(filament);
            }

            @Override
            public void onLongClick(Filament filament) {
                showEditOrDeleteDialog(filament);
            }
        });

        FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddFilamentActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFilaments();
    }

    private void loadFilaments() {
        List<Filament> all = new ArrayList<>(dao.getAll());
        sortList(all);
        adapter.setFilaments(all);
    }

    private void sortList(List<Filament> list) {
        switch (currentSort) {
            case 1:
                list.sort(Comparator.comparingInt(Filament::getRemainingPercent));
                break;
            case 2:
                list.sort(Comparator.comparingInt(Filament::getRemainingPercent).reversed());
                break;
            case 3:
                list.sort(Comparator.comparing(Filament::getPurchaseDate).reversed());
                break;
            default:
                list.sort(Comparator.comparing(Filament::getBrand, String.CASE_INSENSITIVE_ORDER));
                break;
        }
    }

    private void showSortDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sortér efter")
                .setSingleChoiceItems(sortOptions, currentSort, (dialog, which) -> {
                    currentSort = which;
                    dialog.dismiss();
                    loadFilaments();
                })
                .show();
    }

    private void showRegisterPrintDialog(Filament filament) {
        EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Gram brugt på printet");

        new AlertDialog.Builder(this)
                .setTitle("Registrer print")
                .setMessage(filament.getBrand() + " · " + filament.getMaterial()
                        + "\n" + filament.getRemainingWeightG() + " g tilbage")
                .setView(input)
                .setPositiveButton("Træk fra", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    if (text.isEmpty()) return;
                    int grams = Integer.parseInt(text);
                    filament.useFilament(grams);
                    dao.update(filament);
                    loadFilaments();
                })
                .setNegativeButton("Annuller", null)
                .show();
    }

    private void showEditOrDeleteDialog(Filament filament) {
        new AlertDialog.Builder(this)
                .setTitle(filament.getBrand() + " · " + filament.getMaterial())
                .setItems(new String[]{"Rediger", "Slet"}, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(this, AddFilamentActivity.class);
                        intent.putExtra("filamentId", filament.getId());
                        startActivity(intent);
                    } else {
                        confirmDelete(filament);
                    }
                })
                .show();
    }

    private void confirmDelete(Filament filament) {
        new AlertDialog.Builder(this)
                .setTitle("Slet spole?")
                .setMessage("Vil du slette " + filament.getBrand() + " · "
                        + filament.getMaterial() + "?")
                .setPositiveButton("Slet", (dialog, which) -> {
                    dao.delete(filament);
                    loadFilaments();
                })
                .setNegativeButton("Annuller", null)
                .show();
    }
}