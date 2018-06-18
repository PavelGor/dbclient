package com.gordeev.dbclient.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.List;

public class PropertyFactory implements Callback<TableColumn.CellDataFeatures<List<Object>, String>, ObservableValue<String>> {

    private int index;

    public PropertyFactory(int index) {
        this.index = index;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<List<Object>, String> param) {
        return new SimpleStringProperty(String.valueOf(param.getValue().get(index)));
    }
}
