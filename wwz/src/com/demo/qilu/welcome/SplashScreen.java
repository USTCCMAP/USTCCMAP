/**
 * @author ����4+1
 * @version 1.0.0 ����ʱ�䣺2012-08-08
 * ����Ϊ����Ŀ�Ļ�ӭ���档
 * �����������ѡ������ͼ�����ѡ��鿴�Ŷ���Ϣ���������ǡ���
 * ���Ľ�����ƣ��򵥵Ĳ���������������̵ĵ�һӡ��
 */
package com.demo.qilu.welcome;

import com.demo.qilu.MainActivity;
import com.demo.qilu.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SplashScreen extends Activity {
	private ProgressDialog progDialog = null;//����ʱ��ʾ
	private Button btnEntermap;
	private Button btnAboutus;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash);
       }
        
        
        /**
		 * �����ͼ
		 */
        public void onClick_EnterMap(View view) {
        	btnEntermap = (Button) findViewById(R.id.button_enter_map);
        	btnEntermap.setBackgroundResource(R.drawable.enter_map);
        	Handler handler = new Handler();
            handler.postDelayed(new splashhandler(),0);
        }
        
        /**
		 * ��ʾ��������
		 */
        public void onClick_AboutUs(View view){
        	btnAboutus = (Button) findViewById(R.id.button_about_us);
        	btnAboutus.setBackgroundResource(R.drawable.about_us);
        	Intent intent = new Intent();
  			intent.setClass(SplashScreen.this,about_us.class);
  			startActivity(intent);
		}
        
        /**
		 * �ڲ��࣬���ڼ��ص�ͼ�Ķ�����
		 */
        class splashhandler implements Runnable{

            public void run() {
            	
            	progDialog = ProgressDialog.show(SplashScreen.this,
						"","��ͼ�����С�������" , true, false);
				new Thread() {
					@Override
					public void run() {
						try {
							//����������
							 startActivity(new Intent(getApplication(),MainActivity.class));
				                SplashScreen.this.finish();
							progDialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
							//�쳣������ͼ���ش���ʱ��ʾ��
							Toast.makeText(SplashScreen.this, "��ͼ���س�������", Toast.LENGTH_LONG).show();
						}
					}

				}.start();
            }
            
        }
        @Override 
        protected void onRestart() {  
      	btnAboutus = (Button) findViewById(R.id.button_about_us);
      	btnAboutus.setBackgroundResource(R.drawable.about_us1);
          super.onRestart();  
      }  
}
