/**
 * @author 神奇4+1
 * @version 1.0.0 创建时间：2012-08-08
 * 该类为本项目的天气查询功能数据处理。
 */

package com.demo.qilu.weather;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.util.Log;


@SuppressWarnings("unchecked")
public class SaxParserUtil {
	String strUrl = null;
	URL url =  null;
	XMLReader xr =  null;
	SAXParser sp = null;
	
	public SaxParserUtil(String strUrl){
		//URL url = new URL(strUrl);
		this.strUrl = strUrl;
		try {
			url = new URL(strUrl);
		} catch (MalformedURLException e) {
//			e.printStackTrace();
		}
	}
	
	public ArrayList getPostResult( String parent, String classTyp, Map<String, String> param){
		XmlParserUtil parserUtil =null;
		//parse();
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			sp = spf.newSAXParser();
			
			xr = sp.getXMLReader();
			parserUtil = new XmlParserUtil(parent, classTyp);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            HttpPostUtil hpu = new HttpPostUtil();
            String xml = hpu.httpPostData(strUrl, param);
            StringReader sr = new StringReader(new String(xml));
            InputSource is = new InputSource(sr);
						
			xr.setContentHandler(parserUtil);
			xr.parse(is);
		} catch (IOException e) {
			//Log.i("getResult IOException ", e.getMessage());
		} catch (SAXException e) {
			//Log.i("getResult SAXException ", e.getMessage());
		} catch (ParserConfigurationException e) {
			//Log.i("getResult ParserConfigurationException ", e.getMessage());
		} 
		return parserUtil.getResult();
	}
	
	public ArrayList getResult( String parent, String classTyp){		
		//getXMLReader();	
		XmlParserUtil parserUtil =null;
		//parse();
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			sp = spf.newSAXParser();
			
			xr = sp.getXMLReader();
			parserUtil = new XmlParserUtil(parent, classTyp);
						
			xr.setContentHandler(parserUtil);
			xr.parse(new InputSource(url.openStream()));
		} catch (IOException e) {
			Log.i("getResult IOException ", e.getMessage());
		} catch (SAXException e) {
			Log.i("getResult SAXException ", e.getMessage());
		} catch (ParserConfigurationException e) {
			Log.i("getResult ParserConfigurationException ", e.getMessage());
		} 
		return parserUtil.getResult();
	}
}
