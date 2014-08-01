package com.sobremesa.birdwatching.managers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Michael on 2014-03-11.
 */
public enum XenoCantoApiClientManager {

    INSTANCE;

    private String mBaseUrl = "http://www.xeno-canto.org/api/2";

    private RestAdapter mRestAdapter;
    private Map<String, Object> mClients = new HashMap<String, Object>();


    private XenoCantoApiClientManager() {
    }


    @SuppressWarnings("unchecked")
    public <T> T getClient(Context context, Class<T> clazz) {
        if (mRestAdapter == null) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

//            RequestInterceptor requestInterceptor = new RequestInterceptor() {
//                @Override
//                public void intercept(RequestFacade request) {
//
//                    if( SessionManager.INSTANCE.sessionExists() )
//                        request.addHeader("X-Session-Token", SessionManager.INSTANCE.getSession().getToken());
//                }
//            };

            mRestAdapter = new RestAdapter.Builder()
                    .setEndpoint(getBaseUrl())
                    .setConverter(new GsonConverter(gson))
//                    .setRequestInterceptor(requestInterceptor)
                    .build();
        }
        T client = null;
        if ((client = (T) mClients.get(clazz.getCanonicalName())) != null) {
            return client;
        }
        client = mRestAdapter.create(clazz);
        mClients.put(clazz.getCanonicalName(), client);
        return client;
    }

    public void setRestAdapter(RestAdapter restAdapter) {
        mRestAdapter = restAdapter;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }


}
