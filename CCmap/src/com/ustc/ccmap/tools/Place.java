/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-25
 * 该类为存储功能，存储信息形式。
 * 会存储指定地点信息的：
 * 名字“strName”、坐标“gpPostion”、地址“strAddress”、
 * 电话“strTel”、备注“strComment”、经纬度“lat、lnt”等信息。
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
	 * 此类较为简单，如欲了解更多。请看头部注释。
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
    	return "名称："+ strName +" 地址: "+strAddress+"类型： "+strType+"备注： "+ strComment;
    }
  
}
