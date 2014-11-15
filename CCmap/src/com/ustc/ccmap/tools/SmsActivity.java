/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-25
 * 该类为本项目的发送短信功能。
 * 通过调用android手机内置的发送信息功能，实现该功能
 * 当你查询到想要的结果，并要把这个结果分享给你的亲朋好友，就用到了这项功能了。
 * 你可以使用程序默认短信的文字，也可以自行编写短信内容。
 * 良好的界面设计，简单的操作，给你舒心的感受。
 */
package com.ustc.ccmap.tools;

import java.util.List;


import com.ustc.ccmap.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.Phones;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class SmsActivity extends Activity {
    private EditText edtMobile;
    private EditText edtContent;
    private Button btnSend;
    //private Button btnBack;
    private static final String TAG = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);
        edtMobile = (EditText) findViewById(R.id.edittext_number);
        edtContent = (EditText) findViewById(R.id.edittext_content);
        btnSend = (Button) findViewById(R.id.button_send);
        //btnBack = (Button) findViewById(R.id.button_send_back);
        
        Bundle dataBundle = getIntent().getExtras();
        edtContent.setText(dataBundle
				.getString("WEATHER_INFO_MESSAGE"));
        
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String strMobile = edtMobile.getText().toString();
                String strContent = edtContent.getText().toString();
                SmsManager smsManager = SmsManager.getDefault();
                if (strContent.length() > 70) {
                    List<String> contents = smsManager
                            .divideMessage(strContent);
                    for (String sms : contents) {
                        smsManager.sendTextMessage(strMobile, null, sms, null,
                                null);
                    }
                } else {
                    smsManager.sendTextMessage(strMobile, null, strContent,
                            null, null);
                }
                finish();
                //edtContent.setText("");
                //Toast.makeText(SmsActivity.this, R.string.send_info, Toast.LENGTH_SHORT).show();

            }
        });
    }
    
    
    public void OnClick_back(View view){
    	this.finish();
    }
    
    
	 
    /**
     * 调用手机短息接口
     */
    //---sends an SMS message to another device--- 
      @SuppressWarnings("unused")
	@SuppressLint({ "NewApi", "NewApi", "NewApi" })
		private void sendSMS( String phoneNumber, String message) 
                      { 
                              String SENT = "SMS_SENT" ; 
                              String DELIVERED = "SMS_DELIVERED" ; 
              
                              PendingIntent sentPI = PendingIntent.getBroadcast ( this , 0 ,
                                              new Intent( SENT) , 0 ) ; 
              
                              PendingIntent deliveredPI = PendingIntent.getBroadcast ( this , 0 ,
                                              new Intent( DELIVERED) , 0 ) ; 
              
                              //---when the SMS has been sent--- 
                              //当信息发送成功后
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
                  
                  Log.d(TAG, "number" + number);
                  
                  edtMobile.setText(number);
                  break;
              
              default:
                  break;
          }
      }
}



