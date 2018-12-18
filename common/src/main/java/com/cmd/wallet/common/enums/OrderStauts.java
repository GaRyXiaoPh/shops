package com.cmd.wallet.common.enums;

/**
 *
 */
public enum OrderStauts implements ValueEnum {
    UNSEND(1),//待发货
    SENT(2),//已发货
    SUCCESS(3),//已完成
    CANCEL(4),//已取消
    RETURNING_VO(5),//退货中
    RETURNED_VO(6),//已退货
    NORMAL(0),//正常
    RETURNING(1),//退货中
    RETURNED(2);//已退货
    private int value;
    OrderStauts(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
