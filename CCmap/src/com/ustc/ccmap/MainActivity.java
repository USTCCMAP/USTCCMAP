/**
 */

package com.ustc.ccmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.ustc.ccmap.compass.compass;
import com.ustc.ccmap.flowing.Flowing;
import com.ustc.ccmap.search.LongPressOperation;
import com.ustc.ccmap.search.MarkerItemizedOverlay;
import com.ustc.ccmap.search.MyItemizedOverlay;
import com.ustc.ccmap.search.SearchActivity;
import com.ustc.ccmap.store.Store_Add_Activity;
import com.ustc.ccmap.store.Store_Main_Activity;
import com.ustc.ccmap.tools.MapUtility;
import com.ustc.ccmap.tools.SearchDate;
import com.ustc.ccmap.tools.SmsActivity;
import com.ustc.ccmap.tools.information;
import com.ustc.ccmap.weather.WeatherActivity;

public class MainActivity extends Activity implements OnClickListener {
	public static MapView mapView;
	private MapController mapCtrl;
	public GeoPoint gpStart = null;
	public GeoPoint gpDestination = null;
	public static GeoPoint gpPresent;
	//����
	Flowing flowing;
	ApplicationInfo appInfo; 
	SearchDate searchDate;
	//����ʵ��ϵͳ��λ�ķ���
	private LocationManager locationManager = null;
	private ProgressDialog progDialog = null;
	// ��������������ͼ��	
	int[] bmpID = { R.drawable.mark_a, R.drawable.mark_b, R.drawable.mark_c,
					R.drawable.mark_d, R.drawable.mark_e, R.drawable.mark_f,
					R.drawable.mark_g, R.drawable.mark_h, R.drawable.mark_i };
	int[] bmpIDforD = {R.drawable.phone, R.drawable.from, R.drawable.to, R.drawable.search, 
			           R.drawable.send_message, R.drawable.share, R.drawable.store};
	List<information> inforList = new ArrayList<information>(); // ���ڴ洢��ѯ�����н����¼	
	int intShowPage = 0;                                        // ���ڼ�¼��ʾ��ҳ��
	public static int choosedID = -1;                           //���ڼ�¼ѡ���б��еڼ���
	public static boolean isResultDraw = false;                 //�Ƿ��ѱ��
	static boolean isStartGPReady = false;                      //�Ƿ������ó�����
	static boolean isDesGPReady = false;                        //�Ƿ�������Ŀ�ĵ�
	
	//�����������
	View resultView;
	private View loadView;
	private Button btnLoadMore;   //��һҳ
	private Button btnLoadAhead;  //��һҳ
	private TextView txtListHead; //�б�ͷ����ʾ
	ListView listView;            //�б�����б�
	
	//ָ������������
	View detailView;
	private TextView txtDetailHead;
	ListView detailListView;      //����ͷ����ʾ
	
	//������ͼ����ʵ��ͼ��
	private Drawable longPressDrawable;
	private MyItemizedOverlay LongPressOverlay;
	public final int MSG_VIEW_CENTEROK = 10001;
	public final int MSG_VIEW_POPMENU = 10002;		        //���ݿ�ݲ˵�
	public final int MSG_VIEW_TAPOTHERAREA = 10003;         //�����󵥻�����λ��
	public final int MSG_NAVIGATION_SETSTART = 10004;       //���ó����㡪������
	public final int MSG_NAVIGATION_SETDESTINATION = 10005;	//����Ŀ�ĵء�������
	public final int MSG_FORDETAIL = 10006;                 //�������ĸͼ����ʾpopView��Ӧ��¼����
	
	private View popView;       //���ݿ�ݲ˵�����
	private View popDetailView;
	
	private List<Overlay> mapOverlays;           //��ͼ����ͼ��
	public static GeoPoint gpSearchCenter = null;// ��������λ��
	public static GeoPoint longpressGeoPoint = null;
	public static String searchType = "";
	public static String searchCity = "";
	
	public static int longpressOperation = 1;
	//������ͼִ�еĲ����� 1Ϊ��ʾ���ݲ˲˵� 2Ϊѡ���������� 3Ϊѡ������� 4Ϊѡ��Ŀ�ĵ�
	
	/**
	 * �˵���	
	 */
    protected static final int MENU_FUNC1 = Menu.FIRST;		//����һ ָ����    
    protected static final int MENU_FUNC2 = Menu.FIRST+1;	//���ܶ� ����SDK���õĵ�ͼ���е���    
    protected static final int MENU_FUNC3 = Menu.FIRST+2;	//������ ������Χ�Ĳ͹ݵȸ����ص�
    protected static final int MENU_FUNC4 = Menu.FIRST+3;	//������  ����ղ�
    protected static final int MENU_FUNC5 = Menu.FIRST+4;	//������ �˳�
    protected static final int MENU_FUNC6 = Menu.FIRST+5;	//������ ��������Ԥ����ѯ
    protected static final int MENU_FUNC7 = Menu.FIRST+6;	//������  ��ǰλ��
    protected static final int MENU_FUNC8 = Menu.FIRST+7;	//���� �л�����
    protected static final int MENU_FUNC9 = Menu.FIRST+8;	//���ܾ�  ģʽ�л�
    protected static final int MENU_FUNC10 = Menu.FIRST+9;	//����ʮ  ����
    
    /**
     * ��������
     */
	@SuppressLint("NewApi") 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DemoApplication app = (DemoApplication)this.getApplication();
	    if (app.mBMapManager == null) {
	        app.mBMapManager = new BMapManager(getApplicationContext());
	        /**
	         * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
	         */
	        app.mBMapManager.init(new DemoApplication.MyGeneralListener());
	    }
	    
	    if (android.os.Build.VERSION.SDK_INT > 9) {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
	    }
        
