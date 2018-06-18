package com.gordeev.dbclient.jdbc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gordeev.dbclient.entity.ConnectionParameters;
import com.gordeev.dbclient.entity.Data;
import com.gordeev.dbclient.jdbc.entity.Query;
import com.gordeev.dbclient.jdbc.entity.QueryType;
import com.gordeev.dbclient.jdbc.entity.Result;
import com.gordeev.dbclient.jdbc.entity.ResultType;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class JdbcHandler {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcHandler.class);
    public InputStream inputStream;
    public OutputStream outputStream;
    private final static ObjectMapper mapper = new ObjectMapper();
    private final static XmlMapper xmlMapper = new XmlMapper();

    public void connect(ConnectionParameters connectionParameters) throws IOException {
        Socket socket = new Socket(connectionParameters.getUrl(), 5318);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public Data executeQuery(String sqlString) throws IOException {
        Query myQuery = sqlStringToQuery(sqlString); // or convertSql() better?
        LOG.info("SQL was converted to local format Query: {}", myQuery);

        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        mapper.writeValue(outputStream, myQuery);
        LOG.info("Query was send to DB-server");

        xmlMapper.disable(com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE);
        xmlMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        Result result = xmlMapper.readValue(inputStream, Result.class);
        LOG.info("Response was received from DB-server: {}", result);

        return convertResult(result);
    }

    Query sqlStringToQuery(String sqlString) {
        Query query = new Query();
        String[] words = sqlString.split(" ");
        if (words[0].equalsIgnoreCase("create")) {
            if (words[1].equalsIgnoreCase("database")) {
                query.setType(QueryType.CREATE_DATABASE);
                query.setDbName(words[2]);
            } else if (words[1].equalsIgnoreCase("table")) {

                query = createTableQuery(sqlString);

            }
        } else if (words[0].equalsIgnoreCase("select")) {

            query = selectQuery(sqlString);

        } else if (words[0].equalsIgnoreCase("delete")) {

            query = deleteQuery(sqlString);

        } else if (words[0].equalsIgnoreCase("update")) {

            query = updateQuery(sqlString);

        } else if (words[0].equalsIgnoreCase("insert")) {

            query = insertQuery(sqlString);

        } else {
            throw new RuntimeException("not support such type of query");
        }

        return query;
    }

    private Data convertResult(Result result) {
        Data data = new Data();
        if (ResultType.RESULTSET == result.getResultType()) {
            for (int i = 0; i < result.getColumns().size(); i++) {
                data.addColumn(result.getColumns().get(i));
            }
            for (int i = 0; i < result.getRows().size() / result.getColumns().size(); i++) {
                for (int j = 0; j < result.getColumns().size(); j++) {
                    data.addValue(result.getRows().get(j + i * result.getColumns().size()).get(0), j);
                }
            }
        } else {
            data.addColumn(result.getResultType().toString());
            data.addValue(result.getMessage(), 0);
        }

        return data;
    }

    private Query createTableQuery(String sqlString) {
        String[] words = sqlString.split(" ");
        String[] tableName = words[2].split("\\.");//all queries only with syntax : "dbname.dbtable"
        String[] columnsAsString = sqlString.substring(sqlString.indexOf("(") + 1, sqlString.indexOf(")")).split(",");
        Map<String, String> columnsAsMap = new HashMap<>();
        for (String aColumnsAsString : columnsAsString) {
            String[] lines = aColumnsAsString.trim().split(" ");
            columnsAsMap.put(lines[0].trim(), lines[1].trim());
        }

        Query query = new Query();
        query.setType(QueryType.CREATE_TABLE);
        query.setDbName(tableName[0]);
        query.setTableName(tableName[1]);
        query.setColumns(columnsAsMap);
        return query;
    }

    private Query selectQuery(String sqlString) {
        String[] tableName;
        if (sqlString.contains(";")) {
            tableName = sqlString.substring(sqlString.indexOf("FROM"), sqlString.indexOf(";")).trim().split("\\.");//all queries only with syntax : "dbname.dbtable"
        } else {
            tableName = sqlString.substring(sqlString.indexOf("FROM")).trim().split("\\.");//all queries only with syntax : "dbname.dbtable"
        }

        Query query = new Query();
        query.setType(QueryType.SELECT);
        query.setDbName(tableName[0].substring(4).trim());
        query.setTableName(tableName[1]);

        String[] columns = sqlString.substring(sqlString.indexOf("SELECT") + 7, sqlString.indexOf("FROM")).trim().replace(",", "").split(" ");
        for (String column : columns) {
            query.getColumns().put(column, "String");
        }

        return query;
    }

    private Query deleteQuery(String sqlString) {
        String[] words = sqlString.split(" ");
        String[] tableName = words[2].trim().split("\\.");
        String[] conditionsAsString = sqlString.substring(sqlString.indexOf("WHERE") + 6).trim().split("and");
        Map<String, String> conditionsAsMap = new HashMap<>();
        for (String aConditionsAsString : conditionsAsString) {
            String[] condition = aConditionsAsString.split("=");
            conditionsAsMap.put(condition[0].trim(), condition[1].trim());
        }

        Query query = new Query();
        query.setType(QueryType.DELETE);
        query.setDbName(tableName[0].trim());
        query.setTableName(tableName[1].trim());
        query.setConditions(conditionsAsMap);
        return query;
    }

    private Query updateQuery(String sqlString) {
        String[] words = sqlString.split(" ");
        String[] tableName = words[1].trim().split("\\.");
        String[] conditionsAsString = sqlString.substring(sqlString.indexOf("WHERE") + 6).trim().split("and");
        Map<String, String> conditionsAsMap = new HashMap<>();
        for (String aConditionsAsString : conditionsAsString) {
            String[] condition = aConditionsAsString.split("=");
            conditionsAsMap.put(condition[0].trim(), condition[1].trim());
        }
        String[] valuesAsString = sqlString.substring(sqlString.indexOf("SET") + 4, sqlString.indexOf("WHERE")).trim().split(",");
        Map<String, String> valuesAsMap = new HashMap<>();
        for (String aValuesAsString : valuesAsString) {
            String[] value = aValuesAsString.split("=");
            valuesAsMap.put(value[0].trim(), value[1].trim());
        }

        Query query = new Query();
        query.setType(QueryType.UPDATE);
        query.setDbName(tableName[0].trim());
        query.setTableName(tableName[1].trim());
        query.setConditions(conditionsAsMap);
        query.setValues(valuesAsMap);
        return query;
    }

    private Query insertQuery(String sqlString) {
        String[] words = sqlString.split(" ");
        String[] tableName = words[2].trim().split("\\.");
        String[] columnsAsString = sqlString.substring(sqlString.indexOf("(")+1, sqlString.indexOf(")")).trim().split(",");
        String[] valuesAsString = sqlString.substring(sqlString.lastIndexOf("(")+1, sqlString.lastIndexOf(")")).trim().split(",");
        Map<String, String> valuesAsMap = new HashMap<>();
        Map<String, String> columnsAsMap = new HashMap<>();
        for (int i = 0; i < valuesAsString.length; i++) {
            valuesAsMap.put(columnsAsString[i].trim(), valuesAsString[i].trim());
            columnsAsMap.put(columnsAsString[i].trim(), "String");
        }

        Query query = new Query();
        query.setType(QueryType.INSERT);
        query.setDbName(tableName[0].trim());
        query.setTableName(tableName[1].trim());
        query.setValues(valuesAsMap);
        query.setColumns(columnsAsMap);
        return query;
    }
}
