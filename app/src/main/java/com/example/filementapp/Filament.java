package com.example.filementapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "filaments")
public class Filament {

    public static final int START_WASTE_G = 3;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String brand;
    private String material;
    private String color;
    private int totalWeightG;
    private int remainingWeightG;
    private double priceDkk;
    private String notes;
    @NonNull
    private String purchaseDate = "";   // "yyyy-MM-dd", tom = ikke sat

    public Filament(String brand, String material, String color,
                    int totalWeightG, int remainingWeightG,
                    double priceDkk, String notes, @NonNull String purchaseDate) {
        this.brand = brand;
        this.material = material;
        this.color = color;
        this.totalWeightG = totalWeightG;
        this.remainingWeightG = remainingWeightG;
        this.priceDkk = priceDkk;
        this.notes = notes;
        this.purchaseDate = purchaseDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getTotalWeightG() { return totalWeightG; }
    public void setTotalWeightG(int totalWeightG) { this.totalWeightG = totalWeightG; }

    public int getRemainingWeightG() { return remainingWeightG; }
    public void setRemainingWeightG(int remainingWeightG) { this.remainingWeightG = remainingWeightG; }

    public double getPriceDkk() { return priceDkk; }
    public void setPriceDkk(double priceDkk) { this.priceDkk = priceDkk; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @NonNull
    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(@NonNull String purchaseDate) { this.purchaseDate = purchaseDate; }

    public void useFilament(int gramsUsed) {
        int totalUsed = gramsUsed + START_WASTE_G;
        remainingWeightG -= totalUsed;
        if (remainingWeightG < 0) {
            remainingWeightG = 0;
        }
    }

    public int getRemainingPercent() {
        if (totalWeightG == 0) return 0;
        return (int) Math.round(remainingWeightG * 100.0 / totalWeightG);
    }
}