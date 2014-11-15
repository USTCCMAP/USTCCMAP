/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-25
 * 该类为本项目的收藏功能功能。
 * 你可以手动输入你发现的地点， 也可以，在主界面弹出的气泡窗口中选择收藏功能，
 * 会存储所选点的信息，以便于下次通过该地图导航到这里。
 * 在展示的列表中，你可以选择相应的地点记录，以进行下一步的操作，
 * 如导航到这里，修改或删除该地点信息等。
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
	
	private int the_group_expand_position = -1;		//当前打开的父节点
	private int position_parent= -1;

	final  static int CONTEXT_MENU_1 = Menu.FIRST;;
	final  static int CONTEXT_MENU_2 = Menu.FIRST+1;;
	    
	dbAdapter db = new dbAdapter(Store_Main_Activity.this);
	String[] strParent = { "餐饮", "住宿", "娱乐", "购物", "出行", "文教", "服务", "其它"};
	private int strParent_len = strParent.length;
	
	/**
	 *  信息初始化
	*/
	public void getData() {
		
		//初始化第一层，即类型
		for (int i = 0; i < strParent_len; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("parent", strParent[i]);
			parentData.add(map);
		}

		//连接数据库，查询各类型下的存放信息，即name
		db.open();
		
		for (int i = 0; i < strParent_len; i++) {
			ArrayList<Map<String, String>> child = new ArrayList<Map<String, String>>();
			Map<String, String> map = new HashMap<String, String>();	
	
			//从数据库中获取该类型的各数据名称，存放在strChild字符数组中
			String[] strChild = db.queryNameByType(strParent[i]);
		
			//向Map中添加数据，并逐条添加到ArrayList<Map<String, String>> child中
			if(!strChild[0].equals("") ){
			for (int j = 0; j < strChild.length; j++) {
				Map<String, String> maps = new HashMap<String, String>();
				maps.put("child", strChild[j]);					
				child.add(maps);	
				}				
			}
			map.put("child", "添加");
			child.add(map);

			childData.add(child);
		}
		db.close();
		Log.i("db", "close");
	}


	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题样式
		setContentView(R.layout.store_main);
		
		listview = (ExpandableListView) findViewById(R.id.listview);		
		//替换ExpandableListView的打开关闭时的箭头图标		
		listview.setGroupIndicator(this.getResources().getDrawable(R.drawable.store_zicon_selector));
	
		/**
		 * 滑动子列表时在上方显示父节点的view
		 */
		final LinearLayout linear = (LinearLayout) findViewById(R.id.gone_linear);

		/**
		 *  监听父节点打开的事件	
	   	 */
		listview.setOnGroupExpandListener(new OnGroupExpandListener() {					
			public void onGroupExpand(int groupPosition) {
				
				the_group_expand_position = groupPosition;							
//				isExpanding = true;
			}
		});

		/**
		 *  监听父节点关闭的事件
		 */
		listview.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			public void onGroupCollapse(int groupPosition) {
				
				if (linear.getVisibility() == View.VISIBLE) {
					linear.setVisibility(View.GONE);
				}
			}
		});
		
		/**
		 *  为每个ITEM添加监听
		 */
		linear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				linear.setVisibility(View.GONE);
				listview.collapseGroup(the_group_expand_position);
			}
		});
		
		//初始化数据
		getData();

		//添加数据适配器
		final SimpleExpandableListAdapter selAdapter = new SimpleExpandableListAdapter(
				this, parentData, R.layout.storelistparent,new String[] { "parent" }, new int[] { R.id.parend },
				childData, R.layout.storelistchild, new String[] { "child" },new int[] { R.id.child });
		listview.setAdapter(selAdapter);
		
		/**
		 *  设置子类点击监听
		 */
		listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent,
                    View v, int groupPosition, int childPosition,long id) 
            {
            	//记录当前父类节点，为数据更新做准备
	            position_parent = groupPosition;	            
	            String child = childData.get(groupPosition).get(childPosition).get("child");
	            String parents = parentData.get(groupPosition).get("parent");
	            Log.i("groupPosition",String.valueOf(groupPosition));
	            Log.i("parent",parentData.get(groupPosition).get("parent"));
	            
	            if(child.equals("添加"))	//判断是否为添加选项
	            { 	
	            	 
	            	//进入添加页面并关闭当前父类节点
	            	Intent intent = new Intent(Store_Main_Activity.this, Store_Add_Activity.class);
	            	//将父类名即类型传递给addActivity，进行自动添加
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
	 *  对返回的数据进行处理
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.e("STORE", "kkkk");

		ArrayList<Map<String, String>> child = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		//判断返回值的源Activity及操作结果
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
			
			
			
				//重新添加该父类节点下的数据				
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
				map.put("child", "添加");
				child.add(map);
				childData.remove(position_parent);
				childData.add(position_parent, child);				
				db.close();
			}  
}