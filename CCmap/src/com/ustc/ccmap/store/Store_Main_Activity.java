/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-25
 * ����Ϊ����Ŀ���ղع��ܹ��ܡ�
 * ������ֶ������㷢�ֵĵص㣬 Ҳ���ԣ��������浯�������ݴ�����ѡ���ղع��ܣ�
 * ��洢��ѡ�����Ϣ���Ա����´�ͨ���õ�ͼ���������
 * ��չʾ���б��У������ѡ����Ӧ�ĵص��¼���Խ�����һ���Ĳ�����
 * �絼��������޸Ļ�ɾ���õص���Ϣ�ȡ�
 */
package com.ustc.ccmap.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ustc.ccmap.R;
import com.ustc.ccmap.tools.dbAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;

public class Store_Main_Activity extends Activity {

	private static ArrayList<Map<String, String>> parentData = new ArrayList<Map<String, String>>();
	private static ArrayList<ArrayList<Map<String, String>>> childData = new ArrayList<ArrayList<Map<String, String>>>();
	private ExpandableListView listview;
	static final int REQUEST_CODE = 1;
	static final int REQUEST_CODE_1 = 2;
	
	private int the_group_expand_position = -1;		//��ǰ�򿪵ĸ��ڵ�
	private int position_parent= -1;

	final  static int CONTEXT_MENU_1 = Menu.FIRST;;
	final  static int CONTEXT_MENU_2 = Menu.FIRST+1;;
	    
	dbAdapter db = new dbAdapter(Store_Main_Activity.this);
	String[] strParent = { "����", "ס��", "����", "����", "����", "�Ľ�", "����", "����"};
	private int strParent_len = strParent.length;
	
	/**
	 *  ��Ϣ��ʼ��
	*/
	public void getData() {
		
		//��ʼ����һ�㣬������
		for (int i = 0; i < strParent_len; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("parent", strParent[i]);
			parentData.add(map);
		}

		//�������ݿ⣬��ѯ�������µĴ����Ϣ����name
		db.open();
		
		for (int i = 0; i < strParent_len; i++) {
			ArrayList<Map<String, String>> child = new ArrayList<Map<String, String>>();
			Map<String, String> map = new HashMap<String, String>();	
	
			//�����ݿ��л�ȡ�����͵ĸ��������ƣ������strChild�ַ�������
			String[] strChild = db.queryNameByType(strParent[i]);
		
			//��Map��������ݣ���������ӵ�ArrayList<Map<String, String>> child��
			if(!strChild[0].equals("") ){
			for (int j = 0; j < strChild.length; j++) {
				Map<String, String> maps = new HashMap<String, String>();
				maps.put("child", strChild[j]);					
				child.add(maps);	
				}				
			}
			map.put("child", "���");
			child.add(map);

			childData.add(child);
		}
		db.close();
		Log.i("db", "close");
	}


	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��������ʽ
		setContentView(R.layout.store_main);
		
		listview = (ExpandableListView) findViewById(R.id.listview);		
		//�滻ExpandableListView�Ĵ򿪹ر�ʱ�ļ�ͷͼ��		
		listview.setGroupIndicator(this.getResources().getDrawable(R.drawable.store_zicon_selector));
	
		/**
		 * �������б�ʱ���Ϸ���ʾ���ڵ��view
		 */
		final LinearLayout linear = (LinearLayout) findViewById(R.id.gone_linear);

		/**
		 *  �������ڵ�򿪵��¼�	
	   	 */
		listview.setOnGroupExpandListener(new OnGroupExpandListener() {					
			public void onGroupExpand(int groupPosition) {
				
				the_group_expand_position = groupPosition;							
//				isExpanding = true;
			}
		});

		/**
		 *  �������ڵ�رյ��¼�
		 */
		listview.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			public void onGroupCollapse(int groupPosition) {
				
				if (linear.getVisibility() == View.VISIBLE) {
					linear.setVisibility(View.GONE);
				}
			}
		});
		
		/**
		 *  Ϊÿ��ITEM��Ӽ���
		 */
		linear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				linear.setVisibility(View.GONE);
				listview.collapseGroup(the_group_expand_position);
			}
		});
		
		//��ʼ������
		getData();

		//�������������
		final SimpleExpandableListAdapter selAdapter = new SimpleExpandableListAdapter(
				this, parentData, R.layout.storelistparent,new String[] { "parent" }, new int[] { R.id.parend },
				childData, R.layout.storelistchild, new String[] { "child" },new int[] { R.id.child });
		listview.setAdapter(selAdapter);
		
		/**
		 *  ��������������
		 */
		listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent,
                    View v, int groupPosition, int childPosition,long id) 
            {
            	//��¼��ǰ����ڵ㣬Ϊ���ݸ�����׼��
	            position_parent = groupPosition;	            
	            String child = childData.get(groupPosition).get(childPosition).get("child");
	            String parents = parentData.get(groupPosition).get("parent");
	            Log.i("groupPosition",String.valueOf(groupPosition));
	            Log.i("parent",parentData.get(groupPosition).get("parent"));
	            
	            if(child.equals("���"))	//�ж��Ƿ�Ϊ���ѡ��
	            { 	
	            	 
	            	//�������ҳ�沢�رյ�ǰ����ڵ�
	            	Intent intent = new Intent(Store_Main_Activity.this, Store_Add_Activity.class);
	            	//�������������ʹ��ݸ�addActivity�������Զ����
	            	intent.putExtra("parent", parents);
	            	intent.putExtra("frombuiltActivity", "yes");
			        startActivityForResult(intent,REQUEST_CODE);
                    //selAdapter.notifyDataSetChanged();
			        listview.collapseGroup(groupPosition);
				}
	            else{
	            	Intent intent = new Intent(Store_Main_Activity.this, Store_Query_Activity.class);			
			        intent.putExtra("child", child);
			        intent.putExtra("parent", parents);
			    	startActivityForResult(intent,REQUEST_CODE_1);
			    	listview.collapseGroup(groupPosition);
	            }	            
	            return true;
            }
        });
		
	}


	@Override
	protected void onStop() {
		super.onStop();
		parentData.clear();
		childData.clear();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 *  �Է��ص����ݽ��д���
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.e("STORE", "kkkk");

		ArrayList<Map<String, String>> child = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		//�жϷ���ֵ��ԴActivity���������
		if (requestCode == REQUEST_CODE||requestCode == REQUEST_CODE_1) {  
			
			if (resultCode == RESULT_OK){
				Bundle extras = data.getExtras(); 
				Log.e("STORE", "kkkk");
				if (extras != null) {   
					if ("yes".equals(extras.getString("tomap"))) {  
		        		Intent intent = new Intent();  
	        			intent.putExtra("status", "ICON");
	        			intent.putExtra("lat", extras.getInt("lat"));
	        			intent.putExtra("lng", extras.getInt("lng"));
						setResult(RESULT_OK, intent); 
		        	    finish();
					} 
				} 
			}
			
			
			
				//������Ӹø���ڵ��µ�����				
				db.open();
				String[] strChild = db.queryNameByType(parentData.get(position_parent).get("parent"));
				if(!strChild[0].equals(""))
				{
					for (int j = 0; j < strChild.length; j++) {
						Map<String, String> maps = new HashMap<String, String>();
						maps.put("child", strChild[j]);
						Log.e("KK", "KK");
						Log.i("addchild", strChild[j]);
						child.add(maps);	
						}				
					}
				}
				map.put("child", "���");
				child.add(map);
				childData.remove(position_parent);
				childData.add(position_parent, child);				
				db.close();
			}  
}