package com.zjb.obs;

import ch.qos.logback.classic.Logger;
import com.zjb.utils.RWFile;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Urlutils {


    public static Logger logger = (Logger) LoggerFactory.getLogger(Urlutils.class);

    /**
     * 通过url获取照片的二进制流
     * @param imgUrl
     * @return
     */
    public static byte[] image2Base64(String imgUrl) {
        if(imgUrl.isEmpty() || imgUrl==null){
            logger.error("image2Base64 imgUrl is null or enpty");
            return null;
        }
        URL url = null;
        InputStream is = null;
        ByteArrayOutputStream outStream = null;
        HttpURLConnection httpUrl = null;
        byte[] content = null;

        try{
            url = new URL(imgUrl);
            httpUrl = (HttpURLConnection) url.openConnection();

            httpUrl.connect();
            logger.info("httpUrl.getResponseCode() = " + httpUrl.getResponseCode());
            System.out.println("httpUrl.getResponseCode() = " + httpUrl.getResponseCode());
            // httpUrl.getInputStream();
            if(httpUrl.getResponseCode()!=200){
                logger.error("httpUrl.getResponseCode() = " + httpUrl.getResponseCode());
                System.out.println("httpUrl.getResponseCode() = " + httpUrl.getResponseCode());
                httpUrl.disconnect();
                return null;
            }

            is = httpUrl.getInputStream();

            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while( (len=is.read(buffer)) != -1 ){
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            content = outStream.toByteArray();

        }catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            logger.error("image2Base64 fail");
        }finally{
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outStream != null){
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(httpUrl != null){
                httpUrl.disconnect();
            }
        }

        return content;
    }



    public static byte[] image2Byte(String imgPath) {
        if(imgPath.isEmpty() || imgPath==null){
            logger.error("image2Byte imgPath is null or enpty");
            return null;
        }
        File file = new File(imgPath);
        if(!file.isFile()){
            logger.error("image2Byte imgPath is not a file");
            return null;
        }
        InputStream is = null;
        ByteArrayOutputStream outStream = null;
        byte[] content = null;
        try{
            is = new FileInputStream(new File(imgPath));
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while( (len=is.read(buffer)) != -1 ){
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            content = outStream.toByteArray();

        }catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            logger.error("image2Base64 fail");
        }finally{
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outStream != null){
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }


    public static void main(String[] args) {
        String imgPath = "D:\\test\\12345678\\20201204222811172.jpg";

        byte[] content = image2Byte(imgPath);

        System.out.println(content.length);
    }

}
