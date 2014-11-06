/**
 * @author ����4+1
 * @version 1.0.0 ����ʱ�䣺2012-08-08
 * ����Ϊ����Ŀ��������ѯ�������ݴ���
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
			
			//����һЩ��������
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			String useragent = "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;" +
					"Nexus One Build.FRG83)AppleWebKit/553.1(KHTML,like Gecko) " +
					"Version/4.0 Mobile Safari/533.1";
			HttpProtocolParams.setUserAgent(params, useragent);
			
			//���ó�ʱ
			/*�����ӳ���ȡ���ӵĳ�ʱʱ��*/
			ConnManagerParams.setTimeout(params, 50000);
			
			/*���ӳ�ʱ*/
			HttpConnectionParams.setConnectionTimeout(params, 50000);
			
			/*����ʱ*/
			HttpConnectionParams.setSoTimeout(params, 50000);
			
			//����HttpClient֧��HTTP��HTTPS����ģʽ
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", 
					PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", 
					SSLSocketFactory.getSocketFactory(), 443));
			
			//ʹ���̰߳�ȫ�����ӹ���������HttpClient
			ClientConnectionManager conMgr = new 
					ThreadSafeClientConnManager(params, schReg);
			
			httpClientUtil = new DefaultHttpClient(conMgr, params);
		}
		
		return httpClientUtil;
	}
	
	//POST����
	public static String post(String url, NameValuePair... params) {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		
		for (NameValuePair p: params) {
			formparams.add(p);
		}
		
		try {
			UrlEncodedFormEntity entity = new 
					UrlEncodedFormEntity(formparams, CHARSET);
			
			//����POST����
			HttpPost request = new HttpPost(url);
			request.setEntity(entity);
			
			//��������
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute(request);
			
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new RuntimeException("����ʧ��");
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
			throw new RuntimeException("����ʧ��", e);
		}
	}
	
	public static String get(String url) {
		
		//��������
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