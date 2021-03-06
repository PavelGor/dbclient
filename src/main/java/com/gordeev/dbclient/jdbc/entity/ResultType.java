package com.gordeev.dbclient.jdbc.entity;

public enum ResultType {
    RESULTSET("result set"), UPDATED("updated"), ERROR("error");

    private final String type;

    ResultType(String type) {
        this.type = type;
    }

}