        setContentView(R.layout.activity_main);
        
               
        initView();  //��ʼ������     
        flowing();   //��ʾ��ʱ����
    
//		Drawable drawable = getResources().getDrawable(R.drawable.mark_a);
/////////////
//MarkerItemizedOverlay itemizedoverlay = new MarkerItemizedOverlay(drawable, this, mHandler, mapView);
//		OverlayItem overlayitem = new OverlayItem(gpPresent, "", "information");
//		itemizedoverlay.addOverlay(overlayitem);
//		//Overlays.add(itemizedoverlay);
//		mapOverlays.add(itemizedoverlay);
//		mapView.refresh();
	}
    
	/**
	 * ���ؼ�ȷ���Ƿ��˳���ͼ	 
	 */
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {                
           if(keyCode == KeyEvent.KEYCODE_BACK){
      	   
        	   function5();
               return true;
            }else{                
                   return super.onKeyDown(keyCode, event);
            }
    }
	
	/**
	 * ��ʾ��������
	 */
	@SuppressLint("HandlerLeak")
	void flowing(){
		try {
            PackageManager pm = getPackageManager();
            //��øó����UID ���ݰ�����ó����UID
            appInfo = pm.getApplicationInfo("com.ustc.ccmap", PackageManager.GET_ACTIVITIES);
            flowing = new Flowing(appInfo);
            flowingHandler.postDelayed(runnable, 1000 * 6);
        } catch (NameNotFoundException e) {
        	new AlertDialog.Builder(this)
			.setTitle("��ܰ��ʾ")
			.setMessage("�����ǻ��Ͳ�����������ʾ������Ϣ������հ�װ˵����װ��")
			.setNegativeButton("ȷ��", null)
			.show();
        }
	}
	
	/**
	 * ����������ʵ��Handler
	 */
    private Handler flowingHandler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
        	TextView txtFlowing= (TextView)findViewById(R.id.flowing);
        	txtFlowing.setText(flowing.update());
        	flowingHandler.postDelayed(this, 1000 * 3);//���3��ˢ��һ��������ʾ����   
        	}
    };
    
    /**
     * ��ʼ�����ݿ�ݲ˵�����   
     */
	private void initPopView() {
		
		if (null == popView) {
			popView = getLayoutInflater().inflate(R.layout.overlay_popup, null);
			mapView.addView(popView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, gpPresent,
					MapView.LayoutParams.BOTTOM_CENTER));
			popView = (View) findViewById(R.id.pop_MenuView);
			popView.setVisibility(View.GONE);
			//Ϊ�����沿�ְ�ť��Ӽ��� ��Щ�����ض�������ְ�ť������û��һһ��ʼ������ʱ���	
			findViewById(R.id.btn_pop_search).setOnClickListener(this);
			findViewById(R.id.btn_pop_from).setOnClickListener(this);
			findViewById(R.id.btn_pop_to).setOnClickListener(this);
			findViewById(R.id.btn_pop_new).setOnClickListener(this);
		}
	}

	/**
	 * mHandler�������������Ϣ�������������Ƽ����¼�
	 **/
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			//������ݴ�����ĵ�ͼ��ʹ���ݴ��ں�ͼ����ʧ		
			case MSG_VIEW_TAPOTHERAREA:{
				findViewById(R.id.pop_choosecenter).setVisibility(View.INVISIBLE);
				findViewById(R.id.pop_clear).setVisibility(View.INVISIBLE);
				findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
				popView.setVisibility(View.INVISIBLE);
				mapOverlays.remove(LongPressOverlay);
				Log.e("clear", "clear");
				mapView.invalidate();
			
			}
				break;
		    //������ͼѡ��������ִ�и����
			case MSG_NAVIGATION_SETSTART:{
				isStartGPReady = true;
				Log.e("��γ", gpStart.toString());
				mapOverlays.remove(LongPressOverlay);
				mapView.invalidate();
				function2();
			}
			break;
			//������ͼѡ��Ŀ�ĵغ�ִ�и����
			case MSG_NAVIGATION_SETDESTINATION:{
				isDesGPReady = true;
				mapOverlays.remove(LongPressOverlay);
				mapView.invalidate();
				function2();
			}
				break;
			//����ʱ����ͼѡ���������ĺ�ִ�иú���
			case MSG_VIEW_CENTEROK:{
				mapOverlays.remove(LongPressOverlay);
				mapView.invalidate();
				function3(false);
			}
			break;
			//��ʾ�����˵�����
			case MSG_VIEW_POPMENU:{
				OverlayItem overlayitem = new OverlayItem(longpressGeoPoint, "", "");
				if (LongPressOverlay.size() > 0)
					LongPressOverlay.removeOverlay(0);
				popView.setVisibility(View.VISIBLE);
				LongPressOverlay.addOverlay(overlayitem);
				
				if(mapOverlays.contains(LongPressOverlay)){
					mapOverlays.remove(LongPressOverlay);
				}
				Log.e("dd", "mapOverlays.add(LongPressOverlay);");
				mapOverlays.add(LongPressOverlay);
				mapView.invalidate();
				mapView.refresh();
			}
				break;
			//�������ͼ�꣬��ʾ���ݴ���		
				case MSG_FORDETAIL:
				if(null == popDetailView){
					popDetailView = getLayoutInflater().inflate(R.layout.detail_popup, null);
					mapView.addView(popDetailView, new MapView.LayoutParams(
						MapView.LayoutParams.WRAP_CONTENT,
						MapView.LayoutParams.WRAP_CONTENT, inforList.get(choosedID).getPostion(),
						0, -50, MapView.LayoutParams.BOTTOM_CENTER));
				}else{
					MapView.LayoutParams params = (MapView.LayoutParams) popDetailView
							.getLayoutParams();
					params.point = inforList.get(choosedID).getPostion();
					params.width = MapView.LayoutParams.WRAP_CONTENT;
					params.height = MapView.LayoutParams.WRAP_CONTENT;
					mapView.updateViewLayout(popDetailView, params);
				}
					popDetailView.setVisibility(View.VISIBLE);	
					TextView text = (TextView) findViewById(R.id.pop_detail);
					SpannableString txt = new SpannableString(
							inforList.get(choosedID).getName()+"\n" + "����鿴�������飺");
					txt.setSpan(new ForegroundColorSpan(Color.RED), txt.length()-10,
							txt.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					text.setText(txt);
					RelativeLayout button = (RelativeLayout) findViewById(R.id.pop_Detail);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							//����鿴������Ϣ
							Log.e("another", "another");
							popDetailView.setVisibility(View.GONE);
							fillDetail(choosedID);
							showView(detailView);
						}
					});
				break;
			default:
					break;
			}
		}
	};

	
	/**
	 * ��ͼģʽ����
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
	public void handleMessage(Message msg) {//����ѡ��ı��ͼ����
		switch (msg.what) {
		case 0: //��ͨģʽ1
			mapView.setTraffic(true);
			mapView.setSatellite(false);
			break;
		case 1: //��ͨģʽ2
			mapView.setTraffic(false);
			mapView.setSatellite(false);
			break;
		case 2: //����ģʽ1
			mapView.setSatellite(true);
			break;
		case 3: //����ģʽ2
			mapView.setSatellite(true);
			mapView.setTraffic(true);
			break;
		}
	}

	};
	
	
	/**
	 * startActivityForResult(intent, 1)�������Է����򿪵�CTIVITY���ؽ���ļ���	 
	 * * ����һ������setresult()��finish������ִ�иú���
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			//�ڵ�ͼ�ϱ���Լ��鵽���ղ�	
			if(data.getExtras().getString("status").equalsIgnoreCase("ICON")){
				Bundle extras = data.getExtras();  
				Log.e("MAIN", extras.getInt("lat")+"");
				Log.e("MAIN", extras.getInt("lng")+"");
				Log.e("MAIN", "���ղ��б��õ�������");
				GeoPoint stored = new GeoPoint((extras.getInt("lat")), extras.getInt("lng"));
				isDesGPReady = true;
				gpDestination = stored;
				function2();
				
			    Drawable drawable = this.getResources().getDrawable(R.drawable.mark_s);
			    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			    MarkerItemizedOverlay itemizedoverlay = new MarkerItemizedOverlay(drawable, this, mHandler, mapView);
			    itemizedoverlay.addOverlay(new OverlayItem(stored, null, "stored"));
			    mapOverlays.add(itemizedoverlay);
			    isResultDraw = true;
			}else{
				// �����ͼѡ����������
				if (data.getExtras().getString("status").equalsIgnoreCase("choosecenter")) {
					Toast.makeText(MainActivity.this, "������ͼѡ���������", Toast.LENGTH_SHORT).show();
				    if(null != data.getExtras().getString("search_type"))
						 	searchType = data.getExtras().getString("search_type");
				} 
				else 
					showList();
					Log.e("EEEEEEEEEEE", "����" + searchType + "����" + gpSearchCenter.toString());
			}
		}

	}
    
	/**
	 * ��ʼ����������б�	
	 */
	public void initListView() {
		resultView = findViewById(R.id.list);
		listView = (ListView) findViewById(R.id.search_List);
		loadView = getLayoutInflater().inflate(R.layout.load, null);
		btnLoadMore = (Button) loadView.findViewById(R.id.load_NextButton);
		btnLoadMore.setOnClickListener(this);
		btnLoadAhead = (Button) loadView.findViewById(R.id.load_AheadButton);
		listView.addFooterView(loadView);
		btnLoadAhead.setOnClickListener(this);
		txtListHead = (TextView) findViewById(R.id.detail_head);
		findViewById(R.id.btn_toMap).setOnClickListener(this);
		findViewById(R.id.btn_research).setOnClickListener(this);
		findViewById(R.id.btn_toList).setOnClickListener(this);
		txtListHead = (TextView) findViewById(R.id.List_head);
	}
	
	/**
	 * ��ʼ�����Һ� ��ʾָ���ص������б�
	 */
	public void initDetailView() {
		detailView = findViewById(R.id.detail);
		detailListView = (ListView)findViewById(R.id.detail_List);
		txtDetailHead = (TextView) findViewById(R.id.detail_head);
		findViewById(R.id.btn_toMap2).setOnClickListener(this);
		findViewById(R.id.btn_toList2).setOnClickListener(this);
	}
	
	ArrayList<HashMap<String, Object>> info_Listed = new ArrayList<HashMap<String, Object>>();
	SimpleAdapter listAdapter = null;
	/**
	 * ���������Ľ������䵽����б�	
	 */
	public void fillList(List<information> list, int page) {

		int numPage = (0 == inforList.size() % 9) ? inforList.size() / 9 : inforList.size() / 9 + 1;
		// �б�ͷ����ʾ
		txtListHead.setText("��" + inforList.size() + "��(" + numPage + "ҳ)"
				+ "�������" + "\n��" + (int) (page + 1) + "ҳ");
		
		// �жϰ�ť�Ƿ���ʾ
		if (page != numPage -1)
			btnLoadMore.setVisibility(android.view.View.VISIBLE);
		else
			btnLoadMore.setVisibility(android.view.View.INVISIBLE);
		if (page != 0)
			btnLoadAhead.setVisibility(android.view.View.VISIBLE);
		else
			btnLoadAhead.setVisibility(android.view.View.INVISIBLE);
		
		//ÿҳ��ʾ�ļ�¼�����9��
		int num = 9;
		if (page == numPage -1)
			num = inforList.size() % 9;
		info_Listed.clear();
		
		// ����б���Ϣ
		for (int i = page * 9, j = 0; i < page * 9 + num; i++, j++) {
			HashMap<String, Object> infoShow = new HashMap<String, Object>();
			infoShow.put("img", bmpID[j]);
			infoShow.put("name", list.get(i).getName());
			infoShow.put("address", list.get(i).getAddress());
			String strDistance= list.get(i).getDistance()+"";
			if(list.get(i).getDistance() < 1000)
				infoShow.put("distance", "   "+strDistance.substring(0, strDistance.indexOf("."))+"m");
			else{
				strDistance = list.get(i).getDistance()/1000 + "";
				infoShow.put("distance", "   "+strDistance.substring(0, strDistance.indexOf(".")+3)+"km");
			}
			info_Listed.add(infoShow);
		}
		// ��ʾ��Ϣ
		listAdapter = new SimpleAdapter(this, info_Listed, 		// ������Դ
				R.layout.info_list,								
				new String[] { "img", "name", "address", "distance" },
				new int[] { R.id.img, R.id.name, R.id.address, R.id.distance });  // �ֱ��Ӧview ��id
		((ListView) findViewById(R.id.search_List)).setAdapter(listAdapter);
	}
	
	ArrayList<HashMap<String, Object>> detail_Listed = new ArrayList<HashMap<String, Object>>();
	SimpleAdapter detailAdapter = null;
	/**
	 * ���ĳ�������б�
	 */
	public void fillDetail(int id) {
		
		int infoId = 9 * intShowPage +id;
		//������ǵ�һ����ʾ��ʾ�����б����г�ʼ��		
		if(!detail_Listed.isEmpty()){
			txtDetailHead.setText(inforList.get(infoId).getName()+"\n��ַ:"+inforList.get(infoId).getAddress());
			HashMap<String, Object> infoDetail = new HashMap<String, Object>();
			infoDetail.put("img", bmpID[0]);
			infoDetail.put("str", inforList.get(infoId).getTel());
			detail_Listed.set(0, infoDetail); 
			detailAdapter.notifyDataSetChanged();
			detailView.invalidate();
			return;
		}
		
	String[] detail = {"", "�������", "������ȥ", "�ܱ�����", "���Ͷ���", "�������", "����ղ�"};
		detail[0] = inforList.get(infoId).getTel();
		// �б�ͷ����ʾ ����+��ַ
		txtDetailHead.setText(inforList.get(infoId).getName()+"\n"+inforList.get(infoId).getAddress());
		// �����Ϣ
		for(int i = 0; i < detail.length; i++){
			HashMap<String, Object> infoDetail = new HashMap<String, Object>();
			infoDetail.put("img", bmpIDforD[i]);
			infoDetail.put("str", detail[i]);
			detail_Listed.add(infoDetail);
		}
			
		// ��ʾ��Ϣ
		detailAdapter = new SimpleAdapter(this, detail_Listed, 
				R.layout.detail_list,		
				new String[] { "img", "str"},
				new int[] {R.id.detailimg, R.id.detailname});
		((ListView) findViewById(R.id.detail_List)).setAdapter(detailAdapter);
	}

	
	/**
	 * ��ACTIVITYҪ��ʵ�Ľ��� ��ΪҪ�����ڵ�ͼ����������б������б��л���������ͬһ����
	 * @param view Ҫ��ʾ��VIEW
	 */
	private void showView(View view){
		resultView.setVisibility(View.GONE);
		detailView.setVisibility(View.GONE);
		mapView.setVisibility(View.GONE);
		view.setVisibility(View.VISIBLE);
		if(isResultDraw)
			findViewById(R.id.btn_toList).setVisibility(android.view.View.VISIBLE);
	}
	
	/**
	 * ��ʾ����б� �����б���ʾ�͵�ͼ����ʾͼ��
	 */
	public void showList(){
		searchDate = new SearchDate(mapView, MainActivity.this, mHandler);
		try{
			inforList = searchDate.getPlaces();
		}catch (Exception e) {
			Log.e("MainActivity", e.toString());
        	new AlertDialog.Builder(this)
			.setTitle("��ܰ��ʾ")
			.setMessage("�ܱ�Ǹ�������������ԭ����ʱ�޷����ң����Ժ�����")
			.setNegativeButton("ȷ��", null)
			.show();
        	return;
		}
		if(0 == inforList.size()){
			Toast.makeText(MainActivity.this, "δ��������ؼ�¼������ؼ��ʺ���������", Toast.LENGTH_SHORT).show();
			return;
		}
		searchDate.Picture(intShowPage, inforList);//����ʾ�������ڵ�ͼ��
		showView(resultView);					 
		// ���õ�ͼ���� ����ʾ�б�		
		fillList(inforList, intShowPage);
		//����б���� �����ʾ�������
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.getItemAtPosition(arg2);
				fillDetail(arg2);
				choosedID = arg2;
				showView(detailView);
			}
		});
		
		/**
		 * ���������� ���ִ����Ӧ����
		 */
		detailListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.getItemAtPosition(arg2);

				int infoId = 9 * intShowPage + choosedID;
				switch (arg2) {
				case 0:
					//�绰ԤԼ
					Log.e("ITEMDDDDDDDDDDDDDD", "�绰ԤԼ");
					Intent intent_call = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+inforList.get(choosedID).getTel()));
					startActivity(intent_call);
					break;
				case 1:
					//���������			
					Log.e("ITEMDDDDDDDDDDDDDD", "���������");
					gpStart = inforList.get(infoId).getPostion();
					isStartGPReady = true;
					findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
					popView.setVisibility(View.INVISIBLE);
					mapOverlays.remove(LongPressOverlay);
					mapView.invalidate();
					function2();
					break;
				case 2:
					//������ȥ
					Log.e("ITEMDDDDDDDDDDDDDD", "������ȥ");
					gpDestination = inforList.get(infoId).getPostion();
					isDesGPReady = true;
					findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
					popView.setVisibility(View.INVISIBLE);
					mapOverlays.remove(LongPressOverlay);
					mapView.invalidate();
					function2();
					break;
				case 3:
					//�ܱ�����
					Log.e("ITEMDDDDDDDDDDDDDD", "�ܱ�����");
					gpSearchCenter = inforList.get(choosedID).getPostion();
					function3(true);
					break;
				case 4:
					//���Ͷ���
					Log.e("ITEMDDDDDDDDDDDDDD", "���Ͷ���");
					if("�޼�¼".equals(inforList.get(choosedID).getTel())){ //�޺��봦��	
						Toast.makeText(MainActivity.this, "δ���ҵ��õص绰", Toast.LENGTH_SHORT).show();
						break;
					}
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, SmsActivity.class);
					Bundle bundle = new Bundle();
					//��Ϣ���ݵĳ�ʼ��
					String content_message = "����:"+inforList.get(choosedID).getName()
							+"    ��ַ��"+inforList.get(choosedID).getAddress()
							+"    �绰: "+inforList.get(choosedID).getTel();
					bundle.putString("WEATHER_INFO_MESSAGE", content_message);
					intent.putExtras(bundle); 
					startActivity(intent);
					
					break;
			case 5:
					//�������
					Log.e("ITEMDDDDDDDDDDDDDD", "�������");
					String content = "����:"+inforList.get(choosedID).getName()+"    ��ַ:"
					+inforList.get(choosedID).getAddress()+"    �绰:"+inforList.get(choosedID).getTel();
					Intent intent_web=new Intent(Intent.ACTION_SEND);  
					intent_web.setType("text/plain");  //�������������			
					intent_web.putExtra(Intent.EXTRA_SUBJECT, "�������");  //����
					intent_web.putExtra(Intent.EXTRA_TEXT,content  );  //����
					startActivity(Intent.createChooser(intent_web, "ѡ������"));  //Ŀ��Ӧ��ѡ��Ի���ı���
					break;
				case 6:
					//����ղ�
					Log.e("ITEMDDDDDDDDDDDDDD", "����ղ�");
					Intent storeActivity = new Intent(MainActivity.this, Store_Add_Activity.class);
			        storeActivity.putExtra("fromMainActivity", "yes");
			        storeActivity.putExtra("lat", inforList.get(choosedID).getPostion().getLatitudeE6());
			        storeActivity.putExtra("lng", inforList.get(choosedID).getPostion().getLongitudeE6());
			        storeActivity.putExtra("name", inforList.get(choosedID).getName());
			        storeActivity.putExtra("address", inforList.get(choosedID).getAddress());
			        startActivity(storeActivity); 
					break;
				default:
					break;
				}
			}
		});
		
	}

	/**
	 * Ϊ������Ӽ����İ�ť ���Է���ʵ��
	 */
	@Override
	public void onClick(android.view.View v) {
		switch (v.getId()) {
		case R.id.btn_toMap:   //���ص�ͼ
		case R.id.btn_toMap2:
			showView(mapView);
			break;
		case R.id.btn_toList:  //�����б�
		case R.id.btn_toList2:
			showView(resultView);
			break; 
		case R.id.load_NextButton: //��һҳ			
			intShowPage++;
			fillList(inforList, intShowPage);
			searchDate.Picture(intShowPage, inforList);
			break;
		case R.id.load_AheadButton://��һҳ			
			intShowPage--;
			fillList(inforList, intShowPage);
			searchDate.Picture(intShowPage, inforList);
			break;
		case R.id.btn_research:    //��������
			function3(true);
			break;
		case R.id.btn_pop_search: //���ݡ�������
			Log.e("popmenu", "�ܱ�����");
			gpSearchCenter = longpressGeoPoint;
			function3(false);
			findViewById(R.id.btn_pop_new).setVisibility(View.VISIBLE);
			findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
			popView.setVisibility(View.INVISIBLE);
			mapOverlays.remove(LongPressOverlay);
			mapView.invalidate();
			break;
		case R.id.btn_pop_from:  //���ݡ������������
			Log.e("popmenu", "���������");
			gpStart = longpressGeoPoint;
			isStartGPReady = true;
			longpressOperation = 1;
			findViewById(R.id.btn_pop_new).setVisibility(View.VISIBLE);
			findViewById(R.id.pop_choosefrom).setVisibility(View.INVISIBLE);
			findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
			popView.setVisibility(View.INVISIBLE);
			mapOverlays.remove(LongPressOverlay);
			mapView.invalidate();
			function2();
			break;
		case R.id.btn_pop_to:      //���ݡ���������ȥ
			Log.e("popmenu", "������ȥ");
			gpDestination = longpressGeoPoint;
			//////////
			Log.e("pop��γ:", gpDestination.toString());
			isDesGPReady = true;
			longpressOperation = 1;
			findViewById(R.id.btn_pop_new).setVisibility(View.VISIBLE);
			findViewById(R.id.pop_chooseto).setVisibility(View.INVISIBLE);
			findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
			popView.setVisibility(View.INVISIBLE);
			mapOverlays.remove(LongPressOverlay);
			mapView.invalidate();
			function2();
			break;
		case R.id.btn_pop_new:    //���ݡ����ղ�
			Log.e("popmenu", "�ղ�");
			findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
			popView.setVisibility(View.INVISIBLE);
			mapOverlays.remove(LongPressOverlay);
			mapView.invalidate();
			Intent storeActivity = new Intent(MainActivity.this, Store_Add_Activity.class);
	        storeActivity.putExtra("fromMainActivity", "yes");
	        storeActivity.putExtra("lat", longpressGeoPoint.getLatitudeE6());
	        storeActivity.putExtra("lng", longpressGeoPoint.getLongitudeE6());
	        startActivity(storeActivity);  
			break;
		case R.id.whereAmI:    //��ǰλ��
			Log.e("�ҵ�", "��ǰλ��");
			whereAmI();
			break;
		default:
			break;
		}
	}
    
    
	/**
	 * �������ʱĬ�ϵ�ͼ����	 
	 */
    public void initView(){
    	longpressOperation = 1;
    	isResultDraw = false;
    	isStartGPReady = false;
    	isDesGPReady = false;
    	
  		mapView = (MapView) findViewById(R.id.map);
		mapView.setBuiltInZoomControls(true);
		mapCtrl = mapView.getController();
		mapCtrl.setZoom(14);
		findViewById(R.id.whereAmI).setOnClickListener(this);
		
	/**
	 * ��λ����ǰ�Ĵ���
	 */
	progDialog = ProgressDialog.show(MainActivity.this,
				"", "���������Ժ󡭡�", true, false);
		new Thread() {
			@Override
			public void run() {
				try {
					locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					Location nowloc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if(nowloc == null){
						nowloc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					}
					if (nowloc != null) {
						Log.e("AAAAAAAAAAAAAAAAAAAAAAAAAAAA", "latitude: " + nowloc.getLatitude());
						Log.e("AAAAAAAAAAAAAAAAAAAAAAAAAAAA", "latitude: " + nowloc.getLongitude());
						gpPresent = new GeoPoint((int) (nowloc.getLatitude() * 1E6),	//�������ԵĴ���ƫ����ܽ����������*******
				                (int) (nowloc.getLongitude()* 1E6));
					}
					else{
						gpPresent = new GeoPoint((int) (39.904214 * 1E6),//Ĭ�Ͻ�������������Ϊ����
								(int) (116.407413 * 1E6));
					}
					mapCtrl.animateTo(gpPresent);
					progDialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(MainActivity.this, "��ͼ���س������Ժ�����", Toast.LENGTH_LONG).show();
				}
			}
		}.start();
		
		initListView();
		initPopView();
		initDetailView();
		//����popView��ʾ��ͼƬ����
		longPressDrawable = getResources().getDrawable(R.drawable.point_start);
		LongPressOverlay = new MyItemizedOverlay(longPressDrawable, this,
				mapView, popView, mapCtrl);
		//LongPressOverlay.
		
		mapOverlays = mapView.getOverlays();
//		//mapOverlays.add(LongPressOverlay);
//		Log.e("MainActivity",mapOverlays.size()+"");
//		mapOverlays.add(new LongPressOperation(this, mapView, popView, mHandler,
//				mapCtrl)); //������ͼ��Ӧͼ��
//		Log.e("MainActivity",mapOverlays.size()+"");
//		mapView.refresh();
		
		OnLongClickListener ss = new LongPressOperation(this, mapView, popView, mHandler,mapCtrl, getResources().getDrawable(R.drawable.point_start));
		mapView.setOnLongClickListener(ss);
		mapView.setOnTouchListener((OnTouchListener) ss);
		//mapView.setOnGenericMotionListener((OnGenericMotionListener) ss);
		
		//�ҵĵ�ǰλ��ͼ��
		   Drawable drawable = this.getResources().getDrawable(R.drawable.my_location);
		   drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		   MarkerItemizedOverlay itemizedoverlay = new MarkerItemizedOverlay(drawable, this, mapView);
	       itemizedoverlay.addOverlay(new OverlayItem(gpPresent, null, null));
		   mapOverlays.add(itemizedoverlay);
		   mapView.refresh();
    }
    
    

    /**
     * ����˵��ĺ���
     */
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_FUNC1, 0,"ָ����").setIcon(R.drawable.menu_compass);          //ָ����    	
    	menu.add(0, MENU_FUNC2, 0,"����").setIcon(android.R.drawable.ic_menu_compass); //����
    	menu.add(0, MENU_FUNC3, 0,"�����ѯ").setIcon(R.drawable.seach_type);	          //�����ѯ
    	menu.add(0, MENU_FUNC4, 0,"�ҵ��ղ�").setIcon(android.R.drawable.ic_menu_save); //�ҵ��ղ�
    	menu.add(0, MENU_FUNC6, 0,"������ѯ").setIcon(R.drawable.weather);              //������ѯ
    	menu.add(0, MENU_FUNC7, 0,"��ǰλ��");   //��ǰλ��
    	menu.add(0, MENU_FUNC8, 0,"�л�����");   //�л�����
    	menu.add(0, MENU_FUNC9, 0,"ģʽ�л�");   //ģʽ�л�
    	menu.add(0, MENU_FUNC10, 0,"����");     //����
    	menu.add(0, MENU_FUNC5, 0,"�˳�");      //�˳�
    	return super.onCreateOptionsMenu(menu);
    }
    
    
    /**
     * ѡ��˵�ʱִ�еĶ���
     */
    public boolean onOptionsItemSelected(MenuItem item) {
    	longpressOperation = 1;
		switch (item.getItemId()) {
		case MENU_FUNC1: //ָ����		
			function1(); 
			break;
		case MENU_FUNC2: //����
			//��ʼ�������㡢Ŀ�ĵؾ���
			isStartGPReady = false;
			isDesGPReady = false;
			findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
			function2();
			break;
		case MENU_FUNC3: //��������
			function3(true);
			break;
		case MENU_FUNC4: //�ҵ��ղ�
			function4();
			break;
		case MENU_FUNC5: //�˳�����
			function5();
			break;
		case MENU_FUNC6:
			function6(); //������ѯ
			break;
		case MENU_FUNC7: //��ǰλ��
			whereAmI();
			break;
		case MENU_FUNC8: //�л�����
			function8();
			break;
		case MENU_FUNC9: //�л�ģʽ
			function9();
			break;
		case MENU_FUNC10://����
			function10();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    

    /**
     * ���ܣ�ָ����    
     */
    public void function1(){
    	Intent theActivity = new Intent(this, compass.class);
        startActivity(theActivity);
    }
    
    
	/**
	 * ���ܣ��������е���  
	 */
	public void function2(){
		final AlertDialog Dialog;
		LayoutInflater factory = LayoutInflater.from(MainActivity.this);
		final View locationView = factory.inflate(R.layout.navigation_actiivity, null);
		final EditText start = (EditText) locationView
				.findViewById(R.id.startplace);
		final EditText destination = (EditText) locationView
				.findViewById(R.id.endplace);
		
		if(isStartGPReady) start.setText("��ͼѡ�е�");
		if(isDesGPReady) destination.setText("��ͼѡ�е�");
		
		Dialog =new AlertDialog.Builder(this).setIcon(R.drawable.navigation_title).setTitle("����")
				.setView(locationView).setPositiveButton("�ݳ�",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
							//ѡ���˳����㣬��δ�ı����е�����
							if(!(isStartGPReady && "��ͼѡ�е�".equals(start.getText().toString().trim()))) 
								gpStart =  MapUtility.getGeoPoint(MapUtility.getLocationInfo(start.getText().toString())); // ��������
							if(!(isDesGPReady && "��ͼѡ�е�".equals(destination.getText().toString().trim())))
								gpDestination = MapUtility.getGeoPoint(MapUtility.getLocationInfo(destination.getText().toString())); // ��������
							//��ʼ��������Ϊ�´β�ѯ��׼��
							isStartGPReady = false;
							isDesGPReady = false;
							Log.e("MMMMMM", gpStart.toString()+gpDestination.toString());
							//���濪ʼ��ѯ·��
							StringBuilder params = new StringBuilder().append("&dirflg=");
							params.append("d");
							try {
								getMap(params.toString());
							} catch (Exception e) {
								Log.e("����", "ERROR");
							}
						}
					})
					.setNegativeButton("�˳�",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
							//ѡ���˳����㣬��δ�ı����е�����
							if(!(isStartGPReady && "��ͼѡ�е�".equals(start.getText().toString().trim()))) 
								gpStart =  MapUtility.getGeoPoint(MapUtility.getLocationInfo(start.getText().toString())); // ��������
							if(!(isDesGPReady && "��ͼѡ�е�".equals(destination.getText().toString().trim())))
								gpDestination = MapUtility.getGeoPoint(MapUtility.getLocationInfo(destination.getText().toString())); // ��������
							MainActivity.isStartGPReady = false;
							MainActivity.isDesGPReady = false;
							//���濪ʼ��ѯ·��
							StringBuilder params = new StringBuilder().append("&dirflg=");
							params.append("r");
							getMap(params.toString());
							}
						} )
						.setNeutralButton("����", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
							//ѡ���˳����㣬��δ�ı����е�����
							if(!(isStartGPReady && "��ͼѡ�е�".equals(start.getText().toString().trim()))) 
								gpStart =  MapUtility.getGeoPoint(MapUtility.getLocationInfo(start.getText().toString())); // ��������
							if(!(isDesGPReady && "��ͼѡ�е�".equals(destination.getText().toString().trim())))
								gpDestination = MapUtility.getGeoPoint(MapUtility.getLocationInfo(destination.getText().toString())); // ��������
							MainActivity.isStartGPReady = false;
							MainActivity.isDesGPReady = false;
							//���濪ʼ��ѯ·��
							StringBuilder params = new StringBuilder().append("&dirflg=");
							params.append("w");
							getMap(params.toString());
							}
						} ).show();
		
		Dialog.setOnKeyListener(new OnKeyListener(){
           @Override
           public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
               if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            	    longpressOperation = 1;
			   		isStartGPReady = false;
			   		isDesGPReady = false;
			   		Log.e("����", "����");
			   		dialog.dismiss();
               }
               return false;
           }
       });
		
		final Button chooseFrom = (Button) locationView
				.findViewById(R.id.btn_find_geo_on_map1);
		chooseFrom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "������ͼѡ�������", Toast.LENGTH_SHORT).show();
				longpressOperation = 3;
				Log.e("����", "������");
				//�����Ի���
			    Dialog.dismiss();
			}
		});
		final Button chooseTo = (Button) locationView
				.findViewById(R.id.btn_find_geo_on_map2);
		chooseTo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "������ͼѡ��Ŀ�ĵ�", Toast.LENGTH_SHORT).show();
				Log.e("����", "�յ�");
				longpressOperation = 4;
				Dialog.dismiss();
			}
		});
   }
	
	
	 /**
     * ���ܣ���������
	 * @param isNewOpen ����Ϊ�Ƿ��´�����ACTIVITY
	 */
	private void function3(boolean isNewOpen){
	 	Intent intent = new Intent();
		intent.setClass(this, SearchActivity.class);
		// ���´�������ʼ����������Ϊ�ҵĵ�ǰλ��
		if(isNewOpen){
			intShowPage = 0;
			choosedID = -1;
			searchType = "";
			searchCity = "";
			gpSearchCenter = gpPresent;
		}
		else{
			// �ٴδ�
			Bundle bundle = new Bundle();
		    bundle.putString("center_text", "��ͼ��ѡ�еĵ�");
		    if(!"".equals(searchType))
				 bundle.putString("search_type", searchType);
		    intent.putExtras(bundle); 
		}
		startActivityForResult(intent, 1);
	}
    
	/**
	 * ���ܣ��ҵ��ղ�
	 */
	public void function4(){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, Store_Main_Activity.class);
		startActivityForResult(intent, 1);
	}
	
    /**
     * ���ܣ��˳�����
     */
	public void function5(){
        new AlertDialog.Builder(this)
        .setIcon(R.drawable.softicon)
        .setTitle(R.string.app_name)
        .setMessage("ȷ��Ҫ�˳���ͼ��")
        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {//ȡ���˳�
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
        })
        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {//�˳�
                public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                }
        }).show();
	}
	
	
	 /**
     * ���ܣ�������ѯ
     */
    public void function6(){
    	LayoutInflater factory = LayoutInflater.from(MainActivity.this);
		View locationView = factory.inflate(R.layout.check_weather, null);
		final EditText place = (EditText) locationView
				.findViewById(R.id.place1);
			new AlertDialog.Builder(this).setIcon(R.drawable.weather_icon).setTitle("������ѯ").setView(
					locationView).setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if(place.getText().length() == 0){
								Toast.makeText(MainActivity.this, "�������ѯ�ĵ���", Toast.LENGTH_SHORT).show();
							}
							else{
					  			progDialog = ProgressDialog.show(MainActivity.this,
										"","���������Ժ󡭡�" , true, false);
								new Thread() {
									@Override
									public void run() {
										try {
											Intent intent = new Intent();
								  			intent.setClass(MainActivity.this, WeatherActivity.class);
								  			Bundle bundle = new Bundle();
								  			bundle.putString("KEY_PLACE", place.getText().toString().trim());
								  			intent.putExtras(bundle);
											startActivity(intent);
											progDialog.dismiss();
										} catch (Exception e) {
											e.printStackTrace();
											Toast.makeText(MainActivity.this, "����", Toast.LENGTH_LONG).show();
										}
									}

								}.start();
							}
							}
						}).show();
    }
   
    
    /**
     * ���ܣ���ǰλ��
     */
   private void whereAmI() {
		new Thread() {
			@Override
			public void run(){
				try{
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1, mLocationListener);
					mapCtrl.animateTo(gpPresent);
					progDialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(MainActivity.this, "��ͼ���س������Ժ�����", Toast.LENGTH_LONG).show();
				}
			}
		}.start();
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1, mLocationListener);
		mapCtrl.animateTo(gpPresent);
       
	   Drawable drawable = this.getResources().getDrawable(R.drawable.my_location_copy);
	   drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
	   MarkerItemizedOverlay itemizedoverlay = new MarkerItemizedOverlay(drawable, this, mapView);
       itemizedoverlay.addOverlay(new OverlayItem(gpPresent,null,null));
       //���¶�λ����ǰλ�ã���ͼ����
       mapOverlays.remove(1);
	   mapOverlays.add(1, itemizedoverlay);
	   mapView.invalidate();
	}
   
   
	/**
	 * ���ܣ��л�����
	 */
	public void function8(){
		
	    LayoutInflater factory = LayoutInflater.from(MainActivity.this);
	    View locationView = factory.inflate(R.layout.change_city, null);
		final EditText place = (EditText) locationView.findViewById(R.id.city1);
		
		new AlertDialog.Builder(this).setIcon(R.drawable.weather_icon).setTitle("�л�����").setView(
			locationView).setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton){
					if(place.getText().length() == 0)
						Toast.makeText(MainActivity.this, "�����������", Toast.LENGTH_SHORT).show();
					else{
//				    	progDialog = ProgressDialog.show(MainActivity.this,
//						   		"", "���������Ժ󡭡�", true, false);
						new Thread() {
						@Override
						public void run(){
							try{
				     			GeoPoint city;
				     			Log.e("changecity", place.getText().toString());
								city = MapUtility.getGeoPoint(MapUtility
						    			.getLocationInfo(place.getText().toString().trim()));
								
								
										Log.e("changecity", city.toString());
										mapCtrl.animateTo(city);
										//progDialog.dismiss();
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(MainActivity.this, "����", Toast.LENGTH_LONG).show();
							}
						}
					}.start();
				  	}
				}
			}).show();
		}
	
	
	/**
	 * ���ܣ���ͼģʽ
	 */
	public void function9(){
	   
	    	AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
			.setTitle("��ͼģʽ").setItems(
					R.array.map_type,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							handler.sendEmptyMessage(which);//ѡ���ͼ��ģʽ
							}
					}).create();
	    	dlg.show();
	    
	}
	
	

    
	/**
	 * ���ܣ�����
	 */
	public void function10(){
		//����
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, help.class);
		startActivity(intent);
	}
    
    
   private LocationListener mLocationListener = new LocationListener(){

		public void onLocationChanged(Location location) {
			if (location != null){
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				gpPresent = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6)); // ��������*****�������ƫ��
			}
			else
				Toast.makeText(MainActivity.this, "��λ�ݲ����ã����Ժ�����", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderDisabled(String provider) {
			// Do nothing
		}

		@Override
		public void onProviderEnabled(String provider) {
			// Do nothing
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Do nothing
		}
	};
	
	
	/**
	 * ��GeoPoint��ľ�γ����String,String�ش�  
	 */
    private String GeoPointToString(GeoPoint gp) 
    { 
	      String strReturn=""; 
	      try{
	        // ��Location����  
	        if (gp != null){ 
	            double geoLatitude = (int)gp.getLatitudeE6()/1E6; 
	            double geoLongitude = (int)gp.getLongitudeE6()/1E6; 
	            strReturn = String.valueOf(geoLatitude)+","+
	            String.valueOf(geoLongitude); 
	        } 
	      } 
	      catch(Exception e) { 
	    	  e.printStackTrace(); 
	      } 
	      return strReturn; 
    } 
    
   
    /**
     * ����ģ��,��ѯ·��
     */    
    protected void getMap(String params) {
//		Intent i = new Intent(
//		Intent.ACTION_VIEW,
//		Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&saddr="+GeoPointToString(gpStart)
//				+"&daddr="+GeoPointToString(gpDestination)+"&hl=zh&t=m&" + params));
//		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//		i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//		startActivity(i);
    	
		
        NaviPara para = new NaviPara();
        para.startPoint = gpStart;
        para.startName= "�����￪ʼ";
        para.endPoint  = gpDestination;
        para.endName   = "���������";
        BaiduMapNavigation.openBaiduMapNavi(para, this);
    	
	}
    
	
//@Override
//	protected boolean isRouteDisplayed() {
//		return false;
//	}
}