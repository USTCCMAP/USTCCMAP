/**
 * @author 神奇4+1
 * @version 1.0.0 创建时间：2012-08-08
 * 该类为本项目的天气查询功能数据处理。
 */

package com.demo.qilu.weather;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.Handler;
import android.util.Log;


public class DomParseWeather {

	public static List<Weather> getWeatherFromXml(String xmlStr, Handler mHandler) {
		List<Weather> weathers = new ArrayList<Weather>();
		weathers.add(new Weather());
		weathers.add(new Weather());
		weathers.add(new Weather());
		weathers.add(new Weather());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource(new StringReader(xmlStr));
			Document document = builder.parse(inputSource);

			// xml_api_reply
			Element root = document.getDocumentElement();
			// weather.....
			NodeList nodeChildren1 = root.getChildNodes();
			
			Log.e("DomParseWeather", nodeChildren1.getLength()+"");
int n = 0;			
			for (int i = 0; i < nodeChildren1.getLength(); i++) {
				Node nodeChild1 = nodeChildren1.item(i);

				if (!"results".equals(nodeChild1.getNodeName()))
					continue;

                //weather_data
				NodeList nodeChildren2 = nodeChild1.getChildNodes();
				NodeList nodeChildren3 = nodeChildren2.item(3).getChildNodes();
				Log.e("DomParseWeather3", nodeChildren3.getLength()+"");
				Weather w = null;
				for (int j = 1; j < nodeChildren3.getLength(); j++) {
					// forecast_conditions
					Node nodeChild3 = nodeChildren3.item(j);
					Log.e("DomParseWeather3", nodeChild3.getNodeName() +"  "+j);
					
					String flag = nodeChild3.getNodeName();
					//Weather w = new Weather();
					if ("date".equals(flag)) {
						w = weathers.get(n++);
						//w = new Weather();
						String value = nodeChild3.getFirstChild().getNodeValue().trim();
						
						w.setDay_of_week(value.substring(0, 2));
						Log.i("TAG", "date" + " " + value+"  "+w.getDay_of_week());
					} else if ("dayPictureUrl".equals(flag)) {
						String value = nodeChild3.getFirstChild().getNodeValue().trim();//21 ~ 11*c
						Log.i("TAG", "img" + " " + value+" "+ w.getImageUrl());
						w.setImageUrl(value);
						
						w.setImage(WeatherActivity.loadImage(value));
						
					} else if ("weather".equals(flag)) {
						String value = nodeChild3.getFirstChild().getNodeValue().trim();
						w.setCondition(value);
						Log.i("TAG", "condition" + " " + value);
					} else if ("temperature".equals(flag)) {
						String value = nodeChild3.getFirstChild().getNodeValue().trim();//21 ~ 11c
						Log.i("TAG", value);
						if(value.length() < 4){
							w.setHigh_temperature(value.substring(0, value.length()-1));
							w.setLow_temperature(value.substring(0, value.length()-1));
						}else{
							w.setHigh_temperature(value.substring(0, value.indexOf('~')-1));
							w.setLow_temperature(value.substring(value.indexOf('~')+2, value.length()-1));
						}
						//weathers.add(w);
					}
				}
				break;
			}

		} catch (Exception e) {
			e.getStackTrace();
//			mHandler.sendEmptyMessage(WeatherActivity.MSG_GET_WEATHER_FALU);
//			return null;
		}
		Log.e("DomParseWeatherdddd3", weathers.size()+"");
		
		for(int i=0; i < weathers.size(); i++ ){
			Log.e("testtest", "con:"+weathers.get(i).getCondition()+" high"+weathers.get(i).getHigh_temperature() +" low"+weathers.get(i).getLow_temperature() + " da"+weathers.get(i).getDay_of_week()+" "+weathers.get(i).getImageUrl());
		}
		if(null == weathers.get(weathers.size()-1).getDay_of_week() || 0 == weathers.get(weathers.size()-1).getDay_of_week().length()){
			mHandler.sendEmptyMessage(WeatherActivity.MSG_GET_WEATHER_FALU);
			return null;
		}
		
		mHandler.sendEmptyMessage(WeatherActivity.MSG_GET_WEATHER_SUCC);
		return weathers;

	}

}
