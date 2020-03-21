package com.sobatparkir.scannersobatparkir.scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sobatparkir.scannersobatparkir.MainActivity;
import com.sobatparkir.scannersobatparkir.R;
import com.sobatparkir.scannersobatparkir.network.ApiInterface;
import com.sobatparkir.scannersobatparkir.utils.ApiUtils;
import com.sobatparkir.scannersobatparkir.utils.Constans;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerActivity extends AppCompatActivity {

    Context context;
    private SharedPreferences sharedPreferences;
    private ImageView ivBgContent;
    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    private String btn, token;

    ApiInterface mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        context = this;

        Bundle data = getIntent().getExtras();
        btn = data.getString(Constans.TAG_BTN_SCAN);

        ivBgContent = findViewById(R.id.ivBgContent);
        scannerView = findViewById(R.id.scannerView);

        ivBgContent.bringToFront();
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String qrCode = result.getText();
                        sharedPreferences = getSharedPreferences(Constans.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        token = sharedPreferences.getString(Constans.TAG_TOKEN, "Token Kosong");

                        //orderPresenter.requestDataFromServer(token, idSlot);
                        showAlertDialog(qrCode);
                    }
                });
            }
        });

        mApiService = ApiUtils.getAPIService();
        checkCameraPermission();
    }

    private void checkCameraPermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mCodeScanner.startPreview();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void showAlertDialog(final String qrCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Scanning berhasil.");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (btn.equals("masuk")) {
                            requestEnter(qrCode);
                        } else if (btn.equals("keluar")) {
                            requestQuit(qrCode);
                        }

                    }
                });

        builder.setNegativeButton(
                "Scan Lagi",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mCodeScanner.startPreview();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void requestEnter(String qrCode){
        Log.d("QR ", qrCode);
        mApiService.enterRequest("Bearer " + token, qrCode)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("Request Entered : ", response.toString());
                        try {
                            JSONObject JSONResult = new JSONObject(response.body().string());
                            String qrcode = JSONResult.getString("qr_code");
                            Intent iMain = new Intent(ScannerActivity.this, MainActivity.class);
                            Bundle data = new Bundle();
                            data.putString(Constans.TAG_QR_CODE, qrcode);
                            iMain.putExtras(data);
                            startActivity(iMain);
                        } catch (Exception e) {
                            Log.d("Entered Error : ", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.getMessage());
                        Toast.makeText(context,t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void requestQuit(String qrCode){
        Log.d("QR ", qrCode);
        mApiService.quitRequest("Bearer " + token, qrCode)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("Request Quited : ", response.toString());
                        try {
                            JSONObject JSONResult = new JSONObject(response.body().string());
                            String qrcode = JSONResult.getString("qr_code");
                            Intent iMain = new Intent(ScannerActivity.this, MainActivity.class);
                            Bundle data = new Bundle();
                            data.putString(Constans.TAG_QR_CODE, qrcode);
                            iMain.putExtras(data);
                            startActivity(iMain);
                        } catch (Exception e) {
                            Log.d("Entered Error : ", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.getMessage());
                        Toast.makeText(context,t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
