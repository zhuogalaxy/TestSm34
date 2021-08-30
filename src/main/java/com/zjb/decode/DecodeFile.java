package com.zjb.decode;

import ch.qos.logback.classic.Logger;
import com.alibaba.fastjson.JSONObject;


import com.zjb.encode.EncodeFile;
import com.zjb.sm3.SM3Digest;
import com.zjb.sm4.SM4;
import com.zjb.sm4.SM4_Context;
import com.zjb.utils.HttpClientUtils;
import com.zjb.utils.ReadConf;
//import com.zjb.utils.RestTemplateUtils;
import org.slf4j.LoggerFactory;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.support.PropertiesLoaderUtils;
//import org.springframework.http.ResponseEntity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Properties;

/**
 * @author Adam zhao
 * @Time 2020/7/23
 */
public class DecodeFile {

    public Logger logger = (Logger) LoggerFactory.getLogger(EncodeFile.class);

    /**
     * 1-off. 数字信封解封，返回随机数密钥
     * @param evpData 数字信封文件内容 String
     * @return  返回随机数密钥 String
     */
//    public String decodeEnvelope(String evpData){
//        if(evpData==null || evpData.isEmpty()){
//            System.out.println("decodeEnvelope evpData is null or empty");
//            logger.error("decodeEnvelope evpData is null or empty");
//            return null;
//        }
//        //evpData = Base64.getEncoder().encode(evpData);
//        JSONObject reqBody = new JSONObject();
//        String urlDeEnvelope=null;
//        String randomN = null;
//        ReadConf rf= new ReadConf();
//        //Properties prop = new Properties();
//        try{
//            reqBody.clear();
//            reqBody.put("version", "2");
//            reqBody.put("authcode", "asd");
//            reqBody.put("key_id", "sm2001");
//            reqBody.put("evp_data", evpData);
//            //urlDeEnvelope = "http://60.217.194.220:20127/decodeEnvelope";
//            //Properties prop = PropertiesLoaderUtils.loadProperties(new ClassPathResource("conf/config.properties"));
//            //prop.load(new InputStreamReader(new FileInputStream("confDir/config.properties"),"utf-8"));
//            //urlDeEnvelope=prop.getProperty("urlDeEnvelope");
//            rf.setUrlDeEnvelope("urlDeEnvelope");
//            urlDeEnvelope=rf.getUrlDeEnvelope();
//            if(urlDeEnvelope==null){
//                System.out.println("urlDeEnvelope is null");
//                logger.error("urlDeEnvelope is null");
//                return null;
//            }
//            ResponseEntity<JSONObject> repDeEnv = RestTemplateUtils.post(urlDeEnvelope, reqBody, JSONObject.class);
//            if (repDeEnv != null)
//            {
//                JSONObject repBodyDeEnv = repDeEnv.getBody();
//                if (repBodyDeEnv != null)
//                {
//                    int nRetCode = repBodyDeEnv.getIntValue("code");
//                    if (nRetCode == 0)
//                    {
//                        randomN = repBodyDeEnv.getString("origin_data");
//                        if(randomN!=null){
//                            logger.info("decodeEnvelope success！randomN = " + randomN);
//                        }
//                    }else{
//                        System.out.println("decodeEnvelope code = "+nRetCode);
//                        logger.error("decodeEnvelope code = "+nRetCode);
//                        return null;
//                    }
//                }
//            }
//        }catch (Exception e){
//            //e.printStackTrace();
//            logger.error("ExceptionTest Exception:",e);
//        }
//        return randomN;
//    }

    //验签+解密

    /**
     * 1. 验签并解封数字信封，
     * @param evpData
     * @return 返回16字节密钥+32字节密文摘要
     */
    public String decodeSignedAndEnvelope(String evpData){
        if(evpData==null || evpData.isEmpty()){
            System.out.println("decodeSignedAndEnvelope evpData is null or empty");
            logger.error("decodeSignedAndEnvelope evpData is null or empty");
            return null;
        }
        //evpData = Base64.getEncoder().encode(evpData);
        //System.out.println("evpData = "+evpData);
        HttpClientUtils ht=new HttpClientUtils();
        JSONObject reqBody = new JSONObject();
        String urldecodeSignedAndEnvelope=null;
        String originData = null;
//        String key_id=get_decode_Key_id();
//        if(key_id==null  || key_id.isEmpty()){
//            System.out.println("generateKey_id is null");
//            logger.error("generateKey_id is null");
//            return null;
//        }
//        key_id="Device_Q"+key_id.substring(1);
//        System.out.println("key_id = " + key_id);

        ReadConf rf= new ReadConf();
        rf.setVersion("version");
        rf.setAuthcode("authcode");
        rf.setKey_id_de("key_id_de");
        rf.setAlg_signature("alg_signature");
        //Properties prop = new Properties();
        try{
            reqBody.clear();
            reqBody.put("version", rf.getVersion());
            reqBody.put("authcode", rf.getAuthcode());
            reqBody.put("key_id", rf.getKey_id_de());
            //reqBody.put("key_id", key_id);
            reqBody.put("alg_signature",rf.getAlg_signature());
            reqBody.put("evp_data", evpData);
            rf.setUrldecodeSignedAndEnvelope("urldecodeSignedAndEnvelope");
            urldecodeSignedAndEnvelope=rf.getUrldecodeSignedAndEnvelope();
            //System.out.println("urldecodeSignedAndEnvelope = "+urldecodeSignedAndEnvelope);
            if(urldecodeSignedAndEnvelope==null){
                System.out.println("urldecodeSignedAndEnvelope is null");
                logger.error("urldecodeSignedAndEnvelope is null");
                return null;
            }
            //ResponseEntity<JSONObject> repDeEnv = RestTemplateUtils.post(urldecodeSignedAndEnvelope, reqBody, JSONObject.class);
            //if (repDeEnv != null)
           // {
                //JSONObject repBodyDeEnv = repDeEnv.getBody();
            JSONObject repBodyDeEnv= ht.doPost(urldecodeSignedAndEnvelope, reqBody);
                if (repBodyDeEnv != null)
                {
                    int nRetCode = repBodyDeEnv.getIntValue("code");
                    String desc = repBodyDeEnv.getString("description");
                    if (nRetCode == 0)
                    {
                        originData = repBodyDeEnv.getString("origin_data");
                        if(originData!=null){
                            logger.info("decodeSignedAndEnvelope success!");
                        }
                    }else{
                        System.out.println("decodeSignedAndEnvelope code = "+nRetCode);
                        logger.error("decodeSignedAndEnvelope code = "+nRetCode);
                        logger.error("decodeSignedAndEnvelope desc = "+desc);
                        return null;
                    }
                }
            //}
        }catch (Exception e){
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }
        return originData;
    }


