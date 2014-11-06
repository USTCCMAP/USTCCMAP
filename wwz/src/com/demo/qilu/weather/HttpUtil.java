/**
 * @author 神奇4+1
 * @version 1.0.0 创建时间：2012-08-08
 * 该类为本项目的天气查询功能数据处理。
 */

package com.demo.qilu.weather;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {
	
	private static final String CHARSET = HTTP.UTF_8;
	
	private static HttpClient httpClientUtil;
	
	private static final String TAG = "CustomerHttpClient";


	
	private HttpUtil() {
		
	}
	
	public static synchronized HttpClient getHttpClient() {
		
		if (null == httpClientUtil) {
			HttpParams params = new BasicHttpParams();
			
			//设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			String useragent = "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;" +
					"Nexus One Build.FRG83)AppleWebKit/553.1(KHTML,like Gecko) " +
					"Version/4.0 Mobile Safari/533.1";
			HttpProtocolParams.setUserAgent(params, useragent);
			
			//设置超时
			/*从连接池中取连接的超时时间*/
			ConnManagerParams.setTimeout(params, 50000);
			
			/*连接超时*/
			HttpConnectionParams.setConnectionTimeout(params, 50000);
			
			/*请求超时*/
			HttpConnectionParams.setSoTimeout(params, 50000);
			
			//设置HttpClient支持HTTP和HTTPS两种模式
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", 
					PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", 
					SSLSocketFactory.getSocketFactory(), 443));
			
			//使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new 
					ThreadSafeClientConnManager(params, schReg);
			
			httpClientUtil = new DefaultHttpClient(conMgr, params);
		}
		
		return httpClientUtil;
	}
	
	//POST请求
	public static String post(String url, NameValuePair... params) {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		
		for (NameValuePair p: params) {
			formparams.add(p);
		}
		
		try {
			UrlEncodedFormEntity entity = new 
					UrlEncodedFormEntity(formparams, CHARSET);
			
			//创建POST请求
			HttpPost request = new HttpPost(url);
			request.setEntity(entity);
			
			//发送请求
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute(request);
			
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new RuntimeException("请求失败");
			}
			
			HttpEntity resEntity = response.getEntity();
			
			return (resEntity == null) ? null 
					: EntityUtils.toString(resEntity, CHARSET);
		
		} catch (UnsupportedEncodingException e) {
			Log.i(TAG, e.getMessage());
			return null;
		} catch (ClientProtocolException e) {
			Log.i(TAG, e.getMessage());
			return null;
		} catch (IOException e) {
			throw new RuntimeException("连接失败", e);
		}
	}
	
	public static String get(String url) {
		
		//发送请求
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		
		try {
			HttpResponse response = client.execute(request);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity, CHARSET);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}
}