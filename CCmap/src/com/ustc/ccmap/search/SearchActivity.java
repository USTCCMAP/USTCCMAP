/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-28
 * 该类为本项目的分类搜索功能。
 * 在主界面点击“MENU”选择分类查询，便可打开盖功能。
 * 在该功能功能下，你可以实现各种类型的查询，包括：
 * 在当前位置查询、在地图选择搜索中心查询、在指定城市查询等。
 * 本功能界面使用ExpandableListView是界面更加的友好。
 * 并提供热门搜索项目，你可以直接点击所要搜索的种类，便可得到想要的查询结果。
 */

package com.ustc.ccmap.search;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

import com.ustc.ccmap.*;
import com.ustc.ccmap.tools.MapUtility;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

public class SearchActivity extends Activity {
	private static ArrayList<Map<String, String>> parentData = new ArrayList<Map<String, String>>();
	private static ArrayList<ArrayList<Map<String, String>>> childData = new ArrayList<ArrayList<Map<String, String>>>();
	private ExpandableListView elistview;      //热门搜索列表
	SimpleExpandableListAdapter selAdapter;

	private Button btnSearch;
	private Button btnCenter;
	private EditText edtSearch;  //手动输入的输入框。

	
	//热门搜索项目
	String[] strParent = { "餐饮", "住宿", "娱乐", "购物", "出行", "文教", "服务"};
	//具体搜索项目
	String[][] strChild = { { "餐饮", "火锅", "烧烤", "海鲜", "湘菜" },
							{ "酒店", "旅馆", "农家院", "度假村", "招待所" },					
							{ "休闲", "KTV", "夜总会", "电影院" , "健身馆"},
							{ "购物", "超市", "食品", "鲜花店", "书店", "化妆品", "数码电子", "商店"}, 		
							{ "公交站", "停车场", "汽车站", "火车站", "飞机场", "地铁站"},
							{ "学校", "博物馆", "图书馆", "驾校", "教堂" },
							{ "ATM", "医院", "银行", "市政府", "公安局" }};
	
	/**
	 *  信息初始化
	*/
	public void getData() {
		for (int i = 0; i < 7; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("parend", strParent[i]);
			parentData.add(map);
		}
		for (int i = 0; i < 7; i++) {
			ArrayList<Map<String, String>> child = new ArrayList<Map<String, String>>();
			for (int j = 0; j < strChild[i].length; j++) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("child", strChild[i][j]);
				child.add(map);
			}
			childData.add(child);
		}
	}

	/**
	 *  Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		btnCenter = (Button) findViewById(R.id.btn_center);
		edtSearch = (EditText) findViewById(R.id.search_text);

		elistview = (ExpandableListView) findViewById(R.id.listview);
		// 替换ExpandableListView的打开关闭时的箭头图标 使界面更友好。
		elistview.setGroupIndicator(this.getResources().getDrawable(
				R.drawable.store_small_icon));

		
		btnSearch = (Button) findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (0 == edtSearch.getText().length()
						) {
					Toast.makeText(SearchActivity.this, "你忘记输入搜索词啦", 0).show();//未输入搜索关键词
				} else {
					//执行查询
					String typetext = edtSearch.getText().toString().trim();
					MainActivity.searchType = typetext;
					Uri data = Uri.parse(typetext);
					Intent result = new Intent(null, data);
					result.putExtra("status", "writesearch");
					setResult(RESULT_OK, result);
					Log.e("ggg", "BTN writesearch");
					finish();
				}

			}
		});

		//设置中心位置
		btnCenter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//打开设置搜索中心界面。
				EditText txtName = (EditText) findViewById(R.id.choose_center2);
				txtName.setText(btnCenter.getText());
				
				findViewById(R.id.searchView).setVisibility(
						android.view.View.INVISIBLE);
				findViewById(R.id.choose_center).setVisibility(
						android.view.View.VISIBLE);

				//确定按钮
				Button btn_OK = (Button) findViewById(R.id.btn_OK);
				btn_OK.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//手写搜索中心。
						EditText txtName = (EditText) findViewById(R.id.choose_center2);
						String text = txtName.getText().toString();
						if("当前位置".equals(text)){
							Toast.makeText(SearchActivity.this, "请输入搜索中心或指定城市", Toast.LENGTH_SHORT).show();
							return;
						}
						if("have".equals(MainActivity.searchCity)){
							MainActivity.searchCity = text;
							btnCenter.setText(text+"指定");
						}else{
							//获得手写搜索中心的，经纬度。
							MainActivity.gpSearchCenter = MapUtility.getGeoPoint(MapUtility
									.getLocationInfo(text));
							btnCenter.setText(text);
						}
						findViewById(R.id.choose_center).setVisibility(
								android.view.View.GONE);
						findViewById(R.id.searchView).setVisibility(
								android.view.View.VISIBLE);
					}
				});

				//在我的当前位置
				Button btn_centerme = (Button) findViewById(R.id.btn_me);
				btn_centerme.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//点击时，将搜索中心，这位我的当前位置、
						btnCenter.setText("当前位置");
						MainActivity.searchCity = "";
						MainActivity.gpSearchCenter = MainActivity.gpPresent;
						findViewById(R.id.choose_center).setVisibility(
								android.view.View.GONE);
						findViewById(R.id.searchView).setVisibility(
								android.view.View.VISIBLE);
					}
				});

				Button btn_centerchoose = (Button) findViewById(R.id.btn_mapchoose);
				btn_centerchoose.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//奠基石返回地图，在地图上选择搜索中心。
						btnCenter.setText("地图上选定的点");
						MainActivity.searchCity = "";
						String childText = "选定中心";
						Uri data = Uri.parse(childText);
						Intent result = new Intent(null, data);
						result.putExtra("status", "choosecenter");
						if (0 != edtSearch.getText().length()) {
							result.putExtra("search_type", edtSearch.getText().toString().trim());
						}
						setResult(RESULT_OK, result);
						Log.e("ggg", "BTN choosecenter");
						// 设置长按地图触发操作的判定条件。
						MainActivity.longpressOperation = 2;
						finish();
					}
				});
				
				Button btn_city = (Button) findViewById(R.id.btn_city);
				btn_city.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//添加指定城市内搜索的代码
						MainActivity.searchCity = "have";
						Toast.makeText(SearchActivity.this, "输入指定城市", 0).show();
					}
				});
			}
		});


		/**
		 * 监听子点打开的事件
		 */
		elistview
				.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {
						
						// 获得ITEM上的文字信息
						String childText = childData.get(groupPosition)
								.get(childPosition).get("child");
						MainActivity.searchType = childText;
						Uri data = Uri.parse(childText);
						Intent result = new Intent(null, data);
						result.putExtra("status", "search");
						setResult(RESULT_OK, result);
						finish();
						return true;
					}
				});
		
		///////判定打开…
		Bundle bunde = this.getIntent().getExtras();

		getData();
		
		selAdapter = new SimpleExpandableListAdapter(this, parentData,
				R.layout.listparent, new String[] { "parend" },
				new int[] { R.id.parend }, childData, R.layout.listchild,
				new String[] { "child" }, new int[] { R.id.child });
		selAdapter.notifyDataSetChanged();
		
		if (bunde != null) {
			btnCenter.setText(bunde.getString("center_text"));
			if(null != bunde.getString("search_type"))
				edtSearch.setText(bunde.getString("search_type"));
		} 
		elistview.setAdapter(selAdapter);
	}

	/*
	 */
	@Override
	protected void onStop() {
		super.onStop();
		parentData.clear();
		childData.clear();
	}

	/*
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}