/**
 * @author wwz
 * @version 1.0.0 创建时间：2014
 * 该类为本项目的指南针功能。
 * 你可以通过菜单选择“指南针”运行该功能。
 * 良好的运行界面，会给你一种愉悦舒心的感觉
 * 其中关键使用了android地磁传感器，以实现方向辨别。
 */

package com.demo.qilu.compass;

import com.demo.qilu.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class compass extends Activity implements SensorEventListener{
	
	ImageView image;  				//指南针图片
	float currentDegree = 0f;       //指南针图片转过的角度
	
	SensorManager mSensorManager;   //管理器
	
    /** 
     * 创建该ACTIVITY。
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass_main);
        //初始化指南针界面。
        image = (ImageView)findViewById(R.id.znzImage);
        //获取管理服务
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }
    
    @Override 
    protected void onResume(){
    	super.onResume();
    	//注册监听器， 当手机移动是监听重新校正指针方向。
    	mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(
    			Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }
    
    //取消注册
    @Override
    protected void onPause(){
    	mSensorManager.unregisterListener(this);
    	super.onPause();
    	
    }
    
    @Override
    protected void onStop(){
    	mSensorManager.unregisterListener(this);
    	super.onStop();
    	
    }

    //传感器值改变
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//do nothing
	}
	
	/**
	 *  精度改变
	*/
	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
		switch(sensorType){
		case Sensor.TYPE_ORIENTATION:
			float degree = event.values[0]; //获取z转过的角度
			//穿件旋转动画
			RotateAnimation ra = new RotateAnimation(currentDegree,-degree,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		 ra.setDuration(100);//动画持续时间
		 image.startAnimation(ra);
		 currentDegree = -degree;
		 break;
		
		}
	}
}