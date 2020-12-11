package megvii.testfacepass.pa.ui;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.graphics.Bitmap;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lztek.toolkit.Lztek;
import com.sdsmdg.tastytoast.TastyToast;
import com.tencent.mmkv.MMKV;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassAddFaceResult;
import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageType;
import mcv.facepass.types.FacePassRecognitionResult;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.BaoCunBean;
import megvii.testfacepass.pa.beans.HuiFuBean;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.beans.XGBean;
import megvii.testfacepass.pa.beans.ZhiLingBean;
import megvii.testfacepass.pa.camera.CameraManager;
import megvii.testfacepass.pa.camera.CameraManager2;
import megvii.testfacepass.pa.camera.CameraPreview;
import megvii.testfacepass.pa.camera.CameraPreview2;
import megvii.testfacepass.pa.camera.CameraPreviewData;
import megvii.testfacepass.pa.camera.CameraPreviewData2;
import megvii.testfacepass.pa.dialog.MiMaDialog4;

import megvii.testfacepass.pa.tts.Auth;
import megvii.testfacepass.pa.tts.OfflineResource;
import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.DBUtils;
import megvii.testfacepass.pa.utils.DengUT;
import megvii.testfacepass.pa.utils.FacePassUtil;
import megvii.testfacepass.pa.utils.GsonUtil;
import megvii.testfacepass.pa.utils.NV21ToBitmap;
import megvii.testfacepass.pa.utils.RestartAPPTool;
import megvii.testfacepass.pa.utils.SettingVar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import static megvii.testfacepass.pa.tts.IOfflineResourceConst.DEFAULT_SDK_TTS_MODE;
import static megvii.testfacepass.pa.tts.IOfflineResourceConst.PARAM_SN_NAME;
import static megvii.testfacepass.pa.tts.IOfflineResourceConst.TEXT_MODEL;
import static megvii.testfacepass.pa.tts.IOfflineResourceConst.VOICE_MALE_MODEL;




public class MianBanJiActivity3 extends Activity implements CameraManager.CameraListener, CameraManager2.CameraListener2 {


    private TextView faceName;
    protected String appId;
    protected String appKey;
    protected String secretKey;
    protected String sn; // 纯离线合成SDK授权码；离在线合成SDK没有此参数
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private final TtsMode ttsMode = DEFAULT_SDK_TTS_MODE;
    private final boolean isOnlineSDK = TtsMode.ONLINE.equals(DEFAULT_SDK_TTS_MODE);
    // ================ 纯离线sdk或者选择TtsMode.ONLINE  以下参数无用;
    private static final String TEMP_DIR = "/sdcard/baiduTTS"; // 重要！请手动将assets目录下的3个dat 文件复制到该目录
    // 请确保该PATH下有这个文件
    private static final String TEXT_FILENAME = TEMP_DIR + "/" + TEXT_MODEL;
    // 请确保该PATH下有这个文件 ，m15是离线男声
    private static final String MODEL_FILENAME = TEMP_DIR + "/" + VOICE_MALE_MODEL;
    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
    protected SpeechSynthesizer mSpeechSynthesizer;
    //离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_vXXXXXXX.dat为离线男声模型文件；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_vXXXXX.dat为离线女声模型文件;
    // assets目录下bd_etts_common_speech_yyjw_mand_eng_high_am-mix_vXXXXX.dat 为度逍遥模型文件;
    // assets目录下bd_etts_common_speech_as_mand_eng_high_am_vXXXX.dat 为度丫丫模型文件;
    // 在线合成sdk下面的参数不生效
    protected String offlineVoice = OfflineResource.VOICE_MALE;
    private NetWorkStateReceiver netWorkStateReceiver = null;
    //private static String idAll="";
   // private NfcAdapter mNfcAdapter;
   // private PendingIntent mPendingIntent;
   // private ServerManager serverManager;
   // private Bitmap msrBitmap = null;
  /*  private RequestOptions myOptions = new RequestOptions()
            .fitCenter()
            .error(R.drawable.erroy_bg)
           // .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
    .transform(new GlideRoundTransform(MianBanJiActivity3.this,10));*/
//
//    private RequestOptions myOptions2 = new RequestOptions()
//            .fitCenter()
//            .error(R.drawable.erroy_bg)
//            //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
//            .transform(new GlideCircleTransform270(MyApplication.myApplication, 2, Color.parseColor("#ffffffff"), 270));
   // private String serialnumber = GetDeviceId.getDeviceId(MyApplication.myApplication);

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .writeTimeout(20000, TimeUnit.MILLISECONDS)
            .connectTimeout(20000, TimeUnit.MILLISECONDS)
            .readTimeout(20000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
            //        .retryOnConnectionFailure(true)
            .build();
    private final Timer timer = new Timer();
    private TimerTask task;
    //private final Timer timer2 = new Timer();
    //private TimerTask task2;
    private LinkedBlockingQueue<ZhiLingBean.ResultBean> linkedBlockingQueue;
    /* 相机实例 */
    private CameraManager manager;
    private CameraManager2 manager2;

    private static final int cameraWidth = 800;
    private static final int cameraHeight = 600;
   // int screenState = 0;// 0 横 1 竖
    TanChuangThread tanChuangThread;
    // private ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
    private int dw, dh;
    private static final ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
    private static final ConcurrentHashMap<String, String> concurrentHashMap2 = new ConcurrentHashMap<String, String>();
    private BaoCunBean baoCunBean = null;
    private TimeChangeReceiver timeChangeReceiver;
   // private Handler mHandler;
    private FacePassHandler paAccessControl=null;
   // private FacePassHandler paAccessControl2=null;
 //   private Float mCompareThres;
  //  private static String faceId = "";
   // private long feature2 = -1;
    private NV21ToBitmap nv21ToBitmap;
   // private SoundPool soundPool;
    //定义一个HashMap用于存放音频流的ID
  //  private HashMap<Integer, Integer> musicId = new HashMap<>();
    //private Timer mTimer;//距离感应
    //private TimerTask mTimerTask;//距离感应
    private String JHM = null;
    TextView tvTitle_Ir,wangluo;
//    TextView tvName_Ir;//识别结果弹出信息的名字
//    TextView tvTime_Ir;//识别结果弹出信息的时间
//    TextView tvFaceTips_Ir;//识别信息提示
   // LinearLayout layout_loadbg_Ir;//识别提示大框
   // RelativeLayout layout_true_gif_Ir, layout_error_gif_Ir;//蓝色图片动画 红色图片动画
    //ImageView iv_true_gif_in_Ir, iv_true_gif_out_Ir, iv_error_gif_in_Ir, iv_error_gif_out_Ir;//定义旋转的动画
   // Animation gifClockwise, gifAntiClockwise;
   // LinearInterpolator lir_gif;
    private boolean isGET = true;
    private int cishu=5;
    private int jidianqi=6000;
    //private Lztek lztek=null;
  //  private CameraPreviewData mCurrentImage;
    ArrayBlockingQueue<FacePassDetectionResult> mDetectResultQueue;
    ArrayBlockingQueue<FacePassImage> mFeedFrameQueue;
    RecognizeThread mRecognizeThread;
    FeedFrameThread mFeedFrameThread;
    private static final String group_name = "facepasstestx";
    private static StringBuilder builder=null;
    private int timeall = 0;
    private boolean isA=false,isB=false;
   // private int dogTime=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        appId = Auth.getInstance(this).getAppId();
        appKey = Auth.getInstance(this).getAppKey();
        secretKey = Auth.getInstance(this).getSecretKey();
        sn = Auth.getInstance(this).getSn(); // 纯离线合成必须有此参数；离在线合成SDK没有此参数

       // initTTs();
        builder=new StringBuilder();
        baoCunBean = MMKV.defaultMMKV().decodeParcelable("saveBean",BaoCunBean.class);
       // baoCunBean.setMoshengrenPanDing(3);
       // MMKV.defaultMMKV().encode("saveBean",baoCunBean);
        mDetectResultQueue = new ArrayBlockingQueue<FacePassDetectionResult>(5);
        mFeedFrameQueue = new ArrayBlockingQueue<FacePassImage>(1);
        MyApplication.myApplication.addActivity(this);
//        try {
//            lztek=Lztek.create(MyApplication.ampplication);
//           // lztek.watchDogEnable();
//            //lztek.gpioEnable(218);
//           // lztek.setGpioOutputMode(218);
//        }catch (NoClassDefFoundError error){
//            error.printStackTrace();
//        }
        /*
        if (baoCunBean.getDangqianChengShi2()!=null){
            switch (baoCunBean.getDangqianChengShi2()){
                case "智连":
                    jiqiType=0;

                    break;
                case "亮钻":
                    jiqiType=1;

                    break;
                case "TY":
                    jiqiType=2;
                    break;
            }
        }*/

        JHM = baoCunBean.getJihuoma();
        if (JHM == null)
            JHM = "";

        //每分钟的广播
        // private TodayBean todayBean = null;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);
        linkedBlockingQueue = new LinkedBlockingQueue<>(1);
        EventBus.getDefault().register(this);//订阅
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dw = dm.widthPixels;
        dh = dm.heightPixels;
        nv21ToBitmap = new NV21ToBitmap(MianBanJiActivity3.this);
        /* 初始化界面 */
        //  Log.d("MianBanJiActivity3", "jh:" + baoCunBean);
        //初始化soundPool,设置可容纳12个音频流，音频流的质量为5，
//        AudioAttributes abs = new AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .build();
//        soundPool = new SoundPool.Builder()
//                .setMaxStreams(10)   //设置允许同时播放的流的最大值
//                .setAudioAttributes(abs)   //完全可以设置为null
//                .build();
//        //通过load方法加载指定音频流，并将返回的音频ID放入musicId中
//        musicId.put(1, soundPool.load(this, R.raw.tongguo, 1));
//        musicId.put(2, soundPool.load(this, R.raw.wuquanxian, 1));
//        musicId.put(3, soundPool.load(this, R.raw.xinxibupipei, 1));
//        musicId.put(4, soundPool.load(this, R.raw.xianshibie, 1));
//        musicId.put(5, soundPool.load(this, R.raw.shuaka, 1));


        baoCunBean.setHoutaiDiZhi("http://172.17.8.32:8087/front");
      //  MMKV.defaultMMKV().encode("saveBean",baoCunBean);

        initView();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Realm realm = Realm.getDefaultInstance();
//                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        realm.deleteAll();
//                    }
//                });
//            }
//        }).start();

        //serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), baoCunBean.getPort());
        //serverManager.setMyServeInterface(MianBanJiActivity3.this);
      //  serverManager.startServer();


        if (MyApplication.driver.ResumeUsbList()!=0){
            faceName.setVisibility(View.VISIBLE);
            faceName.setText("门禁开关打开失败");
        }else {
            try {
                if (!MyApplication.driver.UartInit()){
                    faceName.setVisibility(View.VISIBLE);
                    faceName.setText("门禁开关初始化失败");
                }
            }catch (Exception e){
                e.printStackTrace();
                faceName.setVisibility(View.VISIBLE);
                faceName.setText("门禁开关初始化失败");
            }
        }



        if (baoCunBean != null) {
            try {
                if (baoCunBean.getJidianqi()!=0){
                    jidianqi=baoCunBean.getJidianqi()*1000;
                }
                if (baoCunBean.getMoshengrenPanDing()!=0){
                    cishu=baoCunBean.getMoshengrenPanDing();
                }

                FacePassHandler.initSDK(getApplicationContext());
                FacePassUtil util = new FacePassUtil();
                util.init(MianBanJiActivity3.this, getApplicationContext(), SettingVar.faceRotation, baoCunBean);

            } catch (Exception e) {
               TastyToast.makeText(MianBanJiActivity3.this,"初始化失败"+e.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR).show();

            }
        }


        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netWorkStateReceiver, filter);
        }


        tanChuangThread = new TanChuangThread();
        tanChuangThread.start();

        mRecognizeThread = new RecognizeThread();
        mRecognizeThread.start();
        mFeedFrameThread = new FeedFrameThread();
        mFeedFrameThread.start();


