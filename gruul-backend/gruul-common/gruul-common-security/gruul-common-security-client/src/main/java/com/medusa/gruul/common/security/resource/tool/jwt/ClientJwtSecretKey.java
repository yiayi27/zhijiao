package com.medusa.gruul.common.security.resource.tool.jwt;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

import java.security.PublicKey;
import java.util.Base64;

/**
 * 认证客户端jwt秘钥数据
 *
 * @author 张治保
 * @since 2023/11/30
 */
public final class ClientJwtSecretKey implements IClientSecretKey {

    /**
     * jwt  验签算法公钥
     */
    private final PublicKey publicKey;

    /**
     * jwt payload 加密算法
     */
    private final AES payloadSecret;

    public ClientJwtSecretKey(String publicKey, String payloadKey) {
        this.publicKey = publicKey(publicKey);
        this.payloadSecret = aesFromKey(payloadKey);
    }


    /**
     * 生成公钥，仅用于非对称加密<br>
     * <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory">
     * 算法见
     * </a>
     *
     * @param base64Key base64公钥密钥
     * @return 公钥 {@link PublicKey}
     */
    private static PublicKey publicKey(String base64Key) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        return SecureUtil.generatePublicKey("EC", key);
    }

    /**
     * aes 加密
     *
     * @param base64Key base64Key
     * @return AES
     */
    private static AES aesFromKey(String base64Key) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        return SecureUtil.aes(key);
    }


    @Override
    public PublicKey verifyKey() {
        return publicKey;
    }

    @Override
    public AES payload() {
        return payloadSecret;
    }
}