package com.zjb.readfile;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

public class GenEnFile {

    final int DOWN_THREAD_NUM = 10; //起10个线程去读取指定文件，后续需要根据文件大小指定几个线程
    final String OUT_FILE_NAME = "D:\\testZip\\yitiantulongji_jinyong-en.txt";
    final String Write_File_Name="D:\\testZip\\yitiantulongji_jinyong-de.txt";
    //final String keywords = "无忌";

    public void genEnFile(){
        String randomN="HFLv8pkjB3Ld2xrRwOYXqg==";
        CountDownLatch doneSignal = new CountDownLatch(DOWN_THREAD_NUM);
        RandomAccessFile[] outArr = new RandomAccessFile[DOWN_THREAD_NUM];
        RandomAccessFile[] write_outArr = new RandomAccessFile[DOWN_THREAD_NUM];
        try{
            long length = new File(OUT_FILE_NAME).length();
            System.out.println("文件总长度："+length+"字节");
            //每线程应该读取的字节数
            long numPerThred = length / DOWN_THREAD_NUM;
            System.out.println("每个线程读取的字节数："+numPerThred+"字节");
            //整个文件整除后剩下的余数
            long left = length % DOWN_THREAD_NUM;

            for (int i = 0; i < DOWN_THREAD_NUM; i++) {
                //为每个线程打开一个输入流、一个RandomAccessFile对象，

                //让每个线程分别负责读取文件的不同部分
                outArr[i] = new RandomAccessFile(OUT_FILE_NAME, "rw");

                write_outArr[i] = new RandomAccessFile(Write_File_Name, "rw");

                if (i != 0) {
//
//                    isArr[i] = new FileInputStream("d:/勇敢的心.rmvb");
                    //以指定输出文件创建多个RandomAccessFile对象

                }
                if (i == DOWN_THREAD_NUM - 1) {
//                    //最后一个线程读取指定numPerThred+left个字节
//                  System.out.println("第"+i+"个线程读取从"+i * numPerThred+"到"+((i + 1) * numPerThred+ left)+"的位置");
                    new FinalReadThread(i * numPerThred, (i + 1) * numPerThred
                            + left, outArr[i],write_outArr[i],doneSignal,randomN).start();
                } else {
                    //每个线程负责读取一定的numPerThred个字节
//                  System.out.println("第"+i+"个线程读取从"+i * numPerThred+"到"+((i + 1) * numPerThred)+"的位置");
                    new ReadThread(i * numPerThred, (i + 1) * numPerThred,
                            outArr[i],write_outArr[i],doneSignal,randomN).start();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
//      finally{
//
//      }
        //确认所有线程任务完成，开始执行主线程的操作
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }


    public static void main(String[] args) {
        GenEnFile gf = new GenEnFile();
        gf.genEnFile();
        System.out.println("success");

    }


}
