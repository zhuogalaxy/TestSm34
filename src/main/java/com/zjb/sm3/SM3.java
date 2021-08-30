package com.zjb.sm3;

import com.zjb.utils.Util;

public class SM3
{
    public static final byte[] iv;
    public static int[] Tj;

    static {
        iv = new byte[] { 115, -128, 22, 111, 73, 20, -78, -71, 23, 36, 66, -41, -38, -118, 6, 0, -87, 111, 48, -68, 22, 49, 56, -86, -29, -115, -18, 77, -80, -5, 14, 78 };
        SM3.Tj = new int[64];
        for (int i = 0; i < 16; ++i) {
            SM3.Tj[i] = 2043430169;
        }
        for (int i = 16; i < 64; ++i) {
            SM3.Tj[i] = 2055708042;
        }
    }

    public static byte[] CF(final byte[] V, final byte[] B) {
        final int[] v = convert(V);
        final int[] b = convert(B);
        return convert(CF(v, b));
    }

    private static int[] convert(final byte[] arr) {
        final int[] out = new int[arr.length / 4];
        final byte[] tmp = new byte[4];
        for (int i = 0; i < arr.length; i += 4) {
            System.arraycopy(arr, i, tmp, 0, 4);
            out[i / 4] = bigEndianByteToInt(tmp);
        }
        return out;
    }

    private static byte[] convert(final int[] arr) {
        final byte[] out = new byte[arr.length * 4];
        byte[] tmp = null;
        for (int i = 0; i < arr.length; ++i) {
            tmp = bigEndianIntToByte(arr[i]);
            System.arraycopy(tmp, 0, out, i * 4, 4);
        }
        return out;
    }

    public static int[] CF(final int[] V, final int[] B) {
        int a = V[0];
        int b = V[1];
        int c = V[2];
        int d = V[3];
        int e = V[4];
        int f = V[5];
        int g = V[6];
        int h = V[7];
        final int[][] arr = expand(B);
        final int[] w = arr[0];
        final int[] w2 = arr[1];
        for (int j = 0; j < 64; ++j) {
            int ss1 = bitCycleLeft(a, 12) + e + bitCycleLeft(SM3.Tj[j], j);
            ss1 = bitCycleLeft(ss1, 7);
            final int ss2 = ss1 ^ bitCycleLeft(a, 12);
            final int tt1 = FFj(a, b, c, j) + d + ss2 + w2[j];
            final int tt2 = GGj(e, f, g, j) + h + ss1 + w[j];
            d = c;
            c = bitCycleLeft(b, 9);
            b = a;
            a = tt1;
            h = g;
            g = bitCycleLeft(f, 19);
            f = e;
            e = P0(tt2);
        }
        final int[] out = { a ^ V[0], b ^ V[1], c ^ V[2], d ^ V[3], e ^ V[4], f ^ V[5], g ^ V[6], h ^ V[7] };
        return out;
    }

    private static int[][] expand(final int[] B) {
        final int[] W = new int[68];
        final int[] W2 = new int[64];
        for (int i = 0; i < B.length; ++i) {
            W[i] = B[i];
        }
        for (int i = 16; i < 68; ++i) {
            W[i] = (P1(W[i - 16] ^ W[i - 9] ^ bitCycleLeft(W[i - 3], 15)) ^ bitCycleLeft(W[i - 13], 7) ^ W[i - 6]);
        }
        for (int i = 0; i < 64; ++i) {
            W2[i] = (W[i] ^ W[i + 4]);
        }
        final int[][] arr = { W, W2 };
        return arr;
    }

    private static byte[] bigEndianIntToByte(final int num) {
        return back(Util.intToBytes(num));
    }

    private static int bigEndianByteToInt(final byte[] bytes) {
        return Util.byteToInt(back(bytes));
    }

    private static int FFj(final int X, final int Y, final int Z, final int j) {
        if (j >= 0 && j <= 15) {
            return FF1j(X, Y, Z);
        }
        return FF2j(X, Y, Z);
    }

    private static int GGj(final int X, final int Y, final int Z, final int j) {
        if (j >= 0 && j <= 15) {
            return GG1j(X, Y, Z);
        }
        return GG2j(X, Y, Z);
    }

    private static int FF1j(final int X, final int Y, final int Z) {
        final int tmp = X ^ Y ^ Z;
        return tmp;
    }

    private static int FF2j(final int X, final int Y, final int Z) {
        final int tmp = (X & Y) | (X & Z) | (Y & Z);
        return tmp;
    }

    private static int GG1j(final int X, final int Y, final int Z) {
        final int tmp = X ^ Y ^ Z;
        return tmp;
    }

    private static int GG2j(final int X, final int Y, final int Z) {
        final int tmp = (X & Y) | (~X & Z);
        return tmp;
    }

    private static int P0(final int X) {
        int y = rotateLeft(X, 9);
        y = bitCycleLeft(X, 9);
        int z = rotateLeft(X, 17);
        z = bitCycleLeft(X, 17);
        final int t = X ^ y ^ z;
        return t;
    }

    private static int P1(final int X) {
        final int t = X ^ bitCycleLeft(X, 15) ^ bitCycleLeft(X, 23);
        return t;
    }

    public static byte[] padding(final byte[] in, final int bLen) {
        int k = 448 - (8 * in.length + 1) % 512;
        if (k < 0) {
            k = 960 - (8 * in.length + 1) % 512;
        }
        final byte[] padd = new byte[++k / 8];
        padd[0] = -128;
        final long n = in.length * 8 + bLen * 512;
        final byte[] out = new byte[in.length + k / 8 + 8];
        int pos = 0;
        System.arraycopy(in, 0, out, 0, in.length);
        pos += in.length;
        System.arraycopy(padd, 0, out, pos, padd.length);
        pos += padd.length;
        final byte[] tmp = back(Util.longToBytes(n));
        System.arraycopy(tmp, 0, out, pos, tmp.length);
        return out;
    }

    private static byte[] back(final byte[] in) {
        final byte[] out = new byte[in.length];
        for (int i = 0; i < out.length; ++i) {
            out[i] = in[out.length - i - 1];
        }
        return out;
    }

    public static int rotateLeft(final int x, final int n) {
        return x << n | x >> 32 - n;
    }

    private static int bitCycleLeft(final int n, int bitLen) {
        bitLen %= 32;
        byte[] tmp = bigEndianIntToByte(n);
        final int byteLen = bitLen / 8;
        final int len = bitLen % 8;
        if (byteLen > 0) {
            tmp = byteCycleLeft(tmp, byteLen);
        }
        if (len > 0) {
            tmp = bitSmall8CycleLeft(tmp, len);
        }
        return bigEndianByteToInt(tmp);
    }

    private static byte[] bitSmall8CycleLeft(final byte[] in, final int len) {
        final byte[] tmp = new byte[in.length];
        for (int i = 0; i < tmp.length; ++i) {
            final int t1 = (byte)((in[i] & 0xFF) << len);
            final int t2 = (byte)((in[(i + 1) % tmp.length] & 0xFF) >> 8 - len);
            final int t3 = (byte)(t1 | t2);
            tmp[i] = (byte)t3;
        }
        return tmp;
    }

    private static byte[] byteCycleLeft(final byte[] in, final int byteLen) {
        final byte[] tmp = new byte[in.length];
        System.arraycopy(in, byteLen, tmp, 0, in.length - byteLen);
        System.arraycopy(in, 0, tmp, in.length - byteLen, byteLen);
        return tmp;
    }
}
