package com.github.reugn.devtools.services;

import java.security.NoSuchAlgorithmException;

public interface HashService {

    String calculateHash(String data, String algo) throws NoSuchAlgorithmException;

    String murmur3_128(String data);

    String urlEncode(String value);

    String urlDecode(String value);

    String base64Encode(String value);

    String base64Decode(String value);
}
