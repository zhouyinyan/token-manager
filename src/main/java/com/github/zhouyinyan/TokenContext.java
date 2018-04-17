package com.github.zhouyinyan;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouyinyan on 2018/4/16.
 * Token实体
 */
public class TokenContext<T> implements Serializable {

    /**
     * 默认过期时间30分
     */
    public static long DEFAULT_EXPIRE_DURATION = TimeUnit.MINUTES.toMillis(30);

    /**
     * Token创建时间（毫秒）
     */
    private long createTimestamp;

    /**
     * Token过期时间（毫秒）
     */
    private long expireTimestamp;

    /**
     * Token串
     */
    private String tokenString;

    /**
     * 参与token的原始信息对象
     */
    private T t;

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public long getExpireTimestamp() {
        return expireTimestamp;
    }

    public void setExpireTimestamp(long expireTimestamp) {
        this.expireTimestamp = expireTimestamp;
    }

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }


    /**
     * TokenBuilder,用于构建Token实例
     */
    public static class TokenBuilder<T>{

        private long createTimestamp;

        private long expireTimestamp;

        private T t;

        //当前时间作为创建时间
        public TokenBuilder<T> createNow(){
            this.createTimestamp = System.currentTimeMillis();
            return this;
        }

        public TokenBuilder<T> createTimestamp(long createTimestamp){
            this.createTimestamp = createTimestamp;
            return this;
        }

        public TokenBuilder<T> expireIn(long expireTimestamp){
            this.expireTimestamp = expireTimestamp;
            checkExpireTime();
            return this;
        }

        public TokenBuilder<T> expireAfter(TimeUnit unit, long value){
            checkCreateTime();
            this.expireTimestamp = this.createTimestamp + unit.toMillis(value);
            return this;
        }

        public TokenBuilder<T> wrap(T t){
            if(Objects.isNull(t)){
                throw new TokenException("参与token的原始信息对象不能为null");
            }
            this.t = t;
            return this;
        }

        public TokenContext<T> build(){
            checkCreateTime();
            checkExpireTime();
            TokenContext tokenContext = new TokenContext();
            tokenContext.setCreateTimestamp(createTimestamp);
            tokenContext.setExpireTimestamp(expireTimestamp);
            tokenContext.setT(t);
            return tokenContext;
        }

        private void checkCreateTime() {
            if(this.createTimestamp <= 0){
                createNow();
            }
        }

        private void checkExpireTime() {
            if(this.expireTimestamp <= 0){
                expireIn(this.expireTimestamp + DEFAULT_EXPIRE_DURATION);
            }
        }
    }
}
