package com.github.zhouyinyan;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by zhouyinyan on 2018/4/17.
 * 注意T中的field只支持 原始类型，包装类型和String类型。
 */
public abstract class AbstractTokenManager<T> implements TokenManager<T> {

    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //=================generate end=====================

    @Override
    public String generate(T t, long createTimestamp, long expireTimestamp) throws TokenException {
        TokenContext<T> tokenContext = new TokenContext.TokenBuilder<T>()
                .createTimestamp(createTimestamp)
                .expireIn(expireTimestamp)
                .wrap(t)
                .build();

        //反射获取T中的filed,并通过其name排序,缓存, 如果包含不支持的类型，直接忽略。
        List<Field> fields = ReflectUtils.getAllFeildsIgoreNotSupportTypesAndIgoreAnno(t.getClass());

        //遍历获取field的value，处理特殊值（处理null值，处理自定义注解等）
        List<String> values = ReflectUtils.fetchValuesAndProcessSpecialValue(fields, t);

        //使用分隔符将原始的value以及过期时间合并为原始信息串（按照name的排序），并唯一化
        String originalString  = concatWithDelimiter(values)
                                .append(tokenContext.getExpireTimestamp())
                                .append(TokenConstants.DELIMITER)
                                .append(UUID.randomUUID().toString())
                                .toString();

        //对原始信息串加密并编码
        String tokenString = encryptAndCode(originalString);
        tokenContext.setTokenString(tokenString);

        //后置处理(用于基于中心化存储的方式实现时，保存到存储中)
        genPostProcess(tokenContext);

        return tokenContext.getTokenString();
    }

    private String encryptAndCode(String originalString) {
        try {
            byte[] encryptData = AESUtil.encrypt(originalString.getBytes(), AESUtil.getContantKey());
            String hexStr = Hex.encodeHexString(encryptData);
            return hexStr;
        } catch (Exception e) {
            throw new TokenException("Token加密异常, e " + e.getMessage());
        }
    }

    private static StringBuffer concatWithDelimiter(List<String> values){
        StringBuffer sb = new StringBuffer();
        values.stream().forEach(v -> sb.append(v).append(TokenConstants.DELIMITER));
        return sb;
    }

    @Override
    public String generate(T t, long expireTimestamp) throws TokenException {
        return generate(t, 0, expireTimestamp);
    }

    @Override
    public String generate(T t) throws TokenException {
        return generate(t, 0);
    }

    protected abstract void genPostProcess(TokenContext<T> tokenContext) throws TokenException;

    //=================generate end=====================

    @Override
    public T validate(String tokenString, Class<T> tClass) throws TokenException {
        TokenContext<T> tokenContext = new TokenContext.TokenBuilder<T>()
                                        .tokenString(tokenString)
                                        .build();
        //验证前置处理
        validatePreProcess(tokenContext);

        //解码出原始信息串
        String originalString = codecAndDecrypt(tokenContext.getTokenString());

        String[] valueArray = originalString.split(TokenConstants.DELIMITER);

        //原始信息，最后两个元素分别为过期时间戳和随机串。
        List<String> values = Arrays.stream(valueArray).collect(Collectors.toList());

        values.remove(values.size() - 1); //去除最后的随机串

        long expireTimestamp = Long.valueOf(values.remove(values.size() - 1)); //去除过期时间戳，并返回。

        //过期校验
        if(expireTimestamp < System.currentTimeMillis()) { //过期
            throw new TokenException("Token已过期，过期时间为:" + simpleDateFormat.format(new Date(expireTimestamp)));
        }

        //解析的原始信息通过反射填充到信息对象中。
        T t = ReflectUtils.fillValuesAndProcessSpecialValue(tClass, values);
        tokenContext.setT(t);
        //失效校验
        isValid(tokenContext);

        return tokenContext.getT();
    }

    protected abstract void isValid(TokenContext<T> tokenContext);

    private String codecAndDecrypt(String tokenString) {
        try {
            byte[] decryptData = AESUtil.decrypt(Hex.decodeHex(tokenString.toCharArray()), AESUtil.getContantKey());
            String originalString = new String(decryptData);
            return originalString;
        } catch (DecoderException e) {
            //伪造校验 解密失败表示伪造
            throw new TokenException("Token解密异常，Token系伪造, e " + e.getMessage());
        }
    }

    protected abstract void validatePreProcess(TokenContext<T> tokenContext);

    @Override
    public boolean inValid(String tokenString) throws TokenException {
        return false;
    }
}
