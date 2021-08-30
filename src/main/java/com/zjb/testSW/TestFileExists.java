package com.zjb.testSW;

import java.io.File;

public class TestFileExists {
    public static void main(String[] args){
        String ERROROBS_DIR = "D:\\testZip\\testdat";
        String []str = null;
        boolean b = false;
        File file = new File(ERROROBS_DIR);
        if(file.isDirectory()){
            str = file.list();
            System.out.println(str.length);
            if(str.length>=0){
                b = true;
            }
        }
        System.out.println(b);
    }

}
