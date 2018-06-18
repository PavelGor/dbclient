package com.gordeev.dbclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/workwithdb.fxml"));
        Parent root = loader.load();

        Handler handler = Handler.getInstance();
        handler.setController(loader.getController());

        primaryStage.setTitle("Work with my Database");
        setUserAgentStylesheet(STYLESHEET_CASPIAN);
        primaryStage.setScene(new Scene(root, 753, 627));
        primaryStage.show();
        LOG.info("Main: initialize GUI");
    }


    public static void main(String[] args) {
        launch(args);
    }




}
