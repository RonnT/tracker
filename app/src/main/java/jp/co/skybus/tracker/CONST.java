package jp.co.skybus.tracker;

/**
 * Created by Roman T. on 05.03.2016.
 */
public class CONST {

    public static final String
            DEFAULT_SERVER_ADDRESS = "http://27.112.106.254/track";

    public static final int
            DEFAULT_LOCATION_CHANGE_THRESHOLD   = 1,          //in meters
            DEFAULT_SENDING_TIME_THRESHOLD      = 15,         //in seconds

            FAST_COLLECT_DATA_INTERVAL          = 1000,        // in msec

            FORBIDDEN_ERROR_CODE                = 403;

    public static final float
            FAST_PERIOD_UPDATE_SPEED            = 0.833333f;          //in meters/sec       1 m/s == 3,6 km/h
}
