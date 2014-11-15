/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-25
 * ����Ϊ�����ѯ���ܣ���ѯ��Ϣ��JSON�ļ�������
 * �������ѯ����и���¼�ģ�
 * ���ơ�strName�������ꡰgpPostion������ַ��strAddress�����绰��strTel�������롰strDistance������Ϣ��
 * ������List<information>������List<information>�д洢��ѯ��������Ѿ������룬�ɽ���Զ����
 * �Ա��������û�չʾ����ǵ��á�
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
	 * ��List<information>��ʽ�����������
	 */
	public List<information> getInfoList() {
		//������������Զ������
		sort();
		return inforList;
	}

	/**
	 * ����name��address��locatiom��tel�� distance��Ϣ
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
					//���Ϊ����Ĭ��ֵ���޼�¼��
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
	 * �������������������
	 */
	public void sort(){
		for(int i = 0; i < inforList.size(); i++){
			for(int j = 0; j < inforList.size() - i - 1; j++){
				if(inforList.get(j).getDistance() > inforList.get(j+1).getDistance()){
					//���������˳���򽻻����ݽ�������
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