//        mHandler = new Handler();
//        Runnable runnable=new Runnable() {
//            @Override
//            public void run() {
//                dogTime++;
//                if (dogTime>=4){
//                    dogTime=0;
//                    if (lztek!=null){
//                        Log.d("MianBanJiActivity3", "lztek.watchDogFeed():" + lztek.watchDogFeed());
//                        Log.d("MianBanJiActivity3", "喂狗");
//                    }
//                }
//                mHandler.postDelayed(this,1000);
//            }
//        };
//
//        mHandler.postDelayed(runnable, 500);//延时100毫秒
       // init_NFC();



//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SystemClock.sleep(5000);
//
//              Subject subject = DBUtils.getSubjectDao().getSubjectById("248");
//              Log.d("MianBanJiActivity3", "subject:" + subject);
//
//            }
//        }).start();


//        if (baoCunBean.isHuoTi()) {
//            if (SettingVar.cameraId == 1) {
//                manager2.open(getWindowManager(), 0, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
//            } else {
//                manager2.open(getWindowManager(), 1, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
//            }
//        }


    //    guanPing();//关屏

        if (baoCunBean.isLight()){//双摄像头
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(30000);
                    Log.d("MianBanJiActivity3", "检测2个头是否有数据");
                    if (!isB || !isA){
                        try {
                            DengUT.reboot();
                            // lztek.hardReboot();
                        }catch (Exception e){
                            Log.d("MianBanJiActivity3", "亮钻设备接口异常"+e.getMessage());
                        }
                    }
                }
            }).start();
        }else {//单摄像头
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(30000);
                    Log.d("MianBanJiActivity3", "检测2个头是否有数据");
                    if (!isB && !isA){
                        try {
                            DengUT.reboot();
                            // lztek.hardReboot();
                        }catch (Exception e){
                            Log.d("MianBanJiActivity3", "亮钻设备接口异常"+e.getMessage());
                        }
                    }
                }
            }).start();
        }

    }


    private void openDoor(){
        byte[] to_send = toByteArray("A00101A2");//开
        MyApplication.driver.WriteData(to_send,to_send.length);
    }

    private void colseDoor(){
        byte[] to_send = toByteArray("A00100A1");//关
        MyApplication.driver.WriteData(to_send,to_send.length);
        builder.delete(0,builder.length());
        concurrentHashMap2.clear();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTitle_Ir.setText("");
            }
        });

    }
    /**
     * 将String转化为byte[]数组
     * @param arg
     *            需要转换的String对象
     * @return 转换后的byte[]数组
     */
    private byte[] toByteArray(String arg) {
        if (arg != null) {
            /* 1.先去除String中的' '，然后将String转换为char数组 */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }
            /* 将char数组中的值转成一个实际的十进制数组 */
            int EvenLength = (length % 2 == 0) ? length : length + 1;
            if (EvenLength != 0) {
                int[] data = new int[EvenLength];
                data[EvenLength - 1] = 0;
                for (int i = 0; i < length; i++) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i] - '0';
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i] - 'a' + 10;
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i] - 'A' + 10;
                    }
                }
                /* 将 每个char的值每两个组成一个16进制数据 */
                byte[] byteArray = new byte[EvenLength / 2];
                for (int i = 0; i < EvenLength / 2; i++) {
                    byteArray[i] = (byte) (data[i * 2] * 16 + data[i * 2 + 1]);
                }
                return byteArray;
            }
        }
        return new byte[] {};
    }


//    public void onViewClicked() {
//        if (baoCunBean.isShowShiPingLiu()){
//            MiMaDialog3 miMaDialog=new MiMaDialog3(MianBanJiActivity3.this,baoCunBean.getMima2());
//            WindowManager.LayoutParams params= miMaDialog.getWindow().getAttributes();
//            params.width=dw;
//            params.height=dh+60;
//            miMaDialog.getWindow().setGravity(Gravity.CENTER);
//            miMaDialog.getWindow().setAttributes(params);
//            miMaDialog.show();
//        }
//    }




    @Override
    protected void onResume() {
        Log.d("MianBanJiActivity3", "重新开始");
        super.onResume();

        manager.open(getWindowManager(), SettingVar.cameraId, cameraWidth, cameraHeight);//前置是1
        if (SettingVar.cameraId==1){
            manager2.open(getWindowManager(), 0, cameraWidth, cameraHeight);//最后一个参数是红外预览方向
        }else {
            manager2.open(getWindowManager(), 1, cameraWidth, cameraHeight);//最后一个参数是红外预览方向
        }
       // Log.d("MianBanJiActivity3", "开始服务");
      //  startService(new Intent(this, SendMsgService.class));
    }


