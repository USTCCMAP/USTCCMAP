/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-25
 * 该类为分类查询功能，查询信息的工具。
 * 通过分类查询，会记录查询结果中，各记录的：
 * 名字“strName”、坐标“gpPostion”、地址“strAddress”、电话“strTel”、距离“strDistance”等信息。
 * 以便于在向用户展示结果是调用。
 */
package com.ustc.ccmap.tools;

import com.baidu.platform.comapi.basestruct.GeoPoint;


public class information {
	private String strName="";
	private GeoPoint gpPostion = null;
	private String strAddress = "";
	private String strTel = "无记录";
	private float strDistance = -1;

	/**
	 * 此类较为简单，如欲了解更多。请看头部注释。
	 */
	public String getName() {
		return strName;
	}
	public void setName(String name) {
		this.strName = name;
	}
	
	public GeoPoint getPostion() {
		return gpPostion;
	}
	public void setPostion(double lat, double lng) {
	        this.gpPostion = new GeoPoint(  (int) (lat * 1E6), 
	        		 			(int) (lng * 1E6));  
	}
	
	public void setPostion(GeoPoint pos) {
        this.gpPostion = pos;
	}
	
	public String getTel() {
		return strTel;
	}
	public void setTel(String tel) {
		this.strTel = tel;
	}
	
	public String getAddress() {
		return strAddress;
	}
	public void setAddress(String address) {
		this.strAddress = address;
	}
	
	public void setDistance(float distance){
		this.strDistance = distance;
	}
	public float getDistance(){
		return this.strDistance;
	}
}
