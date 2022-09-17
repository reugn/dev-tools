package com.github.reugn.devtools.services;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface HashService {

    String calculateHash(String data, String algo) throws NoSuchAlgorithmException;

    String murmur3_128(String data);

    String urlEncode(String value) throws UnsupportedEncodingException;

    String urlDecode(String value) throws UnsupportedEncodingException;

    String base64Encode(String value);

    String base64Decode(String value);
}
