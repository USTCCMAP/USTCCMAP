/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-28
 * 该类为本项目的分类搜索，结果标记在地图的图层，并为之添加监听功能。
 * 选择分类搜索，当有查询结果是，地图上便会在相应点做标记。
 * 我们为不同表示设置了不同的标记团“A, B, C--I”以方便查看。
 * 点击这些标记，会显示查看详情的气泡窗口“POPVIEW”点击会有详细信息。
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
	private Handler mHandler = null;	//实现与主界面所在类的通信。 
     
    public MarkerItemizedOverlay(Drawable defaultMarker, MapView mapView) {  
       super(defaultMarker, mapView);  
       Log.e("Marker*", "构造函数3");
       //super(boundCenterBottom(defaultMarker));  
    }  
    
  //添加图层
    public void addOverlay(OverlayItem overlay) {  
        mOverlays.add(overlay);  
    	this.addItem(overlay);
        Log.e("Marker*", "addOverlay");
        //updateItem(overlay);
		//this.populate();
    }  
    
    //移除图层
	public void removeOverlay(int location) {
		mOverlays.remove(location);
	}
     
	//创建图层
    protected OverlayItem createItem(int i) {  
        return mOverlays.get(i);  
    }  
    
    //图层数目
    public int size() {  
        return mOverlays.size();  
    }  
    
    
    /////////////
    //////////////
    //////////
    ////////////
	/**
	 *  构造方法
	*/
    public MarkerItemizedOverlay(Drawable defaultMarker, MainActivity context, Handler handler, MapView mapView) {  
        this(defaultMarker, mapView);
        Log.e("Marker*", "构造函数1");
        mapContext = context;  
		mHandler = handler;
        }
    
    public MarkerItemizedOverlay(Drawable defaultMarker, Context context, MapView mapView) {  
      this(defaultMarker, mapView);
      Log.e("Marker*", "构造函数2");
      }
    
	/**
	 *  添加图标监听
	*/
    @Override  
    protected boolean onTap(int index) {  
    	Log.e("Marker*", "onTap");
      OverlayItem item = mOverlays.get(index);  
      if(null != item.getSnippet()){
    	  if(!"stored".equals(item.getSnippet().substring(0, 6))){
    		//当点击的图标是，搜索结果在地图上的标记时。
        	  MainActivity.choosedID =  Integer.parseInt(item.getSnippet().substring(0, 1));
        	  mHandler.sendEmptyMessage(mapContext.MSG_FORDETAIL);
    	  }
      }
      return true;  
    }  
}  
