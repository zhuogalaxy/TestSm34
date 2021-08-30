package com.zjb;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class OperateFile {
    private static final int SIZE = 10*1024 * 1024;// 定义单个文件的大小这里采用1m

    public static void main(String[] args) {
        String pathName="D:\\test\\test-20210823\\splitfile";
        String fileName="D:\\test\\test-20210823\\designer-origin-7G.rar";
        File file=new File(fileName);
        long startTime= System.currentTimeMillis();
        splitFile(file,pathName);
        long endTime=System.currentTimeMillis();
        System.out.println("use time: "+ (float)(endTime-startTime)/1000);
    }

    /**
     *
     * @param file
     */
    public static void splitFile(File file, String pathName) {
        try {
            FileInputStream fs = new FileInputStream(file);
            // 定义缓冲区
            byte[] b = new byte[SIZE];
            FileOutputStream fo = null;
            int len = 0;
            int count = 0;

            /**
             * 切割文件时，记录 切割文件的名称和切割的子文件个数以方便合并
             * 这个信息为了简单描述，使用键值对的方式，用到了properties对象
             */
            Properties pro = new Properties();
            // 定义输出的文件夹路径
            File dir = new File(pathName);
            // 判断文件夹是否存在，不存在则创建
            if (!dir.exists()) {
                dir.mkdirs();
            }
            long fileLength=file.length();
            // 切割文件
            while ((len = fs.read(b)) != -1) {
                fo = new FileOutputStream(new File(dir, (count++) + ".zip"));
                fo.write(b, 0, len);
                fo.close();
            }
//            if((len = fs.read(b)) != -1){
//                fo = new FileOutputStream(new File(dir, (count++) + ".zip"));
//                fo.write(b, 0, len);
//                fo.close();
//            }


            // 将被切割的文件信息保存到properties中
            pro.setProperty("partCount", count + "");
            pro.setProperty("fileName", file.getName());
            fo = new FileOutputStream(new File(dir, (count++) + ".properties"));
            // 写入properties文件
            pro.store(fo, "save file info");
            fo.close();
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     *
     * @param dir
     * @throws Exception
     */
    public static void mergeFile(File dir) throws Exception {
        // 读取properties文件的拆分信息
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".properties");
            }
        });
        File file = files[0];
        // 获取该文件的信息
        Properties pro = new Properties();
        FileInputStream fis = new FileInputStream(file);
        pro.load(fis);
        String fileName = pro.getProperty("fileName");
        int splitCount = Integer.valueOf(pro.getProperty("partCount"));
        if (files.length != 1) {
            throw new Exception(dir + ",该目录下没有解析的properties文件或不唯一");
        }

        // 获取该目录下所有的碎片文件
        File[] partFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".part");
            }
        });
        // 将碎片文件存入到集合中
        List<FileInputStream> al = new ArrayList<FileInputStream>();
        for (int i = 0; i < splitCount; i++) {
            try {
                al.add(new FileInputStream(partFiles[i]));
            } catch (Exception e) {
                // 异常
                e.printStackTrace();
            }
        }
        try {
            // 构建文件流集合
            Enumeration<FileInputStream> en = Collections.enumeration(al);
            // 将多个流合成序列流
            SequenceInputStream sis = new SequenceInputStream(en);
            FileOutputStream fos = new FileOutputStream(new File(dir, fileName));
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = sis.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            fos.close();
            sis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
