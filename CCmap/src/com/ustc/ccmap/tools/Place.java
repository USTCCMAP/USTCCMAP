/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-25
 * ����Ϊ�洢���ܣ��洢��Ϣ��ʽ��
 * ��洢ָ���ص���Ϣ�ģ�
 * ���֡�strName�������ꡰgpPostion������ַ��strAddress����
 * �绰��strTel������ע��strComment������γ�ȡ�lat��lnt������Ϣ��
 */
package com.ustc.ccmap.tools;

public class Place {
	String strName;  
    String strAddress;
    String strType;
    String strComment;
    int lat;
    int lng;

	/**
	 * �����Ϊ�򵥣������˽���ࡣ�뿴ͷ��ע�͡�
	 */
    
    public String getName() {  
        return strName;  
    }  
    public void setName(String name) {  
        this.strName = name;  
    }
    public String getAddress() {  
        return strAddress;  
    }  
    public void setAddress(String address) {  
        this.strAddress = address;  
    }  
    public String getType() {  
        return strType;  
    }  
    public void setType(String type) {  
        this.strType = type;  
    }  
    public String getComment() {  
        return strComment;  
    }  
    public void setComment(String comment) {  
        this.strComment = comment;  
    }  
    public int getLat() {  
    	return lat;  
    }  
    public void setLat(int lat) {  
        this.lat = lat;  
    }  
    public int getLng() {  
        return lng;  
    }  
    public void setLng(int lng) {  
        this.lng = lng;  
    }  
    
    public String ToString(){
    	return "���ƣ�"+ strName +" ��ַ: "+strAddress+"���ͣ� "+strType+"��ע�� "+ strComment;
    }
  
}
