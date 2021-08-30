package com.zjb.readfile;

import com.fri.alg.sm.sm4.SM4Utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;

public class ReadThread extends Thread{

    //定义字节数组（取水的竹筒）的长度
    private final int BUFF_LEN = 256;  //64K
    //定义读取的起始点
    private long start;
    //定义读取的结束点
    private long end;
    //将读取到的字节输出到raf中  randomAccessFile可以理解为文件流，即文件中提取指定的一部分的包装对象
    private RandomAccessFile raf;
    //负责写文件
    private RandomAccessFile raf_w;
    //线程中需要指定的关键字
    //private String keywords;
    //此线程读到关键字的次数
    private int curCount = 0;

    private String randomN;

    private CountDownLatch doneSignal;
    public ReadThread(long start, long end, RandomAccessFile raf,RandomAccessFile raf_w,CountDownLatch doneSignal,String randomN){
        this.start = start;
        this.end = end;
        this.raf  = raf;
        //this.keywords = keywords;
        this.doneSignal = doneSignal;
        this.raf_w = raf_w;
        this.randomN = randomN;
    }

    public void run(){
        try {
            raf.seek(start);
            raf_w.seek(start);
            //本线程负责读取文件的大小
            long contentLen = end - start;
            //定义最多需要读取几次就可以完成本线程的读取
            long times = contentLen / BUFF_LEN+1;
            long left= contentLen % BUFF_LEN;
            System.out.println(this.toString() + " 需要读的次数："+times);
            System.out.println(this.toString() + " 需要读的字节数："+contentLen);
            //byte[] buff = new byte[BUFF_LEN];
            byte[] buff = null;
            int hasRead = 0;
            String result = null;
            SM4Utils sm4 = new SM4Utils();
            for (int i = 0; i < times; i++) {
                if(i==times-1){
                    buff = new byte[(int) left];
                }else{
                    buff = new byte[BUFF_LEN];
                }
                //之前SEEK指定了起始位置，这里读入指定字节组长度的内容，read方法返回的是下一个开始读的position
                hasRead = raf.read(buff);
                //如果读取的字节数小于0，则退出循环！ （到了字节数组的末尾）
                if (hasRead < 0) {
                    break;
                }

                sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
                //buff = sm4.doEncrypt_ecb(buff);
                buff = sm4.doDecrypt_ecb(buff);

                result = new String(buff,"utf8");

                //if(contentLen==289390){
                    //System.out.println("result = "+result);
                //}

                //System.out.println("result = "+result);
                //如何将读取的内容重新写入一整个文件，是否会有字符编码问题？待定
                raf_w.write(buff);
                //raf_w.write(result.getBytes("utf8"));
                //buff = resetArray(buff);

                //记录关键词的个数的，暂时用不到
                //int count = this.getCountByKeywords(result, keywords);
                //if(count > 0){
                //this.curCount += count;
                //}
            }

            //KeyWordsCount kc = KeyWordsCount.getCountObject();

            //kc.addCount(this.curCount);

            doneSignal.countDown();//current thread finished! noted by latch object!
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
//    public byte[] resetArray(byte[] a) {
//        byte[] b2 = new byte[a.length];
//        for (int i = 0; i < a.length; i++) {
//            a[i] = b2[i];
//        }
//        return a;
//    }


    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public RandomAccessFile getRaf() {
        return raf;
    }

    public void setRaf(RandomAccessFile raf) {
        this.raf = raf;
    }

    //public int getCountByKeywords(String statement,String key){
        //return statement.split(key).length-1;
    //}

    public int getCurCount() {
        return curCount;
    }

    public void setCurCount(int curCount) {
        this.curCount = curCount;
    }

    public CountDownLatch getDoneSignal() {
        return doneSignal;
    }

    public void setDoneSignal(CountDownLatch doneSignal) {
        this.doneSignal = doneSignal;
    }

    public RandomAccessFile getRaf_w() {
        return raf_w;
    }

    public void setRaf_w(RandomAccessFile raf_w) {
        this.raf_w = raf_w;
    }

    public String getRandomN() {
        return randomN;
    }

    public void setRandomN(String randomN) {
        this.randomN = randomN;
    }
}
