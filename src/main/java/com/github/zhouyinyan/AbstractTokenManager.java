package com.github.zhouyinyan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhouyinyan on 2018/4/17.
 * 注意T中的field只支持 原始类型，包装类型和String类型。
 */
public abstract class AbstractTokenManager<T> implements TokenManager<T> {

    public static char DELIMITER = '|';

    public List<String> fieldNames = new ArrayList<>();

    public AbstractTokenManager() {
        //反射获取T中的filed,并通过其name排序,缓存, 如果包含不支持的类型，抛出异常。

        this.fieldNames.add("f1");
        this.fieldNames.add("f2");
        this.fieldNames.add("f3");
        Collections.sort(this.fieldNames);
    }

    @Override
    public String generate(T t, long createTimestamp, long expareTimestamp) throws TokenException {
        //遍历获取field的value，处理特殊值（转义其中的特殊字符"|", 该特殊字符用于分割符， 处理null值，处理自定义注解等）

        //使用分隔符将原始的value以及过期时间合并为原始信息串（按照name的排序）

        //对原始信息串编码，并唯一化

        //后置处理
        TokenContext<T> tokenContext = TokenContext.createBuilder().build();
        postProcess(tokenContext);
        return null;
    }

    protected abstract void postProcess(TokenContext tokenContext) throws TokenException;

    @Override
    public T validate(String tokenString) throws TokenException {
        TokenContext<T> tokenContext = TokenContext.createBuilder().build();
        //前置处理
        preProcess(tokenContext);

        //解码出原始信息串

        //伪造校验

        //过期校验

        //失效校验

        return tokenContext.getT();
    }

    protected abstract void preProcess(TokenContext tokenContext);

    @Override
    public boolean inValid(String tokenString) throws TokenException {
        return false;
    }
}
