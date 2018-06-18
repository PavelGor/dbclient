package com.gordeev.dbclient.entity;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<String> columns = new ArrayList<>();
    private List<List<Object>> rows = new ArrayList<>();
    private int rowCount = 0;

    public void addColumn(String columnName) {
        columns.add(columnName);
        rows.add(new ArrayList<>());
    }

    public void addValue(Object value, int columnIndex) {
        if (columnIndex <= columns.size()) {
            rows.get(columnIndex).add(value);
            rowCount = Math.max(rowCount, rows.get(columnIndex).size());
        } else {
            throw new RuntimeException("You cannot add value to columnIndex(" + columnIndex + ") bigger then columnCount have(" + columns.size() + ")");
        }
    }

    public Object getValue(int columnIndex, int rowIndex) {
        if (columnIndex > columns.size() || rowIndex > rowCount) {
            throw new RuntimeException("You cannot get value with indexs(" + columnIndex + ", " + rowIndex + ") bigger then data have(" + columns.size() + ", " + rowCount + ")");
        }
        return rows.get(columnIndex).get(rowIndex);
    }

    public String getColumnName(int index) {
        return columns.get(index);
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String toString() {
        return "Data{" +
                ", columns=" + columns +
                ", rows=" + rows +
                ", rowCount=" + rowCount +
                '}';
    }
}
