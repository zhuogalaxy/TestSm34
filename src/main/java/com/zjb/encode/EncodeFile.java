package com.zjb.encode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fri.alg.sm.sm4.SM4Utils;
import com.zjb.decode.DecodeFile;
import com.zjb.sm3.SM3Digest;
import com.zjb.sm4.SM4;
import com.zjb.sm4.SM4_Context;
import com.zjb.utils.HttpClientUtils;
import com.zjb.utils.ReadConf;
//import com.zjb.utils.RestTemplateUtils;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.support.PropertiesLoaderUtils;
//import org.springframework.http.ResponseEntity;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Adam zhao
 * @Time 2020/7/23
 */
public class EncodeFile {

    public Logger logger = (Logger) LoggerFactory.getLogger(EncodeFile.class);


    public static void main(String[] args) throws IOException {
//        EncodeFile encodeFile = new EncodeFile();
//        DecodeFile decodeFile = new DecodeFile();
        //加密
//        String random16=encodeFile.generateRandom();  //16字节长度随机数
//        System.out.println("16random= "+ random16);
//        //System.out.println("16random= "+ DatatypeConverter.printHexBinary(random16));
//        String originData="hello world";
//        byte[] encrypted= encodeFile.sm4Encode(random16,originData.getBytes());  //密文
//        //System.out.println("encrypted= "+ new String(encrypted));
//        System.out.println("encrypted= "+DatatypeConverter.printHexBinary(encrypted));
//        String evpData=encodeFile.encodeEnvelope(random16);  //数字信封
//        System.out.println("evpData= "+ evpData);
//
//        //System.out.println("evpData= "+DatatypeConverter.printHexBinary(evpData));
////        byte[] signData=encodeFile.signPkcs7(encrypted);  //签名
////        System.out.println("signData= "+DatatypeConverter.printHexBinary(signData));
////        //解密
//        //String randomNj=decodeFile.decodeEnvelope(evpData);
//        //System.out.println("randomNj= "+ randomNj);
//
//        //System.out.println("randomNj= "+DatatypeConverter.printHexBinary(randomNj));
//        boolean b= randomNj.equals(random16);
//        System.out.println("数字信封解封结果比较：" + b);
////
//        byte[] originDataNew=decodeFile.sm4Decode(randomNj,encrypted);
//
//        System.out.println("originDataNew = "+ (new String(originDataNew)));
//        //System.out.println("originDataNew = " + new String(originDataNew,"UTF-8"));
//        boolean b1= originData.equals(new String(originDataNew));
//        System.out.println("解密结果比较：" + b1);
        //加密
//        String originData="hello world";
//        String randomN=encodeFile.generateRandom();
//        byte[] encrypted= encodeFile.sm4Encode(randomN,originData.getBytes());  //密文
//        byte[] hash=encodeFile.digestSm3(encrypted); //密文摘要
//        String evpData=encodeFile.encodeSignedAndEnvelope(randomN,hash); //签名并封装数字信封
//        System.out.println("evpData = "+evpData);
//        //解密
//        String deEvpData=decodeFile.decodeSignedAndEnvelope(evpData); //验签，并拆封数字信封
//
//        System.out.println("deEvpData = "+deEvpData);
//
//        boolean b= decodeFile.checkDigestSm3(encrypted,deEvpData);
//        System.out.println("数字信封解封结果比较：" + b);
//
//        if (b){
//            byte[] deRandomN=decodeFile.getRandomN(deEvpData);
//            System.out.println("deRandomN.length = " + deRandomN.length);
//            byte[] deOriginData=decodeFile.sm4Decode(deRandomN,encrypted);
//            System.out.println("解密后： "+ new String(deOriginData,"UTF-8"));
//        }

            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); //精确到毫秒
            String timer = fmt.format(new Date());
            System.out.println(timer);


    }

    public String getTimer(){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); //精确到秒
        String timer = fmt.format(new Date());
        return timer;
    }



    /**
     * 1. 从密码机获取16或32字节长度位随机数
     * @return 16字节长度随机数, String
     */
    public String generateRandom() {
    	HttpClientUtils ht=new HttpClientUtils();
        JSONObject reqBody = new JSONObject();
        String urlGenerateRandom=null;
        String randomN = null;
        ReadConf rf=new ReadConf();
        rf.setVersion("version");
        rf.setAuthcode("authcode");
        rf.setRandLen("randLen");
        //Properties prop = new Properties();
        reqBody.clear();
        reqBody.put("version", rf.getVersion());
        reqBody.put("authcode", rf.getAuthcode());  //内网
        // reqBody.put("authcode", "asd");
        reqBody.put("randLen", rf.getRandLen());
        try {
            rf.setUrlGenerateRandom("urlGenerateRandom");
            urlGenerateRandom=rf.getUrlGenerateRandom();
            //urlGenerateRandom="http://172.31.113.131:8083/generateRandom";
            if(urlGenerateRandom==null){
                System.out.println("urlGenerateRandom is null");
                logger.error("urlGenerateRandom is null");
                return null;
            }
            //断网处理？？？
            //ResponseEntity<JSONObject> repRandom = RestTemplateUtils.post(urlGenerateRandom, reqBody, JSONObject.class);
            
            //if(repRandom != null){
                //JSONObject repBodyRandom = repRandom.getBody();
            JSONObject repBodyRandom=ht.doPost(urlGenerateRandom, reqBody);
                if (repBodyRandom != null)
                {
                    int nRetCode = repBodyRandom.getIntValue("code");
                    String desc = repBodyRandom.getString("description");
                    if (nRetCode == 0)
                    {
                        randomN=repBodyRandom.getString("rand_data");
                        if(randomN!=null){
                            //logger.info("generateRandom success！randomN = " + randomN);
                            logger.info("generateRandom success!");
                        }
                    }else{
                        System.out.println("generateRandom code = "+nRetCode);
                        logger.error("generateRandom code = "+nRetCode);
                        logger.error("generateRandom desc = "+desc);

                        return null;
                    }
                }
            //}
        } catch (Exception e) {
            //e.printStackTrace();
            //System.out.println("Exception: " + e.getCause().getClass() + "," + e.getCause().getMessage());
            //logger.error("Exception: " + e.getCause().getClass() + "," + e.getCause().getMessage());
            logger.error("ExceptionTest Exception:",e);
        }
        return randomN;
    }



    public String get_encode_Key_id() {
        HttpClientUtils ht=new HttpClientUtils();
        JSONObject reqBody = new JSONObject();
        String urlengetDeviceId=null;
        String key_id=null;
        ReadConf rf=new ReadConf();
        rf.setVersion("version");
        rf.setAuthcode("authcode");
        reqBody.clear();
        reqBody.put("version", rf.getVersion());
        reqBody.put("authcode", rf.getAuthcode());  //内网
        // reqBody.put("authcode", "asd");
        //reqBody.put("randLen", rf.getRandLen());
        try {
            rf.setUrlengetDeviceId("urlengetDeviceId");
            urlengetDeviceId=rf.getUrlengetDeviceId();

            //urlGenerateRandom="http://172.31.113.131:8083/generateRandom";
            if(urlengetDeviceId==null){
                System.out.println("urlengetDeviceId is null");
                logger.error("urlengetDeviceId is null");
                return null;
            }
            //断网处理？？？
            //ResponseEntity<JSONObject> repRandom = RestTemplateUtils.post(urlGenerateRandom, reqBody, JSONObject.class);

            //if(repRandom != null){
            //JSONObject repBodyRandom = repRandom.getBody();
            JSONObject repBodyRandom=ht.doPost(urlengetDeviceId, reqBody);
            if (repBodyRandom != null)
            {
                int nRetCode = repBodyRandom.getIntValue("code");
                if (nRetCode == 0)
                {
                    key_id=repBodyRandom.getString("machineId");
                    if(key_id!=null){
                        //logger.info("generateRandom success！randomN = " + randomN);
                        logger.info("getDeviceId success!");
                    }
                }else{
                    System.out.println("getDeviceId code = "+nRetCode);
                    logger.error("getDeviceId code = "+nRetCode);
                    return null;
                }
            }
            //}
        } catch (Exception e) {
            //e.printStackTrace();
            //System.out.println("Exception: " + e.getCause().getClass() + "," + e.getCause().getMessage());
            //logger.error("Exception: " + e.getCause().getClass() + "," + e.getCause().getMessage());
            logger.error("ExceptionTest Exception:",e);
        }
        return key_id;
    }




    /**
     * 2. 利用16字节长度随机数和软加密程序对原文加密
     * @param randomN  16字节长度随机数 String
     * @param originData  原文 byte[]
     * @return 密文 byte[]
     */
    public byte[] sm4Encode(String randomN,byte[] originData){
        if(randomN==null || randomN.isEmpty()){
            System.out.println("sm4Encode randomN is null or empty");
            logger.error("sm4Encode randomN is null or empty");
            return null;
        }
        if(originData==null || originData.length<=0){
            System.out.println("sm4Encode originData is null or empty");
            logger.error("sm4Encode originData is null or empty");
            return null;
        }
        byte[] encrypted=null;
        try {
            SM4_Context ctx_enc = new SM4_Context();
            SM4 sm4_enc = new SM4();
            ctx_enc.isPadding = true;
            ctx_enc.mode = sm4_enc.SM4_ENCRYPT;
            sm4_enc.sm4_setkey_enc(ctx_enc, Base64.getDecoder().decode(randomN));  //使用和http一致的base64编码
            encrypted = sm4_enc.sm4_crypt_ecb(ctx_enc, originData);
            if(encrypted!=null){
                logger.info("sm4Encode success!");
            }
            //字节数组的密文转base64
            //encrypted = Base64.getEncoder().encode(encrypted);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }
        return encrypted;
    }


    /**
     * 新的加密功能
     * @param randomN
     * @param originData
     * @return
     */
    public byte[] sm4Encode_new(String randomN,byte[] originData){
        if(randomN==null || randomN.isEmpty()){
            System.out.println("sm4Encode randomN is null or empty");
            logger.error("sm4Encode randomN is null or empty");
            return null;
        }
        if(originData==null || originData.length<=0){
            System.out.println("sm4Encode originData is null or empty");
            logger.error("sm4Encode originData is null or empty");
            return null;
        }
        byte[] encrypted=null;
        try {
            SM4_Context ctx_enc = new SM4_Context();
            SM4Utils sm4 = new SM4Utils();
            sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
            encrypted = sm4.doEncrypt_ecb(originData);

            //SM4 sm4_enc = new SM4();
            //ctx_enc.isPadding = true;
            //ctx_enc.mode = sm4_enc.SM4_ENCRYPT;
            //sm4_enc.sm4_setkey_enc(ctx_enc, Base64.getDecoder().decode(randomN));  //使用和http一致的base64编码
            //encrypted = sm4_enc.sm4_crypt_ecb(ctx_enc, originData);

            if(encrypted!=null){
                logger.info("sm4Encode success!");
            }
            //字节数组的密文转base64
            //encrypted = Base64.getEncoder().encode(encrypted);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }
        return encrypted;
    }


    /**
     * 新的加密功能，最后一块填充
     * @param randomN
     * @param originData
     * @return
     */
    public byte[] sm4Encode_final(String randomN,byte[] originData){
        if(randomN==null || randomN.isEmpty()){
            System.out.println("sm4Encode randomN is null or empty");
            logger.error("sm4Encode randomN is null or empty");
            return null;
        }
        if(originData==null || originData.length<=0){
            System.out.println("sm4Encode originData is null or empty");
            logger.error("sm4Encode originData is null or empty");
            return null;
        }
        byte[] encrypted=null;
        try {
            SM4_Context ctx_enc = new SM4_Context();
            SM4Utils sm4 = new SM4Utils();
            sm4.setSecretKey(Base64.getDecoder().decode(randomN));  //随机密钥
            //encrypted = sm4.doEncrypt_ecb(originData);
            encrypted = sm4.doFinalEncrypt_ecb(originData);

            if(encrypted!=null){
                logger.info("sm4Encode success!");
            }
            //字节数组的密文转base64
            //encrypted = Base64.getEncoder().encode(encrypted);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }
        return encrypted;
    }


    /**
     * <>去掉，暂时用不到</>  3-off. 利用16字节长度随机数和证书封装数字信封
     * @param randomN  16字节长度随机数 String
     * @return  数字信封数据 evpData String
     */
    //public void encodeEnvelope(String randomN,String bookStr)  证书从外部获取
//    public String encodeEnvelope(String randomN) {
//        if(randomN==null || randomN.isEmpty()){
//            System.out.println("encodeEnvelope randomN is null or empty");
//            logger.error("encodeEnvelope randomN is null or empty");
//            return null;
//        }
//        //判断证书
//        //if(){}
//        String evpData=null;
//        String urlEncEnvelope=null;
//        //Properties prop = new Properties();
//        ReadConf rf = new ReadConf();
//        JSONObject reqBody = new JSONObject();
//        try {
//            reqBody.clear();
//            reqBody.put("version", "2");
//            reqBody.put("authcode", "asd");
//            reqBody.put("origin_data", randomN);
//            reqBody.put("b64_cert", "MIICOzCCAeGgAwIBAgIBATAKBggqgRzPVQGDdTBYMQswCQYDVQQGEwJDTjELMAkGA1UECAwCU0QxCzAJBgNVBAcMAkpOMQ8wDQYDVQQKDAZTQU5TRUMxDDAKBgNVBAsMA1NWUzEQMA4GA1UEAwwHU00yUk9PVDAeFw0xNzExMjkxMjA1NDJaFw0yNzExMjcxMjA1NDJaMGoxCzAJBgNVBAYTAkNOMQswCQYDVQQIDAJTRDEPMA0GA1UECgwGU0FOU0VDMQwwCgYDVQQLDANTVlMxEjAQBgNVBAMMCWxvY2FsaG9zdDEbMBkGCSqGSIb3DQEJARYMdGVzdEB0ZXN0LmNuMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEm/UxsZ/Ry+ySJsUYXNPLZbutppsnIeM4SQgV0R/yJ7naOpxCma7J6X2lzG9+U1Tf9l4H/Ko/4BwSDV0Qzcq2sqOBiTCBhjAJBgNVHRMEAjAAMAsGA1UdDwQEAwIFIDAsBglghkgBhvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYEFLXdoOcWURWnUkjx/i198ZTzIMXzMB8GA1UdIwQYMBaAFFsGWKUfioCQawutTUmpanlU6x+ZMAoGCCqBHM9VAYN1A0gAMEUCIQC2uQAPmtT31ydA6K4uLgkziRM9PX1SroFwR/dEfUaY7QIgSwSS/fDTkGJ8z7hYE8kgXDCOlr+nrYBTbkLD10TFFeg=");
//            reqBody.put("encrypt_alg", "SM4");
//            //Properties prop = PropertiesLoaderUtils.loadProperties(new ClassPathResource("conf/config.properties"));
//            //prop.load(new InputStreamReader(new FileInputStream("confDir/config.properties"),"utf-8"));
//            //String urlEncEnvelope = "http://60.217.194.220:20127/encodeEnvelope";
//            //urlEncEnvelope=prop.getProperty("urlEncEnvelope");
//            rf.setUrlEncEnvelope("urlEncEnvelope");
//            urlEncEnvelope=rf.getUrlEncEnvelope();
//            if(urlEncEnvelope==null){
//                System.out.println("urlEncEnvelope is null");
//                logger.error("urlEncEnvelope is null");
//                return null;
//            }
//            ResponseEntity<JSONObject> repEncEnv = RestTemplateUtils.post(urlEncEnvelope, reqBody, JSONObject.class);
//            if(repEncEnv != null){
//                JSONObject repBodyEncEnv = repEncEnv.getBody();
//                if (repBodyEncEnv != null)
//                {
//                    int nRetCode = repBodyEncEnv.getIntValue("code");
//                    if (nRetCode == 0)
//                    {
//                        evpData = repBodyEncEnv.getString("evp_data");
//                        if(evpData!=null){
//                            logger.info("encodeEnvelope success! evpData= " + evpData);
//                        }
//
//                    }else{
//                        System.out.println("encodeEnvelope code = "+nRetCode);
//                        logger.error("encodeEnvelope code = "+nRetCode);
//                        return null;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            //e.printStackTrace();
//            logger.error("ExceptionTest Exception:",e);
//        }
//        return evpData;
//    }


    /**
     * 3. 对文件密文做摘要，生成32字节的摘要值
     * @param encrypted  密文
     * @return  生成32字节的摘要值
     */
    public byte[] digestSm3(byte[] encrypted){
        if (encrypted==null || encrypted.length<=0){
            System.out.println("sm3Digest encrypted is null or empty");
            logger.error("sm3Digest encrypted is null or empty");
            return null;
        }
        SM3Digest sm3 = new SM3Digest();
        sm3.update(encrypted, 0, encrypted.length);
        byte[] hash = new byte[32];
        sm3.doFinal(hash, 0);
        System.out.println("hash.length = "+hash.length);
        return hash;
    }

    /**
     * 4. 封装带签名的数字信封
     * @param randomN  随机数密钥，16字节
     * @param  @hash  摘要值，32字节
     * @return 数字信封值
     */
//    public String encodeSignedAndEnvelope(String randomN,byte[] hash){
//        if(randomN==null || randomN.isEmpty()){
//            System.out.println("encodeSignedAndEnvelope randomN is null or empty");
//            logger.error("encodeSignedAndEnvelope randomN is null or empty");
//            return null;
//        }
//        if (hash==null || hash.length<=0){
//            System.out.println("encodeSignedAndEnvelope hash is null or empty");
//            logger.error("encodeSignedAndEnvelope hash is null or empty");
//            return null;
//        }
//        String urlencodeSignedAndEnvelope=null;
//        String evpData=null;
//
//        byte[] randomNbyte=Base64.getDecoder().decode(randomN);
//        System.out.println("randomNbyte.length = "+randomNbyte.length);
//
//        byte[] originData=new byte[randomNbyte.length+hash.length];
//        System.arraycopy(randomNbyte,0,originData,0,randomNbyte.length);
//        System.arraycopy(hash,0,originData ,randomNbyte.length,hash.length);
//
//        ReadConf rf = new ReadConf();
//        JSONObject reqBody = new JSONObject();
//        reqBody.clear();
//        reqBody.put("version", "2");
//        reqBody.put("authcode", "ESIzRREiMRIxJREiM0URIjESMSURIjNFESIxEjElESIzRREiMRIxJQ==");
//        reqBody.put("origin_data", Base64.getEncoder().encodeToString(originData));  //输入参数格式为base64编码
//        //reqBody.put("b64_cert", "MIIBrzCCAVSgAwIBAgIIQgoAAAAAAAMwDAYIKoEcz1UBg3UFADAhMQswCQYDVQQGEwJDTjESMBAGA1UEAwwJU00yX1JPT1QxMB4XDTIwMDcyMzE2MDAwMFoXDTI1MTAwMTE1NTk1OVowLjELMAkGA1UEBhMCQ04xDDAKBgNVBAoMA0ZSSTERMA8GA1UEAwwIc20ydGVzdDIwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAR3MAb3qd5/Vrt9cWBtbjwQc0G3xQrZoiuqk3DNUU1r7nicLqiIK5L9xrGcYADWKqdUtN/JOakWPHPFcSch9CBOo2cwZTAOBgNVHQ8BAf8EBAMCADAwEwYDVR0lBAwwCgYIKwYBBQUHAwEwHwYDVR0jBBgwFoAU+d0yjy7ApmqCPLD6C3CZDTsGZqowHQYDVR0OBBYEFN4qnR5IqB2iM1RSnDw2WS+zda1xMAwGCCqBHM9VAYN1BQADRwAwRAIgGm63in3AwWpffbOSCyZ3twpBRNIvIYGbtkWyvuqLArMCIGkFgNA7leymCFXcGJz6T0Rtkeu8t7Dvb4ciZUkvLV/g");
//        reqBody.put("b64_cert",rf.readcert());
//        reqBody.put("key_id", "sm2001");
//        reqBody.put("alg_signature","SM3WithSM2");
//        reqBody.put("encrypt_alg", "SM4");
//        try {
//            rf.setUrlencodeSignedAndEnvelope("urlencodeSignedAndEnvelope");
//           urlencodeSignedAndEnvelope=rf.getUrlencodeSignedAndEnvelope();
//            System.out.println("urlencodeSignedAndEnvelope = "+urlencodeSignedAndEnvelope);
//            if (urlencodeSignedAndEnvelope == null) {
//                System.out.println("encodeSignedAndEnvelope is null");
//                logger.error("encodeSignedAndEnvelope is null");
//                return null;
//            }
//            ResponseEntity<JSONObject> repEncEnv = RestTemplateUtils.post(urlencodeSignedAndEnvelope, reqBody, JSONObject.class);
//            if (repEncEnv != null) {
//                JSONObject repBodyEncEnv = repEncEnv.getBody();
//                if (repBodyEncEnv != null) {
//                    int nRetCode = repBodyEncEnv.getIntValue("code");
//                    if (nRetCode == 0) {
//                        evpData = repBodyEncEnv.getString("evp_data");
//                        if (evpData != null) {
//                            logger.info("encodeSignedAndEnvelope success! evpData= " + evpData);
//                        }
//                    } else {
//                        System.out.println("encodeSignedAndEnvelope code = " + nRetCode);
//                        logger.error("encodeSignedAndEnvelope = " + nRetCode);
//                        return null;
//                    }
//                }
//            }
//        }catch (Exception e) {
//            //e.printStackTrace();
//            logger.error("ExceptionTest Exception:",e);
//        }
//        return evpData;
//   }


    //只对随机数签名和封装数字信封
    public String encodeSignedAndEnvelope(String randomN){
        if(randomN==null || randomN.isEmpty()){
            System.out.println("encodeSignedAndEnvelope randomN is null or empty");
            logger.error("encodeSignedAndEnvelope randomN is null or empty");
            return null;
        }
        HttpClientUtils ht=new HttpClientUtils();
        String urlencodeSignedAndEnvelope=null;
        String evpData=null;
//        String key_id=get_encode_Key_id();
//        if(key_id==null  || key_id.isEmpty()){
//            System.out.println("generateKey_id is null");
//            logger.error("generateKey_id is null");
//            return null;
//        }
//        key_id="Device_Q"+key_id.substring(1);
//        System.out.println("key_id = " + key_id);

        ReadConf rf = new ReadConf();
        rf.setVersion("version");
        rf.setAuthcode("authcode");
        rf.setKey_id("key_id");
        rf.setAlg_signature("alg_signature");
        rf.setEncrypt_alg("encrypt_alg");
        JSONObject reqBody = new JSONObject();
        reqBody.clear();
        reqBody.put("version", rf.getVersion());
        reqBody.put("authcode", rf.getAuthcode());
        reqBody.put("origin_data", randomN);  //输入参数格式为base64编码
        //reqBody.put("b64_cert", "MIIBrzCCAVSgAwIBAgIIQgoAAAAAAAMwDAYIKoEcz1UBg3UFADAhMQswCQYDVQQGEwJDTjESMBAGA1UEAwwJU00yX1JPT1QxMB4XDTIwMDcyMzE2MDAwMFoXDTI1MTAwMTE1NTk1OVowLjELMAkGA1UEBhMCQ04xDDAKBgNVBAoMA0ZSSTERMA8GA1UEAwwIc20ydGVzdDIwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAR3MAb3qd5/Vrt9cWBtbjwQc0G3xQrZoiuqk3DNUU1r7nicLqiIK5L9xrGcYADWKqdUtN/JOakWPHPFcSch9CBOo2cwZTAOBgNVHQ8BAf8EBAMCADAwEwYDVR0lBAwwCgYIKwYBBQUHAwEwHwYDVR0jBBgwFoAU+d0yjy7ApmqCPLD6C3CZDTsGZqowHQYDVR0OBBYEFN4qnR5IqB2iM1RSnDw2WS+zda1xMAwGCCqBHM9VAYN1BQADRwAwRAIgGm63in3AwWpffbOSCyZ3twpBRNIvIYGbtkWyvuqLArMCIGkFgNA7leymCFXcGJz6T0Rtkeu8t7Dvb4ciZUkvLV/g");
        reqBody.put("b64_cert",rf.readcert());
        //reqBody.put("key_id", key_id);
        reqBody.put("key_id", rf.getKey_id());
        reqBody.put("alg_signature",rf.getAlg_signature());
        reqBody.put("encrypt_alg", rf.getEncrypt_alg());
        try {
            rf.setUrlencodeSignedAndEnvelope("urlencodeSignedAndEnvelope");
            urlencodeSignedAndEnvelope=rf.getUrlencodeSignedAndEnvelope();
            //System.out.println("urlencodeSignedAndEnvelope = "+urlencodeSignedAndEnvelope);
            if (urlencodeSignedAndEnvelope == null) {
                System.out.println("encodeSignedAndEnvelope is null");
                logger.error("encodeSignedAndEnvelope is null");
                return null;
            }
            //ResponseEntity<JSONObject> repEncEnv = RestTemplateUtils.post(urlencodeSignedAndEnvelope, reqBody, JSONObject.class);
            //if (repEncEnv != null) {
                //JSONObject repBodyEncEnv = repEncEnv.getBody();
            JSONObject repBodyEncEnv=ht.doPost(urlencodeSignedAndEnvelope, reqBody);
                if (repBodyEncEnv != null) {
                    int nRetCode = repBodyEncEnv.getIntValue("code");
                    String desc = repBodyEncEnv.getString("description");
                    if (nRetCode == 0) {
                        evpData = repBodyEncEnv.getString("evp_data");
                        if (evpData != null) {
                            //logger.info("encodeSignedAndEnvelope success! evpData= " + evpData);
                            logger.info("encodeSignedAndEnvelope success!");
                        }
                    } else {
                        System.out.println("encodeSignedAndEnvelope code = " + nRetCode);
                        logger.error("encodeSignedAndEnvelope code = " + nRetCode);
                        logger.error("encodeSignedAndEnvelope desc = " + desc);
                        return null;
                    }
                }
            //}
        }catch (Exception e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }
        return evpData;
    }

    /**
     * 4-off. 对加密文件进行签名
     * @param //originData  加密后的文件密文 String
     * @return 签名数据 signData String
     */
//    public String signPkcs7(String originData){
//        if(originData==null || originData.isEmpty()){
//            System.out.println("signPkcs7 originData is null or empty");
//            logger.error("signPkcs7 originData is null or empty");
//            return null;
//        }
//        //originData=Base64.getEncoder().encode(originData);
//        String signData=null;
//        String urlSignPkcs7=null;
//        JSONObject reqBody = new JSONObject();
//        ReadConf rf=new ReadConf();
//        //Properties prop = new Properties();
//        reqBody.clear();
//        reqBody.put("version", "2");
//        reqBody.put("authcode", "asd");
//        reqBody.put("origin_data", originData);  //输入参数格式为base64编码
//        reqBody.put("key_id", "sm2001");
//        reqBody.put("alg_digest", "sm3");
//        reqBody.put("origin_data_type", "0");
//        reqBody.put("attach", "1");
//        reqBody.put("full_chain", "0");
//        try {
//            //Properties prop = PropertiesLoaderUtils.loadProperties(new ClassPathResource("conf/config.properties"));
//            //prop.load(new InputStreamReader(new FileInputStream("confDir/config.properties"),"utf-8"));
//            //String urlSignPkcs7 = "http://60.217.194.220:20125/signPkcs7";
//            //urlSignPkcs7=prop.getProperty("urlSignPkcs7");
//            rf.setUrlSignPkcs7("urlSignPkcs7");
//            urlSignPkcs7=rf.getUrlSignPkcs7();
//            if(urlSignPkcs7==null){
//                System.out.println("urlSignPkcs7 is null");
//                logger.error("urlSignPkcs7 is null");
//                return null;
//            }
//            //System.out.println("urlSignPkcs7: "+urlSignPkcs7);
//            ResponseEntity<JSONObject> repSignPkcs7 = RestTemplateUtils.post(urlSignPkcs7, reqBody, JSONObject.class);
//            if (repSignPkcs7 != null)
//            {
//                JSONObject repBodySignPkcs7 = repSignPkcs7.getBody();
//                if (repBodySignPkcs7 != null)
//                {
//                    int nRetCode = repBodySignPkcs7.getIntValue("code");
//                    if (nRetCode == 0)
//                    {
//                        signData = repBodySignPkcs7.getString("signed_data");
//                        if (signData!=null){
//                            logger.info("signPkcs7 success! signData= " + signData);
//                        }
//                    }else{
//                        System.out.println("signPkcs7 code = "+nRetCode);
//                        logger.error("signPkcs7 code = "+nRetCode);
//                        return null;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            //e.printStackTrace();
//            logger.error("ExceptionTest Exception:",e);
//        }
//        return signData;
//    }

    /**
     * 联合签名，对随机数密钥进行签名
     * @param randomN
     * @return
     */
    public String unionSignedAndEnvelope(String randomN){
        if(randomN==null || randomN.isEmpty()){
            System.out.println("unionSignedAndEnvelope randomN is null or empty");
            logger.error("unionSignedAndEnvelope randomN is null or empty");
            return null;
        }
        HttpClientUtils ht=new HttpClientUtils();
        String urlunionSignedAndEnvelope=null;
        String evpData=null;
        String error_msg=null;
        String error_code=null;
        Map<String, Object> data = new HashMap<>();
        ReadConf rf = new ReadConf();
        rf.setPin("pin");
        rf.setUserId("userId");
        rf.setCommand("command");
        //rf.readcert()
        //random
        data.put("command", rf.getCommand());
        data.put("userId", rf.getUserId());
        data.put("pin", rf.getPin());
        data.put("recv_cert", rf.readcert());
        data.put("data", randomN);
        Map<String, Object> map = new HashMap<>();
        map.put("Data", data);
        map.put("SignV", "{}");
        JSONObject repBodyEncEnv=null;
        JSONObject jsonData=null;
        JSONObject reqBody = new JSONObject();
        reqBody.clear();
        reqBody = JSONObject.parseObject(JSON.toJSONString(map));
        try {
            rf.setUrlunionSignedAndEnvelope("urlunionSignedAndEnvelope");
            urlunionSignedAndEnvelope = rf.getUrlunionSignedAndEnvelope();
            if (urlunionSignedAndEnvelope == null) {
                System.out.println("urlunionSignedAndEnvelope is null");
                logger.error("urlunionSignedAndEnvelope is null");
                return null;
            }
            repBodyEncEnv = ht.doPost(urlunionSignedAndEnvelope, reqBody);

            if (repBodyEncEnv != null) {

                String dataContent = repBodyEncEnv.getString("Data");

                if(dataContent!=null){
                    jsonData = JSONObject.parseObject(dataContent);
                    if (jsonData.getString("result").equalsIgnoreCase("0x00000000")) {
                        evpData = jsonData.getJSONObject("responseInfo").getString("p7Data");
                        if (evpData != null) {
                            logger.info("unionSignedAndEnvelope success!");
                        }
                    }else{
                        error_msg = jsonData.getString("msg");
                        error_code = jsonData.getString("result");
                        logger.error("unionSignedAndEnvelope result = " + error_code);
                        logger.error("unionSignedAndEnvelope msg = " + error_msg);
                        return null;
                    }
                }
            }
        }catch (Exception e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }
        return evpData;
    }





}


