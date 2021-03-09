package com.example.intersectiesemaforizata.uberanimation;

import com.example.intersectiesemaforizata.uberanimation.remote.IGoogleApi;
import com.example.intersectiesemaforizata.uberanimation.remote.RetrofitClient;

public class Common {
    public static final String baseURL = "https://googleapis.com";

    public static IGoogleApi getGoogleApi() {
        return RetrofitClient.getClient(baseURL).create(IGoogleApi.class);
    }
}
