package com.cmd.wallet.webadmin.common;

public class PageUtil {
    // 把偏移转换为页数
    public static int offsetToPage(int offset, int limit) {
        if(limit == 0) return offset;
        return offset / limit + 1;
    }
}
