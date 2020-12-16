package megvii.testfacepass.pa;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import android.hardware.usb.UsbManager;
import android.serialport.SerialPort;

import com.tencent.mmkv.MMKV;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Objects;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;
import mcv.facepass.FacePassHandler;
import megvii.testfacepass.pa.beans.BaoCunBean;

import megvii.testfacepass.pa.utils.UnCeHandler;

import static com.Aladdin.HaspUsbHandler.ACTION_USB_PERMISSION;


/**
 * Created by Administrator on 2018/8/3.
 */

public class MyApplication extends Application {
    public static CH34xUARTDriver driver;// 需要将CH34x的驱动类写在APP类下面，使得帮助类的生命周期与整个应用程序的生命周期是相同的
    ArrayList<Activity> list = new ArrayList<Activity>();
    public static Context context;
    private static FacePassHandler facePassHandler=null;
    public static MyApplication myApplication;
    public static Application ampplication;

    public  static String SDPATH ;
    public  static String SDPATH2 ;
    public  static String SDPATH3 ;
   // protected OutputStream mOutputStream;
    //这个是旷世3.6.1 旧文档的
    //public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            /* Read serial port parameters */
            SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", 0);
            String path = sp.getString("DEVICE", "/dev/ttyS2");
            int baudrate = Integer.decode(Objects.requireNonNull(sp.getString("BAUDRATE", "115200")));

            /* Check parameters */
            if ( (path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

            try {
                mSerialPort = new SerialPort(new File(path), baudrate, 0);
            }catch (Exception e){
                e.printStackTrace();
            }
            /* Open the serial port */

        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }


    public FacePassHandler getFacePassHandler() {

        return facePassHandler;
    }

    public void setFacePassHandler(FacePassHandler facePassHandler1){
        facePassHandler=facePassHandler1;
    }



    public void init(){
        //设置该CrashHandler为程序的默认处理器
        UnCeHandler catchExcep = new UnCeHandler(this,this);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象*/
    public void removeActivity(Activity a){
        list.remove(a);
    }

    /**
     * 向Activity列表中添加Activity对象*/
    public void addActivity(Activity a){
        list.add(a);
    }

    /**
     * 关闭Activity列表中的所有Activity*/
    public void finishActivity(){
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }




    @Override
    public void onCreate() {
        super.onCreate();


        ampplication = this;
        myApplication=this;
        context = this;
        driver = new CH34xUARTDriver(
                (UsbManager) getSystemService(Context.USB_SERVICE), this,
                ACTION_USB_PERMISSION);


        SDPATH = getExternalFilesDir(null)+ File.separator+"yinian1";
        SDPATH2 = getExternalFilesDir(null)+File.separator+"yinian2";
        SDPATH3 = getExternalFilesDir(null)+File.separator+"yinian3";
        //   Bugly.init(this, "e92fdff61f", false);
        String rootDir = MMKV.initialize(this);
        System.out.println("mmkv root: " + rootDir);

        BaoCunBean  baoCunBean = MMKV.defaultMMKV().decodeParcelable("saveBean",BaoCunBean.class);
        if (baoCunBean == null) {
            baoCunBean = new BaoCunBean();
            baoCunBean.setHoutaiDiZhi("http://39.108.253.88:8087/front");
            baoCunBean.setTouxiangzhuji("http://39.108.253.88:8087/front");
            baoCunBean.setId(123456L);
            baoCunBean.setShibieFaceSize(30);
            baoCunBean.setShibieFaZhi(73f);
            baoCunBean.setRuKuFaceSize(50);
            baoCunBean.setRuKuMoHuDu(0.3f);
            baoCunBean.setHuoTiFZ(72);
            baoCunBean.setMima(123456);
            baoCunBean.setYusu(5);
            baoCunBean.setYudiao(5);
            baoCunBean.setMima2(123456);
            baoCunBean.setJihuoma("0000-0000-0000-0000-0000");
            baoCunBean.setHuoTi(false);
            baoCunBean.setDangqianShiJian("2");
            baoCunBean.setTianQi(false);
            baoCunBean.setTishiyu("欢迎光临");
            baoCunBean.setPort(6000);
            baoCunBean.setMsrPanDing(true);
            baoCunBean.setConfigModel(1);
            baoCunBean.setMoshengrenPanDing(3);
            baoCunBean.setJidianqi(6);
            baoCunBean.setLight(false);

            MMKV.defaultMMKV().encode("saveBean",baoCunBean);
        }

      //  Log.d("MyApplication", "driver.UsbFeatureSupported():" + driver.UsbFeatureSupported());

      //  Log.i(">>>>>>","oncreate");

        init();
//        String processName = getProcessName();
//        Log.d("MyApplication", processName+"进程");
//
//        if (!TextUtils.isEmpty(processName) && processName.equals(this.getPackageName())) {
//            Log.d("MyApplication", "初始胡");
//            //在这里进行主进程初始化逻辑操作
//
//        }
//
//        int pid = android.os.Process.myPid();
//
//        Log.d("TAG", "MyApplication pid is " + pid);
    }

    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
