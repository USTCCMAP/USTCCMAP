/**
 * @author wwz
 * @version 1.0.0 ����ʱ�䣺2014-10-25
 * ����Ϊ�洢��������ʹ�õĹ��ߣ����ڴ洢���ѯ�ص���Ϣ��
 * ����ͨ��ʹ��androidϵͳ�Դ���sqlite���ݿ⣬���ڴ洢�ص���Ϣ��
 * ��������Է���Ĵ洢���ѯ�����洢�ĵص���Ϣ��
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
	 *  �����ݿ�
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
	 *  �ر����ݿ�
	 */
	public void close(){
		if(db!=null){
			db.close();
			db = null;
		}
	}
	
	/**
	 * ���ݲ�����ʵ�����ݵ���ɾ���
	 */
	/**
	 *  ��������
	 */
	public long insert(Place place){		
		//����һ��ContentValuesʵ����Ȼ�������put()������������������ֵд��ʵ���У������һ������/ֵ�� 
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
		//insert()�У���һ������Ϊ���ݱ�����ƣ��ڶ�������Ϊ�滻���ݣ�����������Ϊ��ʱʹ�ã�����������������Ҫ��ӵ����� 
	}
	
	
	/**
	 *  ɾ������
	 */
	public long deleteAllData(){
		Log.i("Todelete","all");
		//delete()�е�һ������Ϊ�������ڶ���Ϊɾ������������Ϊ��ʱ��ʾɾ����������
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
	 *  ��ѯ����
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
		int Counts = results.getCount();		//���ϵ���������
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
	 *  ��������
	 */
	public long updateOndData(Place place){
		
        ContentValues contentValues = new ContentValues();  
        
        contentValues.put("name", place.getName());  
        contentValues.put("address",place.getAddress());
        contentValues.put("type", place.getType());  
        contentValues.put("comment", place.getComment());
        Log.i("Toupdate", place.getName()+place.getAddress()+place.getType()+place.getComment());
        
        //����ֵΪ���ݿ��б����µ�������
		return db.update("place", contentValues , "name = '"+ place.getName()+"'", null);
		
		}

	/**
	 *  ˽�к���������ѯ���ת��Ϊ�Զ����Placeʵ�������ݿ��ѯ�������ݼ���ָ�룩
	 */
	private Place[] ConvertToPlace(Cursor cursor){

		int Counts = cursor.getCount();		//���ϵ���������
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
        Log.i("creat_table", "������ "+ tableName +" �ɹ���");  
      
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table  if  exists " + tableName);
		Log.i("Upgrade", "�汾�����ɹ���");
	}
}

