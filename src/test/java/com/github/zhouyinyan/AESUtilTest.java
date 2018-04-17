package com.github.zhouyinyan;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zhouyinyan on 2018/4/17.
 */
public class AESUtilTest {

    @Test
    public  void test() throws Exception {
        String data ="AES数据";
        System.out.println("加密前数据: string:"+data);
        System.out.println();

        byte[] key = DigestUtils.getMd5Digest().digest(TokenConstants.AESKEY.getBytes());
        System.out.println(Hex.encodeHexString(key));

        byte[] encryptData = AESUtil.encrypt(data.getBytes(), key);
        String hexStr = Hex.encodeHexString(encryptData);

        System.out.println("加密后数据: hexStr:"+ hexStr);
        System.out.println();


        byte[] decryptData = AESUtil.decrypt(Hex.decodeHex(hexStr.toCharArray()),key);
        System.out.println("解密后数据: string:"+new String(decryptData));

        Assert.assertEquals(data, new String(decryptData));
    }
}
