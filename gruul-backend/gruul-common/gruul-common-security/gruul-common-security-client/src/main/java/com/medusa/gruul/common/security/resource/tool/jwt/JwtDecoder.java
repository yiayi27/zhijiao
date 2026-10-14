package com.medusa.gruul.common.security.resource.tool.jwt;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.core.type.TypeReference;
import com.medusa.gruul.common.security.model.bean.DecodeToken;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.model.enums.TokenType;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.AbstractDeserializer;
import lombok.extern.slf4j.Slf4j;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.Reader;
import java.util.Base64;
import java.util.Map;

/**
 * jwt token 解码器
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Slf4j
public final class JwtDecoder {

    private final JwtParser parser;
    private final ClientJwtSecretKey clientSecretKey;

    public JwtDecoder(ClientJwtSecretKey clientSecretKey) {
        this.clientSecretKey = clientSecretKey;
        this.parser = Jwts.parser()
                .json(new AbstractDeserializer<>() {
                    @Override
                    protected Map<String, ?> doDeserialize(Reader reader) throws Exception {
                        return ISecurity.ExtraRef.S_MAPPER.readValue(reader, new TypeReference<>() {
                        });
                    }
                })
                .verifyWith(clientSecretKey.verifyKey())
                .build();
    }

    /**
     * 移除 Bearer 前缀 如果存在的话
     *
     * @param input token
     * @return 不存在Bearer前缀的 token
     */
    private static String removeBearerPrefix(String input) {
        if (input == null || input.length() < 7 || !input.startsWith("Bearer")) {
            return input;
        }
        // 检查Bearer后的第一个字符是否为空格
        if (input.charAt(6) != ' ') {
            return input;
        }
        // 移除Bearer+空格并清理后续空格
        return input.substring(7);
    }

    /**
     * 解码 token
     *
     * @param tokenType token类型
     * @param token     token
     * @return Tuple2<Boolean, Claims> 1.是否已过期 2.解码后的token 信息
     */
    private Tuple2<Boolean, Claims> parse(TokenType tokenType, String token) {
        try {
            return Tuples.of(Boolean.FALSE, (Claims) (parser.parse(token).getPayload()));
        } catch (ExpiredJwtException exception) {
            Claims claims = exception.getClaims();
            return Tuples.of(Boolean.TRUE, claims);
        } catch (Exception exception) {
            log.debug("jwt parse error", exception);
            throw SecurityException.of(tokenType.getInvalid().exception());
        }
    }

    /**
     * 解码 token
     *
     * @param tokenType token类型
     * @param token     token
     * @return DecodeToken 解码后的token 信息
     */
    public DecodeToken decode(TokenType tokenType, String token) {
        Tuple2<Boolean, Claims> claimsTuple = parse(tokenType, token);
        Claims claims = claimsTuple.getT2();
        TokenType currentType = TokenType.valueOf(claims.get(TokenType.TYPE, String.class));
        if (currentType != tokenType) {
            throw SecurityException.of(SecureCodes.REQUEST_INVALID.exception());
        }
        SecureUser<?> user;
        try {
            user = ISecurity.toUser(
                    clientSecretKey.payload()
                            .decrypt(
                                    Base64.getDecoder().decode(
                                            claims.get(TokenType.KEY, String.class)
                                    )
                            )
            );
        } catch (Exception exception) {
            log.error("payload decode error", exception);
            throw SecurityException.of(tokenType.getInvalid().exception());
        }
        user.setToken(token);
        return new DecodeToken()
                .setExpired(claimsTuple.getT1())
                .setTokenId(claims.getId())
                .setTokenType(currentType)
                .setSecureUser(user)
                .setIssuedAt(DateTime.of(claims.getIssuedAt()).toLocalDateTime())
                .setExpireAt(DateTime.of(claims.getExpiration()).toLocalDateTime());
    }

    /**
     * token 转换为认证信息
     *
     * @param token token
     * @return SecureUserAuthentication 认证信息
     */
    public SecureUserAuthentication authentication(String token) {
        return authentication(token, false);
    }

    /**
     * token 转换为认证信息
     *
     * @param token  token
     * @param logout 是否是登出请求 如果是登出请求 则不校验 token 是否过期
     * @return SecureUserAuthentication 认证信息
     */
    public SecureUserAuthentication authentication(String token, boolean logout) {
        token = removeBearerPrefix(token);
        DecodeToken decode = this.decode(TokenType.T, token);
        if (decode.isExpired() && !logout) {
            throw SecurityException.of(SecureCodes.TOKEN_EXPIRED.exception());
        }
        SecureUser<?> secureUser = decode.getSecureUser();
        String tokenId = decode.getTokenId();
        return new SecureUserAuthentication(tokenId, secureUser);
    }

}
