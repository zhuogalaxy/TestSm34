package com.zjb.sm4;

import com.zjb.utils.Util;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class SM4Utils
{
    private String secretKey;
    private String iv;
    private boolean hexString;

    public SM4Utils() {
        this.secretKey = "";
        this.iv = "";
        this.hexString = false;
    }

    public String encryptData_ECB(final String plainText) {
        try {
            final SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = 1;
            byte[] keyBytes;
            if (this.hexString) {
                keyBytes = Util.hexStringToBytes(this.secretKey);
            }
            else {
                keyBytes = this.secretKey.getBytes();
            }
            final SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            final byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText.getBytes("GBK"));
            String cipherText = Base64.getEncoder().encodeToString(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0) {
                final Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                final Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptData_ECB(final String cipherText) {
        try {
            final SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = 0;
            byte[] keyBytes;
            if (this.hexString) {
                keyBytes = Util.hexStringToBytes(this.secretKey);
            }
            else {
                keyBytes = this.secretKey.getBytes();
            }
            final SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            final byte[] decrypted = sm4.sm4_crypt_ecb(ctx, Base64.getDecoder().decode(cipherText));
            return new String(decrypted, "GBK");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encryptData_CBC(final String plainText) {
        try {
            final SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = 1;
            byte[] keyBytes;
            byte[] ivBytes;
            if (this.hexString) {
                keyBytes = Util.hexStringToBytes(this.secretKey);
                ivBytes = Util.hexStringToBytes(this.iv);
            }
            else {
                keyBytes = this.secretKey.getBytes();
                ivBytes = this.iv.getBytes();
            }
            final SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            final byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, plainText.getBytes("GBK"));
            String cipherText = Base64.getEncoder().encodeToString(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0) {
                final Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                final Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptData_CBC(final String cipherText) {
        try {
            final SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = 0;
            byte[] keyBytes;
            byte[] ivBytes;
            if (this.hexString) {
                keyBytes = Util.hexStringToBytes(this.secretKey);
                ivBytes = Util.hexStringToBytes(this.iv);
            }
            else {
                keyBytes = this.secretKey.getBytes();
                ivBytes = this.iv.getBytes();
            }
            final SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            final byte[] decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, Base64.getDecoder().decode(cipherText));
            return new String(decrypted, "GBK");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }

    public String getIv() {
        return this.iv;
    }

    public void setIv(final String iv) {
        this.iv = iv;
    }

    public boolean isHexString() {
        return this.hexString;
    }

    public void setHexString(final boolean hexString) {
        this.hexString = hexString;
    }

    public static void main(final String[] args) throws IOException {
        String plainText = "abcd";
        final SM4Utils sm4 = new SM4Utils();
        sm4.secretKey = "JeF8U9wHFOMfs2Y8";
        sm4.hexString = false;
        System.out.println("ECB\u6a21\u5f0f");
        String cipherText = sm4.encryptData_ECB(plainText);
        System.out.println("\u5bc6\u6587: " + cipherText);
        System.out.println("");
        plainText = sm4.decryptData_ECB(cipherText);
        System.out.println("\u660e\u6587: " + plainText);
        System.out.println("");
        System.out.println("CBC\u6a21\u5f0f");
        sm4.iv = "UISwD9fW6cFh9SNS";
        cipherText = sm4.encryptData_CBC(plainText);
        System.out.println("\u5bc6\u6587: " + cipherText);
        System.out.println("");
        plainText = sm4.decryptData_CBC(cipherText);
        System.out.println("\u660e\u6587: " + plainText);
    }
}
