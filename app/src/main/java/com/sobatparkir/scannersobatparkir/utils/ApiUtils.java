package com.sobatparkir.scannersobatparkir.utils;

import com.sobatparkir.scannersobatparkir.network.ApiClient;
import com.sobatparkir.scannersobatparkir.network.ApiInterface;

public class ApiUtils {

    public static final String BASE_URL_API = "http://13.67.54.62:8081/api/v1/";

    // Mendeklarasikan Interface ApiInterface
    public static ApiInterface getAPIService(){
        return ApiClient.getClient(BASE_URL_API).create(ApiInterface.class);
    }
}
