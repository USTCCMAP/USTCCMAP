/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-25
 * 该类为本项目的添加收藏功能功能。
 * 你可以手动输入你发现的地点， 也可以，在主界面弹出的气泡窗口中选择收藏功能，
 * 会存储所选点的信息，以便于下次通过该地图导航到这里。
 */
package com.ustc.ccmap.store;

import java.util.ArrayList;

import java.util.HashMap;

import com.ustc.ccmap.R;
import com.ustc.ccmap.tools.Place;
import com.ustc.ccmap.tools.dbAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class Store_Add_Activity extends Activity {

	static int lat = 190;
	static int lng = 190;
	private static int top_activity = 0;
	private static String strName = "";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager wm = getWindowManager();
        Display mDisplay = wm.getDefaultDisplay();
        
        LayoutParams mLayoutParams = getWindow().getAttributes();
        mLayoutParams.y =(int) (mDisplay.getHeight()*-0.039);
        mLayoutParams.height = (int)(mDisplay.getHeight()*1);  //高度为屏幕的1
        mLayoutParams.width = (int)(mDisplay.getWidth()*1);  //宽度为屏幕的1
	        
	        
        setContentView(R.layout.store_add); 
       
    	/**
		 *  从上一级Activity中获取要添加的类型信息并初始化
		 */
        Bundle extras = getIntent().getExtras();   
        if (extras != null) {
        		if(extras.containsKey("frombuiltActivity")){
        			top_activity = 1;
        			String type ="";  
        			type = extras.getString("parent"); 
        			EditText editText_type = (EditText)findViewById(R.id.editText_type);
        			editText_type.setText(type);
        		}
        		else if(extras.containsKey("fromQueryActivity")){
        			top_activity = 2;
        			EditText editText = (EditText)findViewById(R.id.editText_name);
        			editText.setText(extras.getString("name"));
        			strName = extras.getString("name");
    				EditText editText1 = (EditText)findViewById(R.id.editText_address);
    				editText1.setText(extras.getString("address"));
    				EditText editText2 = (EditText)findViewById(R.id.editText_type);  
    				editText2.setText(extras.getString("type"));
    				EditText editText3 = (EditText)findViewById(R.id.editText_comment);
    				editText3.setText(extras.getString("comment"));
        		}
        		else if(extras.containsKey("fromMainActivity")){//由地图转来的
        			//需要存储的地理坐标
        			top_activity = 3;
        			lat = extras.getInt("lat");
        		    lng = extras.getInt("lng");
        		    if(null != extras.getString("name")){
        		    	EditText editText = (EditText)findViewById(R.id.editText_name);
            			editText.setText(extras.getString("name"));
        		    }
        		    if(null != extras.getString("address")){
        		    	EditText editText1 = (EditText)findViewById(R.id.editText_address);
        				editText1.setText(extras.getString("address"));	    	
				    }
        			findViewById(R.id.editText_type).setOnClickListener(new View.OnClickListener() {
    	    			@Override
    	    			public void onClick(View v) {
    	    				chooseType();
    	    			}
    	    		});
        			
//        			Spinner spinner = (Spinner)findViewById(R.id.Spinner_type);
//        			spinner.setVisibility(View.VISIBLE);
//        			ArrayAdapter<CharSequence> mAdapter;
//        			mAdapter = ArrayAdapter.createFromResource(Store_Add_Activity.this, R.array.spinner_type,
//        					android.R.layout.simple_spinner_dropdown_item);
//        			spinner.setAdapter(mAdapter);
//        			OnItemSelectedListener spinnerListener = new myOnItemSelectedListener(Store_Add_Activity.this, mAdapter);
//        			spinner.setOnItemSelectedListener(spinnerListener);
        		}
        }
 
        Button button_add = (Button)findViewById(R.id.button_add);
        button_add.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		
        		dbAdapter db = new dbAdapter(Store_Add_Activity.this);
        		
        		//文本框里获取信息并添加到Place中
        		Place place = new Place();				
				EditText editText = (EditText)findViewById(R.id.editText_name);
				place.setName(editText.getText().toString());
				EditText editText1 = (EditText)findViewById(R.id.editText_address);
				place.setAddress(editText1.getText().toString());	
				EditText editText2 = (EditText)findViewById(R.id.editText_type);
				place.setType(editText2.getText().toString());	
				EditText editText3 = (EditText)findViewById(R.id.editText_comment);
				place.setComment(editText3.getText().toString());
				place.setLat(lat);
				place.setLng(lng);
				//数据有效性判断
				if(isAcceptableData(place)){
					
					//向数据库中添加数据
					db.open();
					if(top_activity == 1 || 3 == top_activity)
						db.insert(place);
					else if(top_activity == 2)
					{
						db.deleteOneData(strName);
						db.insert(place);
					}
					
					db.close();		
				
					//返回操作结果
					Bundle bundle = new Bundle();  
					bundle.putString("child",place.getName());  
					Intent intent = new Intent();  
					intent.putExtras(bundle); 
					setResult(RESULT_OK, intent); 
					
					//提示成功并结束Activity
					Toast.makeText(Store_Add_Activity.this, "操作成功", Toast.LENGTH_SHORT).show();
					finish();
				}
				
        }});
       
        //重置文本框内容（按钮）
        Button button_reset = (Button)findViewById(R.id.button_reset);
        button_reset.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
				Log.i("reset","OK");
				EditText editText = (EditText)findViewById(R.id.editText_name);				
				EditText editText1 = (EditText)findViewById(R.id.editText_address);
				EditText editText3 = (EditText)findViewById(R.id.editText_comment);
				editText.setText("");
				editText1.setText("");		
				editText3.setText("");				
        }});
   
        //取消添加（按钮）
        Button button_cancle = (Button)findViewById(R.id.button_cancle);
        button_cancle.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent();  
				setResult(RESULT_CANCELED, intent); 
				finish();			
        }});
	
	}
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	
	/**
	 *  数据有效性判断
	 */
	public boolean isAcceptableData(Place place){
		if(place.getComment().equals("")){
			place.setComment("");
		}
		if(place.getName().equals("")){
			Toast.makeText(Store_Add_Activity.this, "请输入名称", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(place.getType().equals("")){
			Toast.makeText(Store_Add_Activity.this, "请输入类型", Toast.LENGTH_SHORT).show();	
			return false;
		}
		if(place.getAddress().equals("")){
			Toast.makeText(Store_Add_Activity.this, "请输入地址", Toast.LENGTH_SHORT).show();

			return false;
		}		
		return true;		
	}
	
	void chooseType(){
		final ListView typeChoose = (ListView)findViewById(R.id.list_type);
		typeChoose.setVisibility(View.VISIBLE);
		final String[] strType = { "餐饮", "住宿", "娱乐", "购物", "出行", "文教", "服务", "其它"};
		
		ArrayList<HashMap<String, Object>> typeList = new ArrayList<HashMap<String, Object>>();
		SimpleAdapter typeAdapter = null;
		// 获得信息
		for(int i = 0; i < 8; i++){
			HashMap<String, Object> typeMap = new HashMap<String, Object>();
			typeMap.put("img", R.drawable.store_small_icon);
			typeMap.put("str", strType[i]);
			typeList.add(typeMap);
		}
			
		// 显示信息
		typeAdapter = new SimpleAdapter(this, typeList, 
				R.layout.detail_list,		
				new String[] { "img", "str"},
				new int[] {R.id.detailimg, R.id.detailname});
		typeChoose.setAdapter(typeAdapter);
		
		typeChoose.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.getItemAtPosition(arg2);

				EditText type = (EditText)findViewById(R.id.editText_type);
				type.setText(strType[arg2]);
				findViewById(R.id.list_type).setVisibility(View.GONE);
				}
		});
		
	}

//    private class myOnItemSelectedListener implements OnItemSelectedListener {
//
//        public myOnItemSelectedListener(Activity c, ArrayAdapter<CharSequence> ad) {
//
//        }
//        
//        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
//        	//选择事件处理
//        	EditText editText2 = (EditText)findViewById(R.id.editText_type);
//        	editText2.setText(parent.getItemAtPosition(pos).toString());
//        }
//
//        public void onNothingSelected(AdapterView<?> parent) {
//        	Toast.makeText(Store_Add_Activity.this, "该项不能为空", Toast.LENGTH_SHORT).show();
//        }
//    }
	
}
