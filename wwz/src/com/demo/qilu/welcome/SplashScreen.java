/**
 * @author 神奇4+1
 * @version 1.0.0 创建时间：2012-08-08
 * 该类为本项目的欢迎界面。
 * 在这里你可以选择进入地图，亦可选择查看团队信息“关于我们”。
 * 简洁的界面设计，简单的操作，给你留下深刻的第一印象。
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
	private ProgressDialog progDialog = null;//加载时显示
	private Button btnEntermap;
	private Button btnAboutus;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash);
       }
        
        
        /**
		 * 进入地图
		 */
        public void onClick_EnterMap(View view) {
        	btnEntermap = (Button) findViewById(R.id.button_enter_map);
        	btnEntermap.setBackgroundResource(R.drawable.enter_map);
        	Handler handler = new Handler();
            handler.postDelayed(new splashhandler(),0);
        }
        
        /**
		 * 显示关于我们
		 */
        public void onClick_AboutUs(View view){
        	btnAboutus = (Button) findViewById(R.id.button_about_us);
        	btnAboutus.setBackgroundResource(R.drawable.about_us);
        	Intent intent = new Intent();
  			intent.setClass(SplashScreen.this,about_us.class);
  			startActivity(intent);
		}
        
        /**
		 * 内部类，用于加载地图的动画。
		 */
        class splashhandler implements Runnable{

            public void run() {
            	
            	progDialog = ProgressDialog.show(SplashScreen.this,
						"","地图加载中。。。。" , true, false);
				new Thread() {
					@Override
					public void run() {
						try {
							//运行主程序
							 startActivity(new Intent(getApplication(),MainActivity.class));
				                SplashScreen.this.finish();
							progDialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
							//异常处理，地图加载错误时显示。
							Toast.makeText(SplashScreen.this, "地图加载出错。。。", Toast.LENGTH_LONG).show();
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
