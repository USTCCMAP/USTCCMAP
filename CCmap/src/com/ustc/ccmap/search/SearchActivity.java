/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-28
 * ����Ϊ����Ŀ�ķ����������ܡ�
 * ������������MENU��ѡ������ѯ����ɴ򿪸ǹ��ܡ�
 * �ڸù��ܹ����£������ʵ�ָ������͵Ĳ�ѯ��������
 * �ڵ�ǰλ�ò�ѯ���ڵ�ͼѡ���������Ĳ�ѯ����ָ�����в�ѯ�ȡ�
 * �����ܽ���ʹ��ExpandableListView�ǽ�����ӵ��Ѻá�
 * ���ṩ����������Ŀ�������ֱ�ӵ����Ҫ���������࣬��ɵõ���Ҫ�Ĳ�ѯ�����
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
	private ExpandableListView elistview;      //���������б�
	SimpleExpandableListAdapter selAdapter;

	private Button btnSearch;
	private Button btnCenter;
	private EditText edtSearch;  //�ֶ�����������

	
	//����������Ŀ
	String[] strParent = { "����", "ס��", "����", "����", "����", "�Ľ�", "����"};
	//����������Ŀ
	String[][] strChild = { { "����", "���", "�տ�", "����", "���" },
							{ "�Ƶ�", "�ù�", "ũ��Ժ", "�ȼٴ�", "�д���" },					
							{ "����", "KTV", "ҹ�ܻ�", "��ӰԺ" , "�����"},
							{ "����", "����", "ʳƷ", "�ʻ���", "���", "��ױƷ", "�������", "�̵�"}, 		
							{ "����վ", "ͣ����", "����վ", "��վ", "�ɻ���", "����վ"},
							{ "ѧУ", "�����", "ͼ���", "��У", "����" },
							{ "ATM", "ҽԺ", "����", "������", "������" }};
	
	/**
	 *  ��Ϣ��ʼ��
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
		// �滻ExpandableListView�Ĵ򿪹ر�ʱ�ļ�ͷͼ�� ʹ������Ѻá�
		elistview.setGroupIndicator(this.getResources().getDrawable(
				R.drawable.store_small_icon));

		
		btnSearch = (Button) findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (0 == edtSearch.getText().length()
						) {
					Toast.makeText(SearchActivity.this, "������������������", 0).show();//δ���������ؼ���
				} else {
					//ִ�в�ѯ
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

		//��������λ��
		btnCenter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//�������������Ľ��档
				EditText txtName = (EditText) findViewById(R.id.choose_center2);
				txtName.setText(btnCenter.getText());
				
				findViewById(R.id.searchView).setVisibility(
						android.view.View.INVISIBLE);
				findViewById(R.id.choose_center).setVisibility(
						android.view.View.VISIBLE);

				//ȷ����ť
				Button btn_OK = (Button) findViewById(R.id.btn_OK);
				btn_OK.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//��д�������ġ�
						EditText txtName = (EditText) findViewById(R.id.choose_center2);
						String text = txtName.getText().toString();
						if("��ǰλ��".equals(text)){
							Toast.makeText(SearchActivity.this, "�������������Ļ�ָ������", Toast.LENGTH_SHORT).show();
							return;
						}
						if("have".equals(MainActivity.searchCity)){
							MainActivity.searchCity = text;
							btnCenter.setText(text+"ָ��");
						}else{
							//�����д�������ĵģ���γ�ȡ�
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

				//���ҵĵ�ǰλ��
				Button btn_centerme = (Button) findViewById(R.id.btn_me);
				btn_centerme.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//���ʱ�����������ģ���λ�ҵĵ�ǰλ�á�
						btnCenter.setText("��ǰλ��");
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
						//���ʯ���ص�ͼ���ڵ�ͼ��ѡ���������ġ�
						btnCenter.setText("��ͼ��ѡ���ĵ�");
						MainActivity.searchCity = "";
						String childText = "ѡ������";
						Uri data = Uri.parse(childText);
						Intent result = new Intent(null, data);
						result.putExtra("status", "choosecenter");
						if (0 != edtSearch.getText().length()) {
							result.putExtra("search_type", edtSearch.getText().toString().trim());
						}
						setResult(RESULT_OK, result);
						Log.e("ggg", "BTN choosecenter");
						// ���ó�����ͼ�����������ж�������
						MainActivity.longpressOperation = 2;
						finish();
					}
				});
				
				Button btn_city = (Button) findViewById(R.id.btn_city);
				btn_city.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//���ָ�������������Ĵ���
						MainActivity.searchCity = "have";
						Toast.makeText(SearchActivity.this, "����ָ������", 0).show();
					}
				});
			}
		});


		/**
		 * �����ӵ�򿪵��¼�
		 */
		elistview
				.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {
						
						// ���ITEM�ϵ�������Ϣ
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
		
		///////�ж��򿪡�
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