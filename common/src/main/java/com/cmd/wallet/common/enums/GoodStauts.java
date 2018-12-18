package com.cmd.wallet.common.enums;

public enum GoodStauts implements ValueEnum {
    DOWN(0),//下架
    UP(1);//上架
    private int value;
    GoodStauts(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
