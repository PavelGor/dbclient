package com.gordeev.dbclient.ui.controller;

import com.gordeev.dbclient.entity.ConnectionParameters;
import com.gordeev.dbclient.Handler;
import com.gordeev.dbclient.entity.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Controller {
    private Handler handler = Handler.getInstance();

    @FXML
    private TextArea sqlQueryText;

    @FXML
    private Button executeButton;

    @FXML
    private TableView<List<Object>> tableView;

    public void onManuallySetParameters() {
        Dialog<ConnectionParameters> dialog = new Dialog<>();
        ConnectionParameters connectionParameters = handler.getConnectionParameters();
        dialog.setTitle("Connection settings");

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField url = new TextField();
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        if (connectionParameters == null) {
            url.setPromptText("JDBC Url to DB");
            username.setPromptText("Username");
            password.setPromptText("Password");
        } else {
            url.setText(connectionParameters.getUrl());
            username.setText(connectionParameters.getUser());
            password.setText(connectionParameters.getPassword());
        }

        grid.add(new Label("JDBC Url to DB:"), 0, 0);
        grid.add(url, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(username, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(password, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                connectionParameters.setUrl(url.getText());
                connectionParameters.setUser(username.getText());
                connectionParameters.setPassword(password.getText());
                return connectionParameters;
            }
            return null;
        });

        Optional<ConnectionParameters> result = dialog.showAndWait();
        if (result.isPresent()) {
            System.out.println(connectionParameters);
            handler.connect();
        }
    }

    public void drawResult(Data data) {
        ObservableList<List<Object>> rows = FXCollections.observableArrayList();
        Collection tableColumnList = new ArrayList<>();

        tableView.getColumns().removeAll(tableView.getColumns());

        for (int i = 0; i < data.getColumnCount(); i++) {
            TableColumn<List<Object>, String> tableColumn = new TableColumn<>(data.getColumnName(i));
            tableColumn.setCellValueFactory(new PropertyFactory(i));
            tableColumnList.add(tableColumn);
        }


        for (int i = 0; i < data.getRowCount(); i++) {
            List<Object> row = new ArrayList<>();
            for (int i1 = 0; i1 < data.getColumnCount(); i1++) {
                row.add(data.getValue(i1, i));
            }
            rows.add(row);
        }

        tableView.getColumns().addAll(tableColumnList);
        tableView.setItems(rows);
        tableView.refresh();

    }

    public void onExecuteQuery() {
        handler.executeQuery(sqlQueryText.getText());
    }

    public void notifyUser(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public void setGUIEnableToWork() {
        sqlQueryText.setDisable(false);
        executeButton.setDisable(false);
    }
}
