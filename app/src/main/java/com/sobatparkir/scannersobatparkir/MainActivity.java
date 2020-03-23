package com.sobatparkir.scannersobatparkir;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sobatparkir.scannersobatparkir.login.LoginActivity;
import com.sobatparkir.scannersobatparkir.scanner.ScannerActivity;
import com.sobatparkir.scannersobatparkir.utils.Constans;

public class MainActivity extends AppCompatActivity  {

    SharedPreferences sharedPreferences;
    private TextView tvQrcode, tvNamaPetugas;
    private Button btnEnter, btnQuit;
    private String nama = "";
    private String qrCode = "-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Constans.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        nama = sharedPreferences.getString(Constans.TAG_NAME, "No Name");

        tvNamaPetugas = (TextView) findViewById(R.id.tv_name);
        tvQrcode = (TextView) findViewById(R.id.tv_qrcode);
        btnEnter = (Button) findViewById(R.id.btn_scan_enter);
        btnQuit = (Button) findViewById(R.id.btn_scan_quit);

        tvNamaPetugas.setText("Petugas Parkir : " + nama);

        try {
            Bundle data = getIntent().getExtras();
            Log.d("TAG QR ", data.getString(Constans.TAG_QR_CODE, "qr_code kosong"));
            qrCode = data.getString(Constans.TAG_QR_CODE);
            tvQrcode.setText(qrCode);
        } catch (Exception e) {
            Log.d("TAG QR Error ", e.toString());
        }

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iScanner = new Intent(MainActivity.this, ScannerActivity.class);
                Bundle btn = new Bundle();
                btn.putString(Constans.TAG_BTN_SCAN, "masuk");
                iScanner.putExtras(btn);
                startActivity(iScanner);
            }
        });

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iScanner = new Intent(MainActivity.this, ScannerActivity.class);
                Bundle btn = new Bundle();
                btn.putString(Constans.TAG_BTN_SCAN, "keluar");
                iScanner.putExtras(btn);
                startActivity(iScanner);
            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Konfirmasi")
                .setMessage("Yakin ingin Logout ?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestLogOut();
                    }
                }).setNegativeButton("Tidak", null).show();
    }

    private void requestLogOut() {
        sharedPreferences.edit().remove(Constans.SESSION).commit();
        sharedPreferences.edit().remove(Constans.TAG_ID_USER).commit();
        sharedPreferences.edit().remove(Constans.TAG_NAME).commit();
        sharedPreferences.edit().remove(Constans.TAG_TOKEN).commit();
        Intent iLogout = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(iLogout);
    }
}
