package com.cmd.wallet.common.utils;

import com.cmd.wallet.common.exception.BusinessException;
import com.cmd.wallet.common.exception.ServerException;

//用于判断抛出业务异常
public class Assert {
    public static void failed(int code) {
        throw new BusinessException(code, Language.get(code + "",RequestUtil.getLanguage().toString()));
    }
    public static void check(boolean check, int code){
        if (check){
            throw new BusinessException(code, Language.get(code + "",RequestUtil.getLanguage().toString()));
        }
    }
    public static void check(boolean check, int code, String errmsg){
        if (check){
            String tmp = Language.get(code + "",RequestUtil.getLanguage().toString());
            throw new BusinessException(code, tmp+errmsg);
        }
    }
    public static void checkParam(boolean check, int code, String errmsg){
        if (check){
            throw new BusinessException(code, errmsg);
        }
    }
    public static <T extends Enum<T>> T assertEnumParam(Class<T> clazz, String name, int code, String message) {
        try {
            return Enum.valueOf(clazz, name);
        } catch (Exception e) {
            throw new BusinessException(code, message);
        }
    }
}
