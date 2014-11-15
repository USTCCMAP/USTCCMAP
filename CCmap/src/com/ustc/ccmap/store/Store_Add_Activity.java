/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-25
 * ����Ϊ����Ŀ������ղع��ܹ��ܡ�
 * ������ֶ������㷢�ֵĵص㣬 Ҳ���ԣ��������浯�������ݴ�����ѡ���ղع��ܣ�
 * ��洢��ѡ�����Ϣ���Ա����´�ͨ���õ�ͼ���������
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
        mLayoutParams.height = (int)(mDisplay.getHeight()*1);  //�߶�Ϊ��Ļ��1
        mLayoutParams.width = (int)(mDisplay.getWidth()*1);  //���Ϊ��Ļ��1
	        
	        
        setContentView(R.layout.store_add); 
       
    	/**
		 *  ����һ��Activity�л�ȡҪ��ӵ�������Ϣ����ʼ��
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
        		else if(extras.containsKey("fromMainActivity")){//�ɵ�ͼת����
        			//��Ҫ�洢�ĵ�������
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
        		
        		//�ı������ȡ��Ϣ����ӵ�Place��
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
				//������Ч���ж�
				if(isAcceptableData(place)){
					
					//�����ݿ����������
					db.open();
					if(top_activity == 1 || 3 == top_activity)
						db.insert(place);
					else if(top_activity == 2)
					{
						db.deleteOneData(strName);
						db.insert(place);
					}
					
					db.close();		
				
					//���ز������
					Bundle bundle = new Bundle();  
					bundle.putString("child",place.getName());  
					Intent intent = new Intent();  
					intent.putExtras(bundle); 
					setResult(RESULT_OK, intent); 
					
					//��ʾ�ɹ�������Activity
					Toast.makeText(Store_Add_Activity.this, "�����ɹ�", Toast.LENGTH_SHORT).show();
					finish();
				}
				
        }});
       
        //�����ı������ݣ���ť��
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
   
        //ȡ����ӣ���ť��
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
	 *  ������Ч���ж�
	 */
	public boolean isAcceptableData(Place place){
		if(place.getComment().equals("")){
			place.setComment("");
		}
		if(place.getName().equals("")){
			Toast.makeText(Store_Add_Activity.this, "����������", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(place.getType().equals("")){
			Toast.makeText(Store_Add_Activity.this, "����������", Toast.LENGTH_SHORT).show();	
			return false;
		}
		if(place.getAddress().equals("")){
			Toast.makeText(Store_Add_Activity.this, "�������ַ", Toast.LENGTH_SHORT).show();

			return false;
		}		
		return true;		
	}
	
	void chooseType(){
		final ListView typeChoose = (ListView)findViewById(R.id.list_type);
		typeChoose.setVisibility(View.VISIBLE);
		final String[] strType = { "����", "ס��", "����", "����", "����", "�Ľ�", "����", "����"};
		
		ArrayList<HashMap<String, Object>> typeList = new ArrayList<HashMap<String, Object>>();
		SimpleAdapter typeAdapter = null;
		// �����Ϣ
		for(int i = 0; i < 8; i++){
			HashMap<String, Object> typeMap = new HashMap<String, Object>();
			typeMap.put("img", R.drawable.store_small_icon);
			typeMap.put("str", strType[i]);
			typeList.add(typeMap);
		}
			
		// ��ʾ��Ϣ
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
//        	//ѡ���¼�����
//        	EditText editText2 = (EditText)findViewById(R.id.editText_type);
//        	editText2.setText(parent.getItemAtPosition(pos).toString());
//        }
//
//        public void onNothingSelected(AdapterView<?> parent) {
//        	Toast.makeText(Store_Add_Activity.this, "�����Ϊ��", Toast.LENGTH_SHORT).show();
//        }
//    }
	
}
