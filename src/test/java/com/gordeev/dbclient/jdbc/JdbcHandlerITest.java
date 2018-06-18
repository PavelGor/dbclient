package com.gordeev.dbclient.jdbc;

import com.gordeev.dbclient.entity.ConnectionParameters;
import com.gordeev.dbclient.entity.Data;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class JdbcHandlerITest {
    JdbcHandler jdbcHandler = new JdbcHandler();

    @Before
    public void setUp() throws Exception {
        ConnectionParameters connectionParameters = new ConnectionParameters();
        connectionParameters.setUrl("jdbc:mysql://localhost/userstore?useUnicode=true&characterEncoding=UTF8");
        connectionParameters.setUser("root");
        connectionParameters.setPassword("root");
        jdbcHandler.connect(connectionParameters);
    }

    @Test
    public void executeQueryITest() throws SQLException, IOException {
        //UPDATE users SET name = 123, price = 123, description = 123, img_link = 123 WHERE id = 15
        //INSERT INTO `userstore`.`users` (`id`, `firstName`, `lastName`, `salary`, `dateOfBirth`) VALUES ('15', '123', '123', '123', '2000-02-01');
        Data data = jdbcHandler.executeQuery("SELECT * FROM users");
        //Data data2 = jdbcHandler.executeQuery("INSERT INTO `userstore`.`users` (`firstName`, `lastName`, `salary`, `dateOfBirth`) VALUES ('firstName123', 'lastName123', '123', '2000-02-01')");

        assertEquals(5, data.getColumnCount());
        assertEquals(11, data.getValue(0,0));
        assertEquals("firstName123", data.getValue(1,10));
    }
}