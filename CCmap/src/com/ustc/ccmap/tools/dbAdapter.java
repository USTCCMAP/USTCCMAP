/**
 * @author wwz
 * @version 1.0.0 创建时间：2014-10-25
 * 该类为存储功能能所使用的工具，用于存储或查询地点信息。
 * 该类通过使用android系统自带的sqlite数据库，用于存储地点信息。
 * 在这里可以方便的存储或查询你所存储的地点信息，
 */
package com.ustc.ccmap.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class dbAdapter {
	
	private static final String dbname = "qilu.db";
	private static final int version = 1;
	private final Context context;

	private MySQLiteOpenHelper dbOpenHelper;
	private SQLiteDatabase db;
	

	public dbAdapter(Context context1){
		context = context1;
	}
	
	/**
	 *  打开数据库
	 */
	public void open() throws SQLiteException{
		dbOpenHelper = new MySQLiteOpenHelper(context, dbname, null, version);
		try{
			db = dbOpenHelper.getWritableDatabase();
		}catch(SQLiteException ex){
			db = dbOpenHelper.getReadableDatabase();
		}
	}
	
	/**
	 *  关闭数据库
	 */
	public void close(){
		if(db!=null){
			db.close();
			db = null;
		}
	}
	
	/**
	 * 数据操作，实现数据的增删查改
	 */
	/**
	 *  插入数据
	 */
	public long insert(Place place){		
		//构造一个ContentValues实例，然后调用其put()方法，将属性名及其值写入实例中，及添加一个名称/值对 
        ContentValues contentValues = new ContentValues();
   
        contentValues.put("name", place.getName());  
        contentValues.put("address",place.getAddress());
        contentValues.put("type", place.getType());  
        contentValues.put("comment", place.getComment()); 
        contentValues.put("lat",place.getLat());
        contentValues.put("lng", place.getLng());
        Log.i("Toinsert", place.getName()+place.getAddress()+place.getType()+
        		place.getComment()+String.valueOf(place.getLat()+String.valueOf(place.getLng())));
		return db.insert("place", null , contentValues);	
		//insert()中，第一个参数为数据表的名称，第二个参数为替换数据（第三个参数为空时使用），第三个参数是需要添加的数据 
	}
	
	
	/**
	 *  删除数据
	 */
	public long deleteAllData(){
		Log.i("Todelete","all");
		//delete()中第一个参数为表名，第二个为删除条件，条件为空时表示删除所有数据
		return db.delete("place", null, null);
	}
	
	public long deleteDataByType(String type){
		Log.i("Todelete","type = '"+ type+"'");
		return db.delete("place", "type = '"+ type+"'", null );
	}
	public long deleteOneData(String name){	
		Log.i("Todelete","name = '"+ name+"'");
		return db.delete("place", "name = '"+ name+"'", null );
	}

	
	/**
	 *  查询数据
	 */
	public Place[] queryAllData(){		
		Cursor results = db.query("place", new  String[]{"name","address","type","comment","lat","lng"}, 
				null, null, null, null,null);
		return ConvertToPlace(results);
	}

	public Place[] queryDataByType(String type){
		Cursor results = db.query("place", new  String[]{"name","address","type","comment","lat","lng"}, 
				"type = '"+ type+"'", null, null, null,null);
		return ConvertToPlace(results);
	}

	public Place[] queryOneData(String name){
		
		Cursor results = db.query("place", new  String[]{"name","address","type","comment","lat","lng"}, 
				"name = '"+ name+"'", null, null, null,null);
		return ConvertToPlace(results);
	}

	public String[] queryNameByType(String type){

		Cursor results = db.query("place", new  String[]{"name"}, 
				"type = '"+ type+"'", null, null, null,null);
		int Counts = results.getCount();		//集合的数据数量
		if(Counts==0 || !results.moveToFirst()){
			results.close();
			return new String[]{""};
		}
		String[] string = new String[Counts];
		for(int i = 0; i<Counts; i++){
			string[i] = results.getString(0);
			results.moveToNext();
		}
		results.moveToFirst();
		Log.i("Cursor",String.valueOf(results.isClosed()));
		results.close();
		Log.i("Cursor",String.valueOf(results.isClosed()));
		return string;
	}
	
	
	/**
	 *  更新数据
	 */
	public long updateOndData(Place place){
		
        ContentValues contentValues = new ContentValues();  
        
        contentValues.put("name", place.getName());  
        contentValues.put("address",place.getAddress());
        contentValues.put("type", place.getType());  
        contentValues.put("comment", place.getComment());
        Log.i("Toupdate", place.getName()+place.getAddress()+place.getType()+place.getComment());
        
        //返回值为数据库中被更新的数据量
		return db.update("place", contentValues , "name = '"+ place.getName()+"'", null);
		
		}

	/**
	 *  私有函数，将查询结果转换为自定义的Place实例（数据库查询返回数据集的指针）
	 */
	private Place[] ConvertToPlace(Cursor cursor){

		int Counts = cursor.getCount();		//集合的数据数量
		if(Counts==0 || !cursor.moveToFirst()){
			return null;
		}
		Place[] places = new Place[Counts];
		for(int i = 0; i<Counts; i++){
			places[i] = new Place();
			places[i].setName(cursor.getString(0));
			places[i].setAddress(cursor.getString(1));
			places[i].setType(cursor.getString(2));
			places[i].setComment(cursor.getString(3));
			places[i].setLat(cursor.getInt(4));
			places[i].setLng(cursor.getInt(5));
			cursor.moveToNext();
		}
		cursor.close();
		return places;
	}
	
	
}




class MySQLiteOpenHelper extends SQLiteOpenHelper
{
	public String tableName="place";
    
	public MySQLiteOpenHelper(Context context, String dbname, CursorFactory factory, int version) 
	{
		super(context, dbname, factory, version);
	}

	public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists '" + tableName   + "' (name nvarchar(20) primary key, address nvarchar(20), type nvarchar(10), comment nvarchar(20),lat integer,lng integer)");  
        Log.i("creat_table", "创建表 "+ tableName +" 成功！");  
      
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table  if  exists " + tableName);
		Log.i("Upgrade", "版本升级成功！");
	}
}

