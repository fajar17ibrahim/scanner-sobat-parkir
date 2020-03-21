package com.sobatparkir.scannersobatparkir.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sobatparkir.scannersobatparkir.MainActivity;
import com.sobatparkir.scannersobatparkir.R;
import com.sobatparkir.scannersobatparkir.network.ApiInterface;
import com.sobatparkir.scannersobatparkir.utils.ApiUtils;
import com.sobatparkir.scannersobatparkir.utils.Constans;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    EditText eUsername, ePassword;
    Button btnLogin;
    ProgressDialog loading;

    Context mContext;
    ApiInterface mApiService;

    Boolean session = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(Constans.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        session = sharedPreferences.getBoolean(Constans.SESSION, false);
        if(session) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        mContext = this;
        mApiService = ApiUtils.getAPIService(); // meng-init yang ada di package apihelper
        initComponents();

    }

    private void initComponents() {
        eUsername = (EditText) findViewById(R.id.txt_username);
        ePassword = (EditText) findViewById(R.id.txt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(eUsername.length() == 0) {
                    Toast.makeText(mContext, "Username tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                } else if(ePassword.length() == 0) {
                    Toast.makeText(mContext, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                } else {
                    loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
                    requestLogin();
                }
            }
        });

    }

    private void requestLogin(){
        mApiService.loginRequest(eUsername.getText().toString(), ePassword.getText().toString())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.isSuccessful()){
                            loading.dismiss();
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (response.code() == 200){
                                    // Jika login berhasil maka data nama yang ada di response API
                                    String id = jsonRESULTS.getString("id");
                                    String token = jsonRESULTS.getString("access_token");
                                    String name = jsonRESULTS.getString("name");
                                    Toast.makeText(mContext, "Selamat Datang Sobat Parkir " + name, Toast.LENGTH_SHORT).show();
                                    sharedPreferences = getSharedPreferences(Constans.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(Constans.TAG_ID_USER, id);
                                    editor.putString(Constans.TAG_NAME, name);
                                    editor.putString(Constans.TAG_TOKEN, token);
                                    editor.putBoolean(Constans.SESSION, true);
                                    editor.apply();

                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    startActivity(intent);
                                } else if (response.code() == 400) {
                                    // Jika login gagal
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(mContext, "Login Gagal! Coba lagi", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.getMessage());
                        Toast.makeText(mContext, "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Keluar")
                .setMessage("Keluar aplikasi ?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                    }
                }).setNegativeButton("Tidak", null).show();
    }
}
