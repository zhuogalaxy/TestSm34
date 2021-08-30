package com.zjb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class ReadNLine {

    public static void main(String[] args) throws UnsupportedEncodingException {
        String fileName="D:\\Program Files\\javaworkspace\\jiangsu\\TestSm34\\testFiles\\1.zip";
        File file=new File(fileName);
        long startTime= System.currentTimeMillis();//获取当前系统时间(毫秒)
        String outNLine=tail2(file,3);
        long endTime=System.currentTimeMillis();
        System.out.println("use time: "+ (endTime-startTime));
        System.out.println(outNLine);
       //System.out.println(new String(outNLine.getBytes(),"utf-8"));

    }




    /***
     * read the last N lines from file
     * @param file
     * @param lines
     * @return
     */
    public static String tail2(File file, int lines) {
        RandomAccessFile fileHandler = null;
        try {
            fileHandler =  new RandomAccessFile( file, "r" );
            long fileLength = fileHandler.length() - 1;
            System.out.println("fileLength = "+fileLength);
            StringBuilder sb = new StringBuilder();
            int line = 0;

            for(long filePointer = fileLength; filePointer != -1; filePointer--){
                fileHandler.seek( filePointer );

                int readByte = fileHandler.readByte();
                //char charStr=fileHandler.readChar();

                if( readByte == 0xA ) {
                    if (line == lines) {
                        if (filePointer == fileLength) {
                            continue;
                        } else {
                            break;
                        }
                    }
                } else if( readByte == 0xD ) {
                    line = line + 1;
                    if (line == lines) {
                        if (filePointer == fileLength - 1) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
                sb.append( ( char ) readByte );
                //sb.append(charStr);
            }

            sb.deleteCharAt(sb.length()-1);
            String lastLine = sb.reverse().toString();
            return lastLine;
        } catch( java.io.FileNotFoundException e ) {
            e.printStackTrace();
            return null;
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (fileHandler != null )
                try {
                    fileHandler.close();
                } catch (IOException e) {
                    /* ignore */
                }
        }
    }

}


