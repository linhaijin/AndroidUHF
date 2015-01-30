package com.example.dao;

import java.util.List;

import com.example.common.TableNameStrings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

public class DBAdapter {

	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "SDDKSD_UHF";
	private static final int DATABASE_VERSION = 1;
	public static final String KEY = "KEY";

	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);// ֻ�ڵ�һ�μ���ʱʹ��
		// DBHelper.onCreate(db);
	}

	/*
	 * ��չ��DatabaseHelper
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)// ����һ�����ݿ��ȡ��Ϊbookstitles
		{
			for (int i = 0; i < CreateTableStrings.tables.length; i++) {
				db.execSQL(CreateTableStrings.tables[i]);
			}
		}

		/*
		 * ���ݿ����� (non-Javadoc)
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database
		 * .sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			for (int i = 0; i < TableNameStrings.tablenames.length; i++) {
				db.execSQL("DROP TABLE IF EXISTS "
						+ TableNameStrings.tablenames[i]);
			}
			// db.execSQL("DROP TABLE IF EXISTS MaterialInfo");
			// db.execSQL("DROP TABLE IF EXISTS StockDetail");
			// db.execSQL("DROP TABLE IF EXISTS ProjectInfo");
			// db.execSQL("DROP TABLE IF EXISTS ProviderInfo");
			onCreate(db);
		}
	}

	// ---�����ݿ�---

	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---�ر����ݿ�---
	public void close() {
		DBHelper.close();
	}

	// ---����һ������---
	public long insert(ContentValues initialvalues, String table) {
		return db.insert(table, null, initialvalues);
	}

	// ---����n������---
	public long insertList(List<ContentValues> initialvalues, String table) {
		String DATABASE_TABLES = table;
		long result = 0;
		db.beginTransaction(); // �ύ����
		try {
			for (int i = 0; i < initialvalues.size(); i++) {// ѭ�������ݱ��浽���ݿ�
				if (db.insert(DATABASE_TABLES, null, initialvalues.get(i)) > 0) {
					result++;
				}
			}
			// ���������־Ϊ�ɹ�������������ʱ�ͻ��ύ����
			if (result == initialvalues.size()) {
				db.setTransactionSuccessful();
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("����ʧ��");
			return result = -1;
		} finally {
			// ��������
			db.endTransaction();
		}
		return result;
	}

	// ---���ӱ���---
	public long insertList(ContentValues content,
			List<ContentValues> initialvalues, String maintable,
			String detailtable) {
		long result = 0;
		db.beginTransaction(); // �ύ����
		try {
			if (db.insert(maintable, null, content) > 0) {
				result++;
			}
			for (int i = 0; i < initialvalues.size(); i++) {// ѭ�������ݱ��浽���ݿ�
				if (db.insert(detailtable, null, initialvalues.get(i)) > 0) {
					result++;
				}
			}
			if (result == initialvalues.size() + 1) {
				// ���������־Ϊ�ɹ�������������ʱ�ͻ��ύ����
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("����ʧ��");
			return result = -1;
		} finally {
			// ��������
			db.endTransaction();
		}
		return result;
	}

	// ---ɾ��һ��ָ������---

	public boolean deleteTitle(String table, long rowId) {
		return db.delete(table, KEY + "=" + rowId, null) > 0;
	}

	// ---����ָ��������ֵ---
	public Cursor getAllTitles(String table, String[] columns) {
		return db.query(table, columns, null, null, null, null, null);
	}

	// ---������������ָ��������ֵ---
	public Cursor getAllTitles(String table, String key, String value,String orderbyname) {
		Cursor mCursor = null;
		try {
			mCursor = db.query(table, null, key + "='" + value + "'", null,
					null, null, orderbyname
					+ " COLLATE LOCALIZED ASC");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("getAllTitles", e.toString());
		}
		return mCursor;
	}
	// ---������������ָ��������ֵ---
		public Cursor getAllTitles(String table, String sqlstr, int value) {
			Cursor mCursor = null;
			try {
				mCursor = db.query(table, null, sqlstr , null,
						null, null, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("getAllTitles", e.toString());
			}
			return mCursor;
		}
		// ---������������ָ��������ֵ---
				public Cursor getAllTitles(String table,String[] colums,String sqlstr,String[] selectionArgs,String groupBy,String having, String orderBy) {
					Cursor mCursor = null;
					try {
						mCursor = db.query(table, colums, sqlstr, selectionArgs,
								groupBy, having, orderBy);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e("getAllTitles", e.toString());
					}
					return mCursor;
				}
	// ---����ָ��������ֵ---
	public Cursor getAllTitles(String table, String orderbyname) {
		return db.query(table, null, null, null, null, null, orderbyname
				+ " COLLATE LOCALIZED ASC");
	}
	// ---������������ָ��������ֵ---
		public Cursor getAllTitles(String table, String key, int value,String orderbyname) {
			Cursor mCursor = null;
			try {
				mCursor = db.query(table, null, key + "=" + value, null,
						null, null, orderbyname
						+ " COLLATE LOCALIZED ASC");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("getAllTitles", e.toString());
			}
			return mCursor;
		}
	// ---����ָ��������ֵ---
	public Cursor getAllTitles(String table) {
		return db.query(table, null, null, null, null, null, null);
	}

	// ---����ָ��������ֵ---
	public Cursor getAllByDate(String table, String time) {
		return db.query(table, null, null, null, null, null, time + " DESC");
	}

	// ---����һ��ָ������---
	public Cursor getTitle(String table, String[] columns, long rowId)
			throws SQLException {
		Cursor mCursor = db.query(true, table, columns, KEY + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getTitle(String table, String[] columns, String key,
			String value) throws SQLException {
		Cursor mCursor = null;
		try {
			mCursor = db.query(true, table, columns, key + " like'%" + value
					+ "%'", null, null, null, key + " desc", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("getTitle", e.toString());
		}
		return mCursor;
	}

	// ���ݻ�ȡ��barcode��ѯ���иò�Ʒ������
	public Cursor getTitles(String table, String[] columns, String sqlstr)
			throws SQLException {
		Cursor mCursor = null;
		try {
			mCursor = db.query(true, table, columns, sqlstr, null, null, null,
					null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("getTitle", e.toString());
		}
		return mCursor;
	}

	public String getNameBykey(String table, String value) throws SQLException {
		Cursor mCursor = null;
		String name = null;
		try {
			mCursor = db.query(true, table, new String[] { "Name" }, "Key='"
					+ value + "'", null, null, null, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("getTitle", e.toString());
		}
		if (mCursor.moveToFirst()) {
			name = mCursor.getString(0);
		}
		return name;
	}

	// ---���¶������---

	public long updateList(List<String> initialvalues) {
		long result = 0;
		db.beginTransaction(); // �ύ����

		try {
			for (int i = 0; i < initialvalues.size(); i++) {// ѭ�������ݱ��浽���ݿ�
				db.execSQL(initialvalues.get(i));
				result++;
			}
			// ���������־Ϊ�ɹ�������������ʱ�ͻ��ύ����
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("����ʧ��");
			return result = -1;
		} finally {
			// ��������
			db.endTransaction();
		}
		return result;
	}

	// ͨ��name ��ȡ����
	public String getKeyByName(String table, String key, String value)
			throws SQLException {

		Cursor mCursor = db.query(true, table, new String[] { "Key" }, key
				+ "=" + value, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor.getString(0);
	}

	// ����ʵ����
	public int updateData(String table, ContentValues content, String key,
			String[] value) {
		return db.update(table, content, key + "=?", value);
	}

	// ��������ʱ��
	public void updateTime(String sqlstr) {
		db.execSQL(sqlstr);
	}

	/*
	 * ͨ��ָ��������ȡ���� key��ָ���������ֶ� value:��ѯ������ֵ
	 */
	public Cursor getUserInfoByCondition(String table, String key, String value)
			throws SQLException {

		Cursor mCursor = db.query(true, table, null, key + "='" + value + "'",
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ���ָ��������
	public boolean delete(String table) {
		return db.delete(table, null, null) > 0;
	}
}
