/**
 * @author wwz
 * @version 1.0.0 创建时间�??2014
 * 该类为本项目的核心类
 * 其中实现了，地图的显示�?�即时流量显示�?�地图的基本操作（放缩�?�移动）�??
 * 设有菜单项目，点击�?�MENU”键可见，并 调用其他的ACTIVITY从�?�完成各项功能�??  具体包括：即时流量显示�?�指南针、分类查询�?�导航�?�定位�?�地图模式切换等
 * 网络分享、电话预约查询结果结果�?�地图标记搜索结果�?�发送短信向他们推荐你发现的好地点等功能
 * 其中使用了Handler、startActivityForResult等方法实现与其他类之间的通信
 */

package com.demo.qilu;

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
import com.demo.qilu.compass.compass;
import com.demo.qilu.flowing.Flowing;
import com.demo.qilu.search.LongPressOperation;
import com.demo.qilu.search.MarkerItemizedOverlay;
import com.demo.qilu.search.MyItemizedOverlay;
import com.demo.qilu.search.SearchActivity;
import com.demo.qilu.store.Store_Add_Activity;
import com.demo.qilu.store.Store_Main_Activity;
import com.demo.qilu.tools.MapUtility;
import com.demo.qilu.tools.SearchDate;
import com.demo.qilu.tools.SmsActivity;
import com.demo.qilu.tools.information;
import com.demo.qilu.weather.WeatherActivity;
import com.demo.wwz.R;

public class MainActivity extends Activity implements OnClickListener {
	public static MapView mapView;
	private MapController mapCtrl;
	public GeoPoint gpStart = null;
	public GeoPoint gpDestination = null;
	public static GeoPoint gpPresent;
	//流量
	Flowing flowing;
	ApplicationInfo appInfo; 
	SearchDate searchDate;
	//用来实现系统定位的服�??
	private LocationManager locationManager = null;
	private ProgressDialog progDialog = null;
	// 标记搜索结果�??用图�??	
	int[] bmpID = { R.drawable.mark_a, R.drawable.mark_b, R.drawable.mark_c,
					R.drawable.mark_d, R.drawable.mark_e, R.drawable.mark_f,
					R.drawable.mark_g, R.drawable.mark_h, R.drawable.mark_i };
	int[] bmpIDforD = {R.drawable.phone, R.drawable.from, R.drawable.to, R.drawable.search, 
			           R.drawable.send_message, R.drawable.share, R.drawable.store};
	List<information> inforList = new ArrayList<information>(); // 用于存储查询的所有结果记�??	
	int intShowPage = 0;                                        // 用于记录显示的页�??
	public static int choosedID = -1;                           //用于记录选中列表中第几个
	public static boolean isResultDraw = false;                 //是否已标�??
	static boolean isStartGPReady = false;                      //是否已设置出发点
	static boolean isDesGPReady = false;                        //是否已设置目的地
	
	//搜索结果界面
	View resultView;
	private View loadView;
	private Button btnLoadMore;   //下一�??
	private Button btnLoadAhead;  //上一�??
	private TextView txtListHead; //列表头部提示
	ListView listView;            //列表界面列表
	
	//指定结果详情界面
	View detailView;
	private TextView txtDetailHead;
	ListView detailListView;      //详情头部提示
	
	//长按地图，现实的图标
	private Drawable longPressDrawable;
	private MyItemizedOverlay LongPressOverlay;
	public final int MSG_VIEW_CENTEROK = 10001;
	public final int MSG_VIEW_POPMENU = 10002;		        //气泡快捷菜单
	public final int MSG_VIEW_TAPOTHERAREA = 10003;         //长按后单击其他位�??
	public final int MSG_NAVIGATION_SETSTART = 10004;       //设置出发点�?��?�导�??
	public final int MSG_NAVIGATION_SETDESTINATION = 10005;	//设置目的地�?��?�导�??
	public final int MSG_FORDETAIL = 10006;                 //点击含字母图标显示popView对应记录详情
	
	private View popView;       //气泡快捷菜单窗口
	private View popDetailView;
	
	private List<Overlay> mapOverlays;           //地图�??有图�??
	public static GeoPoint gpSearchCenter = null;// 搜索中心位置
	public static GeoPoint longpressGeoPoint = null;
	public static String searchType = "";
	public static String searchCity = "";
	
