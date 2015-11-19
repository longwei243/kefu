package com.moor.imkf.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.model.entity.Info;
import com.moor.imkf.model.entity.Investigate;

import java.sql.SQLException;

/**
 * 操作数据库的帮助类，使用了OrmLite框架
 * 
 * @author LongWei
 * 
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "qmoorsdk.db";
	private static final int DATABASE_VERSION = 1;
	private Dao<FromToMessage, Integer> fromToMessageDao = null;
	private Dao<Info, Integer> InfoDao = null;
	private Dao<Investigate, Integer> investigateDao = null;


	private DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, FromToMessage.class);
			TableUtils.createTable(connectionSource, Info.class);
			TableUtils.createTable(connectionSource, Investigate.class);
			fromToMessageDao = getFromMessageDao();
			InfoDao = getInfoDao();
			investigateDao = getInvestigateDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVer, int newVer) {
		try {
			TableUtils.dropTable(connectionSource, FromToMessage.class, true);
			TableUtils.dropTable(connectionSource, Info.class, true);
			TableUtils.dropTable(connectionSource, Investigate.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 消息列表
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Dao<FromToMessage, Integer> getFromMessageDao() throws SQLException {
		if (fromToMessageDao == null) {
			fromToMessageDao = getDao(FromToMessage.class);
		}
		return fromToMessageDao;
	}

	/**
	 * 信息
	 *
	 * @return
	 * @throws SQLException
	 */
	public Dao<Info, Integer> getInfoDao() throws SQLException {
		if (InfoDao == null) {
			InfoDao = getDao(Info.class);
		}
		return InfoDao;
	}
	/**
	 * 评价
	 *
	 * @return
	 * @throws SQLException
	 */
	public Dao<Investigate, Integer> getInvestigateDao() throws SQLException {
		if (investigateDao == null) {
			investigateDao = getDao(Investigate.class);
		}
		return investigateDao;
	}

	private static DataBaseHelper instance;

	/**
	 * 单例获取该Helper
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized DataBaseHelper getHelper(Context context) {
		if (instance == null) {
			synchronized (DataBaseHelper.class) {
				if (instance == null)
					instance = new DataBaseHelper(context);
			}
		}

		return instance;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		fromToMessageDao = null;
		InfoDao = null;
		investigateDao = null;
	}
}