package com.tensquare.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.text.SimpleDateFormat;

/**
 * 解析token测试
 */
public class ParseJwtTest {
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMzk1MTExMzMzOCIsInN1YiI6IuWtlOeQqiIsImlhdCI6MTU0MzEzNDI3NSwiZXhwIjoxNTQzMTM0MzM1LCJyb2xlIjoiYWRtaW4ifQ.X3g7lkbPv0c5VFXIGIA80ORKnavoKsIkYuI3Psuw0Mc";
        Claims claims = Jwts.parser().setSigningKey("kongqi").parseClaimsJws(token).getBody();
        System.out.println("用户id:" + claims.getId());
        System.out.println("用户名subject:" + claims.getSubject());
        System.out.println("登陆时间IssuedAt:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(claims.getIssuedAt()));
        System.out.println("过期时间setExpiration:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(claims.getExpiration()));
        System.out.println("用户角色role:"+claims.get("role"));
    }
}