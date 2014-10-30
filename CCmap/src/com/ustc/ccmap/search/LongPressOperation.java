/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-28
 * ����Ϊ����Ŀ���������ͼ�������¼�ͼ��ͼ����Ͳ��ִ����ܡ�
 * �������н��棬������ͼ�ᵯ�����ݴ��ڣ�POPVIEW����
 * ����Ե�����ݴ�����Ӧ��ѡ��Ӷ�ִ�в�ͬ�Ĳ�����
 * ���ͼѡ���ѯ���ġ����õ��������㡢�ղصص���Ϣ�ȡ�
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
	private View mPopView;               //���ݿ�ݲ˵���
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
		//�����ݿ�ݲ˵���ʾʱ���������λ�ã�ʹ����ʧ��
		mHandler.sendEmptyMessage(mContext.MSG_VIEW_TAPOTHERAREA);
		return true;
	}
	
	
	/**
	 *  ���ͼ�����
	*/
    @Override  
    protected boolean onTap(int index) {
		//�����ݿ�ݲ˵���ʾʱ���������λ�ã�ʹ����ʧ��
		Log.e("��LongPressOperation", "���ݲ˵���ʧ");
		mHandler.sendEmptyMessage(mContext.MSG_VIEW_TAPOTHERAREA);
		return true; 
    }  
	
	
	
	

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		//�����������ͼʱ���Ŵ��ͼ��
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
		Log.e("eeeeeeeeee", "�����¼���Ӧ"+MainActivity.longpressOperation);

		//�õ������ص�ľ�γ����Ϣ��
		MainActivity.longpressGeoPoint = mMapView.getProjection().fromPixels(
				(int) event.getX(), (int) event.getY());

		//�ٴ���ʾ���ݴ��ڵ�λ�ü���С����
		MapView.LayoutParams params = (MapView.LayoutParams) mPopView
				.getLayoutParams();
		params.y =  -50;
		params.point = MainActivity.longpressGeoPoint;
		mMapView.updateViewLayout(mPopView, params);
		
		/**
		 *  ��ʾ�������ݲ˵�
		*/
		if (1 == MainActivity.longpressOperation) {
			View popMenu = (View) mPopView.findViewById(R.id.pop_menu);
			popMenu.setVisibility(View.VISIBLE);
			//���ݴ����� ���ͼ��ѡ���Ƿ�ɼ� �����ѯ����ڵ�ͼ����ͼ������ɼ���
			if(MainActivity.isResultDraw)
			{
				mPopView.findViewById(R.id.pop_clear).setVisibility(View.VISIBLE);
				mPopView.findViewById(R.id.pop_clear).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//���ʱ�Ƴ���������ڵ�ͼ�ϵı��
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
		 *  ѡ���������Ĳ���
		*/
		if (2 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "ѡ����������");
			Button chooseCenter = (Button) mPopView
					.findViewById(R.id.pop_choosecenter);
			chooseCenter.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// ȷ���Ƿ���Ϊ��������
			chooseCenter.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//���ʱ������λ����Ϊ���������ġ�
					MainActivity.gpSearchCenter = MainActivity.longpressGeoPoint;
					mPopView.setVisibility(View.GONE);
					mHandler.sendEmptyMessage(mContext.MSG_VIEW_CENTEROK);
					MainActivity.longpressOperation = 1;
				}
			});

		}
		
		/**
		 *  ����ѡ�������
		*/
		if (3 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "ѡ�������");
			Button chooseFrom = (Button) mPopView
					.findViewById(R.id.pop_choosefrom);
			chooseFrom.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// ȷ���Ƿ���Ϊ������
			chooseFrom.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//���ʱ������λ����Ϊ�����������㡣
					mContext.gpStart = MainActivity.longpressGeoPoint;
					mContext.findViewById(R.id.pop_choosefrom).setVisibility(View.INVISIBLE);
					mPopView.setVisibility(View.INVISIBLE);
					MainActivity.longpressOperation = 1;
					mHandler.sendEmptyMessage(mContext.MSG_NAVIGATION_SETSTART);
				}
			});
		}
		
		/**
		 *  ����ѡ���յ�
		*/
		if (4 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "ѡ���յ�");
			Button chooseTo = (Button) mPopView
					.findViewById(R.id.pop_chooseto);
			chooseTo.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// ȷ���Ƿ���ΪĿ�ĵ�
			chooseTo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//���ʱ������λ����Ϊ������Ŀ�ĵء�
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
		Log.e("eeeeeeeeee", "�����¼���Ӧ"+MainActivity.longpressOperation);

		//�õ������ص�ľ�γ����Ϣ��
		MainActivity.longpressGeoPoint = mMapView.getProjection().fromPixels(
				(int) v.getX(), (int) v.getY());

		//�ٴ���ʾ���ݴ��ڵ�λ�ü���С����
		MapView.LayoutParams params = (MapView.LayoutParams) mPopView
				.getLayoutParams();
		params.y =  -50;
		params.point = MainActivity.longpressGeoPoint;
		mMapView.updateViewLayout(mPopView, params);
		
		/**
		 *  ��ʾ�������ݲ˵�
		*/
		if (1 == MainActivity.longpressOperation) {
			View popMenu = (View) mPopView.findViewById(R.id.pop_menu);
			popMenu.setVisibility(View.VISIBLE);
			//���ݴ����� ���ͼ��ѡ���Ƿ�ɼ� �����ѯ����ڵ�ͼ����ͼ������ɼ���
			if(MainActivity.isResultDraw)
			{
				mPopView.findViewById(R.id.pop_clear).setVisibility(View.VISIBLE);
				mPopView.findViewById(R.id.pop_clear).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//���ʱ�Ƴ���������ڵ�ͼ�ϵı��
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
		 *  ѡ���������Ĳ���
		*/
		if (2 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "ѡ����������");
			Button chooseCenter = (Button) mPopView
					.findViewById(R.id.pop_choosecenter);
			chooseCenter.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// ȷ���Ƿ���Ϊ��������
			chooseCenter.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//���ʱ������λ����Ϊ���������ġ�
					MainActivity.gpSearchCenter = MainActivity.longpressGeoPoint;
					mPopView.setVisibility(View.GONE);
					mHandler.sendEmptyMessage(mContext.MSG_VIEW_CENTEROK);
					MainActivity.longpressOperation = 1;
				}
			});

		}
		
		/**
		 *  ����ѡ�������
		*/
		if (3 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "ѡ�������");
			Button chooseFrom = (Button) mPopView
					.findViewById(R.id.pop_choosefrom);
			chooseFrom.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// ȷ���Ƿ���Ϊ������
			chooseFrom.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//���ʱ������λ����Ϊ�����������㡣
					mContext.gpStart = MainActivity.longpressGeoPoint;
					mContext.findViewById(R.id.pop_choosefrom).setVisibility(View.INVISIBLE);
					mPopView.setVisibility(View.INVISIBLE);
					MainActivity.longpressOperation = 1;
					mHandler.sendEmptyMessage(mContext.MSG_NAVIGATION_SETSTART);
				}
			});
		}
		
		/**
		 *  ����ѡ���յ�
		*/
		if (4 == MainActivity.longpressOperation) {
			Log.e("eeeeeeeeee", "ѡ���յ�");
			Button chooseTo = (Button) mPopView
					.findViewById(R.id.pop_chooseto);
			chooseTo.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(mContext.MSG_VIEW_POPMENU);
			// ȷ���Ƿ���ΪĿ�ĵ�
			chooseTo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//���ʱ������λ����Ϊ������Ŀ�ĵء�
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
