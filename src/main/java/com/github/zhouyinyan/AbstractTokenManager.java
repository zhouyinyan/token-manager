package com.github.zhouyinyan;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhouyinyan on 2018/4/17.
 * 注意T中的field只支持 原始类型，包装类型和String类型。
 */
public abstract class AbstractTokenManager<T> implements TokenManager<T> {


    @Override
    public String generate(T t, long createTimestamp, long expireTimestamp) throws TokenException {
        TokenContext<T> tokenContext = new TokenContext.TokenBuilder<T>()
                .createTimestamp(createTimestamp)
                .expireIn(expireTimestamp)
                .wrap(t)
                .build();

        //反射获取T中的filed,并通过其name排序,缓存, 如果包含不支持的类型，直接忽略。
        List<Field> fields = ReflectUtils.getAllFeildsIgoreNotSupportTypesAndIgoreAnno(t.getClass());

        //遍历获取field的value，处理特殊值（转义其中的特殊字符"|", 该特殊字符用于分割符， 处理null值，处理自定义注解等）
        List<String> values = ReflectUtils.fetchValuesAndProcessSpecialValue(fields, t);

        //使用分隔符将原始的value以及过期时间合并为原始信息串（按照name的排序），并唯一化
        String originalString  = concatWithDelimiter(values)
                                .append(tokenContext.getExpireTimestamp())
                                .append(UUID.randomUUID().toString())
                                .toString();

        //对原始信息串加密并编码
        String tokenString = encryptAndCode(originalString);
        tokenContext.setTokenString(tokenString);

        //后置处理
        postProcess(tokenContext);

        return tokenString;
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

    protected abstract void postProcess(TokenContext<T> tokenContext) throws TokenException;

    @Override
    public T validate(String tokenString) throws TokenException {
        TokenContext<T> tokenContext = new TokenContext.TokenBuilder<T>().build();
        //前置处理
        preProcess(tokenContext);

        //解码出原始信息串

        //伪造校验

        //过期校验

        //失效校验

        return tokenContext.getT();
    }

    protected abstract void preProcess(TokenContext<T> tokenContext);

    @Override
    public boolean inValid(String tokenString) throws TokenException {
        return false;
    }
}
