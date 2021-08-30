package com.zjb.utils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.fri.alg.sm.sm3.SM3Digest;
import com.fri.alg.sm.sm4.SM4Utils;
import com.ky.dataDivide.alg2.DataDivide;
import com.zjb.decode.DecodeFile;
import com.zjb.encode.EncodeFile;
//import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


/**
 * @author Adam zhao
 * @Time 2020/7/23
 */
public class RWFile {

    public Logger logger = (Logger) LoggerFactory.getLogger(RWFile.class);

    public static void main01(String[] args) throws JoranException, IOException {
        //D:\Program Files\javaworkspace\jiangsu\TestSm34\testFiles
        //String directoryPath="D:\\Program Files\\javaworkspace\\jiangsu\\TestSm34\\testFiles";
        //String fileName="1.txt";
    	String srcFilePath="D:\\testZip\\testencode\\11000000000020214331164307_p_data_extract_t.txt@vBB2MstNpOIR+2lyR8-Rvf28Qua0kUvnJV+7PIFDtc4=#2021012022010044.dat";
    	String destFilePath= "D:\\testZip\\testdecode\\";
        System.out.println("srcFilePath = "+srcFilePath);
       
        RWFile rwFile=new RWFile();
        //byte[] originData= rwFile.readFile(directoryPath,fileName);
        long startTime1= System.currentTimeMillis();
        //rwFile.enFile1(directoryPath,directoryPath,fileName);
        //rwFile.deFile1(directoryPath,directoryPath,fileName);
        
        boolean ba = rwFile.enFile2(srcFilePath, destFilePath);
        //boolean ba = rwFile.doFileDigest(srcFilePath);
        //boolean ba = rwFile.deFile3(srcFilePath, destFilePath);
        System.out.println("ba = " + ba);
        long endTime1=System.currentTimeMillis();
        System.out.println("enfile use time: "+ (endTime1-startTime1));

    }
    public static void main03(String[] args){

        String srcFilePath="D:\\testZip\\encr\\11000000000020210428215756&G11000000000020210419170700_dn_map_t.txt@fb-fuWBAaN0zpQotLHsObfwj0fWKq4k+jgnZepB7YJA=#2021031522010230.dat";
        String destFilePath = "D:\\testZip\\testdecode\\";
        RWFile re = new RWFile();
        //String keyFile = re.findKeyFileOnly(srcFilePath);

        boolean ba = re.deFile3(srcFilePath, destFilePath);

        System.out.println("result = " + ba);
    }



    /**
             * 加密，只有两个参数
     * @param srcFilePath
     * @param destFilePath
     * @return
     */
    public boolean enFile1(String srcFilePath, String destFilePath){
        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println("enFile: srcFilePath is null or empty");
            logger.error("enFile: srcFilePath is null or empty");
            return false;
        }
        if (destFilePath == null || destFilePath.isEmpty()){
            System.out.println("enFile: destFilePath is null or empty");
            logger.error("enFile: destFilePath is null or empty");
            return false;
        }
       
        
//        File  dir=new File(destFilePath);
//        if (!dir.exists() && !dir.isDirectory()) {
//            dir.mkdirs();
//        }
        
        boolean b=false;
        boolean b1;
        boolean b2;
        EncodeFile ef=new EncodeFile();
        RWFile rwFile=new RWFile();

        long start0=System.currentTimeMillis();
        //byte[] originData=readFile(srcDir,fileName);
        byte[] originData=readFile(srcFilePath);
        long end0=System.currentTimeMillis();
        logger.info("readFile use time: " + (end0-start0) + " ms");

        long start1=System.currentTimeMillis();
        String randomN=ef.generateRandom();
        long end1=System.currentTimeMillis();
        logger.info("generateRandom use time: " + (end1-start1) + " ms");

        long start2=System.currentTimeMillis();
        byte[] encrypted= ef.sm4Encode(randomN,originData);
        long end2=System.currentTimeMillis();
        logger.info("sm4Encode use time: " + (end2-start2) + " ms");

        long start3=System.currentTimeMillis();
        String evpData=ef.encodeSignedAndEnvelope(randomN);
        long end3=System.currentTimeMillis();
        logger.info("encodeSignedAndEnvelope use time: " + (end3-start3) + " ms");

        long start4=System.currentTimeMillis();
        
        //b1= rwFile.writeFile(destDir,fileName,encrypted);
        //b2=rwFile.writeFile(destDir,fileName+".evp",evpData.getBytes());
        
        b1= rwFile.writeFile(destFilePath,encrypted);
        System.out.println("destFilePath = "+destFilePath+".evp");
        if(evpData==null){
            System.out.println("evpData is null");
        }
        b2=rwFile.writeFile(destFilePath+".evp",evpData.getBytes());
        
        long end4=System.currentTimeMillis();
        logger.info("writeFile use time: " + (end4-start4) + " ms");

