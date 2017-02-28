package com.github.soshibby.types;

import com.github.soshibby.swedbank.util.Assert;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henrik on 05/02/2017.
 */
public class Table {

    private int rowOffset = 0;
    private int columnOffset = 0;
    private List<Row> rows = new ArrayList();

    public Table() {

    }

    public Table(int rowOffset, int columnOffset) {
        this.rowOffset = rowOffset;
        this.columnOffset = columnOffset;
    }

    public Row addRow(Row row) {
        rows.add(row);
        return row;
    }

    public Row newRow() {
        return addRow(new Row());
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public void setRowOffset(int rowOffset) {
        this.rowOffset = rowOffset;
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public void setColumnOffset(int columnOffset) {
        this.columnOffset = columnOffset;
    }

    public int getColumnOffset() {
        return columnOffset;
    }

    public List<List<Object>> toSpreadsheet() {
        List<List<Object>> result = new ArrayList();

        rows.forEach(row -> {
            List<Object> columns = new ArrayList();
            row.getColumns().forEach(column -> columns.add(column.getValue()));
            result.add(columns);
        });

        return result;
    }

    public ValueRange toValueRange() {
        return new ValueRange().setValues(this.toSpreadsheet());
    }

    public int getMaxColumns() {
        int maxColumns = 0;

        for (Row row : rows) {
            int columns = row.getColumns().size();

            if (columns > maxColumns) {
                maxColumns = columns;
            }
        }

        return maxColumns;
    }

    public String getRange() {
        Assert.greaterEqualThan(columnOffset, 0, "Column offset must be greater or equal to 0.");
        Assert.greaterEqualThan(rowOffset, 0, "Row offset must be greater or equal to 0.");

        Integer startColumn = columnOffset + 1;
        Integer startRow = rowOffset + 1;

        Integer endColumn = getMaxColumns() == 0 ? columnOffset + 1 : columnOffset + getMaxColumns();
        Integer endRow = rows.size() == 0 ? rowOffset + 1 : rowOffset + rows.size();

        return getChar(startColumn) + startRow + ":" + getChar(endColumn) + endRow;
    }

    private String getChar(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 64)) : null;
    }

}
