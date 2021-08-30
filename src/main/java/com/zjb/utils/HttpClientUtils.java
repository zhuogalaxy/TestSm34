package com.zjb.utils;

import java.io.IOException;
//import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
//import org.apache.http.HttpHost;
//import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
//import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
//import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.params.HttpParams;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.parser.JSONToken;

public class HttpClientUtils {
	
	public static void main(String[] args) throws IOException {
		JSONObject reqBody = new JSONObject();
		JSONObject respon=null;
        String urlGenerateRandom=null;
        String randomN = null;
        ReadConf rf=new ReadConf();
        //Properties prop = new Properties();
        reqBody.clear();
        reqBody.put("version", "2");
        reqBody.put("authcode", "ESIzRREiMRIxJREiM0URIjESMSURIjNFESIxEjElESIzRREiMRIxJQ==");  //内网
        // reqBody.put("authcode", "asd");
        reqBody.put("randLen", "16");        
        rf.setUrlGenerateRandom("urlGenerateRandom");
        urlGenerateRandom=rf.getUrlGenerateRandom();
        HttpClientUtils ht=new HttpClientUtils();
		respon=ht.doPost(urlGenerateRandom, reqBody);
		
		String rand_data = respon.getString("rand_data");
		
		System.out.println(respon);
		System.out.println("rand_data = "+rand_data);
		
	}
	
	
	
	public JSONObject doPost(String url,JSONObject json){
		   //DefaultHttpClient client = new DefaultHttpClient();
		   HttpClient client = HttpClientBuilder.create().build();//获取DefaultHttpClient请求
		   HttpPost post = new HttpPost(url);
		   JSONObject response = null;
		   try {
		     StringEntity s = new StringEntity(json.toString());
		     s.setContentEncoding("UTF-8");
		     s.setContentType("application/json");//发送json数据需要设置contentType
		     post.setEntity(s);
		     HttpResponse res = client.execute(post);
		     if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		       HttpEntity entity = res.getEntity();
		       String result = EntityUtils.toString(res.getEntity());// 返回json格式：
		       //response = JSONObject.fromObject(result);
		       response = JSONObject.parseObject(result);
		     }
		   } catch (Exception e) {
		     throw new RuntimeException(e);
		   }
		   return response;
	}
	
	
	 public JSONObject doGet(String url,JSONObject json) {  
         
	        HttpClient client = HttpClientBuilder.create().build();  
	        HttpGet get = new HttpGet(url); 	        
	        JSONObject response = null;
	        try {
	        	StringEntity s = new StringEntity(json.toString());
			    s.setContentEncoding("UTF-8");
			    s.setContentType("application/json");//发送json数据需要设置contentType
			    ((HttpResponse) get).setEntity(s);
			    
	            HttpResponse res = client.execute(get);  
	            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
	               HttpEntity entity = res.getEntity();	                
	 		       String result = EntityUtils.toString(res.getEntity());   // 返回json格式：
	 		       response = JSONObject.parseObject(result);
	            }  
	        } catch (Exception e) {  
	            throw new RuntimeException(e);  
	              
	        } finally{  
	            //关闭连接 ,释放资源  
	            //client.getConnectionManager().shutdown();  
	        }  
	        return response;  
	    }  
	

}
