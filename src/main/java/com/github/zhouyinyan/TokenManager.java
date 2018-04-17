package com.github.zhouyinyan;

/**
 * Created by zhouyinyan on 2018/4/16.
 * Token管理的核心接口
 */
public interface TokenManager<T> {

    /**
     * 生成Token
     * @param t 参与Token生成的信息对象（比如用户ID）
     * @param createTimestamp 创建时间戳
     * @param expireTimestamp 过期时间戳
     * @return 创建成功后返回Token实例
     * @throws TokenException 创建失败时，抛出异常。
     */
    String generate(T t, long createTimestamp, long expireTimestamp) throws TokenException;

    /**
     * 生成Token， 创建默认为当前时间
     * @param t 参与Token生成的信息对象（比如用户ID）
     * @param expireTimestamp 过期时间戳
     * @return 创建成功后返回Token实例
     * @throws TokenException 创建失败时，抛出异常。
     */
    String generate(T t, long expireTimestamp) throws TokenException;

    /**
     * 生成Token， 创建默认为当前时间， 过期时间为默认过期时间
     * @param t 参与Token生成的信息对象（比如用户ID）
     * @return 创建成功后返回Token实例
     * @throws TokenException 创建失败时，抛出异常。
     */
    String generate(T t) throws TokenException;

    /**
     * 校验Token
     * @param tokenString 被校验的Token
     * @return token携带的信息对象
     * @throws TokenException
     */
    T validate(String tokenString) throws TokenException;

    /**
     * Token失效，用于一些场景下，Token只能使用一次，使用过后就需要失效
     * @param tokenString 被失效的Token
     * @return true 成功失效
     * @throws TokenException 失效失败时，抛出异常
     */
    boolean inValid(String tokenString) throws TokenException;
}
