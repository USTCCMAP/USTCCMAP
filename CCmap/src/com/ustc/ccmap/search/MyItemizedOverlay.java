/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-28
 * ����Ϊ����Ŀ�ģ������ʾ���ݿ�ݲ˵�������ͼ�ꡣ
 * �����������ͼ������ʾ��ݴ��ڣ������·�����ʾ��ͼ�꣬
 * ������ݴ�����ʧ����ͼ�������ʧ��
 * Ϊ��ͬ�ı�ǻ���ʾ��ͬ��ͼ�꣬�ȱ��ڲ�ѯ���ֻ������õĽ�������Ч����
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
	
	//ͼ�㼯��
	private List<OverlayItem> overlays = new ArrayList<OverlayItem>();

	public MyItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker, mapView);
	}

	/**
	 *  ���췽��
	*/
	public MyItemizedOverlay(Drawable defaultMarker, Context context,
			MapView mapView, View popView, MapController mapCtrl) {
		super(defaultMarker, mapView);
//		super(boundCenterBottom(defaultMarker));
		Log.e("����˳��", "MyItemizedOverlay");
	}

	//����ͼ��
	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	//���ͼ��
	public void addOverlay(OverlayItem item) {
		overlays.add(item);
		
		this.removeAll();
    	this.addItem(item);
		Log.e("MyItem*", "addOverlay");
		//this.populate();
	}
	
	//�Ƴ�ͼ��
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
