package com.github.reugn.devtools.services;

import com.github.reugn.devtools.models.JwtToken;

public interface JwtService {

    String encode(JwtToken token, String algorithmName);

    JwtToken decode(String token);

    boolean verify(String token, JwtToken.Signature signature, String algorithmName);
}
