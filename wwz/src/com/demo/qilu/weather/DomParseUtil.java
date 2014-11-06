/**
 * @author 神奇4+1
 * @version 1.0.0 创建时间：2012-08-08
 * 该类为本项目的天气查询功能数据处理。
 */

package com.demo.qilu.weather;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DomParseUtil {

	/**
	 * singleton
	 */
	private static DocumentBuilderFactory documentBuilderFactory = null;

	/**
	 * DocumentBuilderFactory instance (lazy initialization)
	 * @return
	 */
	private static DocumentBuilderFactory getDocumentBuilderFactory(){
		if(documentBuilderFactory == null){
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
		}
		return documentBuilderFactory;
	}

	/**
	 * DocumentBuilder instance
	 * @return
	 */
	private static DocumentBuilder getDocumentBuilder(){
		try {
			return getDocumentBuilderFactory().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return null;
		}
	}

	/**
	 * Converts String containing XML code to Document
	 * @param xmlString
	 * @return <code>Document</code> interface
	 */
	public static Document stringToDocument(String xmlString){
		if(xmlString == null)
			return null;
		
		DocumentBuilder documentBuilder = getDocumentBuilder();
		InputSource inputSource = new InputSource(new StringReader(xmlString));
		try {
			return documentBuilder.parse(inputSource);
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
