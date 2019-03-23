package com.tensquare.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * 生成token测试
 */
public class CreateJwtTest {

    public static void main(String[] args) {

        // setIssuedAt用于设置签发时间
        // signWith用于设置签名秘钥
        // setExpiration用于设置过期时间

        JwtBuilder builder = Jwts.builder().setId("13951113338")
                .setSubject("孔琪")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "kongqi")
                .setExpiration(new Date(new Date().getTime()+60*1000))
                .claim("role","admin");
        System.out.println("生成token为："+builder.compact());
    }

}