//    @Override
//    public void onStarted(String ip) {
//        Log.d("MianBanJiActivity3", "小服务器启动" + ip);
//    }
//
//
//    @Override
//    public void onStopped() {
//        Log.d("MianBanJiActivity3", "小服务器停止");
//    }
//
//    @Override
//    public void onException(Exception e) {
//        Log.d("MianBanJiActivity3", "小服务器异常" + e);
//    }


    /* 相机回调函数 */
    @Override
    public void onPictureTaken(CameraPreviewData cameraPreviewData) {
       // Log.d("MianBanJiActivity3", "cameraPreviewData.front1:" + cameraPreviewData.front);
        /* 如果SDK实例还未创建，则跳过 */
        isA=true;
        if (paAccessControl == null) {
            return;
        }
        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
        FacePassImage image;
        try {
            image = new FacePassImage(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height, SettingVar.faceRotation, FacePassImageType.NV21);
        } catch (FacePassException e) {
            e.printStackTrace();
            return;
        }
        mFeedFrameQueue.offer(image);
    }

    @Override
    public void onPictureTaken2(CameraPreviewData2 cameraPreviewData) {
       // Log.d("MianBanJiActivity3", "cameraPreviewData.front2:" + cameraPreviewData.front);
        isB=true;
        if (paAccessControl == null) {
            return;
        }
        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
        FacePassImage image;
        try {
            image = new FacePassImage(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height, SettingVar.faceRotation, FacePassImageType.NV21);
        } catch (FacePassException e) {
            e.printStackTrace();
            return;
        }
        mFeedFrameQueue.offer(image);
    }

    private class FeedFrameThread extends Thread {
        boolean isIterrupt;

        @Override
        public void run() {
            while (!isIterrupt) {
                try {
                    FacePassImage image = mFeedFrameQueue.take();
                    /* 将每一帧FacePassImage 送入SDK算法， 并得到返回结果 */
                    FacePassDetectionResult detectionResult = null;
                    detectionResult = paAccessControl.feedFrame(image);
                   // Log.d("FeedFrameThread", "1 mDetectResultQueue.size = " + mDetectResultQueue.size());
                   // Log.d("FeedFrameThread", "detectionResult:" + detectionResult);
                    if (detectionResult != null && detectionResult.faceList.length > 0) {
                        if (detectionResult.message.length != 0) {
                            //  Log.d("FeedFrameThread", "插入");
                            // DengUT.getInstance(baoCunBean).openWrite();
                            //showUIResult(2,"","");
                            mDetectResultQueue.offer(detectionResult);
                            //   Log.d("ggggg", "1 mDetectResultQueue.size = " + mDetectResultQueue.size());
                        }

                      //  Log.d("FeedFrameThread", "没人"+DengUT.isOPEN+DengUT.isOPENRed+DengUT.isOPENGreen);
//                        if (DengUT.isOPEN || DengUT.isOPENRed || DengUT.isOPENGreen) {
//                            Log.d("FeedFrameThread", "没人2");
//                            DengUT.isOPEN = false;
//                            DengUT.isOPENGreen = false;
//                            DengUT.isOPENRed = false;
//                            DengUT.isOpenDOR = false;
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tvTitle_Ir.setText("");
//                                }
//                            });
//                          //  DengUT.getInstance(baoCunBean).closeWrite();
//                          //  showUIResult(1,"","");
//                            //启动定时器或重置定时器
////                            if (task2 != null) {
////                                task2.cancel();
////                                //timer.cancel();
////                                task2 = new TimerTask() {
////                                    @Override
////                                    public void run() {
////                                        Message message = new Message();
////                                        message.what = 444;
////                                        mHandler.sendMessage(message);
////                                    }
////                                };
////                                timer2.schedule(task2, 5000);
////                            } else {
////                                task2 = new TimerTask() {
////                                    @Override
////                                    public void run() {
////                                        Message message = new Message();
////                                        message.what = 444;
////                                        mHandler.sendMessage(message);
////                                    }
////                                };
////                                timer2.schedule(task2, 5000);
////                            }
//                        }
                    }
//                    else {
////                        final FacePassFace[] bufferFaceList = detectionResult.faceList;
////                        FacePassMouthOccAttr attr=bufferFaceList[0].mouthOccAttr;
////                        if (attr.is_valid){//有效
////                            String kouzhao="";
////                            switch (attr.mouth_occ_status){
////                                case 0:
////                                    kouzhao="未佩戴口罩";
////                                    break;
////                                case 1:
////                                    kouzhao="面具遮挡";
////                                    break;
////                                case 2:
////                                    kouzhao="已佩戴口罩";
////                                    break;
////                                case 3:
////                                    kouzhao="其他遮挡";
////                                    break;
////                                default:
////                                    kouzhao="";
////                                    break;
////                            }
////                            String finalKouzhao = kouzhao;
////                            runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    tvFaceTips_Ir.setText(finalKouzhao);
////                                }
////                            });
////                        }
//
//                        /*离线模式，将识别到人脸的，message不为空的result添加到处理队列中*/
//                        if (detectionResult.message.length != 0) {
//                            //  Log.d("FeedFrameThread", "插入");
//                           // DengUT.getInstance(baoCunBean).openWrite();
//                            //showUIResult(2,"","");
//                            mDetectResultQueue.offer(detectionResult);
//                            //   Log.d("ggggg", "1 mDetectResultQueue.size = " + mDetectResultQueue.size());
//                        }
////                        if (!DengUT.isOPEN) {
////                            DengUT.isOPEN = true;
////                        }
//
//                    }
                    //     }

                } catch (InterruptedException | FacePassException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isIterrupt = true;
            super.interrupt();
        }
    }


    private class TanChuangThread extends Thread {
        boolean isRing;

        @Override
        public void run() {
            while (!isRing) {
                try {
                    //有动画 ，延迟到一秒一次
                    ZhiLingBean.ResultBean commandsBean = linkedBlockingQueue.take();
                    if (paAccessControl==null){
                        continue;
                    }
                    switch (commandsBean.getCommand()) {
                        case 1001://新增
                        {

                            Bitmap bitmap = null;
                            try {
                                bitmap = Glide.with(MianBanJiActivity3.this).asBitmap()
                                        .load(commandsBean.getImage())
                                        // .sizeMultiplier(0.5f)
                                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                        .get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            FacePassAddFaceResult detectResult = null;
                            if (bitmap != null) {
                                detectResult = paAccessControl.addFace(bitmap);
                              //  Log.d("TanChuangThread", "detectResult:" + detectResult);
                                if (detectResult != null && detectResult.result==0) {
                                   // BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3, commandsBean.getId() + ".png");
                                    //先查询有没有
                                    try {
                                        byte [] faceToken=detectResult.faceToken;
                                        Subject subject2 = DBUtils.getSubjectDao().getSubjectById(commandsBean.getId());
                                        if (subject2 != null) {
                                            try {
                                                paAccessControl.deleteFace(subject2.getTeZhengMa().getBytes());
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            DBUtils.getSubjectDao().delete(subject2);
                                        }
                                        Subject subject = new Subject();
                                        subject.setTeZhengMa(new String(faceToken));
                                        subject.setId(commandsBean.getId());
                                        subject.setPeopleType(commandsBean.getPepopleType() + "");//0是员工 1是访客
                                        subject.setName(commandsBean.getName());
                                        subject.setDepartmentName(commandsBean.getDepartmentName());
                                        subject.setIsOpen(commandsBean.getIsOpen());
                                        try {
                                            DBUtils.getSubjectDao().insertAll(subject);
                                            paAccessControl.bindGroup(group_name,faceToken);
                                            Log.d("MyReceiver", "单个员工入库成功" + subject.getName());
                                        } catch (FacePassException e) {
                                           Log.d("TanChuangThread", e.getMessage()+"添加人员异常1");
                                        }

                                     HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(), commandsBean.getPepopleType(),
                                            "0", "入库成功", commandsBean.getShortId(), JHM);
                                     DBUtils.getHuiFuBeanDao().insertAll(huiFuBean);

                                    } catch (Exception e) {
                                        Log.d("TanChuangThread", e.getMessage()+"添加人员异常2");
                                        try {
                                            HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                                    commandsBean.getPepopleType(), "-1", e.getMessage() + "", commandsBean.getShortId(), JHM);
                                            DBUtils.getHuiFuBeanDao().insertAll(huiFuBean);
                                        }catch (Exception e1){
                                            e1.printStackTrace();
                                        }
                                    }
                                } else {
                                    try {
                                        HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                                commandsBean.getPepopleType(), "-1", "图片质量不合格", commandsBean.getShortId(), JHM);

                                        DBUtils.getHuiFuBeanDao().insertAll(huiFuBean);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                try {
                                    HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                            commandsBean.getPepopleType(), "-1", "图片下载失败", commandsBean.getShortId(), JHM);
                                    DBUtils.getHuiFuBeanDao().insertAll(huiFuBean);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            break;
                        }
                        case 1002://修改
                        {
                            Subject subject=null;
                            try {

                                 subject = DBUtils.getSubjectDao().getSubjectById(commandsBean.getId());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if (subject != null) {
                                FacePassAddFaceResult faceResult = null;
                                Bitmap bitmap = null;
                                try {
                                    if (commandsBean.getImage() != null)
                                        bitmap = Glide.with(MianBanJiActivity3.this).asBitmap()
                                                .load(commandsBean.getImage())
                                                // .sizeMultiplier(0.5f)
                                                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                                .get();
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                                if (bitmap != null) {//有图片
                                    faceResult = paAccessControl.addFace(bitmap);
                                    if (faceResult != null && faceResult.result==0) {
                                        //BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3, commandsBean.getId() + ".png");
                                        try {
                                            byte [] faceToken=faceResult.faceToken;
                                            paAccessControl.bindGroup(group_name, faceToken);
                                            paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
                                            if (commandsBean.getName() != null)
                                                subject.setName(commandsBean.getName());
                                            if (commandsBean.getDepartmentName() != null) {
                                                subject.setDepartmentName(commandsBean.getDepartmentName());
                                            }
                                            if (commandsBean.getPepopleType() != null) {
                                                subject.setPeopleType(commandsBean.getPepopleType());
                                            }
                                            subject.setIsOpen(commandsBean.getIsOpen());

                                            subject.setTeZhengMa(new String(faceToken));
                                            DBUtils.getSubjectDao().insertAll(subject);
                                            HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                                    commandsBean.getPepopleType(), "0", "修改成功", commandsBean.getShortId(), JHM);

                                            DBUtils.getHuiFuBeanDao().insertAll(huiFuBean);

                                        } catch (Exception e) {
                                            Log.d("TanChuangThread", e.getMessage()+"添加人员异常3");
                                            HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                                    commandsBean.getPepopleType(), "-1", e.getMessage() + "", commandsBean.getShortId(), JHM);
                                            DBUtils.getHuiFuBeanDao().insertAll(huiFuBean);
                                        }

                                    } else {
                                        HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                                commandsBean.getPepopleType(), "-1", "图片质量不合格", commandsBean.getShortId(), JHM);
                                        DBUtils.getHuiFuBeanDao().insertAll(huiFuBean);
                                    }
                                } else {//没图片只修改其他值
                                    String name = commandsBean.getName();
                                    String bumen = commandsBean.getDepartmentName();
                                    String pepopleType = commandsBean.getPepopleType();
                                    if (name != null)
                                        subject.setName(name);
                                    if (bumen != null) {
                                        subject.setDepartmentName(bumen);
                                    }
                                    if (pepopleType != null) {
                                        subject.setPeopleType(pepopleType);
                                    }
                                    subject.setIsOpen(commandsBean.getIsOpen());
                                    DBUtils.getSubjectDao().insertAll(subject);
                                    HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                            commandsBean.getPepopleType(), "0", "修改成功", commandsBean.getShortId(), JHM);
                                    DBUtils.getHuiFuBeanDao().insertAll(huiFuBean);
                                }
                            } else {
                                HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                        commandsBean.getPepopleType(), "-1", "未找到人员信息", commandsBean.getShortId(), JHM);
                                DBUtils.getHuiFuBeanDao().insertAll(huiFuBean);
                            }
                        }
                        break;
                        case 1003://删除
                        {
                            Subject subject=null;
                            try {
                                subject = DBUtils.getSubjectDao().getSubjectById(commandsBean.getId());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if (subject != null) {
                                try {
                                    paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
                                }catch (Exception e){
                                    Log.d("TanChuangThread", e.getMessage()+"删除人员异常4");
                                }
                                DBUtils.getSubjectDao().delete(subject);
                                HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                        commandsBean.getPepopleType(), "0", "删除成功", commandsBean.getShortId(), JHM);
                                DBUtils.getHuiFuBeanDao().insertAll(huiFuBean);
                            }else {
                                HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                        commandsBean.getPepopleType(), "-1", "未找到人员信息", commandsBean.getShortId(), JHM);
                                DBUtils.getHuiFuBeanDao().insertAll(huiFuBean);
                            }
                        }
                        break;
                        case 1004://数据同步
                        {
                            link_infoSync();

                        }
                        break;
                       /* case 1005://新增卡
                        {
                            IDCardBean ii =new IDCardBean();
                            ii.setId(System.currentTimeMillis());
                            ii.setIdCard(commandsBean.getCardID());
                            ii.setName(commandsBean.getName());
                            idCardBeanBox.put(ii);
                            HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                    commandsBean.getPepopleType(), "0", "添加ID卡成功", commandsBean.getShortId(), JHM);
                            huiFuBeanBox.put(huiFuBean);
                            isLink = false;

                        }*/
                        /*break;*/
                       /* case 1006://删除卡
                        {
                            List<IDCardBean> ii =idCardBeanBox.query().equal(IDCardBean_.idCard,commandsBean.getCardID()).build().find();
                            for (IDCardBean bean:ii){
                                idCardBeanBox.remove(bean);
                            }
                            HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                    commandsBean.getPepopleType(), "0", "删除ID卡成功", commandsBean.getShortId(), JHM);
                            huiFuBeanBox.put(huiFuBean);
                            isLink = false;
                        }*/
                       /* break;*/
                    }
                } catch (Exception e) {
                    Log.d("TanChuangThread", e.getMessage()+"添加人员异常10");
                }
            }
        }

        @Override
        public void interrupt() {
            isRing = true;
            // Log.d("RecognizeThread", "中断了弹窗线程");
            super.interrupt();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MianBanJiActivity3", "暂停");

//        if (mTimer != null) {
//            mTimer.cancel();
//            mTimer = null;
//        }
//        if (mTimerTask != null) {
//            mTimerTask.cancel();
//            mTimerTask = null;
//        }

    }


    private void initView() {
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SharedPreferences preferences = getSharedPreferences(SettingVar.SharedPrefrence, Context.MODE_PRIVATE);
        SettingVar.isSettingAvailable = preferences.getBoolean("isSettingAvailable", SettingVar.isSettingAvailable);
        SettingVar.cameraId = preferences.getInt("cameraId", SettingVar.cameraId);
        SettingVar.faceRotation = preferences.getInt("faceRotation", SettingVar.faceRotation);
        SettingVar.cameraPreviewRotation = preferences.getInt("cameraPreviewRotation", SettingVar.cameraPreviewRotation);
        SettingVar.cameraFacingFront = preferences.getBoolean("cameraFacingFront", SettingVar.cameraFacingFront);
        SettingVar.cameraPreviewRotation2 = preferences.getInt("cameraPreviewRotation2", SettingVar.cameraPreviewRotation2);
        SettingVar.faceRotation2 = preferences.getInt("faceRotation2", SettingVar.faceRotation2);
        SettingVar.msrBitmapRotation = preferences.getInt("msrBitmapRotation", SettingVar.msrBitmapRotation);

        setContentView(R.layout.activity_mianbanji3);
        faceName=findViewById(R.id.faceName);
        ImageView shezhi = findViewById(R.id.shezhi);
        shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MiMaDialog4 miMaDialog = new MiMaDialog4(MianBanJiActivity3.this, baoCunBean.getMima());
                WindowManager.LayoutParams params = miMaDialog.getWindow().getAttributes();
                params.width = dw;
                params.height = dh;
//                miMaDialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                miMaDialog.getWindow().setAttributes(params);
                miMaDialog.show();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //  private boolean isOP = true;
        int heightPixels = displayMetrics.heightPixels;
        int widthPixels = displayMetrics.widthPixels;
        SettingVar.mHeight = heightPixels;
        SettingVar.mWidth = widthPixels;
        /* 初始化界面 */
        manager = new CameraManager();
        /* 显示人脸位置角度信息 */
        // private XiuGaiGaoKuanDialog dialog = null;
        /* 相机预览界面 */
        CameraPreview cameraView = findViewById(R.id.preview1);
        manager.setPreviewDisplay(cameraView);
        /* 注册相机回调函数 */
        manager.setListener(this);

        manager2 = new CameraManager2();
        CameraPreview2 cameraView2 = findViewById(R.id.preview22);
        manager2.setPreviewDisplay(cameraView2);
        /* 注册相机回调函数 */
        manager2.setListener(this);

//        tvName_Ir = findViewById(R.id.tvName_Ir);//名字
//        tvTime_Ir = findViewById(R.id.tvTime_Ir);//时间
//        tvFaceTips_Ir = findViewById(R.id.tvFaceTips_Ir);//识别信息提示
//        layout_loadbg_Ir = findViewById(R.id.layout_loadbg_Ir);//头像区域的显示的底图背景
//
//        layout_true_gif_Ir = findViewById(R.id.layout_true_gif_Ir);
//        layout_error_gif_Ir = findViewById(R.id.layout_error_gif_Ir);
//        iv_true_gif_in_Ir = findViewById(R.id.iv_true_gif_in_Ir);
//        iv_true_gif_out_Ir = findViewById(R.id.iv_true_gif_out_Ir);
//        iv_error_gif_in_Ir = findViewById(R.id.iv_error_gif_in_Ir);
//        iv_error_gif_out_Ir = findViewById(R.id.iv_error_gif_out_Ir);
        tvTitle_Ir = findViewById(R.id.tvTitle_Ir);
        wangluo=findViewById(R.id.wangluo);
//        //region 动画
//        gifClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anim_face_clockwise);
//        gifAntiClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anim_face_anti_clockwise);
//        lir_gif = new LinearInterpolator();//设置为匀速旋转
//        gifClockwise.setInterpolator(lir_gif);
//        gifAntiClockwise.setInterpolator(lir_gif);

//        iv_true_gif_out_Ir.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        iv_error_gif_out_Ir.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        iv_true_gif_out_Ir.startAnimation(gifClockwise);
//        iv_error_gif_out_Ir.startAnimation(gifClockwise);
//        AssetManager mgr = getAssets();
//        Typeface tf = Typeface.createFromAsset(mgr, "fonts/hua.ttf");
//        tvTitle_Ir.setTypeface(tf);

//        if (baoCunBean.getWenzi1() == null) {
//            tvTitle_Ir.setText("");
//        } else {
//            tvTitle_Ir.setText(baoCunBean.getWenzi1());
//        }

//        if (baoCunBean.getLogo()!=null){
//            try {
//                logo.setImageBitmap(BitmapUtil.base64ToBitmap(baoCunBean.getLogo()));
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
        //showUIResult(1,"","");



    }



    private class RecognizeThread extends Thread {

        boolean isInterrupt;

        @Override
        public void run() {
            while (!isInterrupt) {
                try {
                    FacePassDetectionResult detectionResult = mDetectResultQueue.take();
                    // byte[] detectionResult = mDetectResultQueue.take();
                    FacePassRecognitionResult[] recognizeResult = paAccessControl.recognize(group_name, detectionResult.message);
                  //   Log.d("RecognizeThread", "识别线程");
                    if (recognizeResult != null && recognizeResult.length > 0) {

                        for (FacePassRecognitionResult result : recognizeResult) {
                          //  Log.d("RecognizeThread", "result.trackId:" + result.trackId);
                            //String faceToken = new String(result.faceToken);
                         //   Log.d("RecognizeThread", "paAccessControl.getConfig().searchThreshold:" + paAccessControl.getConfig().searchThreshold);
                          //  Log.d("RecognizeThread", "result.recognitionState:" + result.recognitionState);
                          //  Log.d("RecognizeThread", "result.feedback.searchScore:" + result.detail.searchScore);
                          //  Log.d("RecognizeThread", "result.feedback.searchThreshold:" + result.detail.searchThreshold);
                            if (0 == result.recognitionState) {
                                //识别的
                                //  getFaceImageByFaceToken(result.trackId, faceToken);
                              //  Log.d("RecognizeThread", "result.detail.searchScore:" + result.detail.searchScore);
                               // Log.d("RecognizeThread", "识别了");
                                //  Log.d("RecognizeThread", subjectBox.getAll().get(0).toString());
                              //  Log.d("RecognizeThread", new String(result.faceToken)+"点点滴滴faceToken");
                                Subject subject = DBUtils.getSubjectDao().getSubjectByTZM(new String(result.faceToken));
                                // Log.d("RecognizeThread", "subject:" + subject);
                                if (subject != null) {
                                    if (concurrentHashMap2.get(subject.getId())==null){
                                        concurrentHashMap2.put(subject.getId(),"ID:"+subject.getId()+" 姓名:"+subject.getName()+"\n");
                                        builder.append(concurrentHashMap2.get(subject.getId()));
                                    }else {
                                        continue;
                                    }
//                                    Enumeration<String> stringEnumeration=concurrentHashMap2.elements();
//                                    while (stringEnumeration.hasMoreElements()){
//                                      //  Log.d("RecognizeThread", "stringEnumeration:"+stringEnumeration.nextElement());
//                                        if (stringEnumeration.nextElement().contains(subject.getId())){
//                                            isAA=true;
//                                            break;
//                                        }
//                                    }
//                                    if (isAA){
//                                        continue;
//                                    }
//                                    Enumeration<String> stringEnumeration2=concurrentHashMap2.elements();
//                                    while (stringEnumeration2.hasMoreElements()){
//
//                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tvTitle_Ir.setText(builder.toString());
                                        }
                                    });

                                   //speak(subject.getName());
                                   // if (subject.getPeopleType().equals("1")){
                                        if (subject.getIsOpen()==0){
                                            openDoor();
                                        }
                                        if (task != null) {
                                            task.cancel();
                                            task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    colseDoor();
                                                }
                                            };
                                            timer.schedule(task, baoCunBean.getPort());
                                        } else {
                                            task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    colseDoor();
                                                }
                                            };
                                            timer.schedule(task, baoCunBean.getPort());
                                        }
                                  //  }
                                    for (int i = 0; i < detectionResult.faceList.length; i++) {
                                        FacePassImage images = detectionResult.images[i];
                                        if (images.trackId == result.trackId) {
                                       //     Log.d("RecognizeThread", "detectionResult.faceList[i].mouthOccAttr.is_valid:" + detectionResult.faceList[i].mouthOccAttr.is_valid);
                                         //   Log.d("RecognizeThread", "detectionResult.faceList[i].mouthOccAttr.mouth_occ_status:" + detectionResult.faceList[i].mouthOccAttr.mouth_occ_status);
//                                            if (detectionResult.faceList[i].mouthOccAttr.is_valid){
//                                                String kouzhao="";
//                                                switch (detectionResult.faceList[i].mouthOccAttr.mouth_occ_status){
//                                                    case 0:
//                                                        kouzhao="未佩戴口罩";
//                                                        break;
//                                                    case 1:
//                                                        kouzhao="面具遮挡";
//                                                        break;
//                                                    case 2:
//                                                        kouzhao="已佩戴口罩";
//                                                        break;
//                                                    case 3:
//                                                        kouzhao="其他遮挡";
//                                                        break;
//                                                    default:
//                                                         kouzhao="";
//                                                        break;
//                                                }
//                                                showUIResult(4,subject.getName(),subject.getDepartmentName());
//                                            }
                                            final Bitmap fileBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
                                          //  String paths = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ruitongzipmbj";
                                          //  boolean tt = nv21ToBitmap.saveBitmap(fileBitmap, paths, time + ".png");
                                            link_shangchuanshualian(subject.getId(), fileBitmap, subject.getPeopleType() + "");
                                          //  if (tt) {
                                           //     subject.setZpPath(paths + File.separator + time + ".png");
                                           //     Log.d("RecognizeThread", "subjectBox.put(subject):" + subjectBox.put(subject));
                                         //   }
                                            break;
                                        }
                                    }
                                  //  showUIResult(4,subject.getName(),subject.getDepartmentName());
                                    DengUT.isOPEN = true;
                                 //   msrBitmap = nv21ToBitmap.nv21ToBitmap(result.feedback.rgbImage.image, result.feedback.rgbImage.width, result.feedback.rgbImage.height);

                                } else {
                                    EventBus.getDefault().post("没有查询到人员信息");
                                }

                            } else {
                               // Log.d("RecognizeThread", "未识别"+result.trackId+cishu);
                                //未识别的
                                // 防止concurrentHashMap 数据过多 ,超过一定数据 删除没用的
                                if (concurrentHashMap.size() > 12) {
                                    concurrentHashMap.clear();
                                }
                                if (concurrentHashMap.get(result.trackId) == null) {
                                    //找不到新增
                                    concurrentHashMap.put(result.trackId, 1);
                                } else {
                                    //找到了 把value 加1
                                    concurrentHashMap.put(result.trackId, (concurrentHashMap.get(result.trackId)) + 1);
                                }
                               // Log.d("RecognizeThread", "RecognizeThread:" + concurrentHashMap.size());
                                //判断次数超过3次
                                if (concurrentHashMap.get(result.trackId) == cishu) {
                                    builder.append("陌生人 ID:").append(result.trackId).append("\n");
                                    //speak("");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tvTitle_Ir.setText(builder.toString());
                                            if (task != null) {
                                                task.cancel();
                                                task = new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        colseDoor();
                                                    }
                                                };
                                                timer.schedule(task, baoCunBean.getPort());
                                            } else {
                                                task = new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        colseDoor();
                                                    }
                                                };
                                                timer.schedule(task, baoCunBean.getPort());
                                            }
                                        }
                                    });
                                   // tID = result.trackId;
                                  //  msrBitmap = nv21ToBitmap.nv21ToBitmap(result.feedback.rgbImage.image, result.feedback.rgbImage.width, result.feedback.rgbImage.height);

                                    for (int i = 0; i < detectionResult.faceList.length; i++) {
                                        FacePassImage images = detectionResult.images[i];
                                        if (images.trackId == result.trackId) {
                                        //    Log.d("RecognizeThread", "detectionResult.faceList[i].mouthOccAttr.is_valid:" + detectionResult.faceList[i].mouthOccAttr.is_valid);
                                         //   Log.d("RecognizeThread", "detectionResult.faceList[i].mouthOccAttr.mouth_occ_status:" + detectionResult.faceList[i].mouthOccAttr.mouth_occ_status);
//                                            if (detectionResult.faceList[i].mouthOccAttr.is_valid){
//                                                String kouzhao="";
//                                                switch (detectionResult.faceList[i].mouthOccAttr.mouth_occ_status){
//                                                    case 0:
//                                                        kouzhao="未佩戴口罩";
//                                                        break;
//                                                    case 1:
//                                                        kouzhao="面具遮挡";
//                                                        break;
//                                                    case 2:
//                                                        kouzhao="已佩戴口罩";
//                                                        break;
//                                                    case 3:
//                                                        kouzhao="其他遮挡";
//                                                        break;
//                                                    default:
//                                                        kouzhao="";
//                                                        break;
//                                                }
//                                                showUIResult(3,"陌生人","");
//                                            }

                                            final Bitmap fileBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
                                            //  String paths = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ruitongzipmbj";
                                            //  boolean tt = nv21ToBitmap.saveBitmap(fileBitmap, paths, time + ".png");
                                            link_shangchuanshualian("", fileBitmap,  "");
                                            //  if (tt) {
                                            //     subject.setZpPath(paths + File.separator + time + ".png");
                                            //     Log.d("RecognizeThread", "subjectBox.put(subject):" + subjectBox.put(subject));
                                            //   }
                                            break;
                                        }
                                    }
                                  //  showUIResult(3,"陌生人","");
                                    Log.d("RecognizeThread", "陌生人");
                                    DengUT.isOPEN = true;
                                    //   msrBitmap = nv21ToBitmap.nv21ToBitmap(result.feedback.rgbImage.image, result.feedback.rgbImage.width, result.feedback.rgbImage.height);
                                    //   Log.d("RecognizeThread", "入库"+tID);
                                }
                            }
                        }
                    }

                } catch (InterruptedException | FacePassException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isInterrupt = true;
            super.interrupt();
        }

    }




    @Override
    protected void onStop() {
        Log.d("MianBanJiActivity3", "停止");

        if (manager != null) {
            manager.release();
        }
        if (manager2 != null) {
            manager2.release();
        }
//        if (manager != null) {
//            manager.release();
//        }
//        if (manager2 != null) {
//            manager2.release();
//        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("MianBanJiActivity3", "onRestart");
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MianBanJiActivity3", "onStart");
    }


    @Override
    protected void onDestroy() {
        Log.d("MianBanJiActivity3", "onDestroy");
        if (linkedBlockingQueue != null) {
            linkedBlockingQueue.clear();
        }
        if (mFeedFrameQueue != null) {
            mFeedFrameQueue.clear();
        }
        if (mDetectResultQueue!=null){
            mDetectResultQueue.clear();
        }
        if (mFeedFrameThread != null) {
            mFeedFrameThread.isIterrupt = true;
            mFeedFrameThread.interrupt();
        }

        if (tanChuangThread != null) {
            tanChuangThread.isRing = true;
            tanChuangThread.interrupt();
        }

        if (mRecognizeThread != null) {
            mRecognizeThread.isInterrupt = true;
            mRecognizeThread.interrupt();
        }

        unregisterReceiver(timeChangeReceiver);
        unregisterReceiver(netWorkStateReceiver);
        EventBus.getDefault().unregister(this);//解除订阅

        if (task != null)
            task.cancel();
        timer.cancel();
        try {
            MyApplication.driver.CloseDevice();
        }catch (Exception exception){
            exception.printStackTrace();
        }
//
//        if (task2 != null)
//            task2.cancel();
//        timer2.cancel();

        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
            print("释放资源成功");
        }

        Log.d("MianBanJiActivity3", "onDestroy");
        super.onDestroy();
        MyApplication.myApplication.removeActivity(this);
    }

   // private static final int REQUEST_CODE_CHOOSE_PICK = 1;


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (keyCode == KeyEvent.KEYCODE_MENU) {
//              // startActivity(new Intent(MianBanJiActivity3.this, SheZhiActivity2.class));
//              //  finish();
//            }
//
//        }
//
//        return super.onKeyDown(keyCode, event);
//
//    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {
        if (event.equals("ditu123")) {
            // if (baoCunBean.getTouxiangzhuji() != null)
            //    daBg.setImageBitmap(BitmapFactory.decodeFile(baoCunBean.getTouxiangzhuji()));
            //   Log.d("MainActivity101", "dfgdsgfdgfdgfdg");
            return;
        }

        if (event.equals("kaimen")) {
            menjing1();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(jidianqi);
                    menjing2();
                }
            }).start();
            return;
        }
        if (event.equals("guanbimain")) {
            finish();
            Log.d("MianBanJiActivity3", "关闭Mianbanjia");
            return;
        }
        if (event.equals("configs")){
            //更新配置
            baoCunBean = MMKV.defaultMMKV().decodeParcelable("saveBean",BaoCunBean.class);
            if (baoCunBean.getWenzi1() == null) {
                tvTitle_Ir.setText("");
            } else {
                tvTitle_Ir.setText(baoCunBean.getWenzi1());
            }
            if (baoCunBean.getJidianqi()!=0){
                jidianqi=baoCunBean.getJidianqi();
            }
            if (baoCunBean.getMoshengrenPanDing()!=0){
                cishu=baoCunBean.getMoshengrenPanDing();
            }
        }

        if (event.equals("mFacePassHandler")) {
            paAccessControl = MyApplication.myApplication.getFacePassHandler();
            return;
        }

        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, event, TastyToast.LENGTH_LONG, TastyToast.INFO);
        tastyToast.setGravity(Gravity.CENTER, 0, 0);
        tastyToast.show();

    }


    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:
                    //mianBanJiView.setTime(DateUtils.time(System.currentTimeMillis()+""));
                    // String riqi11 = DateUtils.getWeek(System.currentTimeMillis()) + "   " + DateUtils.timesTwo(System.currentTimeMillis() + "");
                    //  riqi.setTypeface(tf);
                   // String xiaoshiss = DateUtils.timeMinute(System.currentTimeMillis() + "");
                    timeall++;
                    if (timeall>=86400) {//两个月
                    // Log.d("TimeChangeReceiver", "ssss");
                        DengUT.reboot();
                    }
