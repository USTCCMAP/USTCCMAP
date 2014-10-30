/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-28
 * ����Ϊ����Ŀ�ķ����������������ڵ�ͼ��ͼ�㣬��Ϊ֮��Ӽ������ܡ�
 * ѡ��������������в�ѯ����ǣ���ͼ�ϱ������Ӧ������ǡ�
 * ����Ϊ��ͬ��ʾ�����˲�ͬ�ı���š�A, B, C--I���Է���鿴��
 * �����Щ��ǣ�����ʾ�鿴��������ݴ��ڡ�POPVIEW�����������ϸ��Ϣ��
 */
package com.ustc.ccmap.search;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.ustc.ccmap.MainActivity;


@SuppressWarnings("rawtypes")
public class MarkerItemizedOverlay extends ItemizedOverlay<OverlayItem> {  
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();  
    
	private MainActivity mapContext = null;
	private Handler mHandler = null;	//ʵ�����������������ͨ�š� 
     
    public MarkerItemizedOverlay(Drawable defaultMarker, MapView mapView) {  
       super(defaultMarker, mapView);  
       Log.e("Marker*", "���캯��3");
       //super(boundCenterBottom(defaultMarker));  
    }  
    
  //���ͼ��
    public void addOverlay(OverlayItem overlay) {  
        mOverlays.add(overlay);  
    	this.addItem(overlay);
        Log.e("Marker*", "addOverlay");
        //updateItem(overlay);
		//this.populate();
    }  
    
    //�Ƴ�ͼ��
	public void removeOverlay(int location) {
		mOverlays.remove(location);
	}
     
	//����ͼ��
    protected OverlayItem createItem(int i) {  
        return mOverlays.get(i);  
    }  
    
    //ͼ����Ŀ
    public int size() {  
        return mOverlays.size();  
    }  
    
    
    /////////////
    //////////////
    //////////
    ////////////
	/**
	 *  ���췽��
	*/
    public MarkerItemizedOverlay(Drawable defaultMarker, MainActivity context, Handler handler, MapView mapView) {  
        this(defaultMarker, mapView);
        Log.e("Marker*", "���캯��1");
        mapContext = context;  
		mHandler = handler;
        }
    
    public MarkerItemizedOverlay(Drawable defaultMarker, Context context, MapView mapView) {  
      this(defaultMarker, mapView);
      Log.e("Marker*", "���캯��2");
      }
    
	/**
	 *  ���ͼ�����
	*/
    @Override  
    protected boolean onTap(int index) {  
    	Log.e("Marker*", "onTap");
      OverlayItem item = mOverlays.get(index);  
      if(null != item.getSnippet()){
    	  if(!"stored".equals(item.getSnippet().substring(0, 6))){
    		//�������ͼ���ǣ���������ڵ�ͼ�ϵı��ʱ��
        	  MainActivity.choosedID =  Integer.parseInt(item.getSnippet().substring(0, 1));
        	  mHandler.sendEmptyMessage(mapContext.MSG_FORDETAIL);
    	  }
      }
      return true;  
    }  
}  
