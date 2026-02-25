package com.ebuffet.utils.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.ebuffet.models.User;
import com.ebuffet.models.enums.EnumUserRole;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final String jwtSecret = "secret_key";
    private final long jwtExpirationMs = 86400000;

    private final Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

    public String generateToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        String role = user.getRole() != null ? user.getRole().name() : null;
        Long buffetId = user.getBuffetId() != null ? user.getBuffetId() : null;

        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("role", role)
                .withClaim("buffetId", buffetId)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    public String getUsernameFromToken(String token) {
        return decodeToken(token).getSubject();
    }

    public Long getBuffetIdFromToken(String token) {
        try {
            return decodeToken(token).getClaim("buffetId").asLong();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            decodeToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private DecodedJWT decodeToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public Set<EnumUserRole> getRoles(String token) {
        String[] arr = decodeToken(token).getClaim("roles").asArray(String.class);
        if (arr == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(arr)
                .map(EnumUserRole::valueOf)
                .collect(Collectors.toSet());
    }
}