//                    if (xiaoshiss.split(":")[1].contains("9")||xiaoshiss.split(":")[1].contains("09")) {
//                        Log.d("TimeChangeReceiver", "ssss");
//                        DengUT.reboot();
//                    }
                    //1分钟一次指令获取
                    if (baoCunBean.getHoutaiDiZhi() != null && !baoCunBean.getHoutaiDiZhi().equals("") && paAccessControl!=null) {
                        if (isGET){
                            isGET=false;
                            link_get_zhiling();
                        }
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


    //获取指令
    private void link_get_zhiling() {
       // Log.d("MianBanJiActivity3", "开始发送获取指令请求");
        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
            return;
        }
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .get()
                .url(baoCunBean.getHoutaiDiZhi() + "/app/getCommands?" + "serialnumber=" + JHM);
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("AllConnects", "请求失败" +call.request().url().toString()+ e.getMessage());
                    isGET=true;
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "获取指令:" + ss);
                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson = new Gson();
                    ZhiLingBean commandsBean = gson.fromJson(jsonObject, ZhiLingBean.class);
                    if (commandsBean != null && commandsBean.getCode() == 0) {
                        for (ZhiLingBean.ResultBean resultBean : commandsBean.getData()) {
                            linkedBlockingQueue.put(resultBean);
                            SystemClock.sleep(3000);
                        }
                        isGET=true;
                        try {
                            com.alibaba.fastjson.JSONObject oo= JSON.parseObject(commandsBean.getMessage());
                            float threshold= Float.parseFloat(oo.getString("threshold"));
                            int interval= Integer.parseInt(oo.getString("interval"));
                            Log.d("MianBanJiActivity3", "threshold"+threshold);
                            Log.d("MianBanJiActivity3", "interval"+interval);
                            if (interval!=baoCunBean.getJidianqi()){
                                baoCunBean.setJidianqi(interval);
                                MMKV.defaultMMKV().encode("saveBean",baoCunBean);
                                jidianqi=interval*1000;
                            }
                            if (threshold!=baoCunBean.getShibieFaZhi()){
                                baoCunBean.setShibieFaZhi(threshold);
                                MMKV.defaultMMKV().encode("saveBean",baoCunBean);
                                RestartAPPTool.restartAPP(MianBanJiActivity3.this);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else {
                        isGET=true;
                    }
                } catch (Exception e) {
                    isGET=true;
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
                }
            }
        });
    }


    //上传记录
    private void link_shangchuanshualian(String id, Bitmap bitmap, String pepopleType) {
        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
            return;
        }
      //  Log.d("AllConnects", baoCunBean.getHoutaiDiZhi());

        Bitmap bb = BitmapUtil.rotateBitmap(bitmap, SettingVar.msrBitmapRotation);
        RequestBody body = null;
        body = new FormBody.Builder()
                .add("id", id + "")
                .add("pepopleType", pepopleType)
                .add("serialnumber", JHM)
                .add("iamge", Bitmap2StrByBase64(bb))
                .build();
       // Log.d("MianBanJiActivity3", "id:"+id);
    //    Log.d("MianBanJiActivity3", "pepopleType:"+pepopleType);
    //    Log.d("MianBanJiActivity3", "Bitmap2StrByBase64:"+Bitmap2StrByBase64(bb));
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/app/updateFaceTake");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "上传识别记录失败", TastyToast.LENGTH_LONG, TastyToast.INFO);
                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
                        tastyToast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();

                    Log.d("AllConnects", "上传识别记录" + ss);

                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");

                }
            }
        });
    }

    //上传记录
    private void link_shuaka(String id,String name) {
        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
            return;
        }
        RequestBody body = null;
        body = new FormBody.Builder()
                .add("id", id)
                .add("name", name)
                .add("serialnumber", JHM)
                .build();
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/app/updateIDcardTake");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "上传识别记录失败", TastyToast.LENGTH_LONG, TastyToast.INFO);
                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
                        tastyToast.show();
                    }
                });
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();

                    Log.d("AllConnects", "上传识别记录" + ss);

                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");

                }
            }
        });
    }

    //数据同步
    private void link_infoSync() {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONArray array = new JSONArray();

        List<HuiFuBean> huiFuBeanList = DBUtils.getHuiFuBeanDao().getAllSubject();
        if (huiFuBeanList.size()==0)
            return;
        final List<HuiFuBean> huiFuBeans = new ArrayList<>(huiFuBeanList);
            for (HuiFuBean bean : huiFuBeans) {
                JSONObject object = new JSONObject();
                try {
                    object.put("pepopleId", bean.getPepopleId());
                    object.put("pepopleType", bean.getPepopleType());
                    object.put("type", bean.getType());
                    object.put("msg", bean.getMsg());
                    object.put("shortId", bean.getShortId());
                    object.put("serialnumber", JHM);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(object);
            }

        Log.d("MianBanJiActivity3", "数据同步：" + array.toString());
        RequestBody body = RequestBody.create(array.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/app/infoSync");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "数据同步请求失败" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "数据同步:" + ss);
                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                    if (jsonObject.get("code").getAsInt()==0){
                        for (HuiFuBean bean : huiFuBeans) {
                            DBUtils.getHuiFuBeanDao().delete(bean);
                        }
                    }
                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "数据同步");
                }
            }
        });
    }

    //信鸽信息处理
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(XGBean xgBean) {


    }


    private void guanPing() {
        Intent intent = new Intent();
        intent.setAction("LYD_SHOW_NAVIGATION_BAR");
        intent.putExtra("type", 0);
        this.sendBroadcast(intent);
        sendBroadcast(new Intent("com.android.internal.policy.impl.hideNavigationBar"));
        sendBroadcast(new Intent("com.android.systemui.statusbar.phone.statusclose"));
       //8寸防水面板机
        try {
         Lztek lztek=Lztek.create(MyApplication.ampplication);
         lztek.navigationBarSlideShow(false);
         lztek.hideNavigationBar();
        }catch (NoClassDefFoundError e){
            e.printStackTrace();
        }
    }

    private void menjing1() {
        // TPS980PosUtil.setJiaJiPower(1);
        DengUT.getInstance(baoCunBean).openDool();
      //  TPS980PosUtil.setRelayPower(1);
        Log.d("MianBanJiActivity3", "打开");
    }


    private void menjing2() {
        //  TPS980PosUtil.setJiaJiPower(0);
        DengUT.getInstance(baoCunBean).closeDool();
       // TPS980PosUtil.setRelayPower(0);
        Log.d("MianBanJiActivity3", "关闭");
    }



    public class NetWorkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取所有网络连接的信息
            if (connMgr!=null){
                Network[] networks = connMgr.getAllNetworks();
                //用于存放网络连接信息
                StringBuilder sb = new StringBuilder();
                //通过循环将网络信息逐个取出来
                Log.d("MianBanJiActivity3", "networks.length:" + networks.length);
                if (networks.length == 0) {
                    //没网
                    Log.d("MianBanJiActivity3", "没网2");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wangluo.setText("网络连接断开");
                        }
                    });
