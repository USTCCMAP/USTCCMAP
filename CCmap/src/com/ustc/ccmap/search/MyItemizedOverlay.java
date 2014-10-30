/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-28
 * 该类为本项目的，点击显示气泡快捷菜单的所用图标。
 * 长按主界面地图，会显示快捷窗口，在其下方会显示该图标，
 * 如果气泡窗口消失，该图标亦会消失。
 * 为不同的标记会显示不同的图标，既便于查询，又会有良好的界面体验效果。
 */
package com.ustc.ccmap.search;

import java.util.ArrayList;

import java.util.List;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;


public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem>{
	
	//图层集合
	private List<OverlayItem> overlays = new ArrayList<OverlayItem>();

	public MyItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker, mapView);
	}

	/**
	 *  构造方法
	*/
	public MyItemizedOverlay(Drawable defaultMarker, Context context,
			MapView mapView, View popView, MapController mapCtrl) {
		super(defaultMarker, mapView);
//		super(boundCenterBottom(defaultMarker));
		Log.e("运行顺序", "MyItemizedOverlay");
	}

	//创建图层
	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	//添加图层
	public void addOverlay(OverlayItem item) {
		overlays.add(item);
		
		this.removeAll();
    	this.addItem(item);
		Log.e("MyItem*", "addOverlay");
		//this.populate();
	}
	
	//移除图层
	public void removeOverlay(int location) {
		overlays.remove(location);
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		return super.onTap(p, mapView);
	}

	@Override
	protected boolean onTap(int index) {
		return super.onTap(index);
	}

//	@Override
//	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
//		super.draw(canvas, mapView, shadow);
//	}

}
