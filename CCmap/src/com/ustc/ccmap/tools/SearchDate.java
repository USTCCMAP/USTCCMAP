/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-25
 * 该类为本项目的分类搜索功能，搜索结果的处理。
 * 包括查询信息，在结果界面的显示。
 * 显示形式为 图标 + 名称 + 地址 + 距离 
 * 采用分页显示 每张之多显示9条记录，并按距离递增排列，符合人们的基本操作习惯。
 * 将查询结果，在地图相应点标记在地图，以便于查询和其它操作。
 */
package com.ustc.ccmap.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.ustc.ccmap.MainActivity;
import com.ustc.ccmap.R;
import com.ustc.ccmap.search.MarkerItemizedOverlay;


public class SearchDate {
	MapView mMapView;
	Handler mHandler;
	private MainActivity mapContext = null;
	private List<Overlay> mapOverlays;   //图层list
	
	//用来标记搜索结果的图标组
	int[] bmpID = { R.drawable.mark_a, R.drawable.mark_b, R.drawable.mark_c,
			R.drawable.mark_d, R.drawable.mark_e, R.drawable.mark_f,
			R.drawable.mark_g, R.drawable.mark_h, R.drawable.mark_i };
	
	/**
	 * 构造方法
	 */
	public SearchDate(MapView mapView, MainActivity context, Handler handler){
		mMapView = mapView;
		mHandler = handler;
		mapContext = context;
	}
	
	/**
	 * 搜索结果数据的获得,存储在inforlist中
	 * @return
	 * @throws Exception
	 */
		public List<information> getPlaces() throws Exception{
			Log.e("指定城市搜索", "城市"+MainActivity.searchCity);
			String http = "";
			if(!"".equals(MainActivity.searchCity)){
				//判断是否是在指定城市内搜索
				Log.e("指定城市搜索", "城市"+MainActivity.searchCity);
				http = "http://api.map.baidu.com/place/search?query="
						+ URLEncoder.encode(MainActivity.searchType, "UTF-8")
						+"&region="
						+ URLEncoder.encode(MainActivity.searchCity, "UTF-8")
						+ "&output=json&key=ELw6iZee0tc8g1MiGML3aWu3";
			}
			else{
				//判断是不是指定中心搜索
				Log.e("指定中心搜索", MainActivity.gpSearchCenter.toString()+MainActivity.searchType);
				http = "http://api.map.baidu.com/place/search?query="
						+ URLEncoder.encode(MainActivity.searchType, "UTF-8")
						+ "&radius=5000&strChinese&location="
						+ GeoPointToString(MainActivity.gpSearchCenter)
						+ "&output=json&key=ELw6iZee0tc8g1MiGML3aWu3";
			}
			
			Log.e("MainActivity", "33333331");
			
			//从google map api获得信息
			URL url = new URL(http);
			HttpURLConnection conn = null;
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6 * 1000);
			String line;
			StringBuilder builder = new StringBuilder();
			Log.e("MainActivity", "33333331");
			// 判断操作是否连接成功 输出200为成功
			if (200 == conn.getResponseCode()) {Log.e("MainActivity", "dddddddd");
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			}Log.e("MainActivity", "sssssssssss");
			// 获得的JSON信息
			JSONObject json = new JSONObject(builder.toString());
			Log.e("MainActivity", "aaaaaaaaa");
			//队获得的JSON信息进行解析
			inforHandler infor = new inforHandler();
			infor.setInfoList(json);
			Log.e("MainActivity", "共搜索"+infor.getInfoList().size() + "结果");
			return infor.getInfoList();
		}
		
		
		
		/**
		 * 地图上显示搜索结果标记
		 */
		public void Picture(int page, List<information> inforList) {
			int numPage = (0 == inforList.size() % 9) ? inforList.size() / 9 : inforList.size() / 9 + 1;
			//每页显示的记录数 上线9
			int num = 9;
			if (page == numPage -1)
				num = inforList.size() % 9;
			//设置参数表明地图中已有搜索结果的标记 为以后清除地图结果做准备（popView中）
			MainActivity.isResultDraw = true;
			//清除以后图标 第一层为长按响应图层 第二层为我的位置图层 无需清除
			mapOverlays = mMapView.getOverlays();
			for(int i = mapOverlays.size(); i > 2; i--) mapOverlays.remove(i-1);
			//mapOverlays.clear();
			ArrayList<MarkerItemizedOverlay> Overlays = new ArrayList<MarkerItemizedOverlay>();
			Overlays.clear();
			Log.e("searchDate", mapOverlays.size()+"");
			for (int i = 0; i < num; i++) {
				Drawable drawable = mapContext.getResources().getDrawable(bmpID[i]);
		///////////
MarkerItemizedOverlay itemizedoverlay = new MarkerItemizedOverlay(drawable, mapContext, mHandler, MainActivity.mapView);
				OverlayItem overlayitem = new OverlayItem(inforList.get(i).getPostion(), inforList.get(i).getName(), i + "information");
				itemizedoverlay.addOverlay(overlayitem);
				Overlays.add(itemizedoverlay);
				mapOverlays.add(Overlays.get(i));
				mMapView.refresh();
			}
			Log.e("searchDate", mapOverlays.size()+"");
			mMapView.invalidate();
		}
		
		/**
		 * 将GeoPoint里的经纬度以String,String回传  
		 */
	    private String GeoPointToString(GeoPoint gp) 
	    { 
	      String strReturn=""; 
	      try 
	      { 
	        if (gp != null) 
	        { 
	          double geoLatitude = (int)gp.getLatitudeE6()/1E6; 
	          double geoLongitude = (int)gp.getLongitudeE6()/1E6; 
	          strReturn = String.valueOf(geoLatitude)+","+
	            String.valueOf(geoLongitude); 
	        } 
	      } 
	      catch(Exception e) 
	      { 
	        e.printStackTrace(); 
	      } 
	      return strReturn; 
	    } 
		
}
