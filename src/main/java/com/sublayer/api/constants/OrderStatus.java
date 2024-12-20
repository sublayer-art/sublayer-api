package com.sublayer.api.constants;

public enum OrderStatus {

    SALE(1), BID(2);

    private Integer type;

    private OrderStatus(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
