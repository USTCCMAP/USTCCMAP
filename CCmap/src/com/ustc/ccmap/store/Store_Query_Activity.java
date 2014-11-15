/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-25
 * 该类为本项目的列出收藏功能功能。
 * 选择该功能后，会分类列出你所存储的地点信息，以便下一步的操作。
 */
package com.ustc.ccmap.store;

import com.ustc.ccmap.R;
import com.ustc.ccmap.tools.Place;
import com.ustc.ccmap.tools.dbAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Store_Query_Activity extends Activity {

	private static Place places  = new Place();
	static final int REQUEST_CODE_2 = 3;
	public void onCreate(Bundle savedInstanceState) {
		dbAdapter db = new dbAdapter(Store_Query_Activity.this);
		
		super.onCreate(savedInstanceState);
		WindowManager wm = getWindowManager();
        Display mDisplay = wm.getDefaultDisplay();
        
        LayoutParams mLayoutParams = getWindow().getAttributes();
        mLayoutParams.y =(int) (mDisplay.getHeight()*-0.04);
        mLayoutParams.height = (int)(mDisplay.getHeight()*0.55);  //高度为屏幕的0.6
        mLayoutParams.width = (int)(mDisplay.getWidth()*0.7);  //宽度为屏幕的0.9	      
        setContentView(R.layout.store_query); 

        String data="";  
        Bundle extras = getIntent().getExtras();   
        if (extras != null) 
        		data = extras.getString("child"); 
        db.open();
        Place[] place = db.queryOneData(data);
        db.close();
        places = place[0];
        TextView TextView1 = (TextView)findViewById(R.id.label_name);				
        TextView TextView2 = (TextView)findViewById(R.id.label_type);
        TextView TextView3 = (TextView)findViewById(R.id.label_address);
        TextView TextView4 = (TextView)findViewById(R.id.label_comment);
        TextView1.setText("  名称: "+ place[0].getName());
        TextView2.setText("  类型: "+ place[0].getType());
        TextView3.setText("  地址: "+ place[0].getAddress());		
		TextView4.setText("  备注: "+ place[0].getComment());	

        Button button_update = (Button)findViewById(R.id.button_update);
		button_update.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		
                Intent intent = new Intent(Store_Query_Activity.this, Store_Add_Activity.class);			
		        intent.putExtra("name", places.getName());
		        intent.putExtra("type", places.getType());
		        intent.putExtra("comment", places.getComment());
		        intent.putExtra("address", places.getAddress());
		        intent.putExtra("fromQueryActivity", "yes");
		    	startActivityForResult(intent,REQUEST_CODE_2);        		
        		Intent intents = new Intent();  
				setResult(RESULT_OK, intents); 
				finish();			
        }});
		
        
		Button button_return = (Button)findViewById(R.id.button_return);
		button_return.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent();  
				setResult(RESULT_OK, intent); 
				finish();			
        }});
		
		//删除操作
		Button button_delete = (Button)findViewById(R.id.button_delete);
		button_delete.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {	
        		//弹出确认对话框
        		AlertDialog dlg = new AlertDialog.Builder(Store_Query_Activity.this).setTitle("确定删除？")
        		.setPositiveButton("确定", 
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog,int which) {
        						String data="";  
        		    	        Bundle extras = getIntent().getExtras();   
        		    	        if (extras != null) 
        		    	        	data = extras.getString("child");
        		        		dbAdapter db = new dbAdapter(Store_Query_Activity.this);
        						db.open();
        		        	    db.deleteOneData(data);
        		        	    db.close();
        		        	    Intent intent = new Intent();  
        						setResult(RESULT_OK, intent); 
        		        	    Toast.makeText(Store_Query_Activity.this, "删除成功", Toast.LENGTH_SHORT).show();
        		        	    finish();
        					}
        				})
        		.setNegativeButton("取消", 
        				new DialogInterface.OnClickListener() {        				
        					public void onClick(DialogInterface dialog,int which) 
        					{    					
        		        	    Toast.makeText(Store_Query_Activity.this, "取消删除", Toast.LENGTH_SHORT).show();
        					}
        				}).create();
        		
        		dlg.show();           
        		}
        	});
		
		
		/**
		 *  进入地图
		 */
		Button button_EnterMap = (Button)findViewById(R.id.button_enter_map);
		button_EnterMap.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		Log.e("tomap", "tomap");
        		//获取地理坐标 假定存在
        		int lat = places.getLat();
        		int lng = places.getLng();
        		if(190 != places.getLat()){
        			Intent intent = new Intent();  
        			intent.putExtra("tomap", "yes");
        			intent.putExtra("lat", lat);
        			intent.putExtra("lng", lng);
					setResult(RESULT_OK, intent); 
	        	    finish();
        		}
        		else{
                	new AlertDialog.Builder(Store_Query_Activity.this)
        			.setTitle("温馨提示")
        			.setMessage("很抱歉，未找到该地的地理信息，你可以尝试在搜索功能中手动输入搜索。")
        			.setNegativeButton("确定", null)
        			.show();
                	return;
        		}
        }});
		
	}
	protected void onStop() {
		Intent intents = new Intent();  
		setResult(RESULT_OK, intents); 
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		Intent intents = new Intent();  
		setResult(RESULT_OK, intents); 
		super.onDestroy();
	}
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	
	/**
	 *  对返回的数据进行处理
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {} 
		

}
