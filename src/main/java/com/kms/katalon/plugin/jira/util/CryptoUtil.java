package com.kms.katalon.plugin.jira.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.DatatypeConverter;

public final class CryptoUtil {
    private static final String DF_ALGORITHM = "PBEwithSHA1AndDESede";

    private static final String DF_SALT = "K@tal0n STudlO";

    private static final String DF_SECRET_KEY = "S3cReT K3i";
    
    private static final int DF_ITERATION = 20;
    
    private static final String DF_ENCODING = "UTF-8";

    private CryptoUtil() {
        // Disable default constructor
    }

    public static class CrytoInfo {
        private String data;

        private String algorithm;

        private byte[] salt; // public key

        private String privateKey;

        private int iteration = DF_ITERATION;

        private String encode = DF_ENCODING;
    }

    public static CrytoInfo getDefault(String data) {
        return create(DF_ALGORITHM, data, DF_SALT.getBytes(), DF_SECRET_KEY);
    }

    public static CrytoInfo getDefault(String salt, String data) {
        return create(DF_ALGORITHM, data, salt.getBytes(), DF_SECRET_KEY);
    }

    public static CrytoInfo create(String algorithm, String data, byte[] salt, String privateKey) {
        CrytoInfo cryptoInfo = new CrytoInfo();
        cryptoInfo.algorithm = algorithm;
        cryptoInfo.data = data;
        cryptoInfo.salt = salt;
        cryptoInfo.privateKey = privateKey;
        return cryptoInfo;
    }

    public static CrytoInfo create(String algorithmn, String data, byte[] salt, String privateKey, int iteration) {
        CrytoInfo cryptoInfo = create(algorithmn, data, salt, privateKey);
        cryptoInfo.iteration = iteration;
        return cryptoInfo;
    }

    public static CrytoInfo create(String algorithmn, String data, byte[] salt, String privateKey, int iteration,
            String encode) {
        CrytoInfo cryptoInfo = create(algorithmn, data, salt, privateKey, iteration);
        cryptoInfo.encode = encode;
        return cryptoInfo;
    }

    public static String encode(CrytoInfo cryptoInfo) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(cryptoInfo.algorithm);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(cryptoInfo.privateKey.toCharArray()));
        Cipher pbeCipher = Cipher.getInstance(cryptoInfo.algorithm);
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(cryptoInfo.salt, cryptoInfo.iteration));
        return DatatypeConverter.printBase64Binary(pbeCipher.doFinal(cryptoInfo.data.getBytes(cryptoInfo.encode)));
    }

    public static String decode(CrytoInfo cryptoInfo) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(cryptoInfo.algorithm);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(cryptoInfo.privateKey.toCharArray()));
        Cipher pbeCipher = Cipher.getInstance(cryptoInfo.algorithm);
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(cryptoInfo.salt, cryptoInfo.iteration));
        return new String(pbeCipher.doFinal(DatatypeConverter.parseBase64Binary((cryptoInfo.data))), cryptoInfo.encode);
    }
}
