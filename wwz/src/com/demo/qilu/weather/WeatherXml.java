package com.demo.qilu.weather;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class WeatherXml {
	
	//private static String url = "http://www.google.com/ig/api?&weather=";
	//http://api.map.baidu.com/telematics/v3/weather?
	//location=Ö£ÖÝ&output=json&ak=A72e372de05e63c8740b2622d0ed8ab1
	
	private static String url = "http://api.map.baidu.com/telematics/v3/weather?location=";
	
	private static String other = "&output=xml&ak=A72e372de05e63c8740b2622d0ed8ab1";
	
	public static String getWeatherXml(String city) {
		//NameValuePair param = new BasicNameValuePair("city", city);
		Log.e("xmlxmlxmxlmxlmxlm",city);
		//city = "±±¾©";
		String xml = HttpUtil.get(url + city + other);
		return xml;
	}

}
