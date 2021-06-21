/**
 * 
 */
package com.wehang.ystv.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * @author hezongze
 * 
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

	private static final String TAG = "DBHelper";
	/**
	 * 数据库名称
	 */
	public static final String DATABASE_NAME = "download_file.db";
	/**
	 * 数据库版本
	 */
	private static final int DATABASE_VERSION = 1;
	
	private static DBHelper HELPER;

	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}
	
	public static DBHelper getInstance(Context context) {
		if (null == HELPER) {
			HELPER = new DBHelper(context);
		}
		return HELPER;
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, DownloadBean.class);
			TableUtils.createTable(connectionSource, KejianBean.class);
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(TAG, "create database fail");
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, DownloadBean.class, true);
			TableUtils.dropTable(connectionSource, KejianBean.class, true);
			onCreate(sqLiteDatabase, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
