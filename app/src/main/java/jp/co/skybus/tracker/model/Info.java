package jp.co.skybus.tracker.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import jp.co.skybus.tracker.MyApp;

/**
 * Created by Roman T. on 05.03.2016.
 */

@DatabaseTable(tableName = "info")
public class Info {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String imei;

    @DatabaseField
    private double lat;

    @DatabaseField
    private double lng;

    @DatabaseField()
    private long timestamp;

    @DatabaseField
    private float speed;

    @DatabaseField
    private boolean charging;

    @DatabaseField
    private int battery;

    private static Dao<Info, Integer> sDao;

    static {
        try {
            sDao = MyApp.getDBHelper().getInfoDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(Dao<Info, Integer> pDao) throws SQLException {
        pDao.createOrUpdate(this);
    }

    public void delete(Dao<Info, Integer> pDao) throws SQLException {
        pDao.delete(this);
    }

    public static boolean saveAll(final List<Info> pItemList) {
        if (pItemList != null && pItemList.size() > 0) {
            try {
                sDao.callBatchTasks(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        for (Info item : pItemList)
                            item.save(sDao);
                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static boolean deleteAll(final List<Info> pItemList) {
        if (pItemList != null && pItemList.size() > 0) {
            try {
                sDao.callBatchTasks(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        for (Info item : pItemList)
                            item.delete(sDao);
                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }
}
