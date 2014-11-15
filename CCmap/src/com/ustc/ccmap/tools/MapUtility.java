package com.ustc.ccmap.tools;   
  
import java.io.IOException;   

import java.io.InputStream;   
import org.apache.http.HttpEntity;   
import org.apache.http.HttpResponse;   
import org.apache.http.client.ClientProtocolException;   
import org.apache.http.client.HttpClient;   
import org.apache.http.client.methods.HttpGet;   
import org.apache.http.impl.client.DefaultHttpClient;   
import org.json.JSONArray;   
import org.json.JSONException;   
import org.json.JSONObject;   

import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.annotation.SuppressLint;
import android.util.Log;
  
public class MapUtility {   
	
	private static String[] LLT = {
		"�Ϸ� 31.52 117.17",
		"���� 21.33 115.07",
		"���� 39.55 116.24",
		"���� 26.05 119.18",
		"���� 36.04 103.51",
		"���� 23.08 113.14",
		"���� 22.48 108.19",
		"���� 26.35 106.42",
		"���� 20.02 110.20",
		"ʯ��ׯ 38.02 114.30",
		"��� 38.02 114.50",
		"֣�� 34.46 11340",
		"������ 45.44 126.36",
		"�人 30.35 114.17",
		"��ɳ 28.12 112.59",
		"���� 43.54 125.19",
		"�Ͼ� 32.03 118.46",
		"���� 34.15 117.11",
		"�ϲ� 28.40 115.55",
		"���� 41.48 123.25",
		"���ͺ��� 40.48 111.41",
		"���� 38.27 106.16",
		"���� 36.38 101.48",
		"���� 36.40 117.00",
		"̫ԭ 37.54 112.33",
		"Т�� 37.08 111.48",
		"���� 34.17 108.57",
		"�Ϻ� 31.14 121.29",
		"�ɶ� 30.40 104.04",
		"̨���� 25.03 121.30",
		"����� 39.02 117.12",
		"���� 29.39 91.08",
		"����� 21.23 115.12",
		"��³ľ�� 43.45 87.36",
		"���� 25.04 102.42",
		"���� 30.16 120.10",
		"������ 29.35 106.33",
	};
	
	
	
	/**
	 * ͨ��������ַ����øõصľ�����Ϣ���Թ��õ��õصľ�γ��
	 */
    public static JSONObject getLocationInfo(String address) {   
  
//        HttpGet httpGet = new HttpGet("http://maps.google."  
//                + "com/maps/api/geocode/json?address=" + address   
//                + "ka&sensor=false");  
        
        HttpGet httpGet = new HttpGet("http://api.map.baidu.com/geocoder/v2/?address="
        			+ address + "&output=json&ak=tSzVmebGzWQVn33I5thcDoja"); 
        
        
        Log.e("inininininin", "dd");
        
        
        HttpClient client = new DefaultHttpClient();   
        HttpResponse response;   
        StringBuilder stringBuilder = new StringBuilder();   
  
        try {
        	
        	int i =0;
        	
        	do{
        		
        		stringBuilder.delete(0, stringBuilder.length());
        		
            response = client.execute(httpGet);

            HttpEntity entity = response.getEntity();   
            InputStream stream = entity.getContent();   
            int b;   
            while ((b = stream.read()) != -1) {   
                stringBuilder.append((char) b);   
            }   
            
            Log.e("inininininin", stringBuilder.toString());
            
        	}while((-1 == stringBuilder.toString().indexOf("status\":0")) && i++ < 8);
            
            
        } catch (ClientProtocolException e) {   
        	e.printStackTrace();   
        } catch (IOException e) {   
        	e.printStackTrace();   
        }   
  
        JSONObject jsonObject = new JSONObject();   
        try {   
            jsonObject = new JSONObject(stringBuilder.toString());   
        } catch (JSONException e) {   
            e.printStackTrace();   
        }   
        
        return jsonObject;   
    }   
  
	/**
	 * ��ִ�еõ���JSONObject�У� �������õص�ľ�γ��GeoPoint
	 */
    @SuppressLint({ "UseValueOf", "UseValueOf" })
	public static GeoPoint getGeoPoint(JSONObject jsonObject) {   
  
        Double lon = new Double(116.4578935);   
        Double lat = new Double(39.9799343);   

        Log.e("infofoofofofoo", jsonObject.toString());
        
        try {   
        	//����JSONOBJECT��øõصľ�γ��
            lon = jsonObject.getJSONObject("result").getJSONObject("location").getDouble("lng");
                    //.getJSONObject("geometry")
                    
  
            lat = jsonObject.getJSONObject("result").getJSONObject("location").getDouble("lat");
//            ((JSONArray) jsonObject.get("result")).getJSONObject(0)   
//                    //.getJSONObject("geometry")
//                    .getJSONObject("location")   
//                    .getDouble("lat");
  
        } catch (JSONException e) {  
        	//�쳣����
        	Log.e("MapUtiulity", "failure to get the info");
            e.printStackTrace();   
        }   
        return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));   
    }   
  
}  