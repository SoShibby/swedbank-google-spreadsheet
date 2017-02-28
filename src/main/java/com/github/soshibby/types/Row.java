package com.github.soshibby.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henrik on 05/02/2017.
 */
public class Row {

    private List<Column> columns = new ArrayList();

    public void addColumn(Column column) {
        columns.add(column);
    }

    public Row newColumn(Object value) {
        addColumn(new Column(value));
        return this;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