        if(b1 && b2){
            b=true;
            System.out.println("enFile: encode file success");
            logger.info("enFile: encode file success");
        }else{
            System.out.println("enFile: encode file fail");
            logger.error("enFile: encode file fail");
        }
        return b;
    }


    /**
     * 读，加密，只获取文件存储目录
     * @param srcFilePath  源文件目录
     * @param destFilePath  加密后文件目录
     * @return
     * @throws IOException
     */
    public boolean enFile3(String srcFilePath, String destFilePath) throws IOException {
        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println("enFile: srcFilePath is null or empty");
            logger.error("enFile: srcFilePath is null or empty");
            return false;
        }
        if (destFilePath == null || destFilePath.isEmpty()){
            System.out.println("enFile: destFilePath is null or empty");
            logger.error("enFile: destFilePath is null or empty");
            return false;
        }

        boolean b=false;
        boolean b1;
        boolean b2;
        EncodeFile ef=new EncodeFile();
        RWFile rwFile=new RWFile();

        long start1=System.currentTimeMillis();
        String randomN=ef.generateRandom();
        long end1=System.currentTimeMillis();
        logger.info("generateRandom use time: " + (end1-start1) + " ms");

        long start3=System.currentTimeMillis();
        String evpData=ef.encodeSignedAndEnvelope(randomN);
        long end3=System.currentTimeMillis();
        logger.info("encodeSignedAndEnvelope use time: " + (end3-start3) + " ms");

        //新的加密，以 16M 为界限，每16M使用doDecrypt_ecb加密，最后left使用doFinalDecrypt_ecb加密
        //解密，以16M 为界限，每16M使用doDecrypt_ecb解密，最后left使用doFinalDecrypt_ecb解密
        //有个问题，假如 源文件正好16M，加密后会变成（16M + 16）字节，也即是最后一个left要以（16M+16字节）为界限

        File file = new File(srcFilePath);
        InputStream input =null;
        if(file.isFile()){  //判断是否是目录，是目录会报错
            input = new FileInputStream(file);
        }else{
            return b;
        }
        long fileLength = file.length();
        if(fileLength<=0){
            b=false;
            return b;
        }
        //定义一个16MB缓冲区
        long BUFF_LEN = 16 * 1024 * 1024;  //16M
        long left = fileLength % BUFF_LEN;
        long times = fileLength / BUFF_LEN+1;
        //byte[] buf = new byte[(int) BUFF_LEN];
        SM4Utils sm4 = new SM4Utils();
        //int len=0;
        RandomAccessFile out_raf = new RandomAccessFile(destFilePath, "rw");
        out_raf.seek(0);
        if(fileLength<=BUFF_LEN){
            byte[] buf = new byte[(int) fileLength];
            sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
            //buff = sm4.doEncrypt_ecb(buff);
            input.read(buf);
            buf = sm4.doFinalEncrypt_ecb(buf);
            out_raf.write(buf);
        }else{
            for(int i = 0; i < times; i++){
                if(i==(times-1)){
                    byte[] buf = new byte[(int) left];
                    sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
                    //buff = sm4.doEncrypt_ecb(buff);
                    input.read(buf);
                    buf = sm4.doFinalEncrypt_ecb(buf);
                    out_raf.write(buf);
                }else{
                    byte[] buf = new byte[(int) BUFF_LEN];
                    sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
                    //buff = sm4.doEncrypt_ecb(buff);
                    input.read(buf);
                    buf = sm4.doEncrypt_ecb(buf);
                    out_raf.write(buf);
                }
            }
        }
        //if(file.)
        if(input!=null){
            input.close();
        }
        out_raf.close();
        b1=true;

        long start4=System.currentTimeMillis();
        System.out.println("destFilePath = "+destFilePath+".evp");
        if(evpData==null){
            System.out.println("evpData is null");
        }
        b2=rwFile.writeFile(destFilePath+".evp",evpData.getBytes());

        long end4=System.currentTimeMillis();
        logger.info("writeFile use time: " + (end4-start4) + " ms");

        if(b1 && b2){
            b=true;
            System.out.println("enFile: encode file success");
            logger.info("enFile: encode file success");
        }else{
            System.out.println("enFile: encode file fail");
            logger.error("enFile: encode file fail");
        }
        return b;
    }


    /**
     * buff 读， 加密
     * @param srcFilePath
     * @param destFilePath
     * @return
     */
    public boolean enFile2(String srcFilePath, String destFilePath) {
        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println("enFile: srcFilePath is null or empty");
            logger.error("enFile: srcFilePath is null or empty");
            return false;
        }
        if (destFilePath == null || destFilePath.isEmpty()){
            System.out.println("enFile: destFilePath is null or empty");
            logger.error("enFile: destFilePath is null or empty");
            return false;
        }
        //String hashData64=null;
        boolean b=false;
        boolean b1;
        boolean b2;
        EncodeFile ef=new EncodeFile();
        RWFile rwFile=new RWFile();

        long start1=System.currentTimeMillis();
        String randomN=ef.generateRandom();
        if(randomN.isEmpty() || randomN==null){
            logger.error("加密机错误：randomN.isEmpty() || randomN==null");
            logger.info("加密机错误：randomN.isEmpty() || randomN==null");
            return b;
        }
        long end1=System.currentTimeMillis();
        logger.info("generateRandom use time: " + (end1-start1) + " ms");

        long start3=System.currentTimeMillis();
        String evpData=ef.encodeSignedAndEnvelope(randomN);
        if(evpData.isEmpty() || evpData==null){
            logger.error("加密机错误：evpData.isEmpty() || evpData==null");
            logger.info("加密机错误：evpData.isEmpty() || evpData==null");
            return b;
        }
        long end3=System.currentTimeMillis();
        logger.info("encodeSignedAndEnvelope use time: " + (end3-start3) + " ms");

        //新的加密，以 16M 为界限，每16M使用doDecrypt_ecb加密，最后left使用doFinalDecrypt_ecb加密
        //解密，以16M 为界限，每16M使用doDecrypt_ecb解密，最后left使用doFinalDecrypt_ecb解密
        //有个问题，假如 源文件正好16M，加密后会变成（16M + 16）字节，也即是最后一个left要以（16M+16字节）为界限
        InputStream input =null;
        RandomAccessFile out_raf=null;
        try {

            File file = new File(srcFilePath);
            if(file.isFile()){  //判断是否是目录，是目录会报错
                input = new FileInputStream(file);
            }else{
                return b;
            }
            long fileLength = file.length();
            if(fileLength<=0){
                b=false;
                return b;
            }
            //定义一个16MB缓冲区
            long BUFF_LEN = 16 * 1024 * 1024;  //16M
            long left = fileLength % BUFF_LEN;
            long times = fileLength / BUFF_LEN+1;
            //byte[] buf = new byte[(int) BUFF_LEN];
            SM4Utils sm4 = new SM4Utils();
            //int len=0;
            destFilePath=destFilePath+".dat";
            out_raf = new RandomAccessFile(destFilePath, "rw");
            out_raf.seek(0);
            if(fileLength<=BUFF_LEN){
                byte[] buf = new byte[(int) fileLength];
                sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
                //buff = sm4.doEncrypt_ecb(buff);
                input.read(buf);
                buf = sm4.doFinalEncrypt_ecb(buf);
                out_raf.write(buf);
            }else{
                for(int i = 0; i < times; i++){
                    if(i==(times-1)){
                        byte[] buf = new byte[(int) left];
                        sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
                        //buff = sm4.doEncrypt_ecb(buff);
                        input.read(buf);
                        buf = sm4.doFinalEncrypt_ecb(buf);
                        out_raf.write(buf);
                    }else{
                        byte[] buf = new byte[(int) BUFF_LEN];
                        sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
                        //buff = sm4.doEncrypt_ecb(buff);
                        input.read(buf);
                        buf = sm4.doEncrypt_ecb(buf);
                        out_raf.write(buf);
                    }
                }
            }
            //if(file.)
            if(input!=null){
                input.close();
            }
            out_raf.close();
            b1=true;

            long start4=System.currentTimeMillis();
            //System.out.println("destFilePath = "+destFilePath+".evp");
            if(evpData==null){
                System.out.println("evpData is null");
            }
            //b2=rwFile.writeFile(destFilePath+".evp",evpData.getBytes());
            //destFilePath=destFilePath.substring(0,destFilePath.length()-4);
            b2=rwFile.writeFile(destFilePath.substring(0,destFilePath.length()-4)+".key",evpData.getBytes());

            long end4=System.currentTimeMillis();
            logger.info("writeFile use time: " + (end4-start4) + " ms");

            if(b1 && b2){
                b=true;
                System.out.println("enFile: encode file success");
                logger.info("enFile: encode file success");
            }else{
                System.out.println("enFile: encode file fail");
                logger.error("enFile: encode file fail");
            }
        }catch (IOException e){
            logger.info("ExceptionTest Exception:",e);
            logger.error("ExceptionTest Exception:",e);
        }finally {
            try {
                if(input!=null){
                    input.close();
                }
                if(out_raf!=null){
                    out_raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //hashData64 = rwFile.getSm3Digest(destFilePath,32);
        b=doFileDigest(destFilePath);

        return b;
    }


    /**
     * 对文件做摘要值，并改名字, / 改成 -
     * @param srcFilePath
     * @return
     */
    public boolean doFileDigest(String srcFilePath){
        boolean b=false;
        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println("doFileDigest: srcFilePath is null or empty");
            logger.error("doFileDigest: srcFilePath is null or empty");
            return b;
        }
        File file = new File(srcFilePath);
        if(!file.isFile()){
            System.out.println("doFileDigest: srcFilePath is not file");
            logger.error("doFileDigest: srcFilePath is not file");
            return b;
        }
        String hashData64 = getSm3Digest(srcFilePath);
        if(hashData64!=null){
            File oldFile = new File(srcFilePath);
            System.out.println("oldFile.getName()" + oldFile.getName());
            //String newName = oldFile.getName().split(".dat",2)[0]+"@"+hashData64+".dat";
            String newName = oldFile.getName().substring(0,oldFile.getName().length()-4)+"@"+hashData64+".dat";
            System.out.println("newName = "+newName);
            String destPath = srcFilePath.split(oldFile.getName(),2)[0];

            String destFilePath = destPath+newName;
            destFilePath = destFilePath.replace("/","-");
//            if(judgeSystem()==0){
//                destFilePath = destPath+"\\"+newName;
//            }else{
//                destFilePath = destPath+"/"+newName;
//            }
            System.out.println("destFilePath = " + destFilePath);
            File newFile = new File(destFilePath);
//            Path source = oldFile.toPath();
//            Path target = newFile.toPath();
//            try {
//                Files.move(source, target);
//                logger.info(destFilePath + " rename success");
//                System.out.println(destFilePath + " rename success");
//                b=true;
//            } catch (IOException e) {
//                logger.error(destFilePath + " rename fail");
//                System.out.println(destFilePath + " rename fail");
//                e.printStackTrace();
//            }
            if(oldFile.renameTo(newFile)){
                logger.info(destFilePath + " rename success");
                System.out.println(destFilePath + " rename success");
                b=true;
            }else{
                logger.error(destFilePath + " rename fail");
                System.out.println(destFilePath + " rename fail");
            }
        }else{
            logger.error(srcFilePath + " getSm3Digest is null");
            System.out.println(srcFilePath + "getSm3Digest is null");
        }
        return b;
    }

    /**
     * buff 读，加密, 生成dat文件，不生成key文件
     * @param randomN
     * @param srcFilePath
     * @param destFilePath
     * @return
     * @time 2021-04-28 15:45
     */
    public boolean enFile2(String randomN,String srcFilePath, String destFilePath) {
        if(randomN.isEmpty() || randomN==null){
            logger.error("加密机错误：randomN.isEmpty() || randomN==null");
            logger.info("加密机错误：randomN.isEmpty() || randomN==null");
            return false;
        }

        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println("enFile: srcFilePath is null or empty");
            logger.error("enFile: srcFilePath is null or empty");
            return false;
        }
        if (destFilePath == null || destFilePath.isEmpty()){
            System.out.println("enFile: destFilePath is null or empty");
            logger.error("enFile: destFilePath is null or empty");
            return false;
        }

        boolean b=false;
        boolean b1;
        boolean b2;
        EncodeFile ef=new EncodeFile();
        RWFile rwFile=new RWFile();

        //String randomN=ef.generateRandom();

//        long start3=System.currentTimeMillis();
//       String evpData=ef.encodeSignedAndEnvelope(randomN);
//        if(evpData.isEmpty() || evpData==null){
//            logger.error("加密机错误：evpData.isEmpty() || evpData==null");
//            logger.info("加密机错误：evpData.isEmpty() || evpData==null");
//            return b;
//        }
//        long end3=System.currentTimeMillis();
//        logger.info("encodeSignedAndEnvelope use time: " + (end3-start3) + " ms");

        //新的加密，以 16M 为界限，每16M使用doDecrypt_ecb加密，最后left使用doFinalDecrypt_ecb加密
        //解密，以16M 为界限，每16M使用doDecrypt_ecb解密，最后left使用doFinalDecrypt_ecb解密
        //有个问题，假如 源文件正好16M，加密后会变成（16M + 16）字节，也即是最后一个left要以（16M+16字节）为界限
        InputStream input =null;
        RandomAccessFile out_raf=null;
        try {

            File file = new File(srcFilePath);
            if(file.isFile()){  //判断是否是目录，是目录会报错
                input = new FileInputStream(file);
            }else{
                return b;
            }
            long fileLength = file.length();
            if(fileLength<=0){
                b=false;
                return b;
            }
            //定义一个16MB缓冲区
            long BUFF_LEN = 16 * 1024 * 1024;  //16M
            long left = fileLength % BUFF_LEN;
            long times = fileLength / BUFF_LEN+1;
            //byte[] buf = new byte[(int) BUFF_LEN];
            SM4Utils sm4 = new SM4Utils();
            //int len=0;
            destFilePath=destFilePath+".dat";
            out_raf = new RandomAccessFile(destFilePath, "rw");
            out_raf.seek(0);
            if(fileLength<=BUFF_LEN){
                byte[] buf = new byte[(int) fileLength];
                sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
                //buff = sm4.doEncrypt_ecb(buff);
                input.read(buf);
                buf = sm4.doFinalEncrypt_ecb(buf);
                out_raf.write(buf);
            }else{
                for(int i = 0; i < times; i++){
                    if(i==(times-1)){
                        byte[] buf = new byte[(int) left];
                        sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
                        //buff = sm4.doEncrypt_ecb(buff);
                        input.read(buf);
                        buf = sm4.doFinalEncrypt_ecb(buf);
                        out_raf.write(buf);
                    }else{
                        byte[] buf = new byte[(int) BUFF_LEN];
                        sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
                        //buff = sm4.doEncrypt_ecb(buff);
                        input.read(buf);
                        buf = sm4.doEncrypt_ecb(buf);
                        out_raf.write(buf);
                    }
                }
            }
            //if(file.)
            if(input!=null){
                input.close();
            }
            out_raf.close();
            b1=true;

//            long start4=System.currentTimeMillis();
//            //System.out.println("destFilePath = "+destFilePath+".evp");
//            if(evpData==null){
//                System.out.println("evpData is null");
//            }
//            //b2=rwFile.writeFile(destFilePath+".evp",evpData.getBytes());
//            destFilePath=destFilePath.substring(0,destFilePath.length()-4);
//            b2=rwFile.writeFile(destFilePath+".key",evpData.getBytes());
//
//            long end4=System.currentTimeMillis();
//            logger.info("writeFile use time: " + (end4-start4) + " ms");
            b2=true;

            if(b1 && b2){
                b=true;
                System.out.println("enFile: encode file success");
                logger.info("enFile: encode file success");
            }else{
                System.out.println("enFile: encode file fail");
                logger.error("enFile: encode file fail");
            }
        }catch (IOException e){
            logger.info("ExceptionTest Exception:",e);
            logger.error("ExceptionTest Exception:",e);
        }finally {
            try {
                if(input!=null){
                    input.close();
                }
                if(out_raf!=null){
                    out_raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        b=doFileDigest(destFilePath);
        return b;
    }

    /**
     * 生成数字信封文件
     * @param evpData  数字信息内容
     * @param destFilePath  key文件名
     * @time 2021-04-28 15:45
     * @time 2021-05-12 10:16
     * @return
     */
    public boolean generateKeyFile(String evpData,String destFilePath){
        boolean b=false;
        if(evpData.isEmpty() || evpData==null){
            logger.error("加密机错误：evpData.isEmpty() || evpData==null");
            logger.info("加密机错误：evpData.isEmpty() || evpData==null");
            return b;
        }
        RWFile rw = new RWFile();
        b=rw.writeFile(destFilePath+".key",evpData.getBytes());
        if(!b){
            logger.error("generate key file fail");
            logger.info("generate key file fail");
            return b;
        }
        return b;
    }

    
    //不做摘要
    public boolean enFile1(String srcDir, String destDir, String fileName){
        if (srcDir == null || srcDir.isEmpty()) {
            System.out.println("enFile: srcDir is null or empty");
            logger.error("enFile: srcDir is null or empty");
            return false;
        }
        if (destDir == null || destDir.isEmpty()){
            System.out.println("enFile: destDir is null or empty");
            logger.error("enFile: destDir is null or empty");
            return false;
        }
        if(fileName==null || fileName.isEmpty()){
            System.out.println("enFile: fileName is null or empty");
            logger.error("enFile: fileName is null or empty");
            return false;
        }
        File  dir=new File(destDir);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        boolean b=false;
        boolean b1;
        boolean b2;
        EncodeFile ef=new EncodeFile();
        RWFile rwFile=new RWFile();

        long start0=System.currentTimeMillis();
        byte[] originData=readFile(srcDir,fileName);
        long end0=System.currentTimeMillis();
        logger.info("readFile use time: " + (end0-start0) + " ms");

        long start1=System.currentTimeMillis();
        String randomN=ef.generateRandom();
        long end1=System.currentTimeMillis();
        logger.info("generateRandom use time: " + (end1-start1) + " ms");

        long start2=System.currentTimeMillis();
        byte[] encrypted= ef.sm4Encode(randomN,originData);
        long end2=System.currentTimeMillis();
        logger.info("sm4Encode use time: " + (end2-start2) + " ms");

        long start3=System.currentTimeMillis();
        String evpData=ef.encodeSignedAndEnvelope(randomN);
        long end3=System.currentTimeMillis();
        logger.info("encodeSignedAndEnvelope use time: " + (end3-start3) + " ms");

        long start4=System.currentTimeMillis();
        b1= rwFile.writeFile(destDir,fileName,encrypted);
        b2=rwFile.writeFile(destDir,fileName+".evp",evpData.getBytes());
        long end4=System.currentTimeMillis();
        logger.info("writeFile use time: " + (end4-start4) + " ms");

        if(b1 && b2){
            b=true;
            System.out.println("enFile: encode file success");
            logger.info("enFile: encode file success");
        }else{
            System.out.println("enFile: encode file fail");
            logger.error("enFile: encode file fail");
        }
        return b;
    }


    /**
     * 对文件加密: 获取随机数密钥，对源文加密，对密文做摘要，对随机数密钥和摘要封装数字信封
     * @param srcDir 原文件目录
     * @param destDir 目标文件目录
     * @param fileName 文件名
     */
//    public boolean enFile(String srcDir, String destDir, String fileName){
//        if (srcDir == null || srcDir.isEmpty()) {
//            System.out.println("enFile: srcDir is null or empty");
//            logger.error("enFile: srcDir is null or empty");
//            return false;
//        }
//        if (destDir == null || destDir.isEmpty()){
//            System.out.println("enFile: destDir is null or empty");
//            logger.error("enFile: destDir is null or empty");
//            return false;
//        }
//        if(fileName==null || fileName.isEmpty()){
//            System.out.println("enFile: fileName is null or empty");
//            logger.error("enFile: fileName is null or empty");
//            return false;
//        }
//        File  dir=new File(destDir);
//        if (!dir.exists() && !dir.isDirectory()) {
//            dir.mkdirs();
//        }
//        boolean b=false;
//        boolean b1;
//        boolean b2;
//        EncodeFile ef=new EncodeFile();
//        RWFile rwFile=new RWFile();
//        byte[] originData=readFile(srcDir,fileName);
//        String randomN=ef.generateRandom();
//        byte[] encrypted= ef.sm4Encode(randomN,originData);
//        byte[] hash=ef.digestSm3(encrypted);  //对密文做摘要
//        String evpData=ef.encodeSignedAndEnvelope(randomN,hash);
//        //String evpData= ef.encodeEnvelope(randomN);
//        b1= rwFile.writeFile(destDir,fileName,encrypted);
//        b2=rwFile.writeFile(destDir,fileName+".evp",evpData.getBytes());
//        //b2=rwFile.writeFile(destDir,fileName.substring(0, fileName.indexOf("."))+".evp",evpData.getBytes());
//        if(b1 && b2){
//            b=true;
//            System.out.println("enFile: encode file success");
//            logger.info("enFile: encode file success");
//        }else{
//            System.out.println("enFile: encode file fail");
//            logger.error("enFile: encode file fail");
//        }
//        return b;
//    }

    /**
     * 新解密，
     * @param srcFilePath
     * @param destFilePath
     * @return
     */
    public boolean deFile2(String srcFilePath, String destFilePath) {
        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println("deFile: srcDir is null");
            logger.error("deFile: srcDir is null");
            return false;
        }
        if (destFilePath == null || destFilePath.isEmpty()){
            System.out.println("deFile: destDir is null");
            logger.error("deFile: destDir is null");
            return false;
        }
        InputStream input = null;
        RandomAccessFile out_raf=null;
        String deEvpData=null;
        boolean b=false;
        DecodeFile df=new DecodeFile();

        try {
            long start_0=System.currentTimeMillis();

            //byte[] encrypted=readFile(srcFilePath);

            //byte[] evpData=readFile(srcFilePath+".evp");
            byte[] evpData=readFile(srcFilePath.substring(0,srcFilePath.length()-4)+".key");

            if(evpData==null || evpData.length<=0){
                logger.error("密钥文件错误：evpData==null || evpData.length<=0");
                logger.info("密钥文件错误：evpData==null || evpData.length<=0");
                return b;
            }
            long end_0=System.currentTimeMillis();
            logger.info("readFile use time: " + (end_0-start_0) + " ms");

            //解封之后就是随机数
            long start_1=System.currentTimeMillis();
            deEvpData=df.decodeSignedAndEnvelope((new String(evpData,"UTF-8")));
            if(deEvpData.isEmpty() || deEvpData == null){
                logger.error("解密机错误：deEvpData.isEmpty() || deEvpData == null");
                logger.info("解密机错误：deEvpData.isEmpty() || deEvpData == null");
                return b;
            }
            long end_1=System.currentTimeMillis();
            logger.info("decodeSignedAndEnvelope use time: " + (end_1-start_1) + " ms");

            //新的加密，以 16M 为界限，每16M使用doDecrypt_ecb加密，最后left使用doFinalDecrypt_ecb加密
            //解密，以16M 为界限，每16M使用doDecrypt_ecb解密，最后left使用doFinalDecrypt_ecb解密
            //有个问题，假如 源文件正好16M，加密后会变成（16M + 16）字节，也即是最后一个left要以（16M+16字节）为界限
            //(1) 假如最后一块 <=16M && >16字节，正常
            //（2）假如最后一块为<=16字节，则将倒数第二块和最后一块，一并使用doFinalDecrypt_ecb解密

            File file = new File(srcFilePath);
            //InputStream input = null;
            if(file.isFile()){
                input =new FileInputStream(file);
            }else{
                return b;
            }
            long fileLength = file.length();
            if(fileLength<=0){
                b=false;
                return b;
            }
            //定义一个16MB缓冲区
            long BUFF_LEN = 16 * 1024 * 1024;  //16M
            long left = fileLength % BUFF_LEN;
            long times = fileLength / BUFF_LEN+1;
            //byte[] buf = new byte[(int) BUFF_LEN];
            SM4Utils sm4 = new SM4Utils();
            sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));
            int len=0;
            out_raf = new RandomAccessFile(destFilePath, "rw");
            out_raf.seek(0);
            if(fileLength<=BUFF_LEN){
                byte[] buf = new byte[(int) fileLength];
                //sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));  //随机密钥
                //buff = sm4.doEncrypt_ecb(buff);
                input.read(buf);
                buf = sm4.doFinalDecrypt_ecb(buf);
                out_raf.write(buf);
            }else{
                if(left<=16){
                    if(times>2){
                        for(int i = 0; i < times-2; i++){
                            byte[] buf = new byte[(int) BUFF_LEN];
                            //随机密钥
                            //buff = sm4.doEncrypt_ecb(buff);
                            input.read(buf);
                            buf = sm4.doDecrypt_ecb(buf);
                            out_raf.write(buf);
                        }
                        byte[] buf = new byte[(int) (BUFF_LEN+left)];
                        //sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));  //随机密钥
                        //buff = sm4.doEncrypt_ecb(buff);
                        input.read(buf);
                        buf = sm4.doFinalDecrypt_ecb(buf);
                        out_raf.write(buf);
                    }else {
                        byte[] buf = new byte[(int) (BUFF_LEN+left)];
                        //sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));  //随机密钥
                        //buff = sm4.doEncrypt_ecb(buff);
                        input.read(buf);
                        buf = sm4.doFinalDecrypt_ecb(buf);
                        out_raf.write(buf);
                    }
                }else{
                    for(int i = 0; i < times; i++){
                        if(i==(times-1)){
                            byte[] buf = new byte[(int) left];
                            //sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));  //随机密钥
                            //buff = sm4.doEncrypt_ecb(buff);
                            input.read(buf);
                            buf = sm4.doFinalDecrypt_ecb(buf);
                            out_raf.write(buf);
                        }else{
                            byte[] buf = new byte[(int) BUFF_LEN];
                            //sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));  //随机密钥
                            //buff = sm4.doEncrypt_ecb(buff);
                            input.read(buf);
                            buf = sm4.doDecrypt_ecb(buf);
                            out_raf.write(buf);
                        }
                    }
                }

            }
            if(input!=null){
                input.close();
            }
            out_raf.close();
            b=true;

            if(b){
                System.out.println("deFile: decode file success");
                logger.info("deFile: decode file success");
            }else{
                System.out.println("deFile: decode file fail");
                logger.error("deFile: decode file fail");
            }
            long end_3=System.currentTimeMillis();
            //logger.info("writeFile use time: " + (end_3-start_3) + " ms");
        } catch (IOException e) {
            //e.printStackTrace();
            logger.info("ExceptionTest Exception:",e);
            logger.error("ExceptionTest Exception:",e);
        }finally {
            try {
                if(input!=null){
                    input.close();
                }
                if(out_raf!=null){
                    out_raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }


    /**
     * 新解密，2021-03-30
     * @param srcFilePath   待解密的源文件
     * *@param destPath  解密文件存放目录
     * @return
     */
    public boolean deFile3(String srcFilePath, String destPath) {
        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println("deFile: srcDir is null");
            logger.error("deFile: srcDir is null");
            return false;
        }
        if (destPath == null || destPath.isEmpty()){
            System.out.println("deFile: destDir is null");
            logger.error("deFile: destDir is null");
            return false;
        }
        RWFile re = new RWFile();
        InputStream input = null;
        RandomAccessFile out_raf=null;
        String deEvpData=null;
        boolean b=false;
        DecodeFile df=new DecodeFile();
        String destFilePath=null;
        byte[] evpData = null;
        try {
            long start_0=System.currentTimeMillis();

            //byte[] encrypted=readFile(srcFilePath);

            //byte[] evpData=readFile(srcFilePath+".evp");
            //获取分片的key文件，并组合
            //String keyFile = re.findKeyFile(srcFilePath);
            //findKeyFileOnly 针对多个dat文件对应一个key
            String keyFile = re.findKeyFileOnly(srcFilePath);

            if (keyFile!=null){
                evpData=readFile(keyFile);
            }else{
                System.out.println("keyFile is null");
                logger.error("keyFile is null");
                return false;
            }
            //evpData=readFile(srcFilePath.substring(0,srcFilePath.length()-4)+".key");

            if(evpData==null || evpData.length<=0){
                logger.error("密钥文件错误：evpData==null || evpData.length<=0");
                logger.info("密钥文件错误：evpData==null || evpData.length<=0");
                return b;
            }
            long end_0=System.currentTimeMillis();
            logger.info("readFile use time: " + (end_0-start_0) + " ms");

            //解封之后就是随机数
            long start_1=System.currentTimeMillis();
            deEvpData=df.decodeSignedAndEnvelope((new String(evpData,"UTF-8")));
            if(deEvpData.isEmpty() || deEvpData == null){
                logger.error("解密机错误：deEvpData.isEmpty() || deEvpData == null");
                logger.info("解密机错误：deEvpData.isEmpty() || deEvpData == null");
                return b;
            }
            long end_1=System.currentTimeMillis();
            logger.info("decodeSignedAndEnvelope use time: " + (end_1-start_1) + " ms");

            //新的加密，以 16M 为界限，每16M使用doDecrypt_ecb加密，最后left使用doFinalDecrypt_ecb加密
            //解密，以16M 为界限，每16M使用doDecrypt_ecb解密，最后left使用doFinalDecrypt_ecb解密
            //有个问题，假如 源文件正好16M，加密后会变成（16M + 16）字节，也即是最后一个left要以（16M+16字节）为界限
            //(1) 假如最后一块 <=16M && >16字节，正常
            //（2）假如最后一块为<=16字节，则将倒数第二块和最后一块，一并使用doFinalDecrypt_ecb解密

            File file = new File(srcFilePath);
            //destFilePath = destPath + file.getName().split("@",2)[0];
            destFilePath = destPath + file.getName().split("&",2)[1].split("@",2)[0];
            //InputStream input = null;
            if(file.isFile()){
                input =new FileInputStream(file);
            }else{
                return b;
            }
            long fileLength = file.length();
            if(fileLength<=0){
                b=false;
                return b;
            }
            //定义一个16MB缓冲区
            long BUFF_LEN = 16 * 1024 * 1024;  //16M
            long left = fileLength % BUFF_LEN;
            long times = fileLength / BUFF_LEN+1;
            //byte[] buf = new byte[(int) BUFF_LEN];
            SM4Utils sm4 = new SM4Utils();
            sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));
            int len=0;
            out_raf = new RandomAccessFile(destFilePath, "rw");
            out_raf.seek(0);
            if(fileLength<=BUFF_LEN){
                byte[] buf = new byte[(int) fileLength];
                //sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));  //随机密钥
                //buff = sm4.doEncrypt_ecb(buff);
                input.read(buf);
                buf = sm4.doFinalDecrypt_ecb(buf);
                out_raf.write(buf);
            }else{
                if(left<=16){
                    if(times>2){
                        for(int i = 0; i < times-2; i++){
                            byte[] buf = new byte[(int) BUFF_LEN];
                            //随机密钥
                            //buff = sm4.doEncrypt_ecb(buff);
                            input.read(buf);
                            buf = sm4.doDecrypt_ecb(buf);
                            out_raf.write(buf);
                        }
                        byte[] buf = new byte[(int) (BUFF_LEN+left)];
                        //sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));  //随机密钥
                        //buff = sm4.doEncrypt_ecb(buff);
                        input.read(buf);
                        buf = sm4.doFinalDecrypt_ecb(buf);
                        out_raf.write(buf);
                    }else {
                        byte[] buf = new byte[(int) (BUFF_LEN+left)];
                        //sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));  //随机密钥
                        //buff = sm4.doEncrypt_ecb(buff);
                        input.read(buf);
                        buf = sm4.doFinalDecrypt_ecb(buf);
                        out_raf.write(buf);
                    }
                }else{
                    for(int i = 0; i < times; i++){
                        if(i==(times-1)){
                            byte[] buf = new byte[(int) left];
                            //sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));  //随机密钥
                            //buff = sm4.doEncrypt_ecb(buff);
                            input.read(buf);
                            buf = sm4.doFinalDecrypt_ecb(buf);
                            out_raf.write(buf);
                        }else{
                            byte[] buf = new byte[(int) BUFF_LEN];
                            //sm4.setSecretKey(Base64.getDecoder().decode(deEvpData));  //随机密钥
                            //buff = sm4.doEncrypt_ecb(buff);
                            input.read(buf);
                            buf = sm4.doDecrypt_ecb(buf);
                            out_raf.write(buf);
                        }
                    }
                }

            }
            if(input!=null){
                input.close();
            }
            out_raf.close();
            b=true;

            if(b){
                System.out.println("deFile: decode file success");
                logger.info("deFile: decode file success");
            }else{
                System.out.println("deFile: decode file fail");
                logger.error("deFile: decode file fail");
            }
            long end_3=System.currentTimeMillis();
            //logger.info("writeFile use time: " + (end_3-start_3) + " ms");
        } catch (IOException e) {
            //e.printStackTrace();
            logger.info("ExceptionTest Exception:",e);
            logger.error("ExceptionTest Exception:",e);
        }finally {
            try {
                if(input!=null){
                    input.close();
                }
                if(out_raf!=null){
                    out_raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }


    /**
     * 只有两个参数
     * *@param srcDir
     ** *@param destDir
     * *@param fileName
     * @return
     */
    public boolean deFile1(String srcFilePath, String destFilePath) {
        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println("deFile: srcDir is null");
            logger.error("deFile: srcDir is null");
            return false;
        }
        if (destFilePath == null || destFilePath.isEmpty()){
            System.out.println("deFile: destDir is null");
            logger.error("deFile: destDir is null");
            return false;
        }
       
        String deEvpData=null;
        boolean b=false;
        DecodeFile df=new DecodeFile();
        try {
            long start_0=System.currentTimeMillis();
            byte[] encrypted=readFile(srcFilePath);
            byte[] evpData=readFile(srcFilePath+".evp");
            long end_0=System.currentTimeMillis();
            logger.info("readFile use time: " + (end_0-start_0) + " ms");

            //解封之后就是随机数
            long start_1=System.currentTimeMillis();
            deEvpData=df.decodeSignedAndEnvelope((new String(evpData,"UTF-8")));
            long end_1=System.currentTimeMillis();
            logger.info("decodeSignedAndEnvelope use time: " + (end_1-start_1) + " ms");

            long start_2=System.currentTimeMillis();
            byte[] originData=df.sm4Decode(Base64.getDecoder().decode(deEvpData),encrypted);
            if (originData==null || originData.length<=0){
                System.out.println("deFile: sm4Decode fail");
                logger.error("deFile: sm4Decode fail");
                return false;
            }
            long end_2=System.currentTimeMillis();
            logger.info("sm4Decode use time: " + (end_2-start_2) + " ms");

            long start_3=System.currentTimeMillis();
            b = writeFile(destFilePath,originData);
            if(b){
                System.out.println("deFile: decode file success");
                logger.info("deFile: decode file success");
            }else{
                System.out.println("deFile: decode file fail");
                logger.error("deFile: decode file fail");
            }
            long end_3=System.currentTimeMillis();
            logger.info("writeFile use time: " + (end_3-start_3) + " ms");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }
        return b;
    }
    
    
    
    //不做摘要对比，只解密
    public boolean deFile1(String srcDir, String destDir, String fileName) {
        if (srcDir == null || srcDir.isEmpty()) {
            System.out.println("deFile: srcDir is null");
            logger.error("deFile: srcDir is null");
            return false;
        }
        if (destDir == null || destDir.isEmpty()){
            System.out.println("deFile: destDir is null");
            logger.error("deFile: destDir is null");
            return false;
        }
        if(fileName==null || fileName.isEmpty()){
            System.out.println("deFile: fileName is null");
            logger.error("deFile: fileName is null");
            return false;
        }
        File  dir=new File(destDir);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        String deEvpData=null;
        boolean b=false;
        DecodeFile df=new DecodeFile();
        try {
            long start_0=System.currentTimeMillis();
            byte[] encrypted=readFile(srcDir,fileName);
            byte[] evpData=readFile(srcDir,fileName+".evp");
            long end_0=System.currentTimeMillis();
            logger.info("readFile use time: " + (end_0-start_0) + " ms");

            //解封之后就是随机数
            long start_1=System.currentTimeMillis();
            deEvpData=df.decodeSignedAndEnvelope((new String(evpData,"UTF-8")));
            long end_1=System.currentTimeMillis();
            logger.info("decodeSignedAndEnvelope use time: " + (end_1-start_1) + " ms");

            long start_2=System.currentTimeMillis();
            byte[] originData=df.sm4Decode(Base64.getDecoder().decode(deEvpData),encrypted);
            if (originData==null || originData.length<=0){
                System.out.println("deFile: sm4Decode fail");
                logger.error("deFile: sm4Decode fail");
                return false;
            }
            long end_2=System.currentTimeMillis();
            logger.info("sm4Decode use time: " + (end_2-start_2) + " ms");

            long start_3=System.currentTimeMillis();
            b = writeFile(destDir,fileName,originData);
            if(b){
                System.out.println("deFile: decode file success");
                logger.info("deFile: decode file success");
            }else{
                System.out.println("deFile: decode file fail");
                logger.error("deFile: decode file fail");
            }
            long end_3=System.currentTimeMillis();
            logger.info("writeFile use time: " + (end_3-start_3) + " ms");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }
        return b;
    }


    /**
     * 解密生成源文件, 验签并拆分数字信封，验证摘要，解密
     * @param srcDir 原文件目录
     * @param destDir 目标文件目录
     * @param fileName 文件名
     * @return
     */
    //此处应有两个文件目录，原始文件目录scrDir，目标目录 destDir
    public boolean deFile(String srcDir, String destDir, String fileName) {
        if (srcDir == null || srcDir.isEmpty()) {
            System.out.println("deFile: srcDir is null");
            logger.error("deFile: srcDir is null");
            return false;
        }
        if (destDir == null || destDir.isEmpty()){
            System.out.println("deFile: destDir is null");
            logger.error("deFile: destDir is null");
            return false;
        }
        if(fileName==null || fileName.isEmpty()){
            System.out.println("deFile: fileName is null");
            logger.error("deFile: fileName is null");
            return false;
        }
        File  dir=new File(destDir);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        String deEvpData=null;
        boolean b=false;
        DecodeFile df=new DecodeFile();
        try {
            byte[] encrypted=readFile(srcDir,fileName);
            byte[] evpData=readFile(srcDir,fileName+".evp");
            deEvpData=df.decodeSignedAndEnvelope((new String(evpData,"UTF-8")));
            boolean checkB=df.checkDigestSm3(encrypted,deEvpData);
            if (checkB){
                System.out.println("deFile: checkDigestSm3 file success");
                logger.info("deFile: checkDigestSm3 file success");
                byte[] randomN=df.getRandomN(deEvpData);
                byte[] originData=df.sm4Decode(randomN,encrypted);
                if (originData==null || originData.length<=0){
                    System.out.println("deFile: sm4Decode fail");
                    logger.error("deFile: sm4Decode fail");
                    return false;
                }
                b = writeFile(destDir,fileName,originData);
                if(b){
                    System.out.println("deFile: decode file success");
                    logger.info("deFile: decode file success");
                }else{
                    System.out.println("deFile: decode file fail");
                    logger.error("deFile: decode file fail");
                }
            }else{
                System.out.println("deFile: checkDigestSm3 file fail");
                logger.error("deFile: checkDigestSm3 file fail");
            }
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }
        return b;
    }

    /**
             * 读文件内容，返回byte[]
     * @param srcFilePath
     * @return
     */
    public byte[] readFile(String srcFilePath) {
        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println("readFile: srcFilePath is null");
            logger.error("readFile: srcFilePath is null");
            return null;
        }
       
        byte[] fileByte=null; 
        File file = new File(srcFilePath);
        if (!file.exists()){
            System.out.println("readFile: " + srcFilePath+ " is not exist");
            logger.error("readFile: " + srcFilePath+ " is not exist");
            return null;
        }
        //System.out.println("filePath = "+srcFilePath);
        try {
            fileByte = Files.readAllBytes(Paths.get(srcFilePath));
            if (fileByte!=null || fileByte.length<=0){
                System.out.println("readFile: finish");
                logger.info("readFile: finish");
            }
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }

        return fileByte;
    }
    
    
    /**
              * 读文件内容，返回byte[]
     * @param srcDir  文件所在目录
     * @param fileName  文件名
     * @return  返回byte[]
     */
    public byte[] readFile(String srcDir, String fileName) {
        if (srcDir == null || srcDir.isEmpty()) {
            System.out.println("readFile: srcDir is null");
            logger.error("readFile: srcDir is null");
            return null;
        }
        if(fileName==null || fileName.isEmpty()){
            System.out.println("readFile: fileName is null");
            logger.error("readFile: fileName is null");
            return null;
        }
        byte[] fileByte=null;
        StringBuilder filePath=new StringBuilder();
        //windows linux '\\' '\/',判断环境变量
        if(judgeSystem()==0) {
        	filePath=filePath.append(srcDir).append("\\").append(fileName);
        }else {
        	filePath=filePath.append(srcDir).append("/").append(fileName);
        }
        
        //filePath=filePath.append(srcDir).append("\\").append(fileName);
        File file = new File(filePath.toString());
        if (!file.exists()){
            System.out.println("readFile: " + fileName+ " is not exist");
            logger.error("readFile: " + fileName+ " is not exist");
            return null;
        }
        //System.out.println("filePath = "+filePath);
        try {
            fileByte = Files.readAllBytes(Paths.get(filePath.toString()));
            if (fileByte!=null || fileByte.length<=0){
                System.out.println("readFile: finish");
                logger.info("readFile: finish");
            }
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }

        return fileByte;
    }

    //生成文件

    /**
     * 将内容写入文件，并生成密文文件和数字信封文件
     * @param directoryPath  文件目录
     * @param fileName  文件名
     * @param encrypted  文件内容
     * @param evpData 数字信封内容
     * @return  返回true or false
     */
//    public boolean write2File(String directoryPath, String fileName, byte[] encrypted, byte[] evpData) {
//        if (directoryPath == null) {
//            System.out.println("writeFile: directoryPath is null");
//            logger.error("writeFile: directoryPath is null");
//            return false;
//        }
//        if(fileName==null){
//            System.out.println("writeFile: fileName is null");
//            logger.error("writeFile: fileName is null");
//            return false;
//        }
//        if (encrypted==null){
//            System.out.println("writeFile: encrypted is null");
//            logger.error("writeFile: encrypted is null");
//            return false;
//        }
//        if (evpData==null){
//            System.out.println("writeFile: evpData is null");
//            logger.error("writeFile: evpData is null");
//            return false;
//        }
//
//        boolean flag=false;
//        File file = null;
//        File fileEvp=null;
//        File  dir=new File(directoryPath);
//        if (!dir.exists() && !dir.isDirectory()) {
//            dir.mkdirs();
//        }
//        BufferedOutputStream bos = null;
//        FileOutputStream fos = null;
//        BufferedOutputStream bosEvp = null;
//        FileOutputStream fosEvp = null;
//        try {
//            //byte[] bytes = Base64.getDecoder().decode(cipher_data);
//            file=new File(directoryPath+"/"+fileName);  //windows和linux '/' '\'
//            fos = new FileOutputStream(file);
//            bos = new BufferedOutputStream(fos);
//            bos.write(encrypted);
//            bos.close();
//            fos.close();
//            fileEvp=new File(directoryPath+"/"+fileName.substring(0, fileName.indexOf("."))+".evp");  //windows和linux '/' '\'
//            fosEvp = new FileOutputStream(fileEvp);
//            bosEvp = new BufferedOutputStream(fosEvp);
//            bosEvp.write(evpData);
//            bosEvp.close();
//            fosEvp.close();
//            flag=true;
//            System.out.println("writeFile: finish");
//            logger.info("writeFile: finish");
//        } catch (Exception e) {
//            flag=false;
//            //e.printStackTrace();
//            logger.error("ExceptionTest Exception:",e);
//        } finally {
//            if (bosEvp != null) {
//                try {
//                    bosEvp.close();
//                } catch (IOException e) {
//                    //e.printStackTrace();
//                    logger.error("ExceptionTest Exception:",e);
//                }
//            }
//            if (fosEvp != null) {
//                try {
//                    fosEvp.close();
//                } catch (IOException e) {
//                    //e.printStackTrace();
//                    logger.error("ExceptionTest Exception:",e);
//                }
//            }
//            if (bos != null) {
//                try {
//                    bos.close();
//                } catch (IOException e) {
//                    //e.printStackTrace();
//                    logger.error("ExceptionTest Exception:",e);
//                }
//            }
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    //e.printStackTrace();
//                    logger.error("ExceptionTest Exception:",e);
//                }
//            }
//        }
//        return flag;
//    }


    //

    /**
     * write file
     * @param destFilePath  文件路径
     * @param fileByte 	  要写入的内容
     * @return
     */
    public boolean writeFile(String destFilePath, byte[] fileByte) {
        if(destFilePath==null || destFilePath.isEmpty()){
            System.out.println("writeFile: fileName is null");
            logger.error("writeFile: fileName is null");
            return false;
        }
        if (fileByte==null || fileByte.length<=0){
            System.out.println("writeFile: encrypted is null");
            logger.error("writeFile: encrypted is null");
            return false;
        }
        boolean flag=false;
//        StringBuilder filePath=new StringBuilder();
//        if(judgeSystem()==0) {
//        	filePath=filePath.append(destDir).append("\\").append(fileName);
//        }else {
//        	filePath=filePath.append(destDir).append("/").append(fileName);
//        }        
//        
//        File file = new File(filePath.toString());  //windows和linux '/' '\'
//        
//        File  dir=new File(destDir);
//        if (!dir.exists() && !dir.isDirectory()) {
//            dir.mkdirs();
//        }
        File file=new File(destFilePath);
        BufferedOutputStream bos = null;
        //OutputStreamWriter out=null;
        FileOutputStream fos = null;
        try {
            //byte[] bytes = Base64.getDecoder().decode(cipher_data);
            //file=new File(directoryPath+"/"+fileName);  //windows和linux '/' '\'
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(fileByte);
            //out.write(fileByte);
            bos.flush();
            bos.close();
            fos.close();
            flag=true;
            System.out.println("writeFile: finish");
            logger.info("writeFile: finish");
        } catch (Exception e) {
            flag=false;
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                    logger.error("ExceptionTest Exception:",e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                    logger.error("ExceptionTest Exception:",e);
                }
            }
        }
        return flag;
    }
    
    
    
    /**
     * 将内容写入文件
     * @param destDir  文件存储目录
     * @param fileName  文件名
     * @param fileByte  文件内容 byte[]
     * @return
     */
    public boolean writeFile(String destDir, String fileName, byte[] fileByte) {
        if (destDir == null || destDir.isEmpty()) {
            System.out.println("writeFile: srcDir is null");
            logger.error("writeFile: srcDir is null");
            return false;
        }
        if(fileName==null || fileName.isEmpty()){
            System.out.println("writeFile: fileName is null");
            logger.error("writeFile: fileName is null");
            return false;
        }
        if (fileByte==null || fileByte.length<=0){
            System.out.println("writeFile: encrypted is null");
            logger.error("writeFile: encrypted is null");
            return false;
        }
        boolean flag=false;
        StringBuilder filePath=new StringBuilder();
        if(judgeSystem()==0) {
        	filePath=filePath.append(destDir).append("\\").append(fileName);
        }else {
        	filePath=filePath.append(destDir).append("/").append(fileName);
        }        
        
        File file = new File(filePath.toString());  //windows和linux '/' '\'
        
        File  dir=new File(destDir);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        //OutputStreamWriter out=null;
        FileOutputStream fos = null;
        try {
            //byte[] bytes = Base64.getDecoder().decode(cipher_data);
            //file=new File(directoryPath+"/"+fileName);  //windows和linux '/' '\'
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(fileByte);
            //out.write(fileByte);
            bos.flush();
            bos.close();
            fos.close();
            flag=true;
            System.out.println("writeFile: finish");
            logger.info("writeFile: finish");
        } catch (Exception e) {
            flag=false;
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                    logger.error("ExceptionTest Exception:",e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                    logger.error("ExceptionTest Exception:",e);
                }
            }
        }
        return flag;
    }
    
    public int judgeSystem() {
    	int flag=0;  //0 windown, 1 linux
    	String osName = System.getProperties().getProperty("os.name");
        if(osName.equals("Linux"))
        {
        	flag=1;
            System.out.println("running in Linux");
        }
        else
        {        	
            System.out.println("don't running in Linux");
        }
        return flag;
    }

    /**
     * 获取sm3摘要值,对整个文件做摘要
     * @param destFilePath
     * @return
     */
    public String getSm3Digest(String destFilePath)  {
        if (destFilePath == null || destFilePath.isEmpty()){
            System.out.println(destFilePath + " is null or empty");
            logger.error(destFilePath + " is null or empty");
            return null;
        }
        String hashData64=null;
        InputStream in=null;
        byte[] md=new byte[32];
        File file = new File(destFilePath);
        try {
            if (file.isFile()) {  //判断是否是目录，是目录会报错
                in = new FileInputStream(file);
            } else {
                System.out.println(destFilePath + " is not file");
                logger.error(destFilePath + " is not file");
                return hashData64;
            }
            long fileLength = file.length();
            if (fileLength <= 0) {
                System.out.println(destFilePath + " length is 0");
                logger.error(destFilePath + " length is 0");
                return hashData64;
            }
            //定义一个16MB缓冲区
            long BUFF_LEN = 16 * 1024 * 1024;  //16M
            long left = fileLength % BUFF_LEN;
            long times = fileLength / BUFF_LEN+1;
            SM3Digest sm3 = new SM3Digest();
            Base64.Encoder b64e = Base64.getEncoder();
            if(fileLength<=BUFF_LEN){
                byte[] buf = new byte[(int) fileLength];
                in.read(buf);
                sm3.update(buf,0, (int) fileLength);
                sm3.doFinal(md,0);
                //return b64e.encodeToString(md);
            }else{
                for(int i = 0; i < times; i++){
                    if(i==(times-1)){
                        //byte[] buf = new byte[(int) left];
                        sm3.doFinal(md,0);
                    }else{
                        byte[] buf = new byte[(int) BUFF_LEN];
                        in.read(buf);
                        sm3.update(buf,0, (int) BUFF_LEN);
                    }
                }
            }
            hashData64 = b64e.encodeToString(md);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }finally {
            try {
                if(in!=null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hashData64;
    }

    /**
     * 获取sm3摘要值,对整个文件做摘要
     * @param destFilePath
     * @param buffsize   对结尾的buffsize字节的部分文件做摘要，一般buffsize=32
     * @return
     */
    public String getSm3Digest(String destFilePath,long buffsize)  {
        if (destFilePath == null || destFilePath.isEmpty()){
            System.out.println(destFilePath + " is null or empty");
            logger.error(destFilePath + " is null or empty");
            return null;
        }
        String hashData64=null;
        InputStream in=null;
        byte[] md=new byte[32];
        File file = new File(destFilePath);
        try {
            if (file.isFile()) {  //判断是否是目录，是目录会报错
                in = new FileInputStream(file);
            } else {
                System.out.println(destFilePath + " is not file");
                logger.error(destFilePath + " is not file");
                return hashData64;
            }
            long fileLength = file.length();
            if (fileLength <= 0) {
                System.out.println(destFilePath + " length is 0");
                logger.error(destFilePath + " length is 0");
                return hashData64;
            }
            //定义一个16MB缓冲区
            //long BUFF_LEN = 16 * 1024 * 1024;  //16M
            long BUFF_LEN = buffsize;  //32
            long left = fileLength % BUFF_LEN;
            long times = fileLength / BUFF_LEN+1;
            SM3Digest sm3 = new SM3Digest();
            Base64.Encoder b64e = Base64.getEncoder();
            if(fileLength<=BUFF_LEN){
                byte[] buf = new byte[(int) fileLength];
                in.read(buf);
                sm3.update(buf,0, (int) fileLength);
                sm3.doFinal(md,0);
                //return b64e.encodeToString(md);
            }else{
                for(int i = 0; i < times; i++){
                    if(i==(times-1)){
                        byte[] buf = new byte[(int) left];
                        in.read(buf);
                        sm3.update(buf,0, (int) left);
                        sm3.doFinal(md,0);
                    }else{
                        byte[] buf = new byte[(int) BUFF_LEN];
                        in.read(buf);
                        //
                    }
                }
            }
            in.close();
            hashData64 = b64e.encodeToString(md);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }finally {
            try {
                if(in!=null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hashData64;
    }

    /** 针对一个多个dat文件对应一个key文件
     * 通过dat文件查询该目录下的key文件，如果key组合文件已存在，则不做操作，
     * 如果key组合文件不存在，则对已切分的key文件进行组合，还原成未切分的单个key组合文件
     * @param srcFilePath dat文件，前12+14+&，27位是key组合文件的名字
     * @return
     */
    public String findKeyFileOnly(String srcFilePath){
        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println(srcFilePath + " is null or is empty");
            logger.error(srcFilePath + " is null or is empty");
            return null;
        }
        File file = new File(srcFilePath);
        if(!file.isFile()){
            System.out.println(srcFilePath + " is not file");
            logger.error(srcFilePath + " is not file");
            return null;
        }
        String keyFile=null;
        FileInputStream fis=null;
        FileOutputStream fos=null;
        List<byte[]> fileByteList = new ArrayList<byte[]>();
        List<String> fileList = new ArrayList<String>();
        String srcFileName=file.getName().split("@",2)[0];  //txt或者xml文件
        System.out.println("srcFileName = "+srcFileName);
        String srcPath=srcFilePath.split(srcFileName,2)[0];    //文件所在路径
        System.out.println("srcPath = "+srcPath);
        File dir = new File(srcPath);
        if (!dir.isDirectory()){
            System.out.println(srcPath + " is not directory");
            logger.error(srcPath + " is not directory");
            return null;
        }

        String keyCombinName = file.getName().split("&",2)[0]+".key";
        int ketCombinNameLength = file.getName().split("&",2)[0].length();
        System.out.println("keyCombinName length =  "+ketCombinNameLength);
        String keyCombinPath =  null;
        if(judgeSystem()==0){
            keyCombinPath=srcPath+"\\"+keyCombinName;
        }else{
            keyCombinPath = srcPath+"/"+keyCombinName;
        }
        File keyCombinFile = new File(keyCombinPath);
        if(keyCombinFile.exists()){
            System.out.println(keyCombinPath+" exists");
            return keyCombinPath;
        }

        String[] children = dir.list();
        String[] temp = new String[3];
        System.out.println("children.length = " + children.length);
        if (children == null) {
            System.out.println(srcPath + " is not directory");
            logger.error(srcPath + " is not directory");
            return null;
        }else {
            for (int i = 0; i < children.length; i++) {

                //System.out.println(children[i].substring(children[i].length()-3,children[i].length()));
                //System.out.println(children[i].substring(0,(srcFileName.length())));
                if(children[i].length()>4){
                    if(children[i].substring(children[i].length()-4,children[i].length()).equals(".key")){
                        //System.out.println(children[i].substring(0,25));
                        //file.getName().split("&",2)[0];
                        if(children[i].substring(0,ketCombinNameLength).equals(file.getName().split("&",2)[0]) && children[i].substring(children[i].length()-5,children[i].length()).equals("1.key")){
                            temp[0]=children[i];
                        }

                        if(children[i].substring(0,ketCombinNameLength).equals(file.getName().split("&",2)[0]) && children[i].substring(children[i].length()-5,children[i].length()).equals("2.key")){
                            temp[1]=children[i];
                        }
                        if(children[i].substring(0,ketCombinNameLength).equals(file.getName().split("&",2)[0]) && children[i].substring(children[i].length()-5,children[i].length()).equals("3.key")){
                            temp[2]=children[i];
                        }
                    }
                }

            }
            for(int i=0;i<temp.length;i++){
                if(judgeSystem()==0){
                    fileList.add(srcPath+"\\"+temp[i]);
                }else{
                    fileList.add(srcPath+"/"+temp[i]);
                }
            }
            System.out.println(fileList.size());
            try{
                for (int i = 0; i < fileList.size(); i++) {
                    fis = new FileInputStream(new File(fileList.get(i)));
                    byte[] fbyte = new byte[fis.available()];
                    fis.read(fbyte);
                    System.out.println("分片文件：" + fileList.get(i) + "长度：" + fbyte.length);
                    fileByteList.add(fbyte);
                    fis.close();
                }
                /* 还原文件 */
                byte[] out = DataDivide.DataTransformInverse(fileByteList);

                System.out.println("还原后长度：" + out.length);
                /* 去填充 */
                byte[] res3 = DataDivide.PaddingInverse(out, DataDivide.PaddingBlockLength);
                System.out.println("去填充后长度：" + res3.length);

                if(judgeSystem()==0){
                    fos = new FileOutputStream(srcPath + "\\" + keyCombinName);// "docx");
                    //keyFile = srcPath +"\\"+ srcFileName.split("@",2)[0] + ".key";
                    keyFile = srcPath + "\\" + keyCombinName;
                }else{
                    fos = new FileOutputStream(srcPath +"/" + keyCombinName);// "linux");
                    //keyFile = srcPath +"/"+ srcFileName.split("@",2)[0] + ".key";
                    keyFile = srcPath +"/" + keyCombinName;
                }
                //fos = new FileOutputStream(srcPath + srcFileName.split("@",2)[0] + ".key");
                fos.write(res3);
                fos.flush();
                fos.close();
                fis.close();
                //keyFile = srcPath + srcFileName.split("@",2)[0] + ".key";
                //return true;
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                //return false;
            } finally {
                try {
                    if(fis!=null) {
                        fis.close();
                    }
                    if(fos!=null){
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return keyFile;
    }

    /**
     * 通过dat文件查询该目录下的key文件，并对已切分的key文件进行组合，还原成未切分的单个key文件
     * @param srcFilePath  dat文件
     * @return
     */
    public String findKeyFile(String srcFilePath){
        if (srcFilePath == null || srcFilePath.isEmpty()) {
            System.out.println(srcFilePath + " is null or is empty");
            logger.error(srcFilePath + " is null or is empty");
            return null;
        }
        File file = new File(srcFilePath);
        if(!file.isFile()){
            System.out.println(srcFilePath + " is not file");
            logger.error(srcFilePath + " is not file");
            return null;
        }
        String keyFile=null;
        FileInputStream fis=null;
        FileOutputStream fos=null;
        List<byte[]> fileByteList = new ArrayList<byte[]>();
        List<String> fileList = new ArrayList<String>();
        String srcFileName=file.getName().split("@",2)[0];  //txt或者xml文件
        System.out.println("srcFileName = "+srcFileName);
        String srcPath=srcFilePath.split(srcFileName,2)[0];    //文件所在路径
        System.out.println("srcPath = "+srcPath);
        File dir = new File(srcPath);
        if (!dir.isDirectory()){
            System.out.println(srcPath + " is not directory");
            logger.error(srcPath + " is not directory");
            return null;
        }
        String[] children = dir.list();
        String[] temp = new String[3];
        System.out.println("children.length = " + children.length);
        if (children == null) {
            System.out.println(srcPath + " is not directory");
            logger.error(srcPath + " is not directory");
            return null;
        }else {
            for (int i = 0; i < children.length; i++) {

                //System.out.println(children[i].substring(children[i].length()-3,children[i].length()));
                //System.out.println(children[i].substring(0,(srcFileName.length())));
                if(children[i].length()>=srcFileName.length()){
                    if(children[i].substring(0,(srcFileName.length())).equals(srcFileName)){
                        if(children[i].substring(children[i].length()-5,children[i].length()).equals("1.key")){
                            temp[0]=children[i];
                        }
                        if(children[i].substring(children[i].length()-5,children[i].length()).equals("2.key")){
                            temp[1]=children[i];
                        }
                        if(children[i].substring(children[i].length()-5,children[i].length()).equals("3.key")){
                            temp[2]=children[i];
                        }
                    }
                }
            }
            for(int i=0;i<temp.length;i++){
                if(judgeSystem()==0){
                    fileList.add(srcPath+"\\"+temp[i]);
                }else{
                    fileList.add(srcPath+"/"+temp[i]);
                }
            }
            System.out.println(fileList.size());
            try{
                for (int i = 0; i < fileList.size(); i++) {
                    fis = new FileInputStream(new File(fileList.get(i)));
                    byte[] fbyte = new byte[fis.available()];
                    fis.read(fbyte);
                    System.out.println("分片文件：" + fileList.get(i) + "长度：" + fbyte.length);
                    fileByteList.add(fbyte);
                    fis.close();
                }
                /* 还原文件 */
                byte[] out = DataDivide.DataTransformInverse(fileByteList);

                System.out.println("还原后长度：" + out.length);
                /* 去填充 */
                byte[] res3 = DataDivide.PaddingInverse(out, DataDivide.PaddingBlockLength);
                System.out.println("去填充后长度：" + res3.length);

                if(judgeSystem()==0){
                    fos = new FileOutputStream(srcPath +"\\"+ srcFileName.split("@",2)[0] + ".key");// "docx");
                    keyFile = srcPath +"\\"+ srcFileName.split("@",2)[0] + ".key";
                }else{
                    fos = new FileOutputStream(srcPath +"/"+ srcFileName.split("@",2)[0] + ".key");// "linux");
                    keyFile = srcPath +"/"+ srcFileName.split("@",2)[0] + ".key";
                }
                //fos = new FileOutputStream(srcPath + srcFileName.split("@",2)[0] + ".key");
                fos.write(res3);
                fos.flush();
                fos.close();
                fis.close();
                //keyFile = srcPath + srcFileName.split("@",2)[0] + ".key";
                //return true;
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                //return false;
            } finally {
                    try {
                        if(fis!=null) {
                            fis.close();
                        }
                        if(fos!=null){
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return keyFile;
    }

    public static void main(String[] args){
        //联合签名解密
        String srcFilePath="D:\\testZip\\testunionencode\\11000000000020210512100500&11000000000020214331164307_p_data_extract_t.txt@F0wLl3ztnZQiLp3M5FiSTnq3UbHlS1Ry1Aoos09ofHA=#2021031522010230.dat";
        String destFilePath = "D:\\testZip\\testdecode\\";
        //String hash64="Y1UfQn83gOrkRmFfsBNzZkpYBpYSuqYM2mxjTAeQFLs=";

        long startTime1= System.currentTimeMillis();

        RWFile re = new RWFile();
        //String keyFile = re.findKeyFileOnly(srcFilePath);

        boolean ba = re.deFile3(srcFilePath, destFilePath);

        System.out.println("result = " + ba);

        long endTime1=System.currentTimeMillis();
        System.out.println("findKeyFile use time: "+ (endTime1-startTime1));

        //联合签名加密
//        String srcFilePath = "D:\\testZip\\testdat\\11000000000020214331164307_p_data_extract_t.txt";
//        String destFilePath = "D:\\testZip\\testdat\\"+"110000000000"+"20210512100500"+"&"+"11000000000020214331164307_p_data_extract_t.txt";
//        String keyFilePath = "D:\\testZip\\testdat\\"+"110000000000"+"20210512100500";
//        RWFile rw = new RWFile();
//        EncodeFile ef = new EncodeFile();
//        String randomN = ef.generateRandom();
//        String evpData = ef.unionSignedAndEnvelope(randomN);
//
//        boolean key_b = rw.generateKeyFile(evpData,keyFilePath);
//
//        boolean file_b = rw.enFile2(randomN,srcFilePath,destFilePath);
//
//        System.out.println("union sign success");








        //RWFile rf = new RWFile();
        //rf.findKeyFile(srcFilePath);
        //System.out.println(rf.getSm3Digest(srcFilePath));
        //System.out.println(rf.getSm3Digest(srcFilePath).length());

    }


}