	public static int longpressOperation = 1;
	//长按地图执行的操作： 1为显示气泡菜菜单 2为�?�择搜索中心 3为�?�择出发�?? 4为�?�择目的�??
	
	/**
	 * 菜单�??	
	 */
    protected static final int MENU_FUNC1 = Menu.FIRST;		//功能�?? 指南�??    
    protected static final int MENU_FUNC2 = Menu.FIRST+1;	//功能�?? 利用SDK内置的地图进行导�??    
    protected static final int MENU_FUNC3 = Menu.FIRST+2;	//功能�?? 查找周围的餐馆等附近地点
    protected static final int MENU_FUNC4 = Menu.FIRST+3;	//功能�??  添加收藏
    protected static final int MENU_FUNC5 = Menu.FIRST+4;	//功能�?? �??�??
    protected static final int MENU_FUNC6 = Menu.FIRST+5;	//功能�?? 进行天气预报查询
    protected static final int MENU_FUNC7 = Menu.FIRST+6;	//功能�??  当前位置
    protected static final int MENU_FUNC8 = Menu.FIRST+7;	//功八 切换城市
    protected static final int MENU_FUNC9 = Menu.FIRST+8;	//功能�??  模式切换
    protected static final int MENU_FUNC10 = Menu.FIRST+9;	//功能�??  帮助
    
    /**
     * 创建进程
     */
	@SuppressLint("NewApi") 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DemoApplication app = (DemoApplication)this.getApplication();
	    if (app.mBMapManager == null) {
	        app.mBMapManager = new BMapManager(getApplicationContext());
	        /**
	         * 如果BMapManager没有初始化则初始化BMapManager
	         */
	        app.mBMapManager.init(new DemoApplication.MyGeneralListener());
	    }
	    
	    if (android.os.Build.VERSION.SDK_INT > 9) {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
	    }
        
        setContentView(R.layout.activity_main);
        
               
        initView();  //初始化界�??     
        flowing();   //显示即时流量
    
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
	 * 返回键确认是否�??出地�??	 
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
	 * 显示流量代码
	 */
	@SuppressLint("HandlerLeak")
	void flowing(){
		try {
            PackageManager pm = getPackageManager();
            //获得该程序的UID 根据包名获得程序的UID
            appInfo = pm.getApplicationInfo("com.demo.qilu", PackageManager.GET_ACTIVITIES);
            flowing = new Flowing(appInfo);
            flowingHandler.postDelayed(runnable, 1000 * 6);
        } catch (NameNotFoundException e) {
        	new AlertDialog.Builder(this)
			.setTitle("温馨提示")
			.setMessage("可能是机型不符，不能显示流量信息！请参照安装说明安装�??")
			.setNegativeButton("确定", null)
			.show();
        }
	}
	
