package com.github.zhouyinyan;

import com.sun.org.apache.regexp.internal.RE;

import java.lang.reflect.Field;
import java.sql.Ref;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

        //使用分隔符将原始的value以及过期时间合并为原始信息串（按照name的排序）
        StringBuffer originalString  = ReflectUtils.concatWithDelimiter(values);
        originalString.append(expireTimestamp);

        //对原始信息串编码，并唯一化


        //后置处理

        postProcess(tokenContext);
        return null;
    }

    protected void specialValueProcess(String[] values){
        //如果
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
