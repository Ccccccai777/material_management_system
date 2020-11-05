package com.lda.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lda.jwt.JWTToken;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class JWTUtils {
    /**
     * 过期时间3天
     */
    private static  final  long EXPIRE_TIME=60*60*72*1000;

    /**
     * 校验token是否正确
     * @param token 密钥
     * @param secret 用户的密码
     * @return 是否正确
     */

    public static  Boolean verify(String token,String username,String secret){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verify = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
              verify.verify(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    /**
     * 生成签名,6小时后过期
     * @param username 用户名
     * @param secret 用户的密码
     * @return 加密的token
     */
     public static  String sign(String username,String secret){

         try {
             Date date = new Date(System.currentTimeMillis()+EXPIRE_TIME);
             Algorithm algorithm = Algorithm.HMAC256(secret);
             // 附带username信息
             return JWT.create()
                     .withClaim("username", username)
                     .withExpiresAt(date)
                     .sign(algorithm);
         } catch (Exception e) {
             return null;
         }

     }
    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     * @param token
     */
    public static  String getUsername(String token){
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();

        } catch (JWTDecodeException e) {
            return null;
        }
    }
    /**
     * 判断过期
     * @param token
     * @return
     */
    public static  Boolean isExpire(String token) {

        DecodedJWT jwt = JWT.decode(token);
        return System.currentTimeMillis() > jwt.getExpiresAt().getTime();
    }
}