    public String get_decode_Key_id() {
        HttpClientUtils ht=new HttpClientUtils();
        JSONObject reqBody = new JSONObject();
        //String urlengetDeviceId=null;
        String urldegetDeviceId=null;
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
            //rf.setUrlengetDeviceId("urldegetDeviceId");
            //urlengetDeviceId=rf.getUrlengetDeviceId();
            rf.setUrldegetDeviceId("urldegetDeviceId");
            urldegetDeviceId=rf.getUrldegetDeviceId();
            //urlGenerateRandom="http://172.31.113.131:8083/generateRandom";
            if(urldegetDeviceId==null){
                System.out.println("urldegetDeviceId is null");
                logger.error("urldegetDeviceId is null");
                return null;
            }
            //断网处理？？？
            //ResponseEntity<JSONObject> repRandom = RestTemplateUtils.post(urlGenerateRandom, reqBody, JSONObject.class);

            //if(repRandom != null){
            //JSONObject repBodyRandom = repRandom.getBody();
            JSONObject repBodyRandom=ht.doPost(urldegetDeviceId, reqBody);
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
     * 2. 验证摘要，先拆分摘要，再比对验证
     * @param encrypted  文件密文
     * @param origin_data  数字信封拆封之后的内容
     * @return
     */
    //originData, 包含前16字节随机数randomN 和 后32字节摘要值hash
    public boolean checkDigestSm3(byte[] encrypted,String origin_data){
        //System.out.println("origin_data = " + origin_data);
        byte[] originData= Base64.getDecoder().decode(origin_data);
        //System.out.println("originData.length = " + originData.length);
        //先拆解摘要值
        byte[] hashBak=new byte[32];
        byte[] hash=new byte[32];
        boolean b=false;
        System.arraycopy(originData,16, hashBak ,0,hashBak.length);

        SM3Digest sm3 = new SM3Digest();
        sm3.update(encrypted, 0, encrypted.length);
        sm3.doFinal(hash, 0);
        b = new String(hashBak).equals(new String(hash));
        if (!b){
            System.out.println("checkDigestSm3 fail");
            logger.error("checkDigestSm3 fail");
            //return false;
        }
        return b;
    }

    /**
     * 3. 拆分数字信封内容，获得16字节随机数
     * @param origin_data  数字信封拆封之后的内容
     * @return  16字节随机数
     */
    public byte[] getRandomN(String origin_data){
        if(origin_data==null || origin_data.isEmpty()){
            System.out.println("getRandomN origin_data is null or empty");
            logger.error("getRandomN origin_data is null or empty");
            return null;
        }
        byte[] originData=Base64.getDecoder().decode(origin_data);
        byte[] randomN=new byte[16];
        System.arraycopy(originData,0, randomN ,0,randomN.length);
        return randomN;
    }

    /**
     * 3. 利用随机数密钥和软解密程序解密，返回原文byte[]
     * @param randomN  随机数密钥 byte[]
     * @param encrypted  密文 byte[]
     * @return  返回原文 byte[]
     */
    public byte[] sm4Decode(byte[] randomN,byte[] encrypted){
        if(randomN==null || randomN.length<=0){
            System.out.println("sm4Decode randomN is null or empty");
            logger.error("sm4Decode randomN is null or empty");
            return null;
        }
        if(encrypted==null || encrypted.length<=0){
            System.out.println("sm4Decode encrypted is null or empty");
            logger.error("sm4Decode encrypted is null or empty");
            return null;
        }
        byte[] originData=null;
        try {
            SM4_Context ctx_dec = new SM4_Context();
            SM4 sm4_dec = new SM4();
            ctx_dec.isPadding = true;
            ctx_dec.mode = sm4_dec.SM4_DECRYPT;
            //SM4 sm4_dec = new SM4();
            sm4_dec.sm4_setkey_dec(ctx_dec, randomN);
            originData = sm4_dec.sm4_crypt_ecb(ctx_dec, encrypted);
            if(originData!=null){
                logger.info("sm4Decode success");
            }
            //originData = Base64.getEncoder().encode(originData);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }
        return originData;
    }


}
