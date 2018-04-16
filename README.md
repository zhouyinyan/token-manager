# Token生成和校验框架

可应用的场景：

1. CSRF（跨域请求位置）防止
2. 接口防刷
3. Session管理
4. 其他类似用途

其主要用于系统边界的交互安全，阻止不允许的接口调用。



## 核心接口

- TokenManager接口

  - Token generateToken(Map<String,String> paramter, long currentTimeStamp, long expaireTimeStamp) throw TokenExecption;

      生成Token，其参数为参与Token编码的字段。currentTimeStamp为创建时间，expaireTimeStamp为到期时间。

  - Map<String, String> validateToken(Token token) throw TokenException;

    验证Token，返回Token编码的信息

  - boolean  isValid(Token token) throw TokenException;

    校验Token是否有效。

  - boolean inValid(Token token) throw TokenException;

    让Token失效。

##  实现

- LocalTokenManager， 本地实现（采用对称加密算法来保证token不能伪造），无内部状态，不依赖中心化服务(比如db，redis等)。 但其不支持token失效处理。
- JDBCTokenManager， 依赖于db的实现。
- RedisTokenManager,  依赖于Redis的实现。