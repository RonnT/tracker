package jp.co.skybus.tracker.api;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jp.co.skybus.tracker.api.service.ApiService;
import jp.co.skybus.tracker.helper.PrefsHelper;
import jp.co.skybus.tracker.model.DefaultResponseWrapper;
import jp.co.skybus.tracker.model.Info;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Roman T. on 06.03.2016.
 */
public class Api {

    private static final String TAG = "RETROFIT";

    private static ApiService sApiService = createService(ApiService.class, PrefsHelper.getInstance().getServerAddress());

    protected static <S> S createService(Class<S> serviceClass, String pUrl) {

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(5, TimeUnit.SECONDS);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(pUrl)
                .setClient(new OkClient(client))
                .setLogLevel(RestAdapter.LogLevel.FULL).
                        setLog(new RestAdapter.Log() {
                            @Override
                            public void log(String msg) {
                                Log.i(TAG, msg);
                            }
                        }).build();

        return adapter.create(serviceClass);
    }

    public static void sendData(List<Info> pData, Callback<DefaultResponseWrapper> pCallback){
        sApiService.sendData(pData, pCallback);
    }

    public static void refreshApiUrl(){
        sApiService = createService(ApiService.class, PrefsHelper.getInstance().getServerAddress());
    }
}
