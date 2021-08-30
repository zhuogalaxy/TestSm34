package com.zjb.utils;

//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

//import javax.servlet.http.HttpServletRequest;

public class TestDirctory {

    public static void main(String[] args) throws IOException {
//          String urlGenerateRandom = null;
//          ReadConf rf=new ReadConf();
//          rf.setUrlGenerateRandom("urlGenerateRandom");
//          urlGenerateRandom=rf.getUrlGenerateRandom();
//          
//     
//          System.out.println("url= "+urlGenerateRandom);
//
//          byte[] randomN=new byte[16];
//          System.out.println("randomN.length" + randomN.length);
//          
//          RWFile rwf = new RWFile();
//          System.out.println(rwf.judgeSystem());
          
          String path="";
          String parPath="";
          path=TestDirctory.class.getResource(File.separator).getPath();
          parPath=new File(path).getParent();
          System.out.println(path);
          System.out.println(parPath);

//        Properties prop;
//
//        prop = PropertiesLoaderUtils.loadProperties(new ClassPathResource("conf/config.properties"));
//        urlGenerateRandom=prop.getProperty("urlGenerateRandom");
//        System.out.println("url= "+urlGenerateRandom);

        //Properties prop = new Properties();

        //System.out.println("path = " + path);
        //D:\Program Files\javaworkspace\jiangsu\TestSm34\confDir
       // System.out.println(new File(".").getAbsolutePath());
        //prop.load(new InputStreamReader(new FileInputStream("confDir/config.properties"),"utf-8"));
       // urlGenerateRandom = prop.getProperty("urlGenerateRandom");
       // System.out.println("url= "+urlGenerateRandom);


    }
}