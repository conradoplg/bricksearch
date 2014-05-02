package net.cryptoland.bricksearch.desktop;

import net.cryptoland.bricksearch.Part;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PartTableModel extends AbstractTableModel {
    List<Part> parts;
    String[] columnHeaders = {"", "ID", "Usage", "Description"};

    public PartTableModel(List<Part> parts) {
        this.parts = parts;
    }

    @Override
    public int getRowCount() {
        return parts.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnHeaders[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return ImageIcon.class;
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            case 3:
                return String.class;
            default:
                return null;
        }
    }

    @Override
    public int getColumnCount() {
        return columnHeaders.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Part part = parts.get(rowIndex);
        switch (columnIndex) {
            case 0:
                System.out.println("Request: " + rowIndex);
                return null;
            case 1:
                return part.getId();
            case 2:
                return part.getUsageCount();
            case 3:
                return part.getDescription();
            default:
                return null;
        }
    }
}
