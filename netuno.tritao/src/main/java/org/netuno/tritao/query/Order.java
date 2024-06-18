package org.netuno.tritao.query;

public class Order {
    private String column;
    private String order;

    public Order(String column, String order) {
        this.column = column;
        this.order = order;
    }

    public String getColumn() {
        return column;
    }

    public Order setColumn(String column) {
        this.column = column;
        return this;
    }

    public String getOrder() {
        return order;
    }

    public Order setOrder(String order) {
        this.order = order;
        return this;
    }
}
