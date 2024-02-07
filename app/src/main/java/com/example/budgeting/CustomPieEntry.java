package com.example.budgeting;

import com.github.mikephil.charting.data.PieEntry;

public class CustomPieEntry extends PieEntry {
    private String documentName;

    public CustomPieEntry(float value, String label, String documentName) {
        super(value, label);
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return documentName;
    }
}

