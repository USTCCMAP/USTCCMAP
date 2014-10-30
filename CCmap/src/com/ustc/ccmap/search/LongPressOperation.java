/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-28
 * 该类为本项目的主界面地图，长按事件图层和监听和部分处理功能。
 * 在主运行界面，长按地图会弹出气泡窗口（POPVIEW），
 * 你可以点击气泡窗口相应的选项，从而执行不同的操作，
 * 如地图选择查询中心、设置导航出发点、收藏地点信息等。
 */
package com.ustc.ccmap.search;


import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
import com.ustc.ccmap.MainActivity;
import com.ustc.ccmap.R;

public class LongPressOperation extends ItemizedOverlay<OverlayItem> implements OnDoubleTapListener,
		OnGestureListener ,OnTouchListener, OnLongClickListener{

	private MainActivity mContext;
	private MapView mMapView;
	private Handler mHandler; 
	private MapController mMapCtrl; 
	private View mPopView;               //气泡快捷菜单。
	private GestureDetector gestureScanner = new GestureDetector(this);
	private int level = 0;

	public LongPressOperation(MainActivity context, MapView mapView, View popView,
			Handler handler, MapController mapCtrl, Drawable draw) {
		
		super(draw, mapView);
		mContext = context;
		mMapView = mapView;
		mHandler = handler;
		mMapCtrl = mapCtrl;
		mPopView = popView;
		gestureScanner.setIsLongpressEnabled(true);
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
//		return gestureScanner.onTouchEvent(event);//onTouchEvent
//	}
	
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.e("longpressoperation", "touch");
		return gestureScanner.onTouchEvent(event);//onTouchEvent
	}
	

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		//当气泡快捷菜单显示时，点击其他位置，使其消失。
		mHandler.sendEmptyMessage(mContext.MSG_VIEW_TAPOTHERAREA);
		return true;
	}
	
	
	/**
	 *  添加图标监听
	*/
    @Override  
    protected boolean onTap(int index) {
		//当气泡快捷菜单显示时，点击其他位置，使其消失。
		Log.e("：LongPressOperation", "气泡菜单消失");
		mHandler.sendEmptyMessage(mContext.MSG_VIEW_TAPOTHERAREA);
		return true; 
    }  
	
	
	
	

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		//当连续点击地图时，放大地图。
		if (++level % 3 == 0) {
			mMapCtrl.zoomIn();
			level = 0;
		}
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		//do nothing
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		//do nothing
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		//do nothing
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent event) {
		Log.e("eeeeeeeeee", "长按事件响应"+MainActivity.longpressOperation);

		//得到长按地点的经纬度信息。
		MainActivity.longpressGeoPoint = mMapView.getProjection().fromPixels(
				(int) event.getX(), (int) event.getY());

		//再次显示气泡窗口的位置及大小设置
		MapView.LayoutParams params = (MapView.LayoutParams) mPopView
				.getLayoutParams();
		params.y =  -50;
		params.point = MainActivity.longpressGeoPoint;
		mMapView.updateViewLayout(mPopView, params);
		
		/**
		 *  显示基本气泡菜单
		*/
		if (1 == MainActivity.longpressOperation) {
			View popMenu = (View) mPopView.findViewById(R.id.pop_menu);
			popMenu.setVisibility(View.VISIBLE);
			//气泡窗口中 清空图标选项是否可见 如果查询结果在地图上有图标标记则可见。
			if(MainActivity.isResultDraw)
			{
				mPopView.findViewById(R.id.pop_clear).setVisibility(View.VISIBLE);
				mPopView.findViewById(R.id.pop_clear).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//点击时移除搜索结果在地图上的标记
						List<Overlay> mapOverlays = mMapView.getOverlays();
						for(int i = mapOverlays.size(); i > 2; i--) mapOverlays.remove(i - 1);
						MainActivity.isResultDraw = false;
						mContext.findViewById(R.id.btn_toList).setVisibility(android.view.View.INVISIBLE);
						mMapView.invalidate();
						mPopView.findViewById(R.id.pop_clear).setVisibility(View.INVISIBLE);
						if(null != mMapView.findViewById(R.id.pop_Detail))
						mMapView.findViewById(R.id.pop_Detail).setVisibility(View.GONE);
						mPopView.setVisibility(View.INVISIBLE);
					}
				});
			}
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
		}
		
		/**
		 *  选择搜索中心操作
		*/
		if (2 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "选择搜索中心");
			Button chooseCenter = (Button) mPopView
					.findViewById(R.id.pop_choosecenter);
			chooseCenter.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// 确定是否设为搜索中心
			chooseCenter.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//点击时将长按位置设为，搜索中心。
					MainActivity.gpSearchCenter = MainActivity.longpressGeoPoint;
					mPopView.setVisibility(View.GONE);
					mHandler.sendEmptyMessage(mContext.MSG_VIEW_CENTEROK);
					MainActivity.longpressOperation = 1;
				}
			});

		}
		
		/**
		 *  导航选择出发点
		*/
		if (3 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "选择出发点");
			Button chooseFrom = (Button) mPopView
					.findViewById(R.id.pop_choosefrom);
			chooseFrom.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// 确定是否设为出发点
			chooseFrom.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//点击时将长按位置设为，导航出发点。
					mContext.gpStart = MainActivity.longpressGeoPoint;
					mContext.findViewById(R.id.pop_choosefrom).setVisibility(View.INVISIBLE);
					mPopView.setVisibility(View.INVISIBLE);
					MainActivity.longpressOperation = 1;
					mHandler.sendEmptyMessage(mContext.MSG_NAVIGATION_SETSTART);
				}
			});
		}
		
		/**
		 *  导航选择终点
		*/
		if (4 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "选择终点");
			Button chooseTo = (Button) mPopView
					.findViewById(R.id.pop_chooseto);
			chooseTo.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// 确定是否设为目的地
			chooseTo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//点击时将长按位置设为，导航目的地。
					mContext.gpDestination = MainActivity.longpressGeoPoint;
					mContext.findViewById(R.id.pop_chooseto).setVisibility(View.INVISIBLE);
					mPopView.setVisibility(View.INVISIBLE);
					MainActivity.longpressOperation = 1;
					mHandler.sendEmptyMessage(mContext.MSG_NAVIGATION_SETDESTINATION);
				}
			});
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		Log.e("eeeeeeeeee", "长按事件响应"+MainActivity.longpressOperation);

		//得到长按地点的经纬度信息。
		MainActivity.longpressGeoPoint = mMapView.getProjection().fromPixels(
				(int) v.getX(), (int) v.getY());

		//再次显示气泡窗口的位置及大小设置
		MapView.LayoutParams params = (MapView.LayoutParams) mPopView
				.getLayoutParams();
		params.y =  -50;
		params.point = MainActivity.longpressGeoPoint;
		mMapView.updateViewLayout(mPopView, params);
		
		/**
		 *  显示基本气泡菜单
		*/
		if (1 == MainActivity.longpressOperation) {
			View popMenu = (View) mPopView.findViewById(R.id.pop_menu);
			popMenu.setVisibility(View.VISIBLE);
			//气泡窗口中 清空图标选项是否可见 如果查询结果在地图上有图标标记则可见。
			if(MainActivity.isResultDraw)
			{
				mPopView.findViewById(R.id.pop_clear).setVisibility(View.VISIBLE);
				mPopView.findViewById(R.id.pop_clear).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//点击时移除搜索结果在地图上的标记
						List<Overlay> mapOverlays = mMapView.getOverlays();
						for(int i = mapOverlays.size(); i > 2; i--) mapOverlays.remove(i - 1);
						MainActivity.isResultDraw = false;
						mContext.findViewById(R.id.btn_toList).setVisibility(android.view.View.INVISIBLE);
						mMapView.invalidate();
						mPopView.findViewById(R.id.pop_clear).setVisibility(View.INVISIBLE);
						if(null != mMapView.findViewById(R.id.pop_Detail))
						mMapView.findViewById(R.id.pop_Detail).setVisibility(View.GONE);
						mPopView.setVisibility(View.INVISIBLE);
					}
				});
			}
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
		}
		
		/**
		 *  选择搜索中心操作
		*/
		if (2 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "选择搜索中心");
			Button chooseCenter = (Button) mPopView
					.findViewById(R.id.pop_choosecenter);
			chooseCenter.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// 确定是否设为搜索中心
			chooseCenter.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//点击时将长按位置设为，搜索中心。
					MainActivity.gpSearchCenter = MainActivity.longpressGeoPoint;
					mPopView.setVisibility(View.GONE);
					mHandler.sendEmptyMessage(mContext.MSG_VIEW_CENTEROK);
					MainActivity.longpressOperation = 1;
				}
			});

		}
		
		/**
		 *  导航选择出发点
		*/
		if (3 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "选择出发点");
			Button chooseFrom = (Button) mPopView
					.findViewById(R.id.pop_choosefrom);
			chooseFrom.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// 确定是否设为出发点
			chooseFrom.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//点击时将长按位置设为，导航出发点。
					mContext.gpStart = MainActivity.longpressGeoPoint;
					mContext.findViewById(R.id.pop_choosefrom).setVisibility(View.INVISIBLE);
					mPopView.setVisibility(View.INVISIBLE);
					MainActivity.longpressOperation = 1;
					mHandler.sendEmptyMessage(mContext.MSG_NAVIGATION_SETSTART);
				}
			});
		}
		
		/**
		 *  导航选择终点
		*/
		if (4 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "选择终点");
			Button chooseTo = (Button) mPopView
					.findViewById(R.id.pop_chooseto);
			chooseTo.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// 确定是否设为目的地
			chooseTo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//点击时将长按位置设为，导航目的地。
					mContext.gpDestination = MainActivity.longpressGeoPoint;
					mContext.findViewById(R.id.pop_chooseto).setVisibility(View.INVISIBLE);
					mPopView.setVisibility(View.INVISIBLE);
					MainActivity.longpressOperation = 1;
					mHandler.sendEmptyMessage(mContext.MSG_NAVIGATION_SETDESTINATION);
				}
			});
		}
		return true;
	}



}