	/**
	 * 用于流量现实的Handler
	 */
    private Handler flowingHandler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
        	TextView txtFlowing= (TextView)findViewById(R.id.flowing);
        	txtFlowing.setText(flowing.update());
        	flowingHandler.postDelayed(this, 1000 * 3);//间隔3秒刷新一次流量显示数�??   
        	}
    };
    
    /**
     * 初始化气泡快捷菜单窗�??   
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
			//为其上面部分按钮添加监听 有些是在特定情况出现按钮，这里没有一�??初始化，用时添加	
			findViewById(R.id.btn_pop_search).setOnClickListener(this);
			findViewById(R.id.btn_pop_from).setOnClickListener(this);
			findViewById(R.id.btn_pop_to).setOnClickListener(this);
			findViewById(R.id.btn_pop_new).setOnClickListener(this);
		}
	}

	/**
	 * mHandler接受其他类的信息，并起作用类似监听事�??
	 **/
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			//点击气泡窗口外的地图，使气泡窗口和图标消�??		
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
		    //导航地图选择出发点后，执行该语句
			case MSG_NAVIGATION_SETSTART:{
				isStartGPReady = true;
				Log.e("经纬", gpStart.toString());
				mapOverlays.remove(LongPressOverlay);
				mapView.invalidate();
				function2();
			}
			break;
			//导航地图选择目的地后，执行该语句
			case MSG_NAVIGATION_SETDESTINATION:{
				isDesGPReady = true;
				mapOverlays.remove(LongPressOverlay);
				mapView.invalidate();
				function2();
			}
				break;
			//搜索时，地图选择搜索中心后执行该函数
			case MSG_VIEW_CENTEROK:{
				mapOverlays.remove(LongPressOverlay);
				mapView.invalidate();
				function3(false);
			}
			break;
			//显示基本菜单窗口
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
			//单击标记图标，显示气泡窗�??		
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
							inforList.get(choosedID).getName()+"\n" + "点击查看更多详情�??");
					txt.setSpan(new ForegroundColorSpan(Color.RED), txt.length()-10,
							txt.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					text.setText(txt);
					RelativeLayout button = (RelativeLayout) findViewById(R.id.pop_Detail);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							//点击查看更多信息
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
	 * 地图模式设置
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
	public void handleMessage(Message msg) {//根据选择改变地图类型
		switch (msg.what) {
		case 0: //交�?�模�??1
			mapView.setTraffic(true);
			mapView.setSatellite(false);
			break;
		case 1: //交�?�模�??2
			mapView.setTraffic(false);
			mapView.setSatellite(false);
			break;
		case 2: //卫星模式1
			mapView.setSatellite(true);
			break;
		case 3: //卫星模式2
			mapView.setSatellite(true);
			mapView.setTraffic(true);
			break;
		}
	}

	};
	
	
	/**
	 * startActivityForResult(intent, 1)对这种以方法打开的CTIVITY返回结果的监�??	 
	 * * （另�??程序�??setresult()并finish（））执行该函数
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			//在地图上标记自己查到的收�??	
			if(data.getExtras().getString("status").equalsIgnoreCase("ICON")){
				Bundle extras = data.getExtras();  
				Log.e("MAIN", extras.getInt("lat")+"");
				Log.e("MAIN", extras.getInt("lng")+"");
				Log.e("MAIN", "由收藏列表获得地理坐�??");
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
				// 点击地图选择搜索中心
				if (data.getExtras().getString("status").equalsIgnoreCase("choosecenter")) {
					Toast.makeText(MainActivity.this, "长按地图选择查找中心", Toast.LENGTH_SHORT).show();
				    if(null != data.getExtras().getString("search_type"))
						 	searchType = data.getExtras().getString("search_type");
				} 
				else 
					showList();
					Log.e("EEEEEEEEEEE", "种类" + searchType + "中心" + gpSearchCenter.toString());
			}
		}

	}
    
	/**
	 * 初始化搜索结果列�??	
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
	 * 初始化查找后 显示指定地点详情列表
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
	 * 将搜索到的结果，填充到结果列�??	
	 */
	public void fillList(List<information> list, int page) {

		int numPage = (0 == inforList.size() % 9) ? inforList.size() / 9 : inforList.size() / 9 + 1;
		// 列表头部提示
		txtListHead.setText("�??" + inforList.size() + "�??(" + numPage + "�??)"
				+ "搜索结果" + "\n�??" + (int) (page + 1) + "�??");
		
		// 判断按钮是否显示
		if (page != numPage -1)
			btnLoadMore.setVisibility(android.view.View.VISIBLE);
		else
			btnLoadMore.setVisibility(android.view.View.INVISIBLE);
		if (page != 0)
			btnLoadAhead.setVisibility(android.view.View.VISIBLE);
		else
			btnLoadAhead.setVisibility(android.view.View.INVISIBLE);
		
		//每页显示的记录数�??�??9�??
		int num = 9;
		if (page == numPage -1)
			num = inforList.size() % 9;
		info_Listed.clear();
		
		// 获得列表信息
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
		// 显示信息
		listAdapter = new SimpleAdapter(this, info_Listed, 		// 数据来源
				R.layout.info_list,								
				new String[] { "img", "name", "address", "distance" },
				new int[] { R.id.img, R.id.name, R.id.address, R.id.distance });  // 分别对应view 的id
		((ListView) findViewById(R.id.search_List)).setAdapter(listAdapter);
	}
	
	ArrayList<HashMap<String, Object>> detail_Listed = new ArrayList<HashMap<String, Object>>();
	SimpleAdapter detailAdapter = null;
	/**
	 * 填充某地详情列表
	 */
	public void fillDetail(int id) {
		
		int infoId = 9 * intShowPage +id;
		//如果不是第一次显示显示详情列表，进行初始�??		
		if(!detail_Listed.isEmpty()){
			txtDetailHead.setText(inforList.get(infoId).getName()+"\n地址:"+inforList.get(infoId).getAddress());
			HashMap<String, Object> infoDetail = new HashMap<String, Object>();
			infoDetail.put("img", bmpID[0]);
			infoDetail.put("str", inforList.get(infoId).getTel());
			detail_Listed.set(0, infoDetail); 
			detailAdapter.notifyDataSetChanged();
			detailView.invalidate();
			return;
		}
		
	String[] detail = {"", "这里出发", "到这里去", "周边搜索", "发�?�短�??", "网络分享", "添加收藏"};
		detail[0] = inforList.get(infoId).getTel();
		// 列表头部提示 名称+地址
		txtDetailHead.setText(inforList.get(infoId).getName()+"\n"+inforList.get(infoId).getAddress());
		// 获得信息
		for(int i = 0; i < detail.length; i++){
			HashMap<String, Object> infoDetail = new HashMap<String, Object>();
			infoDetail.put("img", bmpIDforD[i]);
			infoDetail.put("str", detail[i]);
			detail_Listed.add(infoDetail);
		}
			
		// 显示信息
		detailAdapter = new SimpleAdapter(this, detail_Listed, 
				R.layout.detail_list,		
				new String[] { "img", "str"},
				new int[] {R.id.detailimg, R.id.detailname});
		((ListView) findViewById(R.id.detail_List)).setAdapter(detailAdapter);
	}

	
	/**
	 * 该ACTIVITY要现实的界面 因为要经常在地图、搜索结果列表�?�详情列表切换，�??以在同一布局
	 * @param view 要显示的VIEW
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
	 * 显示结果列表 包括列表显示和地图上显示图标
	 */
	public void showList(){
		searchDate = new SearchDate(mapView, MainActivity.this, mHandler);
		try{
			inforList = searchDate.getPlaces();
		}catch (Exception e) {
			Log.e("MainActivity", e.toString());
        	new AlertDialog.Builder(this)
			.setTitle("温馨提示")
			.setMessage("很抱歉，可能是网络等原因，暂时无法查找，请稍后重�??")
			.setNegativeButton("确定", null)
			.show();
        	return;
		}
		if(0 == inforList.size()){
			Toast.makeText(MainActivity.this, "未搜索到相关记录，请�??查关键词后重新搜�??", Toast.LENGTH_SHORT).show();
			return;
		}
		searchDate.Picture(intShowPage, inforList);//将显示结果标记在地图�??
		showView(resultView);					 
		// 设置地图隐藏 并显示列�??		
		fillList(inforList, intShowPage);
		//结果列表监听 点击显示详情界面
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.getItemAtPosition(arg2);
				fillDetail(arg2);
				choosedID = arg2;
				showView(detailView);
			}
		});
		
		/**
		 * 详情界面监听 点击执行相应操作
		 */
		detailListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.getItemAtPosition(arg2);

				int infoId = 9 * intShowPage + choosedID;
				switch (arg2) {
				case 0:
					//电话预约
					Log.e("ITEMDDDDDDDDDDDDDD", "电话预约");
					Intent intent_call = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+inforList.get(choosedID).getTel()));
					startActivity(intent_call);
					break;
				case 1:
					//从这里出�??			
					Log.e("ITEMDDDDDDDDDDDDDD", "从这里出�??");
					gpStart = inforList.get(infoId).getPostion();
					isStartGPReady = true;
					findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
					popView.setVisibility(View.INVISIBLE);
					mapOverlays.remove(LongPressOverlay);
					mapView.invalidate();
					function2();
					break;
				case 2:
					//到这里去
					Log.e("ITEMDDDDDDDDDDDDDD", "到这里去");
					gpDestination = inforList.get(infoId).getPostion();
					isDesGPReady = true;
					findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
					popView.setVisibility(View.INVISIBLE);
					mapOverlays.remove(LongPressOverlay);
					mapView.invalidate();
					function2();
					break;
				case 3:
					//周边搜索
					Log.e("ITEMDDDDDDDDDDDDDD", "周边搜索");
					gpSearchCenter = inforList.get(choosedID).getPostion();
					function3(true);
					break;
				case 4:
					//发�?�短�??
					Log.e("ITEMDDDDDDDDDDDDDD", "发�?短信");
					if("无记�??".equals(inforList.get(choosedID).getTel())){ //无号码处�??	
						Toast.makeText(MainActivity.this, "未查找到该地电话", Toast.LENGTH_SHORT).show();
						break;
					}
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, SmsActivity.class);
					Bundle bundle = new Bundle();
					//信息内容的初始化
					String content_message = "名称:"+inforList.get(choosedID).getName()
							+"    地址�??"+inforList.get(choosedID).getAddress()
							+"    电话: "+inforList.get(choosedID).getTel();
					bundle.putString("WEATHER_INFO_MESSAGE", content_message);
					intent.putExtras(bundle); 
					startActivity(intent);
					
					break;
			case 5:
					//网络分享
					Log.e("ITEMDDDDDDDDDDDDDD", "网络分享");
					String content = "名称:"+inforList.get(choosedID).getName()+"    地址:"
					+inforList.get(choosedID).getAddress()+"    电话:"+inforList.get(choosedID).getTel();
					Intent intent_web=new Intent(Intent.ACTION_SEND);  
					intent_web.setType("text/plain");  //分享的数据类�??			
					intent_web.putExtra(Intent.EXTRA_SUBJECT, "网络分享");  //主题
					intent_web.putExtra(Intent.EXTRA_TEXT,content  );  //内容
					startActivity(Intent.createChooser(intent_web, "选择类型"));  //目标应用选择对话框的标题
					break;
				case 6:
					//添加收藏
					Log.e("ITEMDDDDDDDDDDDDDD", "添加收藏");
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
	 * 为以上添加监听的按钮 赋以方法实现
	 */
	@Override
	public void onClick(android.view.View v) {
		switch (v.getId()) {
		case R.id.btn_toMap:   //返回地图
		case R.id.btn_toMap2:
			showView(mapView);
			break;
		case R.id.btn_toList:  //返回列表
		case R.id.btn_toList2:
			showView(resultView);
			break; 
		case R.id.load_NextButton: //下一�??			
			intShowPage++;
			fillList(inforList, intShowPage);
			searchDate.Picture(intShowPage, inforList);
			break;
		case R.id.load_AheadButton://上一�??			
			intShowPage--;
			fillList(inforList, intShowPage);
			searchDate.Picture(intShowPage, inforList);
			break;
		case R.id.btn_research:    //重新搜索
			function3(true);
			break;
		case R.id.btn_pop_search: //气泡—�?�搜�??
			Log.e("popmenu", "周边搜索");
			gpSearchCenter = longpressGeoPoint;
			function3(false);
			findViewById(R.id.btn_pop_new).setVisibility(View.VISIBLE);
			findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
			popView.setVisibility(View.INVISIBLE);
			mapOverlays.remove(LongPressOverlay);
			mapView.invalidate();
			break;
		case R.id.btn_pop_from:  //气泡—�?�从这里出发
			Log.e("popmenu", "从这里出�??");
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
		case R.id.btn_pop_to:      //气泡—�?�到这里�??
			Log.e("popmenu", "到这里去");
			gpDestination = longpressGeoPoint;
			//////////
			Log.e("pop经纬:", gpDestination.toString());
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
		case R.id.btn_pop_new:    //气泡—�?�收�??
			Log.e("popmenu", "收藏");
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
		case R.id.whereAmI:    //当前位置
			Log.e("我的", "当前位置");
			whereAmI();
			break;
		default:
			break;
		}
	}
    
    
	/**
	 * 进入界面时默认地图界�??	 
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
	 * 定位到当前的代码
	 */
	progDialog = ProgressDialog.show(MainActivity.this,
				"", "处理中请稍后…�??", true, false);
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
						gpPresent = new GeoPoint((int) (nowloc.getLatitude() * 1E6),	//用来粗略的处理偏差，不能解决线�?�问�??*******
				                (int) (nowloc.getLongitude()* 1E6));
					}
					else{
						gpPresent = new GeoPoint((int) (39.904214 * 1E6),//默认将城市中心设置为北京
								(int) (116.407413 * 1E6));
					}
					mapCtrl.animateTo(gpPresent);
					progDialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(MainActivity.this, "地图加载出错。请稍后重试", Toast.LENGTH_LONG).show();
				}
			}
		}.start();
		
		initListView();
		initPopView();
		initDetailView();
		//长按popView显示的图片设�??
		longPressDrawable = getResources().getDrawable(R.drawable.point_start);
		LongPressOverlay = new MyItemizedOverlay(longPressDrawable, this,
				mapView, popView, mapCtrl);
		//LongPressOverlay.
		
		mapOverlays = mapView.getOverlays();
