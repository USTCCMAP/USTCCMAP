/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014
 * ����Ϊ����Ŀ��ָ���빦�ܡ�
 * �����ͨ���˵�ѡ��ָ���롱���иù��ܡ�
 * ���õ����н��棬�����һ���������ĵĸо�
 * ���йؼ�ʹ����android�شŴ���������ʵ�ַ�����
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
	
	ImageView image;  				//ָ����ͼƬ
	float currentDegree = 0f;       //ָ����ͼƬת���ĽǶ�
	
	SensorManager mSensorManager;   //������
	
    /** 
     * ������ACTIVITY��
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass_main);
        //��ʼ��ָ������档
        image = (ImageView)findViewById(R.id.znzImage);
        //��ȡ�������
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }
    
    @Override 
    protected void onResume(){
    	super.onResume();
    	//ע��������� ���ֻ��ƶ��Ǽ�������У��ָ�뷽��
    	mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(
    			Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }
    
    //ȡ��ע��
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

    //������ֵ�ı�
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//do nothing
	}
	
	/**
	 *  ���ȸı�
	*/
	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
		switch(sensorType){
		case Sensor.TYPE_ORIENTATION:
			float degree = event.values[0]; //��ȡzת���ĽǶ�
			//������ת����
			RotateAnimation ra = new RotateAnimation(currentDegree,-degree,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		 ra.setDuration(100);//��������ʱ��
		 image.startAnimation(ra);
		 currentDegree = -degree;
		 break;
		
		}
	}
}