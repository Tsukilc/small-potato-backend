package org.tsukilc.authservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tsukilc.common.exception.BusinessException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    /**
     * 密钥
     */
    @Value("${jwt.secret:tsukilcSecretKey12345678901234567fsalkfjaslfkasjflkasjflkasjfsdjkhgakghrdsiaghnrbntrieaiuyviouaemjkldsunbhreiousmvfoiaseurtniobhuyeyboniusemjvitgutryibonasbjetrgvoeysl;eyhjnk;mdsjefsnefimslkmjelnjeftgklrsdedsr890}")
    private String secret;

    /**
     * 有效期（毫秒）
     */
    @Value("${jwt.expiration:86400000000}")
    private long expiration;

    /**
     * 生成JWT
     *
     * @param userId 用户ID
     * @return JWT令牌
     */
    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从JWT令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        try {
            // 去除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            
            return claims.getBody().getSubject();
        } catch (Exception e) {
            throw new BusinessException(401, "无效的令牌: " + e.getMessage());
        }
    }

    /**
     * 验证JWT令牌是否有效
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            // 去除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
                    
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 