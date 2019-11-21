package com.microsoft.onlineid.sts;

import java.security.SecureRandom;

public class DeviceCredentialGenerator {
    static final String LegalPasswordCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-_=+[]{}/?;:'\",.<>`~";
    static final String LegalUsernameCharacters = "abcdefghijklmnopqrstuvwxyz";
    static final String LogicalDevicePrefix = "02";
    static final int PasswordLength = 16;
    static final int UsernameLength = 20;
    private final SecureRandom _randomNumberGenerator;

    public DeviceCredentialGenerator() {
        this._randomNumberGenerator = new SecureRandom();
    }

    DeviceCredentialGenerator(SecureRandom random) {
        this._randomNumberGenerator = random;
    }

    public DeviceCredentials generate() {
        return new DeviceCredentials(LogicalDevicePrefix + generateRandomString(LegalUsernameCharacters, 20 - LogicalDevicePrefix.length()), generateRandomString(LegalPasswordCharacters, PasswordLength));
    }

    private String generateRandomString(String legalCharacters, int length) {
        char[] output = new char[length];
        int legalCharCount = legalCharacters.length();
        for (int i = 0; i < output.length; i++) {
            output[i] = legalCharacters.charAt(this._randomNumberGenerator.nextInt(legalCharCount));
        }
        return new String(output);
    }
}
