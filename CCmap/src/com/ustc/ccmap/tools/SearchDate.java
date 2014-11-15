/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-25
 * ����Ϊ����Ŀ�ķ����������ܣ���������Ĵ���
 * ������ѯ��Ϣ���ڽ���������ʾ��
 * ��ʾ��ʽΪ ͼ�� + ���� + ��ַ + ���� 
 * ���÷�ҳ��ʾ ÿ��֮����ʾ9����¼����������������У��������ǵĻ�������ϰ�ߡ�
 * ����ѯ������ڵ�ͼ��Ӧ�����ڵ�ͼ���Ա��ڲ�ѯ������������
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
	private List<Overlay> mapOverlays;   //ͼ��list
	
	//����������������ͼ����
	int[] bmpID = { R.drawable.mark_a, R.drawable.mark_b, R.drawable.mark_c,
			R.drawable.mark_d, R.drawable.mark_e, R.drawable.mark_f,
			R.drawable.mark_g, R.drawable.mark_h, R.drawable.mark_i };
	
	/**
	 * ���췽��
	 */
	public SearchDate(MapView mapView, MainActivity context, Handler handler){
		mMapView = mapView;
		mHandler = handler;
		mapContext = context;
	}
	
	/**
	 * ����������ݵĻ��,�洢��inforlist��
	 * @return
	 * @throws Exception
	 */
		public List<information> getPlaces() throws Exception{
			Log.e("ָ����������", "����"+MainActivity.searchCity);
			String http = "";
			if(!"".equals(MainActivity.searchCity)){
				//�ж��Ƿ�����ָ������������
				Log.e("ָ����������", "����"+MainActivity.searchCity);
				http = "http://api.map.baidu.com/place/search?query="
						+ URLEncoder.encode(MainActivity.searchType, "UTF-8")
						+"&region="
						+ URLEncoder.encode(MainActivity.searchCity, "UTF-8")
						+ "&output=json&key=ELw6iZee0tc8g1MiGML3aWu3";
			}
			else{
				//�ж��ǲ���ָ����������
				Log.e("ָ����������", MainActivity.gpSearchCenter.toString()+MainActivity.searchType);
				http = "http://api.map.baidu.com/place/search?query="
						+ URLEncoder.encode(MainActivity.searchType, "UTF-8")
						+ "&radius=5000&strChinese&location="
						+ GeoPointToString(MainActivity.gpSearchCenter)
						+ "&output=json&key=ELw6iZee0tc8g1MiGML3aWu3";
			}
			
			Log.e("MainActivity", "33333331");
			
			//��google map api�����Ϣ
			URL url = new URL(http);
			HttpURLConnection conn = null;
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6 * 1000);
			String line;
			StringBuilder builder = new StringBuilder();
			Log.e("MainActivity", "33333331");
			// �жϲ����Ƿ����ӳɹ� ���200Ϊ�ɹ�
			if (200 == conn.getResponseCode()) {Log.e("MainActivity", "dddddddd");
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			}Log.e("MainActivity", "sssssssssss");
			// ��õ�JSON��Ϣ
			JSONObject json = new JSONObject(builder.toString());
			Log.e("MainActivity", "aaaaaaaaa");
			//�ӻ�õ�JSON��Ϣ���н���
			inforHandler infor = new inforHandler();
			infor.setInfoList(json);
			Log.e("MainActivity", "������"+infor.getInfoList().size() + "���");
			return infor.getInfoList();
		}
		
		
		
		/**
		 * ��ͼ����ʾ����������
		 */
		public void Picture(int page, List<information> inforList) {
			int numPage = (0 == inforList.size() % 9) ? inforList.size() / 9 : inforList.size() / 9 + 1;
			//ÿҳ��ʾ�ļ�¼�� ����9
			int num = 9;
			if (page == numPage -1)
				num = inforList.size() % 9;
			//���ò���������ͼ��������������ı�� Ϊ�Ժ������ͼ�����׼����popView�У�
			MainActivity.isResultDraw = true;
			//����Ժ�ͼ�� ��һ��Ϊ������Ӧͼ�� �ڶ���Ϊ�ҵ�λ��ͼ�� �������
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
		 * ��GeoPoint��ľ�γ����String,String�ش�  
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
