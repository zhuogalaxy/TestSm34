package com.zjb;

import com.zjb.sm3.SM3Digest;
import com.zjb.sm4.SM4;
import com.zjb.sm4.SM4_Context;

import javax.xml.bind.DatatypeConverter;

public class Testing {
    //private static final com.zjb.sm4.SM4 SM4 = ;

    public static void main(String[] args) throws Exception {
        //Testing.testSm4();
        Testing.testSm3();
    }

    public static void testSm4() throws Exception {

        byte[] keyBytes = { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef, (byte) 0xfe,
                (byte) 0xdc, (byte) 0xba, (byte) 0x98, (byte) 0x76, (byte) 0x54, (byte) 0x32, (byte) 0x10 };
        byte[] plainBytes = { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef, (byte) 0xfe,
                (byte) 0xdc, (byte) 0xba, (byte) 0x98, (byte) 0x76, (byte) 0x54, (byte) 0x32, (byte) 0x10 };

        //keyBytes 密钥？怎么获得？
        //plainBytes  原文？
        //encrypted 密文？


        // SM4 ECB模式加密
        SM4_Context ctx_enc = new SM4_Context();
        ctx_enc.isPadding = true;
        ctx_enc.mode = SM4.SM4_ENCRYPT;
        SM4 sm4_enc = new SM4();
        sm4_enc.sm4_setkey_enc(ctx_enc, keyBytes);
        byte[] encrypted = sm4_enc.sm4_crypt_ecb(ctx_enc, plainBytes);

        System.out.printf("    key: 0x%s\n", DatatypeConverter.printHexBinary(keyBytes));
        System.out.printf("    plain: 0x%s\n", DatatypeConverter.printHexBinary(plainBytes));
        System.out.printf("    encrypt: 0x%s\n", DatatypeConverter.printHexBinary(encrypted));

        // SM4 ECB模式解密
        System.out.println("******SM4 ECB模式解密******");
        SM4_Context ctx_dec = new SM4_Context();
        ctx_dec.isPadding = true;
        ctx_dec.mode = SM4.SM4_DECRYPT;
        SM4 sm4_dec = new SM4();
        sm4_dec.sm4_setkey_dec(ctx_dec, keyBytes);
        byte[] plainBytes_dec = sm4_dec.sm4_crypt_ecb(ctx_dec, encrypted);

        boolean b = new String(plainBytes).equals(new String(plainBytes_dec));
        System.out.println("    ECB加密解密结果比较：" + b);

    }
    public static void testSm3(){
        // SM3测试
        // plain: 0x616263
        // hash1: 0x66C7F0F462EEEDD9D1F2D46BDC10E4E24167C4875CF2F7A2297DA02B8F4BA8E0
        // hash2: 0xDEBE9FF92275B8A138604889C18E5A4D6FDB70E5387E5765293DCBA39C0C5732
        byte[] plain = { 0x61, 0x62, 0x63 };
        byte[] value = { 0x66, (byte) 0xC7, (byte) 0xF0, (byte) 0xF4, (byte) 0x62, (byte) 0xEE, (byte) 0xED,
                (byte) 0xD9, (byte) 0xD1, (byte) 0xF2, (byte) 0xD4, (byte) 0x6B, (byte) 0xDC, (byte) 0x10, (byte) 0xE4,
                (byte) 0xE2, (byte) 0x41, (byte) 0x67, (byte) 0xC4, (byte) 0x87, (byte) 0x5C, (byte) 0xF2, (byte) 0xF7,
                (byte) 0xA2, (byte) 0x29, (byte) 0x7D, (byte) 0xA0, (byte) 0x2B, (byte) 0x8F, (byte) 0x4B, (byte) 0xA8,
                (byte) 0xE0 };
        byte[] plain2 = null;
        byte[] value2 = { (byte) 0xde, (byte) 0xbe, (byte) 0x9f, (byte) 0xf9, (byte) 0x22, (byte) 0x75, (byte) 0xb8,
                (byte) 0xa1, (byte) 0x38, (byte) 0x60, (byte) 0x48, (byte) 0x89, (byte) 0xc1, (byte) 0x8e, (byte) 0x5a,
                (byte) 0x4d, (byte) 0x6f, (byte) 0xdb, (byte) 0x70, (byte) 0xe5, (byte) 0x38, (byte) 0x7e, (byte) 0x57,
                (byte) 0x65, (byte) 0x29, (byte) 0x3d, (byte) 0xcb, (byte) 0xa3, (byte) 0x9c, (byte) 0x0c, (byte) 0x57,
                (byte) 0x32 };
        System.out.println("        ******SM3基准测试******");
        System.out.println("******GMT 0004-2012 SM3密码杂凑算法 A.1 示例1******");
        SM3Digest sm3 = new SM3Digest();
        sm3.update(plain, 0, plain.length);
        byte[] hash = new byte[32];
        sm3.doFinal(hash, 0);
        System.out.printf("    plain: 0x%s\n", DatatypeConverter.printHexBinary(plain));
        System.out.printf("    sm3 hash: 0x%s\n", DatatypeConverter.printHexBinary(hash));
        // 比较结果
        boolean b = new String(value).equals(new String(hash));
        System.out.println("    SM3运算结果与标准结果比较：" + b);

        System.out.println("******GMT 0004-2012 SM3密码杂凑算法 A.2 示例2******");
        plain2 = new byte[64];
        for (int i = 0; i < 16; i++) {
            plain2[4 * i] = 0x61;
            plain2[4 * i + 1] = 0x62;
            plain2[4 * i + 2] = 0x63;
            plain2[4 * i + 3] = 0x64;
        }
        sm3 = new SM3Digest();
        sm3.update(plain2, 0, plain2.length);
        hash = new byte[32];
        sm3.doFinal(hash, 0);
        System.out.printf("    plain: 0x%s\n", DatatypeConverter.printHexBinary(plain2));
        System.out.printf("    sm3 hash: 0x%s\n", DatatypeConverter.printHexBinary(hash));
        // 比较结果
        b = new String(value2).equals(new String(hash));
        System.out.println("    SM3运算结果与标准结果比较：" + b);
        System.out.println("-----------------------------------------------------");
    }



}
