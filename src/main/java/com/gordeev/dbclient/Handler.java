package com.gordeev.dbclient;

import com.gordeev.dbclient.entity.ConnectionParameters;
import com.gordeev.dbclient.ui.controller.Controller;
import com.gordeev.dbclient.entity.Data;
import com.gordeev.dbclient.jdbc.JdbcHandler;
import com.gordeev.dbclient.ui.HtmlCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Handler {
    private final static Handler INSTANCE = new Handler();

    private Handler() {
    }

    public static Handler getInstance() {
        return INSTANCE;
    }

    private static final Logger LOG = LoggerFactory.getLogger(Handler.class);
    private Controller controller;
    private JdbcHandler jdbcHandler = new JdbcHandler();
    private HtmlCreator htmlCreator = new HtmlCreator();
    private ConnectionParameters connectionParameters = new ConnectionParameters();

    public void connect() {
        if (connectionParameters != null) {
            try {
                jdbcHandler.connect(connectionParameters);
                controller.setGUIEnableToWork();
                controller.notifyUser("Connection established. You can work with Queries");
                LOG.info("Connection established. User can work with Queries");
            }  catch (IOException e) {
                controller.notifyUser("Connection didn't established. Set parameters manually, press Connection settings");
                LOG.info("Connection didn't established.");
            }
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void executeQuery(String query) {
        try {
            Data data = jdbcHandler.executeQuery(query);
            htmlCreator.createHtml(data);
            controller.drawResult(data);
            LOG.info("Response was drawn in client UI");
        } catch (IOException e) {
            controller.notifyUser("Incorrect query. Try again");
            LOG.info("Error: {}", e);
        }
    }

    public ConnectionParameters getConnectionParameters() {
        return connectionParameters;
    }
}
