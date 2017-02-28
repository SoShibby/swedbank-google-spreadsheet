package com.github.soshibby.types;

/**
 * Created by Henrik on 05/02/2017.
 */
public class Column {

    private Object value;

    public Column() {

    }

    public Column(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
