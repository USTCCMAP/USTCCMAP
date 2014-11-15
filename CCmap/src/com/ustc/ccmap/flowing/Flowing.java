/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-25
 * ����Ϊ����Ŀ�ļ�ʱ������ع��ܡ�
 * �������н��棬���½ǻ���ʾ���������ʹ��
 * ֱ���رո�������ķѵ��ۼ�������
 * �˹�������ԣ����ֵ������Ӧandroid�������߶���ƣ�
 * ����ʹ��ʱ���ɿ���ʹ���������õķ��ģ��ܰ�����ܺõĽ�ʡ���ѡ�
 * ʵ����Ҫ����android2.2�����ϰ汾��TrafficStats���ʹ�á�
 */
package com.ustc.ccmap.flowing;

import android.content.pm.ApplicationInfo;
import android.net.TrafficStats;

public class Flowing{

	ApplicationInfo appInfo; //������ĳ�����Ϣ
	long oldTraffic;         //������¼���򿪸�����ǵ�����������
	long nowTraffic;         //ÿ�ε���ʱ�ģ�ʵʱ�������ݡ�
	long Traffic;
	
	//������¼���򿪸�����ǵ���������, Ϊfianl�������ձ�����
	/*
	static long  getUidRxBytes(int uid)  //��ȡĳ������UID�Ľ����ֽ���
	static long  getUidTxBytes(int uid) //��ȡĳ������UID�ķ����ֽ���
    */
	public Flowing(ApplicationInfo appinfo){
		appInfo = appinfo;
		oldTraffic = TrafficStats.getUidRxBytes(appInfo.uid) 
				+ TrafficStats.getUidRxBytes(appInfo.uid);
	}
	
	/**
	 *  ���ظó��򱾴��������������ڸ�������������
	 */
	public String update(){
		//ʵʱ�������ݡ�
		nowTraffic = TrafficStats.getUidRxBytes(appInfo.uid) + TrafficStats.getUidRxBytes(appInfo.uid);
		Traffic = (nowTraffic - oldTraffic) / 1024;    //Ҫ��ʾ��ʹ��������
		String returnTraffic = "";
		//������ʹ�ý���ʱ����KBΪ��λ������ȷ��С�����һλ��
		if(Traffic < 1024){
			returnTraffic = Traffic + 0.05 +"";
			return returnTraffic.substring(0, returnTraffic.indexOf(".") + 2) + "KB";
		}
		else{
			//������ʹ�ý϶�ʱ����MBΪ��λ������ȷ��С�����һλ��
			returnTraffic = Traffic / 1024 + 0.05 +"";
			return returnTraffic.substring(0, returnTraffic.indexOf(".") + 3) + "MB";
		}
	}
}
