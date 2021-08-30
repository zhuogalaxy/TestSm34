package com.zjb.utils;

import ch.qos.logback.classic.Logger;
//import com.zjb.encode.EncodeFile;
import com.zjb.decode.DecodeFile;
import com.zjb.encode.EncodeFile;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.util.Properties;

public class ReadConf {
    public Logger logger = (Logger) LoggerFactory.getLogger(ReadConf.class);

    private String urlGenerateRandom;
    private String urlEncEnvelope;
    private String urlDeEnvelope;
    private String urlSignPkcs7;
    private String urlVerifyPkcs7;
    private String urlencodeSignedAndEnvelope;
    private String urldecodeSignedAndEnvelope;

    private String urlengetDeviceId;
    private String urldegetDeviceId;

    private String version;
    private String authcode;
    private String randLen;
    private String key_id;
    private String alg_signature;
    private String encrypt_alg;
    private String key_id_de;

    private String accessKeyId;
    private String secretAccessKey;
    private String endPoint;
    private String photoBucketName;
    private String fingerBucketName;
    private String pin;
    private String userId;
    private String command;
    private String urlunionSignedAndEnvelope;



    public String getKey_id_de() {
        return key_id_de;
    }

    public void setKey_id_de(String key_id_de) {
        this.key_id_de = readconf(key_id_de);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = readconf(version);
    }

    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = readconf(authcode);
    }

    public String getRandLen() {
        return randLen;
    }

    public void setRandLen(String randLen) {
        this.randLen = readconf(randLen);
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = readconf(key_id);
    }

    public String getAlg_signature() {
        return alg_signature;
    }

    public void setAlg_signature(String alg_signature) {
        this.alg_signature = readconf(alg_signature);
    }

    public String getEncrypt_alg() {
        return encrypt_alg;
    }

    public void setEncrypt_alg(String encrypt_alg) {
        this.encrypt_alg = readconf(encrypt_alg);
    }


    public String getUrlencodeSignedAndEnvelope() {
        return urlencodeSignedAndEnvelope;
    }

    public void setUrlencodeSignedAndEnvelope(String urlencodeSignedAndEnvelope) {
        this.urlencodeSignedAndEnvelope = readconf(urlencodeSignedAndEnvelope);
    }

    public String getUrldecodeSignedAndEnvelope() {
        return urldecodeSignedAndEnvelope;
    }

    public void setUrldecodeSignedAndEnvelope(String urldecodeSignedAndEnvelope) {
        this.urldecodeSignedAndEnvelope = readconf(urldecodeSignedAndEnvelope);
    }

    public String getUrlGenerateRandom() {
        return urlGenerateRandom;
    }

    public void setUrlGenerateRandom(String urlGenerateRandom) {
        //urlGenerateRandom=readconf("urlGenerateRandom");
        //urlGenerateRandom="urlGenerateRandom";
        this.urlGenerateRandom = readconf(urlGenerateRandom);
    }

    public String getUrlEncEnvelope() {
        return urlEncEnvelope;
    }

    public void setUrlEncEnvelope(String urlEncEnvelope) {

        this.urlEncEnvelope = readconf(urlEncEnvelope);
    }

    public String getUrlDeEnvelope() {
        return urlDeEnvelope;
    }

    public void setUrlDeEnvelope(String urlDeEnvelope) {
        this.urlDeEnvelope = readconf(urlDeEnvelope);
    }

    public String getUrlSignPkcs7() {
        return urlSignPkcs7;
    }

    public void setUrlSignPkcs7(String urlSignPkcs7) {
        this.urlSignPkcs7 = readconf(urlSignPkcs7);
    }

    public String getUrlVerifyPkcs7() {
        return urlVerifyPkcs7;
    }

    public void setUrlVerifyPkcs7(String urlVerifyPkcs7) {
        this.urlVerifyPkcs7 = readconf(urlVerifyPkcs7);
    }


    public String getUrlengetDeviceId() {
        return urlengetDeviceId;
    }

    public void setUrlengetDeviceId(String urlengetDeviceId) {
        this.urlengetDeviceId = readconf(urlengetDeviceId);
    }

    public String getUrldegetDeviceId() {
        return urldegetDeviceId;
    }

    public void setUrldegetDeviceId(String urldegetDeviceId) {
        this.urldegetDeviceId = readconf(urldegetDeviceId);
    }


    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = readconf(accessKeyId);
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = readconf(secretAccessKey);
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = readconf(endPoint);
    }

    public String getPhotoBucketName() {
        return photoBucketName;
    }

    public void setPhotoBucketName(String photoBucketName) {
        this.photoBucketName = readconf(photoBucketName);
    }

    public String getFingerBucketName() {
        return fingerBucketName;
    }

    public void setFingerBucketName(String fingerBucketName) {
        this.fingerBucketName = readconf(fingerBucketName);
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = readconf(pin);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = readconf(userId);
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = readconf(command);
    }

    public String getUrlunionSignedAndEnvelope() {
        return urlunionSignedAndEnvelope;
    }

    public void setUrlunionSignedAndEnvelope(String urlunionSignedAndEnvelope) {
        this.urlunionSignedAndEnvelope = readconf(urlunionSignedAndEnvelope);
    }

    public String readconf(String str){
        String url="";
        Properties prop = new Properties();
        RWFile rf=new RWFile();
        String path="";
        path=ReadConf.class.getResource("/").getPath();
        InputStreamReader inputRead=null;
        FileInputStream fileInput=null;
        //path=new File(path).getParent();
        path=new File(path).getParent().replace("%20"," ").replace("target","");
        System.out.println("path = "+path);
        logger.info("path = "+path);
        try {
            //prop.load(new InputStreamReader(new FileInputStream("conf/config.properties"),"utf-8"));
        	//"D:\apache-tomcat-8.5.57\IdentCenterManage\conf"
        	
        	//prop.load(new InputStreamReader(new FileInputStream("WEB-INF/conf/config.properties"),"utf-8"));
        	
        	//prop.load(new InputStreamReader(new FileInputStream("D:\\apache-tomcat-8.5.57\\webapps\\IdentCenterManage\\conf\\config.properties"),"utf-8"));
            //URLDecoder.decode(path,"UTF-8");

        	if(rf.judgeSystem()==0) {
                fileInput=new FileInputStream(path+"\\conf\\config.properties");
                inputRead=new InputStreamReader(fileInput,"utf-8");
        		//prop.load(new InputStreamReader(new FileInputStream(path+"\\conf\\config.properties"),"utf-8"));
                prop.load(inputRead);
            }else {
                fileInput=new FileInputStream(path+"/conf/config.properties");
                inputRead=new InputStreamReader(fileInput,"utf-8");
            	//prop.load(new InputStreamReader(new FileInputStream(path+"/conf/config.properties"),"utf-8"));
                prop.load(inputRead);
        	}
            
        	
        	//prop.load(new InputStreamReader(new FileInputStream(path+File.separator+"conf"+File.separator+"config.properties"),"utf-8"));
        	
        	url=prop.getProperty(str);
            if(url==null || url.isEmpty()){
                System.out.println("readconf url is empty");
                logger.error("readconf url is empty");
                //return null;
            }
            fileInput.close();
            inputRead.close();
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }finally {
            try{
                if(fileInput!=null){
                    fileInput.close();
                }
                if(inputRead!=null){
                    inputRead.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return url;
    }


    public String readcert(){
    	RWFile rf=new RWFile();
        String b64_cert=null;
        BufferedReader reader = null;
        String tempstr=null;
        StringBuilder temp=new StringBuilder();
        String path="";
        path=ReadConf.class.getResource("/").getPath();  //此处"/"表示获取这个类的路径
        InputStreamReader inputRead=null;
        FileInputStream fileInput=null;
        System.out.println("path1 = "+path);
        //path=new File(path).getParent();
        path=new File(path).getParent().replace("%20"," ").replace("target","");
        System.out.println("path2 = "+path);
        logger.info("path = "+path);
        try {
            //reader = new BufferedReader(new InputStreamReader(new FileInputStream("conf/encryptCrt.cert"),"utf-8"));
        	//reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\apache-tomcat-8.5.57\\webapps\\IdentCenterManage\\conf\\encryptCrt.cert"),"utf-8"));
            
            //reader = new BufferedReader(new InputStreamReader(new FileInputStream(path+File.separator+"conf"+File.separator+"encryptCrt.cert"),"utf-8"));
            //URLDecoder.decode(path,"UTF-8");
        	if(rf.judgeSystem()==0) {
                fileInput=new FileInputStream(path+"\\conf\\encryptCrt.cert");
                inputRead=new InputStreamReader(fileInput,"utf-8");
        		//reader = new BufferedReader(new InputStreamReader(new FileInputStream(path+"\\conf\\encryptCrt.cert"),"utf-8"));
                reader = new BufferedReader(inputRead);
        	}else {
                fileInput=new FileInputStream(path+"/conf/encryptCrt.cert");
                inputRead=new InputStreamReader(fileInput,"utf-8");
                //reader = new BufferedReader(new InputStreamReader(new FileInputStream(path+"\\conf\\encryptCrt.cert"),"utf-8"));
                reader = new BufferedReader(inputRead);

            	//reader = new BufferedReader(new InputStreamReader(new FileInputStream(path+"/conf/encryptCrt.cert"),"utf-8"));
            }
        	
        	while ((tempstr=reader.readLine()) != null) {
                temp.append(tempstr);
            }
            reader.close();
            b64_cert=temp.toString();
            if(b64_cert==null || b64_cert.isEmpty()){
                System.out.println("readcert b64_cert is empty");
                logger.error("readcert b64_cert is empty");
                //return null;
            }
            fileInput.close();
            inputRead.close();
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("ExceptionTest Exception:",e);
        }finally {
            try{
                if(fileInput!=null){
                    fileInput.close();
                }
                if(inputRead!=null){
                    inputRead.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b64_cert;
    }

    public static void main(String[] args) {
        ReadConf rf=new ReadConf();
        //rf.setAccessKeyId("accessKeyId");
        //rf.getAccessKeyId();
        rf.setPin("pin");
        rf.setUserId("userId");
        rf.setCommand("command");
        rf.setUrlunionSignedAndEnvelope("urlunionSignedAndEnvelope");
        System.out.println("pin = "+rf.getPin());
        System.out.println("userId = "+rf.getUserId());
        System.out.println("command = "+rf.getCommand());
        System.out.println("urlunionSignedAndEnvelope = "+rf.getUrlunionSignedAndEnvelope());
        //System.out.println(rf.readcert());
        //EncodeFile ef = new EncodeFile();

        //DecodeFile df = new DecodeFile();
        //System.out.println();
        //System.out.println(df.get_decode_Key_id());


    }
}
