package com.example.ceshi_room_shujuku;

import android.Manifest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;




import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        methodRequiresTwoPermission();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private final int RC_CAMERA_AND_LOCATION=10000;

    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.CAMERA,
                Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_COARSE_LOCATION
                ,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WAKE_LOCK,
                Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.INTERNET};

        if (EasyPermissions.hasPermissions(this, perms)) {
            // 已经得到许可，就去做吧 //第一次授权成功也会走这个方法
            Log.d("BaseActivity", "成功获得权限");


            startActivity(new Intent(this,MainActivity.class));

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "需要授予app权限,请点击确定",
                    RC_CAMERA_AND_LOCATION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        Log.d("BaseActivity", "list.size():" + list.size());

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
        Log.d("BaseActivity", "list.size():" + list.size());
        Toast.makeText(BaseActivity.this,"权限被拒绝无法正常使用app",Toast.LENGTH_LONG).show();

    }


}
