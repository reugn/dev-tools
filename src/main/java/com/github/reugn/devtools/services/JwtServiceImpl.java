package com.github.reugn.devtools.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.reugn.devtools.models.JwtToken;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.github.reugn.devtools.models.JwtToken.*;
import static java.lang.String.format;

public class JwtServiceImpl implements JwtService {

    private static final String RSA = "RSA";
    private static final String EC = "EC";
    private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";
    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";

    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public String encode(JwtToken token, String algorithmName) {
        try {
            Algorithm algorithm = getAlgorithm(algorithmName, token.getSignature());
            return JWT.create().withHeader(token.getHeader()).withPayload(token.getPayload()).sign(algorithm);
        } catch (Exception e) {
            throw new JWTCreationException(e.getMessage(), e);
        }
    }

    @Override
    public JwtToken decode(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return new JwtToken(urlDecode(decoded.getHeader()), urlDecode(decoded.getPayload()), null);
    }

    @Override
    public boolean verify(String token, JwtToken.Signature signature, String algorithmName) {
        try {
            JWT.require(getAlgorithm(algorithmName, signature)).build().verify(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private String urlDecode(String data) {
        try {
            byte[] json = Base64.getUrlDecoder().decode(data);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(json, Object.class));
        } catch (Exception e) {
            throw new JWTDecodeException(e.getMessage());
        }
    }

    private Algorithm getAlgorithm(String algorithmName, JwtToken.Signature signature)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        switch (algorithmName) {
            case HS256:
                return Algorithm.HMAC256(signature.getSecret());
            case HS384:
                return Algorithm.HMAC384(signature.getSecret());
            case HS512:
                return Algorithm.HMAC512(signature.getSecret());

            case RS256:
                return Algorithm.RSA256((RSAPublicKey) getPublicKey(signature.getPublicKey(), RSA),
                        (RSAPrivateKey) getPrivateKey(signature.getPrivateKey(), RSA));
            case RS384:
                return Algorithm.RSA384((RSAPublicKey) getPublicKey(signature.getPublicKey(), RSA),
                        (RSAPrivateKey) getPrivateKey(signature.getPrivateKey(), RSA));
            case RS512:
                return Algorithm.RSA512((RSAPublicKey) getPublicKey(signature.getPublicKey(), RSA),
                        (RSAPrivateKey) getPrivateKey(signature.getPrivateKey(), RSA));

            case ES256:
                return Algorithm.ECDSA256((ECPublicKey) getPublicKey(signature.getPublicKey(), EC),
                        (ECPrivateKey) getPrivateKey(signature.getPrivateKey(), EC));
            case ES384:
                return Algorithm.ECDSA384((ECPublicKey) getPublicKey(signature.getPublicKey(), EC),
                        (ECPrivateKey) getPrivateKey(signature.getPrivateKey(), EC));
            case ES512:
                return Algorithm.ECDSA512((ECPublicKey) getPublicKey(signature.getPublicKey(), EC),
                        (ECPrivateKey) getPrivateKey(signature.getPrivateKey(), EC));

            default:
                throw new IllegalArgumentException(format("Unsupported JWT algorithm: %s", algorithmName));
        }
    }

    private PublicKey getPublicKey(String publicKeyPem, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!publicKeyPem.startsWith(PUBLIC_KEY_HEADER) || !publicKeyPem.endsWith(PUBLIC_KEY_FOOTER)) {
            throw new IllegalArgumentException("Invalid public key format");
        }

        String encoded = publicKeyPem.replace(PUBLIC_KEY_HEADER, "")
                .replace(PUBLIC_KEY_FOOTER, "").replace("\n", "").trim();

        byte[] decoded = Base64.getDecoder().decode(encoded);

        KeyFactory kf = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);

        return kf.generatePublic(keySpec);
    }


    private PrivateKey getPrivateKey(String privateKeyPem, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!privateKeyPem.startsWith(PRIVATE_KEY_HEADER) || !privateKeyPem.endsWith(PRIVATE_KEY_FOOTER)) {
            throw new IllegalArgumentException("Invalid private key format");
        }

        String encoded = privateKeyPem.replace(PRIVATE_KEY_HEADER, "")
                .replace(PRIVATE_KEY_FOOTER, "").replace("\n", "").trim();

        byte[] decoded = Base64.getDecoder().decode(encoded);

        KeyFactory kf = KeyFactory.getInstance(algorithm);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

        return kf.generatePrivate(keySpec);
    }
}