//		//mapOverlays.add(LongPressOverlay);
//		Log.e("MainActivity",mapOverlays.size()+"");
//		mapOverlays.add(new LongPressOperation(this, mapView, popView, mHandler,
//				mapCtrl)); //长按地图响应图层
//		Log.e("MainActivity",mapOverlays.size()+"");
//		mapView.refresh();
		
		OnLongClickListener ss = new LongPressOperation(this, mapView, popView, mHandler,mapCtrl, getResources().getDrawable(R.drawable.point_start));
		mapView.setOnLongClickListener(ss);
		mapView.setOnTouchListener((OnTouchListener) ss);
		//mapView.setOnGenericMotionListener((OnGenericMotionListener) ss);
		
		//我的当前位置图标
		   Drawable drawable = this.getResources().getDrawable(R.drawable.my_location);
		   drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		   MarkerItemizedOverlay itemizedoverlay = new MarkerItemizedOverlay(drawable, this, mapView);
	       itemizedoverlay.addOverlay(new OverlayItem(gpPresent, null, null));
		   mapOverlays.add(itemizedoverlay);
		   mapView.refresh();
    }
    
    

    /**
     * 构�?�菜单的函数
     */
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_FUNC1, 0,"指南�??").setIcon(R.drawable.menu_compass);          //指南�??    	
    	menu.add(0, MENU_FUNC2, 0,"导航").setIcon(android.R.drawable.ic_menu_compass); //导航
    	menu.add(0, MENU_FUNC3, 0,"分类查询").setIcon(R.drawable.seach_type);	          //分类查询
    	menu.add(0, MENU_FUNC4, 0,"我的收藏").setIcon(android.R.drawable.ic_menu_save); //我的收藏
    	menu.add(0, MENU_FUNC6, 0,"天气查询").setIcon(R.drawable.weather);              //天气查询
    	menu.add(0, MENU_FUNC7, 0,"当前位置");   //当前位置
    	menu.add(0, MENU_FUNC8, 0,"切换城市");   //切换城市
    	menu.add(0, MENU_FUNC9, 0,"模式切换");   //模式切换
    	menu.add(0, MENU_FUNC10, 0,"帮助");     //帮助
    	menu.add(0, MENU_FUNC5, 0,"�??�??");      //�??�??
    	return super.onCreateOptionsMenu(menu);
    }
    
    
    /**
     * 选择菜单时执行的动作
     */
    public boolean onOptionsItemSelected(MenuItem item) {
    	longpressOperation = 1;
		switch (item.getItemId()) {
		case MENU_FUNC1: //指南�??		
			function1(); 
			break;
		case MENU_FUNC2: //导航
			//初始化出发点、目的地均无
			isStartGPReady = false;
			isDesGPReady = false;
			findViewById(R.id.pop_menu).setVisibility(View.INVISIBLE);
			function2();
			break;
		case MENU_FUNC3: //分类搜索
			function3(true);
			break;
		case MENU_FUNC4: //我的收藏
			function4();
			break;
		case MENU_FUNC5: //�??出程�??
			function5();
			break;
		case MENU_FUNC6:
			function6(); //天气查询
			break;
		case MENU_FUNC7: //当前位置
			whereAmI();
			break;
		case MENU_FUNC8: //切换城市
			function8();
			break;
		case MENU_FUNC9: //切换模式
			function9();
			break;
		case MENU_FUNC10://帮助
			function10();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    

    /**
     * 功能：指南针    
     */
    public void function1(){
    	Intent theActivity = new Intent(this, compass.class);
        startActivity(theActivity);
    }
    
    
	/**
	 * 功能：用来进行导�??  
	 */
	public void function2(){
		final AlertDialog Dialog;
		LayoutInflater factory = LayoutInflater.from(MainActivity.this);
		final View locationView = factory.inflate(R.layout.navigation_actiivity, null);
		final EditText start = (EditText) locationView
				.findViewById(R.id.startplace);
		final EditText destination = (EditText) locationView
				.findViewById(R.id.endplace);
		
		if(isStartGPReady) start.setText("地图选中�??");
		if(isDesGPReady) destination.setText("地图选中�??");
		
		Dialog =new AlertDialog.Builder(this).setIcon(R.drawable.navigation_title).setTitle("导航")
				.setView(locationView).setPositiveButton("驾车",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
							//选中了出发点，并未改变其中的文字
							if(!(isStartGPReady && "地图选中�??".equals(start.getText().toString().trim()))) 
								gpStart =  MapUtility.getGeoPoint(MapUtility.getLocationInfo(start.getText().toString())); // 地理坐标
							if(!(isDesGPReady && "地图选中�??".equals(destination.getText().toString().trim())))
								gpDestination = MapUtility.getGeoPoint(MapUtility.getLocationInfo(destination.getText().toString())); // 地理坐标
							//初始化参数，为下次查询做准备
							isStartGPReady = false;
							isDesGPReady = false;
							Log.e("MMMMMM", gpStart.toString()+gpDestination.toString());
							//下面�??始查询路�??
							StringBuilder params = new StringBuilder().append("&dirflg=");
							params.append("d");
							try {
								getMap(params.toString());
							} catch (Exception e) {
								Log.e("导航", "ERROR");
							}
						}
					})
					.setNegativeButton("乘车",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
							//选中了出发点，并未改变其中的文字
							if(!(isStartGPReady && "地图选中�??".equals(start.getText().toString().trim()))) 
								gpStart =  MapUtility.getGeoPoint(MapUtility.getLocationInfo(start.getText().toString())); // 地理坐标
							if(!(isDesGPReady && "地图选中�??".equals(destination.getText().toString().trim())))
								gpDestination = MapUtility.getGeoPoint(MapUtility.getLocationInfo(destination.getText().toString())); // 地理坐标
							MainActivity.isStartGPReady = false;
							MainActivity.isDesGPReady = false;
							//下面�??始查询路�??
							StringBuilder params = new StringBuilder().append("&dirflg=");
							params.append("r");
							getMap(params.toString());
							}
						} )
						.setNeutralButton("步行", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
							//选中了出发点，并未改变其中的文字
							if(!(isStartGPReady && "地图选中�??".equals(start.getText().toString().trim()))) 
								gpStart =  MapUtility.getGeoPoint(MapUtility.getLocationInfo(start.getText().toString())); // 地理坐标
							if(!(isDesGPReady && "地图选中�??".equals(destination.getText().toString().trim())))
								gpDestination = MapUtility.getGeoPoint(MapUtility.getLocationInfo(destination.getText().toString())); // 地理坐标
							MainActivity.isStartGPReady = false;
							MainActivity.isDesGPReady = false;
							//下面�??始查询路�??
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
			   		Log.e("参数", "重置");
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
				Toast.makeText(MainActivity.this, "长按地图选择出发�??", Toast.LENGTH_SHORT).show();
				longpressOperation = 3;
				Log.e("导航", "出发�??");
				//消除对话�??
			    Dialog.dismiss();
			}
		});
		final Button chooseTo = (Button) locationView
				.findViewById(R.id.btn_find_geo_on_map2);
		chooseTo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "长按地图选择目的�??", Toast.LENGTH_SHORT).show();
				Log.e("导航", "终点");
				longpressOperation = 4;
				Dialog.dismiss();
			}
		});
   }
	
	
	 /**
     * 功能：分类搜�??
	 * @param isNewOpen 参数为是否新打开搜索ACTIVITY
	 */
	private void function3(boolean isNewOpen){
	 	Intent intent = new Intent();
		intent.setClass(this, SearchActivity.class);
		// 重新打开搜索初始化搜索中心为我的当前位置
		if(isNewOpen){
			intShowPage = 0;
			choosedID = -1;
			searchType = "";
			searchCity = "";
			gpSearchCenter = gpPresent;
		}
		else{
			// 再次打开
			Bundle bundle = new Bundle();
		    bundle.putString("center_text", "地图中�?�中的点");
		    if(!"".equals(searchType))
				 bundle.putString("search_type", searchType);
		    intent.putExtras(bundle); 
		}
		startActivityForResult(intent, 1);
	}
    
	/**
	 * 功能：我的收�??
	 */
	public void function4(){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, Store_Main_Activity.class);
		startActivityForResult(intent, 1);
	}
	
    /**
     * 功能：�??出程�??
     */
	public void function5(){
        new AlertDialog.Builder(this)
        .setIcon(R.drawable.softicon)
        .setTitle(R.string.app_name)
        .setMessage("确定要�??出地图？")
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//取消�??�??
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
        })
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//�??�??
                public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                }
        }).show();
	}
	
	
	 /**
     * 功能：天气查�??
     */
    public void function6(){
    	LayoutInflater factory = LayoutInflater.from(MainActivity.this);
		View locationView = factory.inflate(R.layout.check_weather, null);
		final EditText place = (EditText) locationView
				.findViewById(R.id.place1);
			new AlertDialog.Builder(this).setIcon(R.drawable.weather_icon).setTitle("天气查询").setView(
					locationView).setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if(place.getText().length() == 0){
								Toast.makeText(MainActivity.this, "请输入查询的地名", Toast.LENGTH_SHORT).show();
							}
							else{
					  			progDialog = ProgressDialog.show(MainActivity.this,
										"","处理中请稍后…�??" , true, false);
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
											Toast.makeText(MainActivity.this, "出错", Toast.LENGTH_LONG).show();
										}
									}

								}.start();
							}
							}
						}).show();
    }
   
    
    /**
     * 功能：当前位�??
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
					Toast.makeText(MainActivity.this, "地图加载出错。请稍后重试", Toast.LENGTH_LONG).show();
				}
			}
		}.start();
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1, mLocationListener);
		mapCtrl.animateTo(gpPresent);
       
	   Drawable drawable = this.getResources().getDrawable(R.drawable.my_location_copy);
	   drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
	   MarkerItemizedOverlay itemizedoverlay = new MarkerItemizedOverlay(drawable, this, mapView);
       itemizedoverlay.addOverlay(new OverlayItem(gpPresent,null,null));
       //重新定位到当前位置，并图标标�??
       mapOverlays.remove(1);
	   mapOverlays.add(1, itemizedoverlay);
	   mapView.invalidate();
	}
   
   
	/**
	 * 功能：切换城�??
	 */
	public void function8(){
		
	    LayoutInflater factory = LayoutInflater.from(MainActivity.this);
	    View locationView = factory.inflate(R.layout.change_city, null);
		final EditText place = (EditText) locationView.findViewById(R.id.city1);
		new AlertDialog.Builder(this).setIcon(R.drawable.weather_icon).setTitle("切换城市").setView(
			locationView).setPositiveButton("确定",
				new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton){
					if(place.getText().length() == 0)
						Toast.makeText(MainActivity.this, "请输入城市名", Toast.LENGTH_SHORT).show();
					else{
				    	progDialog = ProgressDialog.show(MainActivity.this,
						   		"", "处理中请稍后…�??", true, false);
						new Thread() {
						@Override
						public void run(){
							try{
				     			GeoPoint city;
								city = MapUtility.getGeoPoint(MapUtility
						    			.getLocationInfo(place.getText().toString().trim()));
										Log.e("changecity", city.toString());
										mapCtrl.animateTo(city);
										progDialog.dismiss();
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(MainActivity.this, "出错", Toast.LENGTH_LONG).show();
							}
						}
					}.start();
				  	}
				}
			}).show();
		}
	
	
	/**
	 * 功能：地图模�??
	 */
	public void function9(){
	   
	    	AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
			.setTitle("地图模式").setItems(
					R.array.map_type,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							handler.sendEmptyMessage(which);//选择地图的模�??
							}
					}).create();
	    	dlg.show();
	    
	}
	
	

    
	/**
	 * 功能：帮�??
	 */
	public void function10(){
		//帮助
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, help.class);
		startActivity(intent);
	}
    
    
   private LocationListener mLocationListener = new LocationListener(){

		public void onLocationChanged(Location location) {
			if (location != null){
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				gpPresent = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6)); // 地理坐标*****解决地理偏差
			}
			else
				Toast.makeText(MainActivity.this, "定位暂不可用，请稍后重试", Toast.LENGTH_SHORT).show();
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
	 * 将GeoPoint里的经纬度以String,String回传  
	 */
    private String GeoPointToString(GeoPoint gp) 
    { 
	      String strReturn=""; 
	      try{
	        // 当Location存在  
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
     * 导航模块,查询路径
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
        para.startName= "从这里开�??";
        para.endPoint  = gpDestination;
        para.endName   = "到这里结�??";
        BaiduMapNavigation.openBaiduMapNavi(para, this);
    	
	}
    
	
//@Override
//	protected boolean isRouteDisplayed() {
//		return false;
//	}
}
