package com.cmd.wallet.common.enums;

/**
 * 0：不允许，1：允许
 */
public enum SalesPermit implements ValueEnum {
    NO(0),//不允许
    YES(1);//允许
    private int value;
    SalesPermit(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
