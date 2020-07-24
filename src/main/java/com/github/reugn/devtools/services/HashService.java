package com.github.reugn.devtools.services;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashService {

    private HashService() {
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String calculateHash(String data, String algo) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algo);
        byte[] enc = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return HashCode.fromBytes(enc).toString();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String murmur3_128(String data) {
        return Hashing.murmur3_128().hashBytes(data.getBytes()).toString();
    }

    public static String urlEncode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replace("+", "%20");
    }

    public static String urlDecode(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
    }

    public static String base64Encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String base64Decode(String value) {
        return new String(Base64.getDecoder().decode(value.getBytes(StandardCharsets.UTF_8)));
    }
}
