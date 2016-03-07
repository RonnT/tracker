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

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private int id;

    @DatabaseField
    private String ime;

    @DatabaseField
    private double lat;

    @DatabaseField
    private double lng;

    @DatabaseField()
    private long time;

    @DatabaseField
    private float speed;

    @DatabaseField
    private boolean charge;

    @DatabaseField
    private float battery;

    @DatabaseField
    private float accuracy;

    @DatabaseField
    private int sat;

    @DatabaseField
    private String provider;

    private static Dao<Info, Integer> sDao;

    static {
        try {
            sDao = MyApp.getDBHelper().getInfoDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Info> getAll() {
        try {
            return sDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isCharge() {
        return charge;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }

    public float getBattery() {
        return battery;
    }

    public void setBattery(float battery) {
        this.battery = battery;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
