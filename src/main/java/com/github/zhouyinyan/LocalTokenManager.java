package com.github.zhouyinyan;

/**
 * Created by zhouyinyan on 2018/4/16.
 * 本地(inJVM)实现，无中心化服务，无状态
 * 不支持token失效。
 */
public class LocalTokenManager<T> extends AbstractTokenManager<T> {

    @Override
    protected void postProcess(TokenContext tokenContext) throws TokenException {

    }

    @Override
    protected void preProcess(TokenContext tokenContext) {

    }

    @Override
    public boolean inValid(String tokenString) throws TokenException {
        throw new TokenException("LocalTokenManager not support invalid");
    }
}
