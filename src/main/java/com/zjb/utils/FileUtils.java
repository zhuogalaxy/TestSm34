package com.zjb.utils;

import com.zjb.encode.EncodeFile;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {
    final static int threshold=160*1024*1024;

    public byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[threshold];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;
    }

    public static byte[] toByteArray(String filename) throws IOException {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }


    public static byte[] toByteArray3(String filename) throws IOException {

        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(filename, "r").getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size()).load();
            System.out.println(byteBuffer.isLoaded());
            byte[] result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
                // System.out.println("remain");
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean enFileCycle(String srcFilePath, String destFilePath) {
        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println("enFile: srcFilePath is null or empty");
            //logger.error("enFile: srcFilePath is null or empty");
            return false;
        }
        if (destFilePath == null || destFilePath.isEmpty()){
            System.out.println("enFile: destFilePath is null or empty");
            //logger.error("enFile: destFilePath is null or empty");
            return false;
        }

        boolean b=false;
        boolean b2;
        EncodeFile ef=new EncodeFile();
        RWFile rwFile=new RWFile();
        String randomN=ef.generateRandom();
        InputStream is = null;
        OutputStream os = null;
        FileInputStream fileIn=null;
        FileChannel fc = null;
        MappedByteBuffer[] mappedBufArray;
        int number;
        long fileLength=0;
        //读取操作
        byte[] result = null;
        byte[] encrypted=null;
        //int len = 0;//实际读取大小
        //int offset=0;
        int j=0;
        //循环读取
        try{
            os = new FileOutputStream(destFilePath,true);
            //fc = new RandomAccessFile(srcFilePath, "r").getChannel();
            fileIn = new FileInputStream(srcFilePath);
            fc = fileIn.getChannel();
            fileLength = fc.size();
            number = (int) Math.ceil((double) fileLength / (double) Integer.MAX_VALUE);
            mappedBufArray = new MappedByteBuffer[number];

            long preLength = 0;
            long regionSize = (long) Integer.MAX_VALUE;// 映射区域的大小
            for (int i = 0; i < number; i++) {// 将文件的连续区域映射到内存文件映射数组中
                if (fileLength - preLength < (long) Integer.MAX_VALUE) {
                    regionSize = fileLength - preLength;// 最后一片区域的大小
                }
                mappedBufArray[i] = fc.map(FileChannel.MapMode.READ_ONLY, preLength, regionSize).load();
                preLength += regionSize;// 下一片区域的开始

                while(mappedBufArray[i].remaining() > 0){
                    System.out.println("mappedBufArray[i]=" + mappedBufArray[i].remaining());
                    j++;
                    //输出 字节数组转成字符串
                    if(mappedBufArray[i].remaining()>threshold){
                        result=new byte[threshold];
                        mappedBufArray[i].get(result, 0, result.length);
                        //offset+=result.length;
                        encrypted= ef.sm4Encode(randomN,result);

                        os.write(encrypted,0,encrypted.length);

                        os.flush();//强制刷新出去
                    }else{
                        result=new byte[mappedBufArray[i].remaining()];
                        mappedBufArray[i].get(result, 0,mappedBufArray[i].remaining());
                        encrypted= ef.sm4Encode(randomN,result);

                        os.write(encrypted,0,encrypted.length);

                        os.flush();//强制刷新出去
                    }
                    System.out.println("i = "+j);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileIn != null){
                try {
                    fileIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String evpData=ef.encodeSignedAndEnvelope(randomN);
        if(evpData==null){
            System.out.println("evpData is null");
        }
        b2=rwFile.writeFile(destFilePath+".evp",evpData.getBytes());
        if(b2){
            System.out.println("enFile: encode file success");
        }else{
            System.out.println("enFile: encode file fail");

        }
        return b;
    }

    public static void main(String[] args){
        String srcFilePath="D:\\testZip\\test20G.rar1.zip";
        String destFilePath="D:\\testZip\\test20G.rar1.zip--test";
        System.out.println("srcFilePath = "+srcFilePath);
        FileUtils fu = new FileUtils();

        long startTime1= System.currentTimeMillis();

        fu.enFileCycle(srcFilePath,destFilePath);

        long endTime1=System.currentTimeMillis();
        System.out.println("enfile use time: "+ (endTime1-startTime1));
        System.out.println("success");

    }


}
