/**
 * @author 神奇4+1
 * @version 1.0.0 创建时间：2012-08-08
 * 该类为本项目的天气查询功能数据处理。
 */

package com.demo.qilu.weather;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

@SuppressWarnings("unchecked")
public class XmlParserUtil extends DefaultHandler {
	final static int TYPE_STRING = 1;
	final static int TYPE_INTEGER = 2;
	final static int TYPE_DATE = 3;

	Object vo = null;
	Method method = null;

	String parent = null;
	String classType = null;
	String methodName = null;
	int type = 0;
	boolean parseFlag = false;

	ArrayList result = new ArrayList();

	public XmlParserUtil(String parent, String classType) {
		// this.obj = obj;
		this.parent = parent;
		this.classType = classType;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		String tmp = null;
		if (localName.equals(parent)) {
			try {
				Class c = Class.forName(classType);
				vo = c.newInstance();
				parseFlag = true;
			} catch (ClassNotFoundException e) {
				 Log.i("startElement ClassNotFoundException ",
				 e.getMessage());
			} catch (IllegalAccessException e) {
				 Log.i("startElement IllegalAccessException ",
				 e.getMessage());
			} catch (InstantiationException e) {
				 Log.i("startElement InstantiationException ",
				 e.getMessage());
			}
		} else if (parseFlag) {
			tmp = localName.toUpperCase();
			tmp = tmp.substring(0, 1);
			tmp = tmp + localName.substring(1);

			methodName = "set" + tmp;
			try {
				Class[] methodParamClass = new Class[] { String.class };

				method = vo.getClass().getMethod(methodName, methodParamClass);
			} catch (Exception e) {

			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		try {
			if (method != null && parseFlag) {
				String s = new String(ch, start, length).trim();
				if (s != null && !"".equals(s) && parseFlag) {
					Object[] args = new Object[] { new String(ch, start, length) };

					method.invoke(vo, args);
				}
			}
		} catch (IllegalArgumentException e) {
			 Log.i("characters IllegalArgumentException ", e.getMessage());
		} catch (IllegalAccessException e) {
			 Log.i("characters IllegalAccessException", e.getMessage());
		} catch (InvocationTargetException e) {
			 Log.i("characters InvocationTargetException", e.getMessage());
		}
	}

	public ArrayList getResult() {
		return result;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals(parent)) {
			result.add(vo);
			parseFlag = false;
		}
	}
}
