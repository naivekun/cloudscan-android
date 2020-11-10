package com.naivekun.cloudscan;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText serverAddrEdit;
    Button saveButton;
    Button startButton;
    String currentServerAddr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }

        serverAddrEdit = (EditText) findViewById(R.id.server_addr_edittext);
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        String lastServerAddrString = sp.getString("server_addr", "https://pornhub.com");
        serverAddrEdit.setText(lastServerAddrString);
        currentServerAddr = lastServerAddrString;

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor spe = sp.edit();
                String newServerAddr = serverAddrEdit.getText().toString();
                spe.putString("server_addr", newServerAddr);
                spe.apply();
                Toast.makeText(getApplicationContext(), "set server to "+newServerAddr, Toast.LENGTH_SHORT).show();
            }
        });


        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                intent.putExtra("server_addr", currentServerAddr);
                startActivity(intent);
            }
        });

    }

}