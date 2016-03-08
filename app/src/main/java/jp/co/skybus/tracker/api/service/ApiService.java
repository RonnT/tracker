package jp.co.skybus.tracker.api.service;

import java.util.List;

import jp.co.skybus.tracker.model.DefaultResponseWrapper;
import jp.co.skybus.tracker.model.Info;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Roman Titov on 18.01.2016.
 */
public interface ApiService {

    @POST("/input")
    void sendData(@Body List<Info> objectList, Callback<DefaultResponseWrapper> pCallback);
}