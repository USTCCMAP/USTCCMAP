/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-25
 * ����Ϊ�����ѯ���ܣ���ѯ��Ϣ�Ĺ��ߡ�
 * ͨ�������ѯ�����¼��ѯ����У�����¼�ģ�
 * ���֡�strName�������ꡰgpPostion������ַ��strAddress�����绰��strTel�������롰strDistance������Ϣ��
 * �Ա��������û�չʾ����ǵ��á�
 */
package com.ustc.ccmap.tools;

import com.baidu.platform.comapi.basestruct.GeoPoint;


public class information {
	private String strName="";
	private GeoPoint gpPostion = null;
	private String strAddress = "";
	private String strTel = "�޼�¼";
	private float strDistance = -1;

	/**
	 * �����Ϊ�򵥣������˽���ࡣ�뿴ͷ��ע�͡�
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
