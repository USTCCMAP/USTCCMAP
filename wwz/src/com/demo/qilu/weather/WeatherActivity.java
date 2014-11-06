/**
 * @author 神奇4+1
 * @version 1.0.0 创建时间：2012-08-08
 * 该类为本项目的天气查询功能。
 * 在主界面点击“MENU”选择天气查询，便可打开盖功能。
 */

package com.demo.qilu.weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.qilu.R;
import com.demo.qilu.tools.SmsActivity;


public class WeatherActivity extends Activity {
	
	Activity context = this;
	private ListView listView;
	private Drawable[] images;
	private String[] dates;
	private String[] temperatures;
	private String[] conditions;
	private double[] low;
	private double[] high;
	private WeatherAdapter adapter;
	String place;
	private String messagestr = "";																//用来存储发送短信的内容
	public static EditText phoneNumInput;
	private TextView City_title;
	
	public static final int MSG_GET_WEATHER_SUCC = 10001;
	public static final int MSG_GET_WEATHER_FALU = 10002;
	
	private boolean isReady = false;

	//以下是菜单项
    protected static final int MENU_FUNC1 = Menu.FIRST;	//功能一  发送短信
    protected static final int MENU_FUNC2 = Menu.FIRST+1;	//功能二   退出
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    if (android.os.Build.VERSION.SDK_INT > 9) {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
	    }
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_activity);
		
		initView();
		try{
			initData();
//			adapter = new WeatherAdapter(context, images, dates, temperatures, conditions);
//			
//			listView.setAdapter(adapter);
		}catch(Exception e){
			new AlertDialog.Builder(this)
			.setTitle("温馨提示")
			.setMessage("很抱歉，可能是网络等原因，暂时无法查找，请稍后重试")
			.setNegativeButton("确定", null)
			.show();
        	finish();
		}
		
	}

	private void initView() {
		//用来接收传来的城市的名字
		Bundle bundle = this.getIntent().getExtras();
		place = bundle.getString("KEY_PLACE");
		City_title = (TextView) findViewById(R.id.Weather_city_title);
        City_title.setText(place);
        messagestr += place+"天气状况"+"\n";						//短息接受者接到的地点提示
		listView = (ListView) findViewById(R.id.list);
	}
	
	
	List<Weather> ws = null;
	
	private Handler handler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	        // TODO Auto-generated method stub
	        switch(msg.what){
	            case MSG_GET_WEATHER_SUCC:
	            {
	            	//isReady = true;
	            	try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	showDate();
	            	break;
	            }
	            case MSG_GET_WEATHER_FALU :
	            {
	            	Log.e("weatheractivity", "failure to get the weathre information");
	            	finish();
	            }
	            default:break;
	        }
	    }

	};
	
	void getDate(){
		
		String xmlStr = WeatherXml.getWeatherXml(place);
		Log.e("BBBBBBBB", xmlStr);
		if(null == xmlStr){
			new AlertDialog.Builder(this)
			.setTitle("温馨提示")
			.setMessage("很抱歉，可能是网络等原因，暂时无法查找，请稍后重试")
			.setNegativeButton("确定", null)
			.show();
	    	finish();
		}
		ws = DomParseWeather.getWeatherFromXml(xmlStr, handler);
	}
	
	void showDate(){
		
    	  if(ws == null){
//			new AlertDialog.Builder(this)
//			.setTitle("温馨提示")
//			.setMessage("很抱歉111111111，可能是网络等原因，无法获得天气信息，请稍后重试")
//			.setNegativeButton("确定", null)
//			.show();
//	       	finish();
		 }
      	
    		images = new Drawable[ws.size()];
    		dates = new String[ws.size()];
    		temperatures = new String[ws.size()];
    		conditions = new String[ws.size()];
    		low = new double[ws.size()];
    		high = new double[ws.size()];
    		
    		Weather w = null;

    		for (int i = 0; i < ws.size(); i++) {
    			w = ws.get(i);
    			
    			images[i] = w.getImage();//loadImage(w.getImageUrl());
    			dates[i] = w.getDay_of_week();
    			conditions[i] = w.getCondition();
    			low[i] = Integer.parseInt(w.getLow_temperature());
    			high[i] = Integer.parseInt(w.getHigh_temperature());
    			temperatures[i] = String.valueOf(low[i]).substring(0, 2) 
    					+ "℃-" + String.valueOf(high[i]).substring(0, 2)+"℃";
    		}
    		
//    		try {
//				Thread.sleep(4000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
    		//dataShift();
    		
			adapter = new WeatherAdapter(context, images, dates, temperatures, conditions);
			listView.setAdapter(adapter);
	}
	
	
	private Thread getWeatherDate = new Thread(new Runnable(){
	    @Override
	    public void run() {
	        // TODO Auto-generated method stub
			getDate();
	    }
	});
	

	private void initData() throws Exception{
		
		int i = 0;
//		while(!isReady){
//			i++;
//			isReady = false;
//		}
//    	showDate();
//		new Thread() {
//			@Override
//			public void run() {
//				getDate();
//			}
//		}.start();
//		
		//Thread.sleep(2000);
//		SystemClock.sleep(1000);
//		
		getWeatherDate.start();
		
//	  if(ws == null){
//			new AlertDialog.Builder(this)
//			.setTitle("温馨提示")
//			.setMessage("很抱歉，可能是网络等原因，无法获得天气信息，请稍后重试")
//			.setNegativeButton("确定", null)
//			.show();
//	       	finish();
//		 }
//		
//		images = new Drawable[ws.size()];
//		dates = new String[ws.size()];
//		temperatures = new String[ws.size()];
//		conditions = new String[ws.size()];
//		low = new double[ws.size()];
//		high = new double[ws.size()];
//		
//		Weather w = null;
//
//		for (int i = 0; i < ws.size(); i++) {
//			int lt;		//最低气温
//			int ht;		//最高气温
//			w = ws.get(i);
//			
//			images[i] = loadImage(w.getImageUrl());
//			dates[i] = w.getDay_of_week();
//			conditions[i] = w.getCondition();
//			low[i] = Integer.parseInt(w.getLow_temperature());
//			high[i] = Integer.parseInt(w.getHigh_temperature());
//			lt = (int) (5d/9d*(Integer.parseInt(w.getLow_temperature())-32));
//			ht= (int) (5d/9d*(Integer.parseInt(w.getHigh_temperature())-32));
//			messagestr += dates[i]+" "+conditions[i]+" "+lt+"℃-"+ht+"℃"+"\n";
//			Log.e("天气状况",messagestr);
//		}
//		
////		dataShift();
		
	}
	
	public static Drawable loadImage(String imageUrl) {
		try {
			return Drawable.createFromStream(
					(InputStream) new URL(imageUrl).getContent(), "image");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	} 
	
//	private void dataShift() {
//		for (int i = 0; i < dates.length; i++) {
//			if ("Mon".equals(dates[i])) {
//				dates[i] = "星期一";
//				
//			} else if ("Tue".equals(dates[i])) {
//				dates[i] = "星期二";
//			
//			} else if ("Wed".equals(dates[i])) {
//				dates[i] = "星期三";
//			
//			} else if ("Thu".equals(dates[i])) {
//				dates[i] = "星期四";
//			
//			} else if ("Fri".equals(dates[i])) {
//				dates[i] = "星期五";
//			
//			} else if ("Sat".equals(dates[i])) {
//				dates[i] = "星期六";
//			
//			} else if ("Sun".equals(dates[i])) {
//				dates[i] = "星期日";
//			}
//		}
		
//		for (int i = 0; i < conditions.length; i++) {
//			
//			if ("Chance of Rain".equals(conditions[i])) {
//				conditions[i] = "有雨";
//			
//			} else if ("Clear".equals(conditions[i])) {
//				conditions[i] = "晴";
//			
//			} else if ("Partly Sunny".equals(conditions[i])) {
//				conditions[i] = "多云间晴";
//			
//			} else if ("Mostly Sunny".equals(conditions[i])) {
//				conditions[i] = "晴间多云";
//			}else if("Chance of Storm".equals(conditions[i])) {
//				conditions[i] = "有风暴";
//				
//			}
//		}
		
//		for (int i = 0; i < temperatures.length; i++) {
//			Log.i("TAG", low[i] + "）））））））");
//			
//			low[i] = 5d/9d*(low[i]-32);
//			high[i] = 5d/9d*(high[i]-32);
//			temperatures[i] = String.valueOf(low[i]).substring(0, 2) 
//					+ "℃-" + String.valueOf(high[i]).substring(0, 2)+"℃";
//		}
//	}
	
	 public boolean onCreateOptionsMenu(Menu menu) {
	    	menu.add(0, MENU_FUNC1, 0,"发送短信").setIcon(R.drawable.message_send);
	    	menu.add(0, MENU_FUNC2, 0,"退出").setIcon(android.R.drawable.ic_menu_close_clear_cancel);//内置图标;
	    	return super.onCreateOptionsMenu(menu);
	    }
	    
	    
	    
	    
	    //选择菜单时执行的动作
	    public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case MENU_FUNC1:
				function1();
				break;
			case MENU_FUNC2:
				function2();
				break;
			}
			return super.onOptionsItemSelected(item);
		}
	    
	    
	    //发送短信
		public void function1(){
	    	/*LayoutInflater factory = LayoutInflater.from(WeatherActivity.this);
			View locationView = factory.inflate(R.layout.send_weather_message_dialog, null);
			phoneNumInput = (EditText) locationView
					.findViewById(R.id.phone_num_input);
			new AlertDialog.Builder(this).setIcon(R.drawable.message_title).setTitle("输入号码").setView(
					locationView).setPositiveButton("发送",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							//得到号码
							String phoneNum = phoneNumInput.getText().toString().trim();
							//发送短信
							sendSMS(phoneNum,messagestr);
							Log.e("开始发送短信",messagestr);
							}
						})
						.setNegativeButton("取消",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								//什么都不做
								}
							} ).show();*/
			Intent intent = new Intent();
			intent.setClass(WeatherActivity.this, SmsActivity.class);
			Bundle bundle = new Bundle();
			String content_message = messagestr;
			bundle.putString("WEATHER_INFO_MESSAGE", content_message);
			intent.putExtras(bundle); 
			startActivity(intent);

	    }
	    
	    
	    //退出
	public void function2(){
	    	this.finish();
	    }
	

	/*private void sendSMS( String phoneNumber, String message) 
    { 
            String SENT = "SMS_SENT" ; 
            String DELIVERED = "SMS_DELIVERED" ; 

            PendingIntent sentPI = PendingIntent.getBroadcast ( this , 0 ,
                            new Intent( SENT) , 0 ) ; 

            PendingIntent deliveredPI = PendingIntent.getBroadcast ( this , 0 ,
                            new Intent( DELIVERED) , 0 ) ; 

            //---when the SMS has been sent--- 
            registerReceiver( new BroadcastReceiver( ) { 
                    @Override
                    public void onReceive( Context arg0, Intent arg1) { 
                            switch ( getResultCode( ) ) 
                            { 
                            case Activity.RESULT_OK :
                                    Toast.makeText ( getBaseContext( ) , "SMS sent" , 
                                                    Toast.LENGTH_SHORT ) .show ( ) ; 
                                    break ; 
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE :
                                    Toast.makeText ( getBaseContext( ) , "Generic failure" , 
                                                    Toast.LENGTH_SHORT ) .show ( ) ; 
                                    break ; 
                            case SmsManager.RESULT_ERROR_NO_SERVICE :
                                    Toast.makeText ( getBaseContext( ) , "No service" , 
                                                    Toast.LENGTH_SHORT ) .show ( ) ; 
                                    break ; 
                            case SmsManager.RESULT_ERROR_NULL_PDU :
                                    Toast.makeText ( getBaseContext( ) , "Null PDU" , 
                                                    Toast.LENGTH_SHORT ) .show ( ) ; 
                                    break ; 
                            case SmsManager.RESULT_ERROR_RADIO_OFF :
                                    Toast.makeText ( getBaseContext( ) , "Radio off" , 
                                                    Toast.LENGTH_SHORT ) .show ( ) ; 
                                    break ; 
                            }
                    }
            }, new IntentFilter( SENT) ) ;

            //---when the SMS has been delivered--- 
            registerReceiver( new BroadcastReceiver( ) { 
                    @Override
                    public void onReceive( Context arg0, Intent arg1) { 
                            switch ( getResultCode( ) ) 
                            { 
                            case Activity.RESULT_OK :
                                    Toast.makeText ( getBaseContext( ) , "SMS delivered" , 
                                                    Toast.LENGTH_SHORT ) .show ( ) ; 
                                    break ; 
                            case Activity.RESULT_CANCELED :
                                    Toast.makeText ( getBaseContext( ) , "SMS not delivered" , 
                                                    Toast.LENGTH_SHORT ) .show ( ) ; 
                                    break ; 
                            } 
                    } 
            } , new IntentFilter( DELIVERED) ) ; 

            SmsManager sms = SmsManager.getDefault ( ) ; 
            sms.sendTextMessage ( phoneNumber, null , message, sentPI, deliveredPI) ; 
    }


		public void Onclick_check_num(View view){
			Intent i = new Intent(Intent.ACTION_PICK);
			i.setType("vnd.android.cursor.dir/phone");
			startActivityForResult(i, 0);
		}


		protected void onActivityResult (int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);

			switch (requestCode) {
			case 0:
				if (data == null) {
					return;
				}
				Uri uri = data.getData();
				Cursor cursor = getContentResolver().query(uri, null, null, null, null);
				cursor.moveToFirst();

				String number = cursor.getString(cursor.getColumnIndexOrThrow(Phones.NUMBER));
				phoneNumInput.setText(number);
				break;

			default:
				break;
			}
		}	*/
	

}
