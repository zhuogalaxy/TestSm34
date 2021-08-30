package com.zjb.utils;

import ch.qos.logback.core.joran.spi.JoranException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetFilename {


    public List getFileName(String srcPath){

        if (srcPath==null || srcPath.isEmpty()){
            return null;
        }

        List list = new ArrayList();
        try {
            File file = new File(srcPath.trim());
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                list.add(srcPath.trim()+"\\"+filelist[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;



    }
    public List getFileName2(String srcPath,List list){

        if (srcPath==null || srcPath.isEmpty()){
            return list;
        }
                //new ArrayList();

        File f = new File(srcPath.trim());
        if (!f.exists()) {
            System.out.println(srcPath + " not exists");
            return list;
        }
        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                getFileName2(fs.getPath().trim(),list);
                //System.out.println(fs.getName() + "srcPath");
            } else {
                list.add(fs.getPath().trim()+"\\"+fs.getName());
                //System.out.println(fs.getName());
            }
        }
        return list;

    }

    public static void main(String[] args)  {

        String str="D:\\Program Files\\javaworkspace\\TestSm34\\target";

        GetFilename gf = new GetFilename();
        List list=new ArrayList();
        list = gf.getFileName2(str,list);

        for (int i = 0; i < list.size(); i++) {
            String name = (String) list.get(i);   //需要进行String进行强转
            System.out.println(name);
        }

    }



}
