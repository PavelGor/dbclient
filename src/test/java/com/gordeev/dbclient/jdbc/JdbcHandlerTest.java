package com.gordeev.dbclient.jdbc;

import com.gordeev.dbclient.entity.Data;
import com.gordeev.dbclient.jdbc.entity.Query;
import com.gordeev.dbclient.jdbc.entity.QueryType;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JdbcHandlerTest {
    JdbcHandler jdbcHandler = new JdbcHandler();

    @Test
    public void executeQueryTest() throws IOException {
        //prepare
        String testResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<resultSet>\n" +
                "<resultType>UPDATED</resultType>\n" +
                "<message>Database was created successfully</message>\n" +
                "</resultSet>";
        jdbcHandler.outputStream = new FileOutputStream(new File("queryToServer.json"));
        jdbcHandler.inputStream = new ByteArrayInputStream(testResult.getBytes());

        //when
        Data data = jdbcHandler.executeQuery("CREATE DATABASE testdb");

        //then
        assertEquals("UPDATED", data.getColumnName(0));
        assertEquals("Database was created successfully", data.getValue(0, 0));
        System.out.println(data);
    }

    @Test
    public void createTableQueryTest() {
        //prepare
        Query actualQuery = new Query();
        String sql = "CREATE TABLE test2db.test4dbtable (id int, name String, salary double)";
        actualQuery.setDbName("test2db");
        actualQuery.setTableName("test4dbtable");
        actualQuery.setType(QueryType.CREATE_TABLE);
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "int");
        columns.put("name", "String");
        columns.put("salary", "double");
        actualQuery.setColumns(columns);

        //when
        Query expectedQuery = jdbcHandler.sqlStringToQuery(sql);

        //then
        assertEquals(expectedQuery.getTableName(), actualQuery.getTableName());
        assertEquals(expectedQuery.getDbName(), actualQuery.getDbName());
        assertEquals(expectedQuery.getType(), actualQuery.getType());
        assertEquals(expectedQuery.getColumns().get("id"), actualQuery.getColumns().get("id"));
    }

    @Test
    public void selectQueryTest() {
        //prepare
        Query actualQuery = new Query();
        String sql = "Select id, name, salary FROM test2db.test4dbtable;";
        actualQuery.setDbName("test2db");
        actualQuery.setTableName("test4dbtable");
        actualQuery.setType(QueryType.SELECT);
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "int");
        columns.put("name", "String");
        columns.put("salary", "double");
        actualQuery.setColumns(columns);

        //when
        Query expectedQuery = jdbcHandler.sqlStringToQuery(sql);

        //then
        assertEquals(expectedQuery.getDbName(), actualQuery.getDbName());
        assertEquals(expectedQuery.getTableName(), actualQuery.getTableName());
        assertEquals(expectedQuery.getType(), actualQuery.getType());
        assertEquals(expectedQuery.getColumns().get("name"), actualQuery.getColumns().get("name"));

    }

    @Test
    public void deleteQueryTest() {
        //prepare
        Query actualQuery = new Query();
        String sql = "DELETE FROM test2db.test4dbtable WHERE id=1";
        actualQuery.setDbName("test2db");
        actualQuery.setTableName("test4dbtable");
        actualQuery.setType(QueryType.DELETE);
        Map<String, String> conditions = new HashMap<>();
        conditions.put("id", "1");
        actualQuery.setConditions(conditions);

        //when
        Query expectedQuery = jdbcHandler.sqlStringToQuery(sql);

        //then
        assertEquals(expectedQuery.getDbName(), actualQuery.getDbName());
        assertEquals(expectedQuery.getTableName(), actualQuery.getTableName());
        assertEquals(expectedQuery.getType(), actualQuery.getType());
        assertEquals(expectedQuery.getConditions().get("id"), actualQuery.getConditions().get("id"));
    }

    @Test
    public void updateQueryTest() {
        //prepare
        Query actualQuery = new Query();
        String sql = "UPDATE test2db.test4dbtable SET id=1, name=2, salary=3 WHERE id=1";
        actualQuery.setDbName("test2db");
        actualQuery.setTableName("test4dbtable");
        actualQuery.setType(QueryType.UPDATE);
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "int");
        columns.put("name", "String");
        columns.put("salary", "double");
        actualQuery.setColumns(columns);
        Map<String, String> values = new HashMap<>();
        values.put("id", "1");
        values.put("name", "2");
        values.put("salary", "3");
        actualQuery.setValues(values);

        //when
        Query expectedQuery = jdbcHandler.sqlStringToQuery(sql);

        //then
        assertEquals(expectedQuery.getDbName(), actualQuery.getDbName());
        assertEquals(expectedQuery.getTableName(), actualQuery.getTableName());
        assertEquals(expectedQuery.getType(), actualQuery.getType());
        assertEquals(expectedQuery.getValues().get("name"), actualQuery.getValues().get("name"));
        assertEquals(expectedQuery.getConditions().get("id"), "1");
    }

    @Test
    public void insertQueryTest() {
        //prepare  INSERT INTO table_name (column1, column2, column3, ...) VALUES (value1, value2, value3, ...);
       Query actualQuery = new Query();
        String sql = "INSERT INTO test2db.test4dbtable (id, name, salary) VALUES (1,2,3)";
        actualQuery.setDbName("test2db");
        actualQuery.setTableName("test4dbtable");
        actualQuery.setType(QueryType.INSERT);
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "int");
        columns.put("name", "String");
        columns.put("salary", "double");
        actualQuery.setColumns(columns);
        Map<String, String> values = new HashMap<>();
        values.put("id", "1");
        values.put("name", "2");
        values.put("salary", "3");
        actualQuery.setValues(values);

        //when
        Query expectedQuery = jdbcHandler.sqlStringToQuery(sql);

        //then
        assertEquals(expectedQuery.getDbName(), actualQuery.getDbName());
        assertEquals(expectedQuery.getTableName(), actualQuery.getTableName());
        assertEquals(expectedQuery.getType(), actualQuery.getType());
        assertEquals(expectedQuery.getValues().get("name"), actualQuery.getValues().get("name"));
    }
}