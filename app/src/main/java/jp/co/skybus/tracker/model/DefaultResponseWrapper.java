package jp.co.skybus.tracker.model;

/**
 * Created by Roman T. on 06.03.2016.
 */
public class DefaultResponseWrapper{
    private Data data;
    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return data.message;
    }

    public String getErrorMessage(){
        return message;
    }

    private class Data{
        String message;
    }
}
