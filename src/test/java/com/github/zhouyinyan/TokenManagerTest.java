package com.github.zhouyinyan;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
         * 即使信息为空，也能正常生成token
         * 实际此时用于生成token的信息仅为到期时间
         */
        TestTokenInfo testTokenInfo2 = new TestTokenInfo();
        String tokenString2 = tokenManager.generate(testTokenInfo2);
        Assert.assertTrue(tokenString2 != null && tokenString2.length() > 0);

    }

    @Test
    public void testValidate(){

    }

    @Test
    public void testInValid(){

    }
}
