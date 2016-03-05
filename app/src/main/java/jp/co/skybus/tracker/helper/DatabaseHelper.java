package jp.co.skybus.tracker.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import jp.co.skybus.tracker.model.Info;

public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "tracker";
    private static final int DATABASE_VERSION = 2; // because 1 - empty db, but we have to update it initially

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private Dao<Info, Integer> mInfoDao = null;

    public Dao<Info, Integer> getInfoDao() throws SQLException {
        if (mInfoDao == null) mInfoDao = getDao(Info.class);
        return mInfoDao;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource cs) {
    }

    @Override
    public void close() {
        super.close();
    }
}
