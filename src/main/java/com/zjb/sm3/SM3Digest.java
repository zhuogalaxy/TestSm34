package com.zjb.sm3;



//import org.bouncycastle.util.encoders.*;

public class SM3Digest
{
    private static final int BYTE_LENGTH = 32;
    private static final int BLOCK_LENGTH = 64;
    private static final int BUFFER_LENGTH = 64;
    private byte[] xBuf;
    private int xBufOff;
    private byte[] V;
    private int cntBlock;

    public SM3Digest() {
        this.xBuf = new byte[64];
        this.V = SM3.iv.clone();
        this.cntBlock = 0;
    }

    public SM3Digest(final SM3Digest t) {
        this.xBuf = new byte[64];
        this.V = SM3.iv.clone();
        this.cntBlock = 0;
        System.arraycopy(t.xBuf, 0, this.xBuf, 0, t.xBuf.length);
        this.xBufOff = t.xBufOff;
        System.arraycopy(t.V, 0, this.V, 0, t.V.length);
    }

    public int doFinal(final byte[] out, final int outOff) {
        final byte[] tmp = this.doFinal();
        System.arraycopy(tmp, 0, out, 0, tmp.length);
        return 32;
    }

    public void reset() {
        this.xBufOff = 0;
        this.cntBlock = 0;
        this.V = SM3.iv.clone();
    }

    public void update(final byte[] in, final int inOff, final int len) {
        final int partLen = 64 - this.xBufOff;
        int inputLen = len;
        int dPos = inOff;
        if (partLen < inputLen) {
            System.arraycopy(in, dPos, this.xBuf, this.xBufOff, partLen);
            inputLen -= partLen;
            dPos += partLen;
            this.doUpdate();
            while (inputLen > 64) {
                System.arraycopy(in, dPos, this.xBuf, 0, 64);
                inputLen -= 64;
                dPos += 64;
                this.doUpdate();
            }
        }
        System.arraycopy(in, dPos, this.xBuf, this.xBufOff, inputLen);
        this.xBufOff += inputLen;
    }

    private void doUpdate() {
        final byte[] B = new byte[64];
        for (int i = 0; i < 64; i += 64) {
            System.arraycopy(this.xBuf, i, B, 0, B.length);
            this.doHash(B);
        }
        this.xBufOff = 0;
    }

    private void doHash(final byte[] B) {
        final byte[] tmp = SM3.CF(this.V, B);
        System.arraycopy(tmp, 0, this.V, 0, this.V.length);
        ++this.cntBlock;
    }

    private byte[] doFinal() {
        final byte[] B = new byte[64];
        final byte[] buffer = new byte[this.xBufOff];
        System.arraycopy(this.xBuf, 0, buffer, 0, buffer.length);
        final byte[] tmp = SM3.padding(buffer, this.cntBlock);
        for (int i = 0; i < tmp.length; i += 64) {
            System.arraycopy(tmp, i, B, 0, B.length);
            this.doHash(B);
        }
        return this.V;
    }

    public void update(final byte in) {
        final byte[] buffer = { in };
        this.update(buffer, 0, 1);
    }

    public int getDigestSize() {
        return 32;
    }

    public static void main(final String[] args) {
//        final byte[] md = new byte[32];
//        final byte[] msg1 = "abc".getBytes();
//        final SM3Digest sm3 = new SM3Digest();
//        sm3.update(msg1, 0, msg1.length);
//        sm3.doFinal(md, 0);
//        final String s = new String(Hex.encode(md));
//        System.out.println(s);
    }
}

