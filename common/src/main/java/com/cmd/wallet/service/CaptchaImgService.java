package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.utils.CageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CaptchaImgService {

    private static final int LEN = 4;
    private static final int OVERTIME = 3;
    private static final String PRE_KEY = "CAPTCHA_IMG_";

    private RedisTemplate<String, String> redUsersTemplate;

    @Autowired
    public CaptchaImgService(RedisTemplate<String, String> redUsersTemplate) {
        this.redUsersTemplate = redUsersTemplate;
        this.redUsersTemplate.setKeySerializer(new StringRedisSerializer());
        this.redUsersTemplate.setValueSerializer(new StringRedisSerializer());
    }

    public String getWord(String nationalCode, String mobile) {
        String key = PRE_KEY +mobile;
        String word = CageUtil.getWordsNumber(LEN);
        //去掉区号，不然安卓和ios都过不了
        this.redUsersTemplate.opsForValue().set(key, word, OVERTIME, TimeUnit.MINUTES);
        return word;
    }

    public void check(String nationalCode, String mobile, String words) {
        //去掉区号，不然安卓和ios都过不了
        String key = PRE_KEY +mobile;
        String targetWords = this.redUsersTemplate.opsForValue().get(key);
        Assert.check(targetWords == null, ErrorCode.ERR_USER_CAPTCHA_ERROR);
        Assert.check(!targetWords.toLowerCase().equals(words.toLowerCase()), ErrorCode.ERR_USER_CAPTCHA_ERROR);
        this.redUsersTemplate.delete(key);
    }
}