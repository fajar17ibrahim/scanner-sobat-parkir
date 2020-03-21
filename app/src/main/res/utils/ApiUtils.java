package com.sobatparkir.mainactivity.utils;

import com.sobatparkir.mainactivity.network.ApiClient;
import com.sobatparkir.mainactivity.network.ApiInterface;

public class ApiUtils {

    public static final String BASE_URL_API = "http://35.240.141.139:8081/api/v1/";

    // Mendeklarasikan Interface ApiInterface
    public static ApiInterface getAPIService(){
        return ApiClient.getClient(BASE_URL_API).create(ApiInterface.class);
    }
}
