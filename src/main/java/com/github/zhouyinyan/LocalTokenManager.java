package com.github.zhouyinyan;

/**
 * Created by zhouyinyan on 2018/4/16.
 * 本地(inJVM)实现，无中心化服务，无状态
 * 不支持token失效。
 */
public class LocalTokenManager<T> extends AbstractTokenManager<T> {

    @Override
    protected void genPostProcess(TokenContext<T> tokenContext) throws TokenException {
        //nothing need to do
    }

    @Override
    protected void validatePreProcess(TokenContext<T> tokenContext) {
        //nothing need to do
    }

    @Override
    protected void isValid(TokenContext<T> tokenContext) {
        //nothing need to do
    }

    @Override
    public boolean inValid(String tokenString) throws TokenException {
        throw new TokenException("LocalTokenManager not support invalid");
    }
}
