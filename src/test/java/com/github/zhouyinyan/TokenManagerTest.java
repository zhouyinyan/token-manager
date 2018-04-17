package com.github.zhouyinyan;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by zhouyinyan on 2018/4/16.
 */
public class TokenManagerTest {

    TokenManager<TestTokenInfo> tokenManager;

    @Before
    public void setup(){
        tokenManager = new LocalTokenManager<>();
    }

    @Test
    public void testGenerate(){
        //====case 1=========
        /**
         * 参与Token编码的原始信息对象为null，应该抛出异常
         */
        boolean throwException1 = false;
        try {
            tokenManager.generate(null, 0, 0);
        } catch (TokenException e) {
            throwException1 = true;
        }
        Assert.assertTrue(throwException1);


        //====case 2=========
        /**
         * 正常生成Token
         */
        TestTokenInfo testTokenInfo2 = new TestTokenInfo();
        testTokenInfo2.setId(UUID.randomUUID().toString());
        testTokenInfo2.setF4("zyy");
        String tokenString2 = tokenManager.generate(testTokenInfo2);
        System.out.println(tokenString2);
        Assert.assertTrue(tokenString2 != null && tokenString2.length() > 0);

    }

    @Test
    public void testValidate(){
        TestTokenInfo testTokenInfo2 = new TestTokenInfo();
        testTokenInfo2.setId(UUID.randomUUID().toString());
        testTokenInfo2.setF4("zyy");
        String tokenString2 = tokenManager.generate(testTokenInfo2);
        System.out.println(tokenString2);
        Assert.assertTrue(tokenString2 != null && tokenString2.length() > 0);

        TestTokenInfo returnTokenInfo = tokenManager.validate(tokenString2, TestTokenInfo.class);
        System.out.println(returnTokenInfo);
    }

    @Test
    public void testInValid(){

    }
}
