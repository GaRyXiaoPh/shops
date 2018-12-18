package com.cmd.wallet.common.enums;

/**
 *
 */
public enum ImageType implements ValueEnum {
    SLIDE_SHOW(0),//轮播图
    MALL_SEND(1),//发货凭证
    MALL_RETURN(2),//退货凭证
    BUS_LICENSE(3),//营业执照
    SHOP_PHOTO(4);//门面照片
    private int value;
    ImageType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
