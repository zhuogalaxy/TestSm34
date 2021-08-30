package com.zjb.utils;

import java.math.*;

public class Util
{
    private static final char[] DIGITS_LOWER;
    private static final char[] DIGITS_UPPER;

    static {
        DIGITS_LOWER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        DIGITS_UPPER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }

    public static byte[] intToBytes(final int num) {
        final byte[] bytes = { (byte)(0xFF & num >> 0), (byte)(0xFF & num >> 8), (byte)(0xFF & num >> 16), (byte)(0xFF & num >> 24) };
        return bytes;
    }

    public static int byteToInt(final byte[] bytes) {
        int num = 0;
        int temp = (0xFF & bytes[0]) << 0;
        num |= temp;
        temp = (0xFF & bytes[1]) << 8;
        num |= temp;
        temp = (0xFF & bytes[2]) << 16;
        num |= temp;
        temp = (0xFF & bytes[3]) << 24;
        num |= temp;
        return num;
    }

    public static byte[] longToBytes(final long num) {
        final byte[] bytes = new byte[8];
        for (int i = 0; i < 8; ++i) {
            bytes[i] = (byte)(0xFFL & num >> i * 8);
        }
        return bytes;
    }

    public static byte[] byteConvert32Bytes(final BigInteger n) {
        byte[] tmpd = null;
        if (n == null) {
            return null;
        }
        if (n.toByteArray().length == 33) {
            tmpd = new byte[32];
            System.arraycopy(n.toByteArray(), 1, tmpd, 0, 32);
        }
        else if (n.toByteArray().length == 32) {
            tmpd = n.toByteArray();
        }
        else {
            tmpd = new byte[32];
            for (int i = 0; i < 32 - n.toByteArray().length; ++i) {
                tmpd[i] = 0;
            }
            System.arraycopy(n.toByteArray(), 0, tmpd, 32 - n.toByteArray().length, n.toByteArray().length);
        }
        return tmpd;
    }

    public static BigInteger byteConvertInteger(final byte[] b) {
        if (b[0] < 0) {
            final byte[] temp = new byte[b.length + 1];
            temp[0] = 0;
            System.arraycopy(b, 0, temp, 1, b.length);
            return new BigInteger(temp);
        }
        return new BigInteger(b);
    }

    public static String getHexString(final byte[] bytes) {
        return getHexString(bytes, true);
    }

    public static String getHexString(final byte[] bytes, final boolean upperCase) {
        String ret = "";
        for (int i = 0; i < bytes.length; ++i) {
            ret = String.valueOf(ret) + Integer.toString((bytes[i] & 0xFF) + 256, 16).substring(1);
        }
        return upperCase ? ret.toUpperCase() : ret;
    }

