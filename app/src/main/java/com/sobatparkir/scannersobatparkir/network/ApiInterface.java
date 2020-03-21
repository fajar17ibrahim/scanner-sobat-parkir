package com.sobatparkir.scannersobatparkir.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("auth/login")
    Call<ResponseBody> loginRequest(@Field("username") String username,
                                    @Field("password") String password);

    @PUT("order/scan-enter/{qr_code}")
    Call<ResponseBody> enterRequest(@Header("Authorization") String bearer, @Path("qr_code") String qrCode);

    @PUT("order/scan-quit/{qr_code}")
    Call<ResponseBody> quitRequest(@Header("Authorization") String bearer, @Path("qr_code") String qrCode);

}
