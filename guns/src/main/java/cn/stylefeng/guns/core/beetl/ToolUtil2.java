package cn.stylefeng.guns.core.beetl;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

public class ToolUtil2 {
    public static String dateType(Object o) {
        return o instanceof Date ? DateUtil.formatDateTime((Date)o) : o.toString();
    }

    public static String currentTime() {
        return DateUtil.formatDateTime(new Date());
    }
}
