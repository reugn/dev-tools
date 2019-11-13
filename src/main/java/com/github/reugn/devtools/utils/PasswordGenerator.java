package com.github.reugn.devtools.utils;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class PasswordGenerator {
    private String dictionary;

    private static String lowerCharsString = "abcdefghijklmnopqrstuvwxyz";
    private static String digitsString = "0123456789";
    private static String upperCharsString = lowerCharsString.toUpperCase(Locale.ENGLISH);
    private static String symbolsString = "!@#$%^&*?";

    private PasswordGenerator(PasswordGeneratorBuilder builder) {
        StringBuilder buff = new StringBuilder();
        if (builder.useLower) {
            buff.append(lowerCharsString);
        }
        if (builder.useDigits) {
            buff.append(digitsString);
        }
        if (builder.useUpper) {
            buff.append(upperCharsString);
        }
        if (builder.useSymbols) {
            buff.append(symbolsString);
        }
        dictionary = buff.toString();
    }

    public String generate(int length) {
        if (dictionary.isEmpty()) return "";
        StringBuilder password = new StringBuilder(length);
        Random random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            password.append(dictionary.charAt(random.nextInt(dictionary.length())));
        }
        return password.toString();
    }

    public static class PasswordGeneratorBuilder {
        private boolean useLower;
        private boolean useUpper;
        private boolean useDigits;
        private boolean useSymbols;

        public PasswordGeneratorBuilder() {
            this.useLower = false;
            this.useUpper = false;
            this.useDigits = false;
            this.useSymbols = false;
        }

        public PasswordGeneratorBuilder withLowerChars(boolean useLower) {
            this.useLower = useLower;
            return this;
        }

        public PasswordGeneratorBuilder withUpperChars(boolean useUpper) {
            this.useUpper = useUpper;
            return this;
        }

        public PasswordGeneratorBuilder withDigits(boolean useDigits) {
            this.useDigits = useDigits;
            return this;
        }

        public PasswordGeneratorBuilder withSymbols(boolean useSymbols) {
            this.useSymbols = useSymbols;
            return this;
        }

        public PasswordGenerator build() {
            return new PasswordGenerator(this);
        }
    }
}
