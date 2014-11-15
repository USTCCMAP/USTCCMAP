/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-25
 * 该类为分类查询功能，查询信息的JSON文件解析。
 * 会解析查询结果中各记录的：
 * 名称“strName”、坐标“gpPostion”、地址“strAddress”、电话“strTel”、距离“strDistance”等信息。
 * 并返回List<information>。其中List<information>中存储查询结果，并已经按距离，由近及远排序。
 * 以便于在向用户展示结果是调用。
 */
package com.ustc.ccmap.tools;

import java.util.ArrayList;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.helpers.DefaultHandler;

import com.ustc.ccmap.MainActivity;

import android.location.Location;
import android.util.Log;

public class inforHandler extends DefaultHandler {
	private List<information> inforList = new ArrayList<information>();

	/**
	 * 以List<information>形式返回搜索结果
	 */
	public List<information> getInfoList() {
		//按距离排序，由远及近。
		sort();
		return inforList;
	}

	/**
	 * 解析name、address、locatiom、tel、 distance信息
	 * @param jsonObject
	 * @throws JSONException
	 */
	public void setInfoList(JSONObject jsonObject) throws JSONException {
		JSONArray results = jsonObject.getJSONArray("results");
		Log.e("INFORHANDLER",results.length()+"");
		if (results != null)
			try {
				for (int i = 0; i < results.length(); i++) {
					information currentInfo = new information();
					currentInfo.setName(results.getJSONObject(i).getString("name"));
					currentInfo.setAddress(results.getJSONObject(i).getString("address"));
					//如果为空有默认值“无记录”
					currentInfo.setTel(results.getJSONObject(i).getString("telephone"));
					
					currentInfo.setPostion(results.getJSONObject(i)
									.getJSONObject("location")
									.getDouble("lat"),
									results.getJSONObject(i)
											.getJSONObject("location")
											.getDouble("lng"));
					float[] distance = new float[1];
					Location.distanceBetween((double) MainActivity.gpSearchCenter.getLatitudeE6() / 1E6,
											(double) MainActivity.gpSearchCenter.getLongitudeE6() / 1E6,
											(double) currentInfo.getPostion().getLatitudeE6() / 1E6,
											(double) currentInfo.getPostion().getLongitudeE6() / 1E6,
											distance);
					currentInfo.setDistance(distance[0]);
					Log.e("INFORHANDLER",currentInfo.getAddress());
					inforList.add(currentInfo);
				}
			} catch (Exception e) {
				Log.e("INFORHANDLER",e.toString());
			}

	}
	
	/**
	 * 对搜索结果按距离排序
	 */
	public void sort(){
		for(int i = 0; i < inforList.size(); i++){
			for(int j = 0; j < inforList.size() - i - 1; j++){
				if(inforList.get(j).getDistance() > inforList.get(j+1).getDistance()){
					//如果不符合顺序，则交换数据进行排序
					information Info = new information();
					Info.setName(inforList.get(j).getName());
					Info.setAddress(inforList.get(j).getAddress());
					Info.setTel(inforList.get(j).getTel());
					Info.setDistance(inforList.get(j).getDistance());
					Info.setPostion(inforList.get(j).getPostion());
					
					inforList.get(j).setName(inforList.get(j+1).getName());
					inforList.get(j).setAddress(inforList.get(j+1).getAddress());
					inforList.get(j).setTel(inforList.get(j+1).getTel());
					inforList.get(j).setDistance(inforList.get(j+1).getDistance());
					inforList.get(j).setPostion(inforList.get(j+1).getPostion());
					
					inforList.get(j+1).setName(Info.getName());
					inforList.get(j+1).setAddress(Info.getAddress());
					inforList.get(j+1).setTel(Info.getTel());
					inforList.get(j+1).setDistance(Info.getDistance());
					inforList.get(j+1).setPostion(Info.getPostion());
				}
			}
		}

	}

}