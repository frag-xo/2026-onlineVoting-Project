package org.mjc.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成 token
     *
     * @param userId 用户ID
     * @param uname 用户名
     * @param utype 用户角色
     * @return token
     */
    public String generateToken(Long userId, String uname, String utype) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("uname", uname);
        claims.put("utype", utype);

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析 token
     *
     * @param token token
     * @return Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 token 中获取用户ID
     *
     * @param token token
     * @return 用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 token 中获取用户名
     *
     * @param token token
     * @return 用户名
     */
    public String getUname(String token) {
        Claims claims = parseToken(token);
        return claims.get("uname", String.class);
    }

    /**
     * 从 token 中获取用户角色
     *
     * @param token token
     * @return 用户角色
     */
    public String getUtype(String token) {
        Claims claims = parseToken(token);
        return claims.get("utype", String.class);
    }

    /**
     * 验证 token 是否有效
     *
     * @param token token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
