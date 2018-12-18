package com.cmd.wallet.common.enums;

/**
 *
 */
public enum ReputationStauts implements ValueEnum {
    GOOD(0),//好评
    MIDDLE(1),//中评
    BAD(2);//差评

    private int value;
    ReputationStauts(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