//                    if (serverManager != null) {
//                        serverManager.stopServer();
//                        serverManager = null;
//                    }
                }
                for (Network network : networks) {
                    //获取ConnectivityManager对象对应的NetworkInfo对象
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                    if (networkInfo!=null && networkInfo.isConnected()) {
                        //连接上
                        Log.d("MianBanJiActivity3", "有网2");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wangluo.setText("");
                            }
                        });
//                        if (serverManager != null) {
//                            serverManager.stopServer();
//                            serverManager = null;
//                        }
//                        serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), baoCunBean.getPort());
//                        serverManager.setMyServeInterface(MianBanJiActivity3.this);
//                        serverManager.startServer();
                        break;
                    }
                }
            }
        }
    }


    /**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     */
    public String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 90, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }



//    /**
//     * 显示结果UI
//     *
//     * @param state 状态 1 初始状态  2 识别中,出现提示语  3 识别失败  4 识别成功
//     */
//    protected void showUIResult(final int state, final String name, final String detectFaceTime) {
//        if (state==STATE){
//            return;
//        }else {
//            STATE=state;
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//              //  Log.d("MianBanJiActivity3", "state:" + state);
//                switch (state) {
//                    case 1: {//初始状态
//                        layout_true_gif_Ir.setVisibility(View.INVISIBLE);//蓝色图片动画
//                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
//                        iv_true_gif_in_Ir.setVisibility(View.INVISIBLE);//蓝色圈内层
//                        iv_true_gif_out_Ir.setVisibility(View.INVISIBLE);//蓝色圈外层
//                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
//                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
//                        layout_loadbg_Ir.setVisibility(View.INVISIBLE);//识别结果大框
//                        tvName_Ir.setVisibility(View.GONE);//姓名
//                        tvTime_Ir.setVisibility(View.GONE);//时间
//                        tvFaceTips_Ir.setVisibility(View.GONE);//识别提示
//                        tvName_Ir.setText("");
//                        tvTime_Ir.setText("");
//                        tvFaceTips_Ir.setText("");
//                        break;
//                    }
//                    case 2: {//识别中,出现提示语
//                        layout_true_gif_Ir.setVisibility(View.VISIBLE);//蓝色图片动画
//                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
//                        iv_true_gif_in_Ir.setVisibility(View.VISIBLE);//蓝色圈内层
//                        iv_true_gif_out_Ir.setVisibility(View.VISIBLE);//蓝色圈外层
//                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
//                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
//                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
//                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.true_bg);//切换背景
//                        tvName_Ir.setVisibility(View.GONE);//姓名
//                        tvTime_Ir.setVisibility(View.GONE);//时间
//                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
//                        tvName_Ir.setText("");
//                        tvTime_Ir.setText("");
//                        tvFaceTips_Ir.setText("识别中,请稍后...");
//                        break;
//                    }
//                    case 3: {//识别失败
//                        layout_true_gif_Ir.setVisibility(View.INVISIBLE);//蓝色图片动画
//                        layout_error_gif_Ir.setVisibility(View.VISIBLE);//红色图片动画
//                        iv_true_gif_in_Ir.setVisibility(View.INVISIBLE);//蓝色圈内层
//                        iv_true_gif_out_Ir.setVisibility(View.INVISIBLE);//蓝色圈外层
//                        iv_error_gif_in_Ir.setVisibility(View.VISIBLE);//红色圈内层
//                        iv_error_gif_out_Ir.setVisibility(View.VISIBLE);//红色圈外层
//                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
//                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.error_bg);//切换背景
//                        tvName_Ir.setVisibility(View.GONE);//姓名
//                        tvTime_Ir.setVisibility(View.VISIBLE);//时间
//                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
//                        tvName_Ir.setText("");
//                        tvTime_Ir.setText("无权限通过,请重试");
//                     //   tvFaceTips_Ir.setText(kouzhao);
//                        break;
//                    }
//                    case 4: {//识别成功
//                        layout_true_gif_Ir.setVisibility(View.VISIBLE);//蓝色图片动画
//                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
//                        iv_true_gif_in_Ir.setVisibility(View.VISIBLE);//蓝色圈内层
//                        iv_true_gif_out_Ir.setVisibility(View.VISIBLE);//蓝色圈外层
//                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
//                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
//                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
//                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.true_bg);//切换背景
//                        tvName_Ir.setVisibility(View.VISIBLE);//姓名
//                        tvTime_Ir.setVisibility(View.VISIBLE);//时间
//                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
//                        tvName_Ir.setText(name);
//                        tvTime_Ir.setText("部门:"+detectFaceTime);
//                     //   tvFaceTips_Ir.setText(kouzhao);
//                        break;
//                    }
//                }
//            }
//        });
//    }

    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(this, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
           print("异常:"+e.getMessage());
        }
        return offlineResource;
    }

    private void initTTs() {
        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        createOfflineResource(offlineVoice);

        LoggerProxy.printable(false); // 日志打印在logcat中
        boolean isSuccess;
        if (!isOnlineSDK) {
            // 检查2个离线资源是否可读
            isSuccess = checkOfflineResources();
            if (!isSuccess) {
                return;
            } else {
                print("离线资源存在并且可读, 目录：" + TEMP_DIR);
            }
        }
        // 日志更新在UI中，可以换成MessageListener，在logcat中查看日志
      //  SpeechSynthesizerListener listener = new MessageListener(mHandler);

        // 1. 获取实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);

        // 2. 设置listener
       // mSpeechSynthesizer.setSpeechSynthesizerListener(listener);

        // 3. 设置appId，appKey.secretKey
        int result = mSpeechSynthesizer.setAppId(appId);
        checkResult(result, "setAppId");
        result = mSpeechSynthesizer.setApiKey(appKey, secretKey);
        checkResult(result, "setApiKey");

        // 4. 如果是纯离线SDK需要离线功能的话
        if (!isOnlineSDK) {
            // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);

            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
            // 该参数设置为TtsMode.MIX生效。
            // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        }

        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声  3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "14");
        // 设置合成的语速，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "6");
        // 设置合成的语调，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        // mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL); // 调整音频输出

        if (sn != null) {
            // 纯离线sdk这个参数必填；离在线sdk没有此参数
            mSpeechSynthesizer.setParam(PARAM_SN_NAME, sn);
        }

        // x. 额外 ： 自动so文件是否复制正确及上面设置的参数
        Map<String, String> params = new HashMap<>();
        // 复制下上面的 mSpeechSynthesizer.setParam参数
        // 上线时请删除AutoCheck的调用
        if (!isOnlineSDK) {
            params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
        }

        // 检测参数，通过一次后可以去除，出问题再打开debug
       // InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
//        AutoCheck.getInstance(getApplicationContext()).check(initConfig, new Handler() {
//            @Override
//            /**
//             * 开新线程检查，成功后回调
//             */
//            public void handleMessage(Message msg) {
//                if (msg.what == 100) {
//                    AutoCheck autoCheck = (AutoCheck) msg.obj;
//                    synchronized (autoCheck) {
//                        String message = autoCheck.obtainDebugMessage();
//                        print(message); // 可以用下面一行替代，在logcat中查看代码
//                        // Log.w("AutoCheckMessage", message);
//                    }
//                }
//            }
//
//        });

        // 6. 初始化
        result = mSpeechSynthesizer.initTts(ttsMode);
        checkResult(result, "initTts");

    }
    /**
     * 在线SDK不需要调用，纯离线SDK会检查资源文件
     *
     * 检查 TEXT_FILENAME, MODEL_FILENAME 这2个文件是否存在，不存在请自行从assets目录里手动复制
     *
     * @return 检测是否成功
     */
    private boolean checkOfflineResources() {
        String[] filenames = {TEXT_FILENAME, MODEL_FILENAME};
        for (String path : filenames) {
            File f = new File(path);
            if (!f.canRead()) {
                print("[ERROR] 文件不存在或者不可读取，请从demo的assets目录复制同名文件到："
                        + f.getAbsolutePath());
                print("[ERROR] 初始化失败！！！");
                return false;
            }
        }
        return true;
    }

    private static final String TEXT = "欢迎使用百度语音合成，请在代码中修改合成文本";
    private void speak(String name) {
        /* 以下参数每次合成时都可以修改
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
         *  设置在线发声音人： 0 普通女声（默认） 1 普通男声  3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); 设置合成的音量，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5"); 设置合成的语速，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5"); 设置合成的语调，0-9 ，默认 5
         *
         */
        if (mSpeechSynthesizer == null) {
            print("[ERROR], 初始化失败");
            return;
        }

        if (name.equals("")){
            int result = mSpeechSynthesizer.speak("                                             请注意有陌生人闯入");
            checkResult(result, "speak");
        }else {
            int result = mSpeechSynthesizer.speak("                                            您好，欢迎"+name+"光临");
            checkResult(result, "speak");
        }
        print("合成并播放");
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
            print("error code :" + result + " method:" + method);
        }else {
            Log.d("MianBanJiActivity3", "result:" + result);
        }
    }

    private void print(String message) {
        Log.i("语音合成前的文字:", message);
    }

//    private void closePing(){
//        //启动定时器或重置定时器
//        if (task != null) {
//            task.cancel();
//            //timer.cancel();
//            task = new TimerTask() {
//                @Override
//                public void run() {
//                    Message message = new Message();
//                    //关屏
//                    message.what = 444;
//                    mHandler.sendMessage(message);
//                }
//            };
//            timer.schedule(task, jidianqi);
//        } else {
//            task = new TimerTask() {
//                @Override
//                public void run() {
//                    Message message = new Message();
//                    //关屏
//                    message.what = 444;
//                    mHandler.sendMessage(message);
//
//                }
//            };
//            timer.schedule(task, jidianqi);
//        }
//    }

}
