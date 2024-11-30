package com.github.reugn.devtools.models;

public class JwtToken {
    public static final String HS256 = "HS256";
    public static final String HS384 = "HS384";
    public static final String HS512 = "HS512";

    public static final String RS256 = "RS256";
    public static final String RS384 = "RS384";
    public static final String RS512 = "RS512";

    public static final String ES256 = "ES256";
    public static final String ES384 = "ES384";
    public static final String ES512 = "ES512";

    private final String header;
    private final String payload;
    private final Signature signature;

    public JwtToken(String header, String payload, Signature signature) {
        this.header = header;
        this.payload = payload;
        this.signature = signature;
    }

    public String getHeader() {
        return header;
    }

    public String getPayload() {
        return payload;
    }

    public Signature getSignature() {
        return signature;
    }

    public static class Signature {
        private final String secret;
        private final String publicKey;
        private final String privateKey;

        private Signature(Builder builder) {
            this.secret = builder.secret;
            this.publicKey = builder.publicKey;
            this.privateKey = builder.privateKey;
        }

        public static Builder create() {
            return new Builder();
        }

        public String getSecret() {
            return secret;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public static class Builder {
            private String secret;
            private String publicKey;
            private String privateKey;

            public Builder withSecret(String secret) {
                this.secret = secret;
                return this;
            }

            public Builder withPublicKey(String publicKey) {
                this.publicKey = publicKey;
                return this;
            }

            public Builder withPrivateKey(String privateKey) {
                this.privateKey = privateKey;
                return this;
            }

            public Signature build() {
                return new Signature(this);
            }
        }
    }
}
