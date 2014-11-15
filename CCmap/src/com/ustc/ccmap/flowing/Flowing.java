/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-25
 * 该类为本项目的即时流量监控功能。
 * 在主运行界面，右下角会显示该软件本次使用
 * 直至关闭该软件所耗费的累加流量。
 * 此功能能针对，部分调查对象反应android耗流量高而设计，
 * 让你使用时即可看到使用流量，用的放心，能帮助你很好的节省花费。
 * 实现主要依靠android2.2及以上版本的TrafficStats类的使用。
 */
package com.ustc.ccmap.flowing;

import android.content.pm.ApplicationInfo;
import android.net.TrafficStats;

public class Flowing{

	ApplicationInfo appInfo; //本程序的程序信息
	long oldTraffic;         //用来记录，打开该软件是的流量基数。
	long nowTraffic;         //每次调用时的，实时流量数据。
	long Traffic;
	
	//用来记录，打开该软件是的流量基数, 为fianl类型最终变量。
	/*
	static long  getUidRxBytes(int uid)  //获取某个网络UID的接受字节数
	static long  getUidTxBytes(int uid) //获取某个网络UID的发送字节数
    */
	public Flowing(ApplicationInfo appinfo){
		appInfo = appinfo;
		oldTraffic = TrafficStats.getUidRxBytes(appInfo.uid) 
				+ TrafficStats.getUidRxBytes(appInfo.uid);
	}
	
	/**
	 *  返回该程序本次所耗流量，用于更新所耗流量。
	 */
	public String update(){
		//实时流量数据。
		nowTraffic = TrafficStats.getUidRxBytes(appInfo.uid) + TrafficStats.getUidRxBytes(appInfo.uid);
		Traffic = (nowTraffic - oldTraffic) / 1024;    //要显示的使用流量。
		String returnTraffic = "";
		//当流量使用较少时，以KB为单位，并精确到小数点后一位。
		if(Traffic < 1024){
			returnTraffic = Traffic + 0.05 +"";
			return returnTraffic.substring(0, returnTraffic.indexOf(".") + 2) + "KB";
		}
		else{
			//当流量使用较多时，以MB为单位，并精确到小数点后一位。
			returnTraffic = Traffic / 1024 + 0.05 +"";
			return returnTraffic.substring(0, returnTraffic.indexOf(".") + 3) + "MB";
		}
	}
}
