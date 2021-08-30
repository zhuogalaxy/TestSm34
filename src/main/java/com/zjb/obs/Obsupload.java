package com.zjb.obs;

import ch.qos.logback.classic.Logger;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import com.zjb.utils.RWFile;
import com.zjb.utils.ReadConf;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Obsupload {

    public static Logger logger = (Logger) LoggerFactory.getLogger(RWFile.class);
    public static ReadConf rf = new ReadConf();
    public static ObsClient obsClient;
    /**
     * 获取照片桶名
     * @return
     */
    public static String getPhotoBucketName(){
        rf.setPhotoBucketName("photoBucketName");
        String photo_bucket_name = rf.getPhotoBucketName();
        if(photo_bucket_name.isEmpty() || photo_bucket_name==null){
            logger.error("photo_bucket_name.isEmpty() || photo_bucket_name==null");
            return null;
        }
        System.out.println(photo_bucket_name);
        return photo_bucket_name;
    }

    /**
     * 获取指纹桶名
     * @return
     */
    public static String getFingerBucketName(){
        rf.setFingerBucketName("fingerBucketName");
        String finger_bucket_name = rf.getFingerBucketName();
        if(finger_bucket_name.isEmpty() || finger_bucket_name==null){
            logger.error("finger_bucket_name.isEmpty() || finger_bucket_name==null");
            return null;
        }
        System.out.println(finger_bucket_name);
        return finger_bucket_name;
    }


    public static ObsClient getObsClient(){

        if(obsClient==null)
        {
            rf.setAccessKeyId("accessKeyId");
            rf.setSecretAccessKey("secretAccessKey");
            rf.setEndPoint("endPoint");
            String endPoint = rf.getEndPoint();
            String ak = rf.getAccessKeyId();
            String sk = rf.getSecretAccessKey();
            obsClient = new ObsClient(ak, sk, endPoint);
            return obsClient;
        }
        else
            return obsClient;
    }



    public String getUUID(){
        String uuid = java.util.UUID.randomUUID().toString().replace("-","");
        return uuid;
    }

    /**
     * upload base64(file) to obs
     * @param content
     * @param bucket_name
     * @return  objectname
     */
    public String uploadBase64(String content, String bucket_name){
        if(content.isEmpty() || content==null){
            logger.error("obs upload content is null or enpty");
            return null;
        }
        //ReadConf rf1 = new ReadConf();
        rf.setAccessKeyId("accessKeyId");
        rf.setSecretAccessKey("secretAccessKey");
        rf.setEndPoint("endPoint");
        //rf.setPhotoBucketName("photoBucketName");

        String endPoint = rf.getEndPoint();
        String ak = rf.getAccessKeyId();
        String sk = rf.getSecretAccessKey();
        //String bucket_name = rf.getPhotoBucketName();
        String objectname = null;
        InputStream finput=null;

        //ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        ObsClient obsClient = Obsupload.getObsClient();
        long startTime1= System.currentTimeMillis();
        try{
            objectname = java.util.UUID.randomUUID().toString().replace("-","");
            finput = new ByteArrayInputStream(content.getBytes());
            obsClient.putObject(bucket_name, objectname, finput);
            System.out.println("upload success");
        }catch(ObsException e){
            e.getResponseCode();
            e.getErrorMessage();
            e.getErrorCode();
            e.getResponseStatus();
            logger.error(String.valueOf(e.getResponseCode()));
            logger.error(e.getErrorMessage());
            logger.error(e.getErrorCode());
            logger.error(e.getResponseStatus());
            System.out.println("obs upload fail");
            logger.error("obs upload fail");
            objectname="obs upload fail";
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            System.out.println("obs upload fail");
            logger.error("obs upload fail");
            objectname="obs upload fail";
            //return null;
        }finally {
            if(finput!=null){
                try {
                    finput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (obsClient!=null){
                try {
                    obsClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        long endTime1=System.currentTimeMillis();
        System.out.println("obsupload use time: "+ (endTime1-startTime1));
        return objectname;
    }

    /**
     * content 输入为二进制字节数组或者base64字节数组
     * @param content
     * @return
     */
    public static String uploadBase64(byte[] content,String bucket_name){
        if(content.length<=0 || content==null){
            logger.error("obs upload content is null or enpty");
            return null;
        }
        ReadConf rf1 = new ReadConf();
        rf1.setAccessKeyId("accessKeyId");
        rf1.setSecretAccessKey("secretAccessKey");
        rf1.setEndPoint("endPoint");
        //rf1.setBucketName("bucketName");

        String endPoint = rf1.getEndPoint();
        String ak = rf1.getAccessKeyId();
        String sk = rf1.getSecretAccessKey();
        //String bucket_name = rf1.getBucketName();
        String objectname = null;
        InputStream finput=null;
        String beforeEtag=null;

        //ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        ObsClient obsClient = Obsupload.getObsClient();
        long startTime1= System.currentTimeMillis();
        try{
            objectname = java.util.UUID.randomUUID().toString().replace("-","");
            finput = new ByteArrayInputStream(content);
            //将文件放入到对应目录下，只需要在objectname前添加目录名和/
            //obsClient.putObject(bucket_name, "zhejiang/"+objectname, finput);
            PutObjectResult pt = obsClient.putObject(bucket_name, objectname, finput);
            //通过Etag判断完整性(2021/07/01)
            beforeEtag = pt.getEtag();
            System.out.println("beforeEtag = " + beforeEtag);
            ObjectMetadata metadata = obsClient.getObjectMetadata(bucket_name, objectname);
            System.out.println("afterEtag = " + metadata.getEtag());
            if(beforeEtag.equals(metadata.getEtag())){
                System.out.println("upload success");
            }else{
                System.out.println("obs upload fail,data upload is not whole");
                logger.error("obs upload fail,data upload is not whole");
                objectname="obs upload fail";
            }
            //System.out.println("upload success");
        }catch(ObsException e){
            e.getResponseCode();
            e.getErrorMessage();
            e.getErrorCode();
            e.getResponseStatus();
            logger.error(String.valueOf(e.getResponseCode()));
            logger.error(e.getErrorMessage());
            logger.error(e.getErrorCode());
            logger.error(e.getResponseStatus());
            System.out.println("obs upload fail");
            logger.error("obs upload fail");
            objectname="obs upload fail";
        }
//        catch (Exception e){
//            e.printStackTrace();
//            logger.error(e.getMessage());
//            System.out.println("obs upload fail");
//            logger.error("obs upload fail");
//            objectname="obs upload fail";
//            //return objectname;
//            //return null;
//        }
        finally {
            if(finput!=null){
                try {
                    finput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            if (obsClient!=null){
//                try {
//                    obsClient.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        long endTime1=System.currentTimeMillis();
        System.out.println("obsupload use time: "+ (endTime1-startTime1));
        return objectname;
    }


    /**
     * content 输入为二进制字节数组或者base64字节数组
     * @param content
     * @return
     */
    public static String uploadBase64(byte[] content,String dir,String bucket_name){
        String objectname = "obs upload fail";
        if(content.length<=0 || content==null){
            logger.error("obs upload content is null or enpty");
            return objectname;
        }
        ReadConf rf1 = new ReadConf();
        rf1.setAccessKeyId("accessKeyId");
        rf1.setSecretAccessKey("secretAccessKey");
        rf1.setEndPoint("endPoint");
        //rf1.setBucketName("bucketName");

        String endPoint = rf1.getEndPoint();
        String ak = rf1.getAccessKeyId();
        String sk = rf1.getSecretAccessKey();
        //String bucket_name = rf1.getBucketName();
        //String objectname = null;
        InputStream finput=null;
        String beforeEtag=null;

        //ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        ObsClient obsClient = Obsupload.getObsClient();
        long startTime1= System.currentTimeMillis();
        try{
            objectname = java.util.UUID.randomUUID().toString().replace("-","");
            finput = new ByteArrayInputStream(content);
            //将文件放入到对应目录下，只需要在objectname前添加目录名和/
            //obsClient.putObject(bucket_name, "zhejiang/"+objectname, finput);
            PutObjectResult pt = obsClient.putObject(bucket_name, dir+"/"+objectname, finput);
            //通过Etag判断完整性(2021/07/01)
            beforeEtag = pt.getEtag();
            System.out.println("beforeEtag = " + beforeEtag);
            ObjectMetadata metadata = obsClient.getObjectMetadata(bucket_name, dir+"/"+objectname);
            System.out.println("afterEtag = " + metadata.getEtag());
            if(beforeEtag.equals(metadata.getEtag())){
                System.out.println("upload success");
            }else{
                System.out.println("obs upload fail,data upload is not whole");
                logger.error("obs upload fail,data upload is not whole");
                objectname="obs upload fail";
            }
            //System.out.println("upload success");
        }catch(ObsException e){
            e.getResponseCode();
            e.getErrorMessage();
            e.getErrorCode();
            e.getResponseStatus();
            logger.error(String.valueOf(e.getResponseCode()));
            logger.error(e.getErrorMessage());
            logger.error(e.getErrorCode());
            logger.error(e.getResponseStatus());
            System.out.println("obs upload fail");
            logger.error("obs upload fail");
            objectname="obs upload fail";
        }
//        catch (Exception e){
//            e.printStackTrace();
//            logger.error(e.getMessage());
//            System.out.println("obs upload fail");
//            logger.error("obs upload fail");
//            objectname="obs upload fail";
//            //return objectname;
//            //return null;
//        }
        finally {
            if(finput!=null){
                try {
                    finput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            if (obsClient!=null){
//                try {
//                    obsClient.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        long endTime1=System.currentTimeMillis();
        System.out.println("obsupload use time: "+ (endTime1-startTime1));
        return objectname;
    }

    /**
     * content 输入为二进制字节数组或者base64字节数组
     * @param content
     * @return
     */
    public static String uploadBase64(byte[] content,String dir,String middir,String bucket_name){
        if(content.length<=0 || content==null){
            logger.error("obs upload content is null or enpty");
            return null;
        }
        ReadConf rf1 = new ReadConf();
        rf1.setAccessKeyId("accessKeyId");
        rf1.setSecretAccessKey("secretAccessKey");
        rf1.setEndPoint("endPoint");
        //rf1.setBucketName("bucketName");

        String endPoint = rf1.getEndPoint();
        String ak = rf1.getAccessKeyId();
        String sk = rf1.getSecretAccessKey();
        //String bucket_name = rf1.getBucketName();
        String objectname = null;
        InputStream finput=null;
        String beforeEtag=null;

        //ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        ObsClient obsClient = Obsupload.getObsClient();
        long startTime1= System.currentTimeMillis();
        try{
            objectname = java.util.UUID.randomUUID().toString().replace("-","");
            finput = new ByteArrayInputStream(content);
            //将文件放入到对应目录下，只需要在objectname前添加目录名和/
            //obsClient.putObject(bucket_name, "zhejiang/"+objectname, finput);
            PutObjectResult pt = obsClient.putObject(bucket_name, dir+"/"+middir+"/"+objectname, finput);

            //通过Etag判断完整性(2021/07/01)
            beforeEtag = pt.getEtag();
            //System.out.println("beforeEtag = " + beforeEtag);
            ObjectMetadata metadata = obsClient.getObjectMetadata(bucket_name, dir+"/"+middir+"/"+objectname);
            //System.out.println("afterEtag = " + metadata.getEtag());
            if(beforeEtag.equals(metadata.getEtag())){
                System.out.println("upload success");
            }else{
                System.out.println("obs upload fail,data upload is not whole");
                logger.error("obs upload fail,data upload is not whole");
                objectname="obs upload fail";
            }

            //System.out.println("upload success");

            //PutObjectResult pt=obsClient.putObject(bucket_name, dir+"/"+middir+"/"+objectname, finput);
            //pt.getEtag();//判断数据完整性
        }catch(ObsException e){
            e.getResponseCode();
            e.getErrorMessage();
            e.getErrorCode();
            e.getResponseStatus();
            logger.error(String.valueOf(e.getResponseCode()));
            logger.error(e.getErrorMessage());
            logger.error(e.getErrorCode());
            logger.error(e.getResponseStatus());
            System.out.println("obs upload fail");
            logger.error("obs upload fail");
            objectname="obs upload fail";
        }
//        catch (Exception e){
//            e.printStackTrace();
//            logger.error(e.getMessage());
//            System.out.println("obs upload fail");
//            logger.error("obs upload fail");
//            objectname="obs upload fail";
//            //return objectname;
//            //return null;
//        }
        finally {
            if(finput!=null){
                try {
                    finput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            if (obsClient!=null){
//                try {
//                    obsClient.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        long endTime1=System.currentTimeMillis();
        System.out.println("obsupload use time: "+ (endTime1-startTime1));
        return objectname;
    }

    /**
     *
     * @param bucket_name
     * @param //object_name
     * @return
     */
    public static void getObjInfo(String bucket_name){
        if(bucket_name.isEmpty() || bucket_name==null){
            logger.error("obs getObjInfo bucket_name is null or enpty");
            //return null;
        }
//        if(object_name.isEmpty() || object_name==null){
//            logger.error("obs getObjInfo object_name is null or enpty");
//            return null;
//        }
        ReadConf rf1 = new ReadConf();
        rf1.setAccessKeyId("accessKeyId");
        rf1.setSecretAccessKey("secretAccessKey");
        rf1.setEndPoint("endPoint");
        //rf1.setBucketName("bucketName");

        String endPoint = rf1.getEndPoint();
        String ak = rf1.getAccessKeyId();
        String sk = rf1.getSecretAccessKey();
        //String bucket_name = rf1.getBucketName();
        //String objectname = null;
        //InputStream finput=null;
        String lastModified=null;
        int i=0;

        //ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        ObsClient obsClient = Obsupload.getObsClient();
        long startTime1= System.currentTimeMillis();
        try{
            ObjectListing result = obsClient.listObjects(bucket_name);
            for(ObsObject obsObject : result.getObjects()) {
                i++;
                System.out.println("\t" + obsObject.getObjectKey());
                //System.out.println("\t" + obsObject.getOwner());
            }
            if(result.isTruncated()){
                System.out.println("\t"+ "i = "+ i);
                System.out.println("\t"+ "next marker = "+ result.getNextMarker());
            }


//            ObjectMetadata metadata = obsClient.getObjectMetadata(bucket_name, object_name);
//            System.out.println("\t" + metadata.getContentType());
//            System.out.println("\t" + "length = "+metadata.getContentLength());
//            //System.out.println("\t" + "property = "+metadata.getUserMetadata("property"));
//
//            System.out.println("\t" + "lasttime = " + metadata.getLastModified());
//            //metadata.getEtag();//
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//            lastModified = formatter.format(metadata.getLastModified());

        }catch(ObsException e){
            e.getResponseCode();
            e.getErrorMessage();
            e.getErrorCode();
            e.getResponseStatus();
            logger.error(String.valueOf(e.getResponseCode()));
            logger.error(e.getErrorMessage());
            logger.error(e.getErrorCode());
            logger.error(e.getResponseStatus());
            System.out.println("obs upload fail");
            logger.error("obs upload fail");
            //objectname="obs upload fail";
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            System.out.println("obs upload fail");
            logger.error("obs upload fail");
            //objectname="obs upload fail";
            //return objectname;
            //return null;
        }

        long endTime1=System.currentTimeMillis();
        System.out.println("obsupload use time: "+ (endTime1-startTime1));
        //return objectname;
        //return lastModified;
    }

    /**
     * 只从obs中获取对象
     * @param bucket_name
     * @param object_name
     */
    public static void downObjOnlyRead(String bucket_name,String object_name){
        if(bucket_name.isEmpty() || bucket_name==null){
            logger.error("obs getObjInfo bucket_name is null or enpty");
            //return null;
        }
        if(object_name.isEmpty() || object_name==null){
            logger.error("obs getObjInfo object_name is null or enpty");
            //return null;
        }
        ReadConf rf1 = new ReadConf();
        rf1.setAccessKeyId("accessKeyId");
        rf1.setSecretAccessKey("secretAccessKey");
        rf1.setEndPoint("endPoint");
        //rf1.setBucketName("bucketName");

        String endPoint = rf1.getEndPoint();
        String ak = rf1.getAccessKeyId();
        String sk = rf1.getSecretAccessKey();
        //String bucket_name = rf1.getBucketName();
        //String objectname = null;
        //InputStream finput=null;
        String lastModified=null;
        int i=0;
        BufferedReader br = null;
        BufferedWriter bw = null;
        //ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        ObsClient obsClient = Obsupload.getObsClient();
        long startTime1= System.currentTimeMillis();
        try{
            ObsObject obsObject = obsClient.getObject(bucket_name,object_name);
            br = new BufferedReader(new InputStreamReader(obsObject.getObjectContent(),"UTF-8"));
            //BufferedWriter bw = new BufferedWriter(new FileWriter(new File(destPath)));
            //bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destPath, true)));

            //String line;
//            while((line = br.readLine()) != null)
//            {
//                bw.write(line);
//            }
            bw.flush();
            br.close();
            bw.close();

        }catch(ObsException e){
            e.getResponseCode();
            e.getErrorMessage();
            e.getErrorCode();
            e.getResponseStatus();
            logger.error(String.valueOf(e.getResponseCode()));
            logger.error(e.getErrorMessage());
            logger.error(e.getErrorCode());
            logger.error(e.getResponseStatus());
            System.out.println("downObj fail");
            logger.error("downObj fail");
            //objectname="obs upload fail";
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            System.out.println("downObj fail");
            logger.error("downObj fail");
            //objectname="obs upload fail";
            //return objectname;
            //return null;
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        long endTime1=System.currentTimeMillis();
        System.out.println("downObjOnlyRead use time: "+ (endTime1-startTime1));
        //return objectname;
        //return lastModified;
    }

    /**
     * 下载单个对象并写入文件
     * @param bucket_name
     * @param object_name
     * @param destPath
     */
    public static void downObj(String bucket_name,String object_name,String destPath){
        if(bucket_name.isEmpty() || bucket_name==null){
            logger.error("obs getObjInfo bucket_name is null or enpty");
            //return null;
        }
        if(object_name.isEmpty() || object_name==null){
            logger.error("obs getObjInfo object_name is null or enpty");
            //return null;
        }
        ReadConf rf1 = new ReadConf();
        rf1.setAccessKeyId("accessKeyId");
        rf1.setSecretAccessKey("secretAccessKey");
        rf1.setEndPoint("endPoint");
        //rf1.setBucketName("bucketName");

        String endPoint = rf1.getEndPoint();
        String ak = rf1.getAccessKeyId();
        String sk = rf1.getSecretAccessKey();
        //String bucket_name = rf1.getBucketName();
        //String objectname = null;
        //InputStream finput=null;
        String lastModified=null;
        int i=0;
        BufferedReader br = null;
        BufferedWriter bw = null;
        //ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        ObsClient obsClient = Obsupload.getObsClient();
        long startTime1= System.currentTimeMillis();
        try{
            ObsObject obsObject = obsClient.getObject(bucket_name,object_name);
            br = new BufferedReader(new InputStreamReader(obsObject.getObjectContent(),"UTF-8"));
            //BufferedWriter bw = new BufferedWriter(new FileWriter(new File(destPath)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destPath, true)));

            String line;
            while((line = br.readLine()) != null)
            {
                bw.write(line);
            }
            bw.flush();
            br.close();
            bw.close();

        }catch(ObsException e){
            e.getResponseCode();
            e.getErrorMessage();
            e.getErrorCode();
            e.getResponseStatus();
            logger.error(String.valueOf(e.getResponseCode()));
            logger.error(e.getErrorMessage());
            logger.error(e.getErrorCode());
            logger.error(e.getResponseStatus());
            System.out.println("downObj fail");
            logger.error("downObj fail");
            //objectname="obs upload fail";
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            System.out.println("downObj fail");
            logger.error("downObj fail");
            //objectname="obs upload fail";
            //return objectname;
            //return null;
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        long endTime1=System.currentTimeMillis();
        System.out.println("downObj use time: "+ (endTime1-startTime1));
        //return objectname;
        //return lastModified;
    }

    /**
     * 列举出所有对象，包括文件夹
     * @param bucket_name
     */
    public static void getAllObj(String bucket_name,String destPath){
        if(bucket_name.isEmpty() || bucket_name==null){
            logger.error("obs getObjInfo bucket_name is null or enpty");
        }

        ReadConf rf1 = new ReadConf();
        rf1.setAccessKeyId("accessKeyId");
        rf1.setSecretAccessKey("secretAccessKey");
        rf1.setEndPoint("endPoint");
        //rf1.setBucketName("bucketName");

        String endPoint = rf1.getEndPoint();
        String ak = rf1.getAccessKeyId();
        String sk = rf1.getSecretAccessKey();
        String lastModified=null;
        int i=0;

        //ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        ObsClient obsClient = Obsupload.getObsClient();
        long startTime1= System.currentTimeMillis();
        try{
            ListObjectsRequest request = new ListObjectsRequest(bucket_name);
            //request.setMaxKeys(1000);
            // 设置文件夹分隔符"/"
            request.setDelimiter("/");
            ObjectListing result = obsClient.listObjects(request);
            System.out.println("Objects in the root directory:");
            for(ObsObject obsObject : result.getObjects())
            {
                //System.out.println("\t" + obsObject.getObjectKey());
                //System.out.println("\t" + obsObject.getOwner());
                downObj(request.getBucketName(),obsObject.getObjectKey(),destPath);
            }
            listObjectsByPrefix(obsClient, request, result,destPath);

        }catch(ObsException e){
            e.getResponseCode();
            e.getErrorMessage();
            e.getErrorCode();
            e.getResponseStatus();
            logger.error(String.valueOf(e.getResponseCode()));
            logger.error(e.getErrorMessage());
            logger.error(e.getErrorCode());
            logger.error(e.getResponseStatus());
            System.out.println("getAllObj fail");
            logger.error("getAllObj fail");
            //objectname="obs upload fail";
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            System.out.println("getAllObj fail");
            logger.error("getAllObj fail");
        }

        long endTime1=System.currentTimeMillis();
        System.out.println("getAllObj use time: "+ (endTime1-startTime1));

    }

    /**
     * 递归函数，列举出文件夹下所有子文件夹和对象
     * @param obsClient
     * @param request
     * @param result
     * @throws ObsException
     */
    public static void listObjectsByPrefix(ObsClient obsClient, ListObjectsRequest request, ObjectListing result,String destPath) throws ObsException
    {
        for(String prefix : result.getCommonPrefixes())
        {
            System.out.println("Objects in folder [" + prefix + "]:");
            request.setPrefix(prefix);
            result = obsClient.listObjects(request);
            for(ObsObject obsObject : result.getObjects())
            {
                //System.out.println("\t" + obsObject.getObjectKey());
                downObj(request.getBucketName(),obsObject.getObjectKey(),destPath);

            }
            listObjectsByPrefix(obsClient, request, result,destPath);
        }
    }




    /**
     * base64 to binary
     * @param base64Str
     * @return
     */
    public static byte[] base64String2Byte(String base64Str){
        if(base64Str==null || base64Str.isEmpty()){
            System.out.println("base64String2Byte is null or empty");
            logger.error("base64String2Byte is null or empty");
            return null;
        }
        byte[] b =  Base64.getDecoder().decode(base64Str);
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {
                b[i] += 256;
            }
        }
        return b;
    }



    public static void main(String[] args)  {
//        String base64="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";
//
//        Obsupload ou = new Obsupload();
//        String bucket_name = ou.getPhotoBucketName();
//        //String object_name = "8d869772d7494426b3cc94916bdc0f68";
//        //String destPath = "D:\\test\\file-1.txt";
//        //ou.getObjInfo(bucket_name);
//        //ou.downObj(bucket_name,"beijing"+"/"+object_name,destPath);
//        //ou.getAllObj(bucket_name,destPath);
//        //ou.downObjOnlyRead(bucket_name,object_name);
//        //String imgurl = "https://b-ssl.duitang.com/uploads/item/201605/10/20160510001106_2YjCN.thumb.700_0.jpeg";
//        String imgurl = "https://b-ssl.duitang.com/uploads/item/201605/10/20160510001106_2YjCN.thumb.700_.jpeg";
//        Urlutils uu = new Urlutils();
//
//        byte[] content= uu.image2Base64(imgurl);
//        if(content!=null){
//            System.out.println(content.length);
//        }else{
//
//        }

        RWFile rw = new RWFile();

        String folderName = "D:\\test\\12345678";
        String fileName = null;
        if(folderName!= null && !folderName.isEmpty()){

            File dir = new File(folderName);

            if (dir.isDirectory()){

                String[] children = dir.list();
                if (children != null){
                    for (int i = 0; i < children.length; i++){
                        if(rw.judgeSystem()==0){
                            fileName=dir+"\\"+children[i];
                            System.out.println(fileName);
                        }else{
                            fileName=dir+"/"+children[i];
                            System.out.println(fileName);

                        }


                    }

                }

            }


        }





        //System.out.println(content.length);

        //String object_name = ou.uploadBase64(content,bucket_name);

        //System.out.println(object_name);




        //Object prostr=ou.getObjInfo(bucket_name,"beijing"+"/"+"haidian"+"/"+object_name);
        //System.out.println(prostr);
        //int i=0;
        //String object_name = null;
//        for(i=0;i<1000;i++) {
//            object_name = ou.uploadBase64(ou.base64String2Byte(base64), "beijing", "haidian", bucket_name);
//        }
        //System.out.println(base64.substring(1,13));
        //System.out.println("i =" + i);
        //System.out.println("bucket_name = " + bucket_name);
        //System.out.println("object_name = " + object_name);
        //System.out.println("object_name length = " + object_name.length());


        //List list = new ArrayList();
        //list.add(base64);

    }

}
