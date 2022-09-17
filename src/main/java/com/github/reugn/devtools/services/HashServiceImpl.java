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

public class HashServiceImpl implements HashService {

    @Override
    public String calculateHash(String data, String algo) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algo);
        byte[] enc = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return HashCode.fromBytes(enc).toString();
    }

    @Override
    public String murmur3_128(String data) {
        return Hashing.murmur3_128().hashBytes(data.getBytes()).toString();
    }

    @Override
    public String urlEncode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replace("+", "%20");
    }

    @Override
    public String urlDecode(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
    }

    @Override
    public String base64Encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String base64Decode(String value) {
        return new String(Base64.getDecoder().decode(value.getBytes(StandardCharsets.UTF_8)));
    }
}