    public static void printHexString(final byte[] bytes) {
        for (int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = String.valueOf('0') + hex;
            }
            System.out.print("0x" + hex.toUpperCase() + ",");
        }
        System.out.println("");
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        final int length = hexString.length() / 2;
        final char[] hexChars = hexString.toCharArray();
        final byte[] d = new byte[length];
        for (int i = 0; i < length; ++i) {
            final int pos = i * 2;
            d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(final char c) {
        return (byte)"0123456789ABCDEF".indexOf(c);
    }

    public static char[] encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? Util.DIGITS_LOWER : Util.DIGITS_UPPER);
    }

    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        int i = 0;
        int j = 0;
        while (i < l) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0xF & data[i]];
            ++i;
        }
        return out;
    }

    public static String encodeHexString(final byte[] data) {
        return encodeHexString(data, true);
    }

    public static String encodeHexString(final byte[] data, final boolean toLowerCase) {
        return encodeHexString(data, toLowerCase ? Util.DIGITS_LOWER : Util.DIGITS_UPPER);
    }

    protected static String encodeHexString(final byte[] data, final char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }

    public static byte[] decodeHex(final char[] data) {
        final int len = data.length;
        if ((len & 0x1) != 0x0) {
            throw new RuntimeException("Odd number of characters.");
        }
        final byte[] out = new byte[len >> 1];
        int f;
        for (int i = 0, j = 0; j < len; ++j, f |= toDigit(data[j], j), ++j, out[i] = (byte)(f & 0xFF), ++i) {
            f = toDigit(data[j], j) << 4;
        }
        return out;
    }

    protected static int toDigit(final char ch, final int index) {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    public static String StringToAsciiString(final String content) {
        String result = "";
        for (int max = content.length(), i = 0; i < max; ++i) {
            final char c = content.charAt(i);
            final String b = Integer.toHexString(c);
            result = String.valueOf(result) + b;
        }
        return result;
    }

    public static String hexStringToString(final String hexString, final int encodeType) {
        String result = "";
        for (int max = hexString.length() / encodeType, i = 0; i < max; ++i) {
            final char c = (char)hexStringToAlgorism(hexString.substring(i * encodeType, (i + 1) * encodeType));
            result = String.valueOf(result) + c;
        }
        return result;
    }

    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        final int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; --i) {
            final char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            }
            else {
                algorism = c - '7';
            }
            result += (int)(Math.pow(16.0, max - i) * algorism);
        }
        return result;
    }

    public static String hexStringToBinary(String hex) {
        hex = hex.toUpperCase();
        String result = "";
        for (int max = hex.length(), i = 0; i < max; ++i) {
            final char c = hex.charAt(i);
            switch (c) {
                case '0': {
                    result = String.valueOf(result) + "0000";
                    break;
                }
                case '1': {
                    result = String.valueOf(result) + "0001";
                    break;
                }
                case '2': {
                    result = String.valueOf(result) + "0010";
                    break;
                }
                case '3': {
                    result = String.valueOf(result) + "0011";
                    break;
                }
                case '4': {
                    result = String.valueOf(result) + "0100";
                    break;
                }
                case '5': {
                    result = String.valueOf(result) + "0101";
                    break;
                }
                case '6': {
                    result = String.valueOf(result) + "0110";
                    break;
                }
                case '7': {
                    result = String.valueOf(result) + "0111";
                    break;
                }
                case '8': {
                    result = String.valueOf(result) + "1000";
                    break;
                }
                case '9': {
                    result = String.valueOf(result) + "1001";
                    break;
                }
                case 'A': {
                    result = String.valueOf(result) + "1010";
                    break;
                }
                case 'B': {
                    result = String.valueOf(result) + "1011";
                    break;
                }
                case 'C': {
                    result = String.valueOf(result) + "1100";
                    break;
                }
                case 'D': {
                    result = String.valueOf(result) + "1101";
                    break;
                }
                case 'E': {
                    result = String.valueOf(result) + "1110";
                    break;
                }
                case 'F': {
                    result = String.valueOf(result) + "1111";
                    break;
                }
            }
        }
        return result;
    }

    public static String AsciiStringToString(final String content) {
        String result = "";
        for (int length = content.length() / 2, i = 0; i < length; ++i) {
            final String c = content.substring(i * 2, i * 2 + 2);
            final int a = hexStringToAlgorism(c);
            final char b = (char)a;
            final String d = String.valueOf(b);
            result = String.valueOf(result) + d;
        }
        return result;
    }

    public static String algorismToHexString(final int algorism, final int maxLength) {
        String result = "";
        result = Integer.toHexString(algorism);
        if (result.length() % 2 == 1) {
            result = "0" + result;
        }
        return patchHexString(result.toUpperCase(), maxLength);
    }

    public static String byteToString(final byte[] bytearray) {
        String result = "";
        for (int length = bytearray.length, i = 0; i < length; ++i) {
            final char temp = (char)bytearray[i];
            result = String.valueOf(result) + temp;
        }
        return result;
    }

    public static int binaryToAlgorism(final String binary) {
        final int max = binary.length();
        int result = 0;
        for (int i = max; i > 0; --i) {
            final char c = binary.charAt(i - 1);
            final int algorism = c - '0';
            result += (int)(Math.pow(2.0, max - i) * algorism);
        }
        return result;
    }

    public static String algorismToHEXString(final int algorism) {
        String result = "";
        result = Integer.toHexString(algorism);
        if (result.length() % 2 == 1) {
            result = "0" + result;
        }
        result = result.toUpperCase();
        return result;
    }

    public static String patchHexString(String str, final int maxLength) {
        String temp = "";
        for (int i = 0; i < maxLength - str.length(); ++i) {
            temp = "0" + temp;
        }
        str = (String.valueOf(temp) + str).substring(0, maxLength);
        return str;
    }

    public static int parseToInt(final String s, final int defaultInt, final int radix) {
        int i = 0;
        try {
            i = Integer.parseInt(s, radix);
        }
        catch (NumberFormatException ex) {
            i = defaultInt;
        }
        return i;
    }

    public static int parseToInt(final String s, final int defaultInt) {
        int i = 0;
        try {
            i = Integer.parseInt(s);
        }
        catch (NumberFormatException ex) {
            i = defaultInt;
        }
        return i;
    }

    public static byte[] hexToByte(final String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        final char[] arr = hex.toCharArray();
        final byte[] b = new byte[hex.length() / 2];
        String swap;
        int byteint;
        for (int i = 0, j = 0, l = hex.length(); i < l; swap = new StringBuilder().append(arr[i++]).append(arr[i]).toString(), byteint = (Integer.parseInt(swap, 16) & 0xFF), b[j] = new Integer(byteint).byteValue(), ++i, ++j) {}
        return b;
    }

    public static String byteToHex(final byte[] b) {
        if (b == null) {
            throw new IllegalArgumentException("Argument b ( byte array ) is null! ");
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; ++n) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1) {
                hs = String.valueOf(hs) + "0" + stmp;
            }
            else {
                hs = String.valueOf(hs) + stmp;
            }
        }
        return hs.toUpperCase();
    }

    public static byte[] subByte(final byte[] input, final int startIndex, final int length) {
        final byte[] bt = new byte[length];
        for (int i = 0; i < length; ++i) {
            bt[i] = input[i + startIndex];
        }
        return bt;
    }
}


