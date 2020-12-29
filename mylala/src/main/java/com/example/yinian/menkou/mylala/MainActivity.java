package com.example.yinian.menkou.mylala;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;



public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private File file22 =null;
    private int num=0;
    private int num2=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        methodRequiresTwoPermission();

        //每分钟的广播
        // private TodayBean todayBean = null;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        TimeChangeReceiver timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);

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

            start();

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
        Log.d("BaseActivity", "list.size():" + list.size());
        Toast.makeText(MainActivity.this,"权限被拒绝无法正常使用app",Toast.LENGTH_LONG).show();

    }



    private void start() {
        //初始化
        String SDPATH = "/storage/emulated/0/Android/data/com.ruitong.mechanical.a555666/files/yinian1";
        file22 = new File(SDPATH,"lala.txt");
        if (!file22.isFile()) {
            try {
                Log.d("ggg", "file22.mkdirs():" + file22.createNewFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


     class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:
                    num2++;
                    if (num2>=3){
                        num2=0;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SystemClock.sleep(10000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        InputStream instream = null;
                                        try {
                                            instream = new FileInputStream(file22);
                                            InputStreamReader inputreader = new InputStreamReader(instream);
                                            BufferedReader buffreader = new BufferedReader(inputreader);
                                            String line = null;
                                            //分行读取
                                            StringBuilder sb = new StringBuilder("");
                                            try {
                                                while ((line = buffreader.readLine()) != null) {
                                                    sb.append(line);
                                                }
                                                instream.close();
                                                Log.d("MianBanJiActivity3", "副app读取文本的值"+sb.toString());
                                                num++;
                                                if (!sb.toString().equals("zhu")){
                                                    if (num>=2){
                                                        num=0;
                                                        //重启主app
                                                        Log.d("MianBanJiActivity3", "重启主app");
                                                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.ruitong.mechanical.a555666");
                                                        if (intent != null) {
                                                            startActivity(intent);
                                                        }
                                                    }
                                                }else {
                                                    num=0;
                                                }
                                                //写入fu
                                                FileOutputStream outStream = null;
                                                try {
                                                    outStream = new FileOutputStream(file22);
                                                    outStream.write("fu".getBytes());
                                                    outStream.close();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                        }).start();
                    }

                    break;
                case Intent.ACTION_TIME_CHANGED:
                    //设置了系统时间
                    // Toast.makeText(context, "system time changed", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    //设置了系统时区的action
                    //  Toast.makeText(context, "system time zone changed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


  public static class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }

}