package jp.co.skybus.tracker.helper;

/**
 * Created by Roman T. on 05.03.2016.
 */
public class Utilities {

    public static final int TIMESTAMP_MULTIPLIER = 1000;

    public static long getCurrentTimestamp(){
        return System.currentTimeMillis()/TIMESTAMP_MULTIPLIER;
    }

}
