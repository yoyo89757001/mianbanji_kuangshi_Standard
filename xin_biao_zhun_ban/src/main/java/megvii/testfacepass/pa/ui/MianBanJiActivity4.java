package megvii.testfacepass.pa.ui;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.SerialPort;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hwit.HwitManager;
import com.lztek.toolkit.Lztek;
import com.sdsmdg.tastytoast.TastyToast;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import Lib.Reader.MT.Function;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.query.LazyList;
import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassAddFaceResult;
import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassFace;
import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageType;
import mcv.facepass.types.FacePassMouthOccAttr;
import mcv.facepass.types.FacePassRecognitionResult;
import mcv.facepass.types.FacePassRecognitionResultType;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.ConfigBean;
import megvii.testfacepass.pa.beans.DaKaBean;
import megvii.testfacepass.pa.beans.DaKaBean_;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.beans.Subject_;
import megvii.testfacepass.pa.beans.XGBean;
import megvii.testfacepass.pa.beans.ZhiLingBean;
import megvii.testfacepass.pa.camera.CameraManager;
import megvii.testfacepass.pa.camera.CameraPreview;
import megvii.testfacepass.pa.camera.CameraPreviewData;
import megvii.testfacepass.pa.dialog.MiMaDialog3;
import megvii.testfacepass.pa.dialog.MiMaDialog4;
import megvii.testfacepass.pa.tuisong_jg.ServerManager;
import megvii.testfacepass.pa.utils.AppUtils;
import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.DateUtils;
import megvii.testfacepass.pa.utils.DengUT;
import megvii.testfacepass.pa.utils.FacePassUtil;
import megvii.testfacepass.pa.utils.FileUtil;
import megvii.testfacepass.pa.utils.GsonUtil;
import megvii.testfacepass.pa.utils.NV21ToBitmap;
import megvii.testfacepass.pa.utils.ScanGunKeyEventHelper;
import megvii.testfacepass.pa.utils.SettingVar;
import megvii.testfacepass.pa.view.FaceView;
import megvii.testfacepass.pa.view.GlideRoundTransform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static megvii.testfacepass.pa.utils.SettingVar.cameraFacingFront;


public class MianBanJiActivity4 extends Activity implements CameraManager.CameraListener, SensorEventListener ,ScanGunKeyEventHelper.OnScanSuccessListener{

    @BindView(R.id.xiping)
    ImageView xiping;
    @BindView(R.id.tishiyu)
    TextView tishiyu;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.logo)
    TextView logo;
    @BindView(R.id.faceLinearLayout)
    LinearLayout faceLinearLayout;
    @BindView(R.id.faceName)
    TextView faceName;
    @BindView(R.id.faceImage)
    ImageView faceImage;

    public static Activity activity;
    //private FaceView faceView;
    private boolean isReadCard=false;
    private ServerManager mServerManager;
    private NetWorkStateReceiver netWorkStateReceiver = null;
    private SensorManager sm;
    private Box<Subject> subjectBox = null;
  //  private NfcAdapter mNfcAdapter;
 //   private PendingIntent mPendingIntent;
   // private Box<IDCardBean> idCardBeanBox = MyApplication.myApplication.getIdCardBeanBox();
   // private Bitmap msrBitmap = null;
    private RequestOptions myOptions = new RequestOptions()
            .fitCenter()
            .error(R.drawable.erroy_bg)
           // .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
    .transform(new GlideRoundTransform(MianBanJiActivity4.this,10));

    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .writeTimeout(20000, TimeUnit.MILLISECONDS)
            .connectTimeout(20000, TimeUnit.MILLISECONDS)
            .readTimeout(20000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
            //        .retryOnConnectionFailure(true)
            .build();
    private final Timer timer = new Timer();
    private TimerTask task;
    private final Timer timer2 = new Timer();
    private TimerTask task2;
    private LinkedBlockingQueue<ZhiLingBean.ResultBean> linkedBlockingQueue;
    /* 相机实例 */
    private CameraManager manager;
  //  private CameraManager2 manager2;
    /* 显示人脸位置角度信息 */
    // private XiuGaiGaoKuanDialog dialog = null;
    /* 相机预览界面 */
    private CameraPreview cameraView;
  //  private CameraPreview2 cameraView2;
    private static final int cameraWidth = 720;
    private static final int cameraHeight = 640;
  //  private boolean isOP = true;
    private int heightPixels;
    private int widthPixels;
    int screenState = 0;// 0 横 1 竖
    TanChuangThread tanChuangThread;
    // private ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
    private int dw, dh;
    private ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
    //private Box<HuiFuBean> huiFuBeanBox = null;
  //  private Box<FaceIDBean> faceIDBeanBox = MyApplication.myApplication.getFaceIDBeanBox();
    private ConfigBean configBean = null;
    private TimeChangeReceiver timeChangeReceiver;
    private Handler mHandler;
    private boolean lkl=false;
    private FacePassHandler paAccessControl=null;
    private NV21ToBitmap nv21ToBitmap;
    private SoundPool soundPool;
    //定义一个HashMap用于存放音频流的ID
    private HashMap<Integer, Integer> musicId = new HashMap<>();
    private Box<DaKaBean> daKaBeanBox =MyApplication.myApplication.getDaKaBeanBox();;
    private int pp = 0;
    private ReadThread mReadThread;
    private ReadThread2 mReadThread2;
    private ReadThread3 mReadThread3;
    private InputStream mInputStream;
    private Timer mTimer;//距离感应
    private TimerTask mTimerTask;//距离感应
    private int pm = 0;
    private boolean isPM = true;
    private boolean isPM2 = true;
    private float juli = 1;
    private String JHM = null;
    TextView tvTitle_Ir;
    TextView tvName_Ir;//识别结果弹出信息的名字
    TextView tvName_Ir2;//识别结果弹出信息的名字
    TextView tvTime_Ir;//识别结果弹出信息的时间
    TextView tvTime_Ir2;//识别结果弹出信息的时间
    TextView tvFaceTips_Ir;//识别信息提示
    TextView tvFaceTips_Ir2;//识别信息提示
    LinearLayout layout_loadbg_Ir;//识别提示大框
    LinearLayout layout_loadbg_Ir2;//识别提示大框
    RelativeLayout layout_true_gif_Ir, layout_error_gif_Ir;//蓝色图片动画 红色图片动画
    ImageView iv_true_gif_in_Ir, iv_true_gif_out_Ir, iv_error_gif_in_Ir, iv_error_gif_out_Ir;//定义旋转的动画
    Animation gifClockwise, gifAntiClockwise;
    LinearInterpolator lir_gif;
    private int jiqiType=-1;
    private boolean isGET = true;
    private int cishu=5;
    private int jidianqi=6000;
    private Lztek lztek=null;
    private Function mFuncs = null;
    private int loc_readerHandle=-1;
    private boolean isCLOSDLED =false;
  //  private CameraPreviewData mCurrentImage;
    ArrayBlockingQueue<FacePassDetectionResult> mDetectResultQueue;
    ArrayBlockingQueue<FacePassImage> mFeedFrameQueue;
    RecognizeThread mRecognizeThread;
    FeedFrameThread mFeedFrameThread;
    private static final String group_name = "facepasstestx";
    private int STATE=1;
    //private String faceID=null;
    private ScanGunKeyEventHelper mScanGunKeyEventHelper;
    //private String cardID=null;
    private FaceAndCardWork faceAndCardWork=null;
    private FaceWork faceWork=null;
    private IcCardWork icCardWork=null;
    private FaceOrCardWork faceOrCardWork=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       // huiFuBeanBox = MyApplication.myApplication.getHuiFuBeanBox();
        subjectBox = MyApplication.myApplication.getSubjectBox();
        configBean = MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class);
        mDetectResultQueue = new ArrayBlockingQueue<FacePassDetectionResult>(5);
        mFeedFrameQueue = new ArrayBlockingQueue<FacePassImage>(1);
        MyApplication.myApplication.addActivity(this);
        try {
            lztek=Lztek.create(MyApplication.ampplication);
            lztek.gpioEnable(configBean.getGpio());
            lztek.setGpioOutputMode(configBean.getGpio());
        }catch (NoClassDefFoundError error){
            error.printStackTrace();
        }
        activity=this;
        mScanGunKeyEventHelper=new ScanGunKeyEventHelper(this);

        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netWorkStateReceiver, filter);
        }

        if (configBean.getDangqianChengShi2()!=null){
            switch (configBean.getDangqianChengShi2()){
                case "ZL001":
                    jiqiType=0;
                    break;
                case "LZ001":
                    jiqiType=1;
                    break;
                case "TY001":
                    jiqiType=2;
                    break;
            }
        }


//        DateUtils.execSuCmd("date " + DateUtils.datas(1234567899876L)
//                + "\n busybox hwclock -w\n");


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                subjectBox.removeAll();
//                SystemClock.sleep(5000);
//                for (int i=0;i<500;i++){
//                    Subject subject=new Subject();
//                    subject.setName("A"+i);
//                    subject.setSid(i+"");
//                    subject.setEntryTime(System.currentTimeMillis());
//                    subjectBox.put(subject);
//                    Log.d("MianBanJiActivity3", "i:" + i);
//                }
//                Log.d("MianBanJiActivity3", "顶顶顶");
//            }
//        }).start();

        JHM = configBean.getJihuoma();
        if (JHM == null)
            JHM = "";

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SystemClock.sleep(6000);
//                for (Subject subject : subjectBox.getAll()) {
//                    try {
//                        paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
//                    } catch (FacePassException e) {
//                        e.printStackTrace();
//                    }
//                    subjectBox.remove(subject);
//                }
//            }
//        }).start();


        Log.d("MianBanJiActivity3", "subjectBox.query().build().findLazy().size():" + subjectBox.query().build().findLazy().size());

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
        nv21ToBitmap = new NV21ToBitmap(MianBanJiActivity4.this);
        /* 初始化界面 */
        //  Log.d("MianBanJiActivity3", "jh:" + configBean);
        //初始化soundPool,设置可容纳12个音频流，音频流的质量为5，
        AudioAttributes abs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)   //设置允许同时播放的流的最大值
                .setAudioAttributes(abs)   //完全可以设置为null
                .build();
        //通过load方法加载指定音频流，并将返回的音频ID放入musicId中
        musicId.put(1, soundPool.load(this, R.raw.tongguo, 1));
        musicId.put(2, soundPool.load(this, R.raw.wuquanxian, 1));
        musicId.put(3, soundPool.load(this, R.raw.xinxibupipei, 1));
        musicId.put(4, soundPool.load(this, R.raw.xianshibie, 1));
        musicId.put(5, soundPool.load(this, R.raw.shuaka, 1));
        musicId.put(6, soundPool.load(this, R.raw.shualianyanz, 1));//请刷脸

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        //过期删除
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteFaceByTime();
            }
        }).start();

        initView();


        try {

            if (configBean.getJidianqi()!=0){
                jidianqi= configBean.getJidianqi();
            }
            if (configBean.getRetryCount()!=0){
                cishu= configBean.getRetryCount();
            }
            FacePassHandler.initSDK(getApplicationContext());
            FacePassUtil util = new FacePassUtil();
            util.init(MianBanJiActivity4.this, getApplicationContext(), SettingVar.faceRotation, configBean);

        } catch (Exception e) {
           TastyToast.makeText(MianBanJiActivity4.this,"初始化失败"+e.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
        }



//        JSONObject jsonObject=new JSONObject();
//        try {
//            jsonObject.put("searchThreshold",72);//识别阈值
//            jsonObject.put("livenessThreshold",72);//活体阈值
//            jsonObject.put("livenessEnabled",1);//启用活体 1启用 0不启用
//            jsonObject.put("faceMinThreshold",50);//识别时的最小人脸
//            jsonObject.put("lowBrightnessThreshold",70);//最小亮度
//            jsonObject.put("highBrightnessThreshold",210);//最大亮度
//            jsonObject.put("brightnessSTDThreshold",82);//阴阳脸
//            jsonObject.put("retryCount",3);//陌生人重试次数
//            jsonObject.put("addFaceMinThreshold",90);//入库人脸最小值
//            jsonObject.put("addFaceBlurThreshold",0.3f);//入库人脸模糊度
//            jsonObject.put("configModel",2);//1为只刷脸模式，2为可以刷脸也可以刷卡模式,3刷脸加刷卡双重认证
//            jsonObject.put("companyName","XX公司");//公司名称
//            jsonObject.put("isOpenDoor",0);//0:不开启本地密码开门模式,1:开启本地密码开门模式（点击面板机中间输入密码开门）
//            jsonObject.put("relayInterval",5000);//继电器开启后的闭合间隔时间
//            jsonObject.put("pwd1",123456);//进入设置界面的密码
//            jsonObject.put("pwd2",123456);//门禁开门的密码
//            jsonObject.put("heartbeatIntervalTime",1);//心跳请求间隔时间，只能时分钟的倍数，默认一分钟
//            jsonObject.put("taskIntervalTime",1);//所有任务请求间隔时间，只能时分钟的倍数，默认一分钟
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.d("MianBanJiActivity3", jsonObject.toString());

        tanChuangThread = new TanChuangThread();
        tanChuangThread.start();

        mRecognizeThread = new RecognizeThread();
        mRecognizeThread.start();
        mFeedFrameThread = new FeedFrameThread();
        mFeedFrameThread.start();


        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NotNull Message msg) {
                switch (msg.what) {
                    case 111: {
                        Subject subject = (Subject) msg.obj;
                        //Log.d("MianBanJiActivity3", "subject:" + subject);
                        if (subject.getSid()!= null) {
                            DengUT.getInstance(configBean).openDool();
                            //启动定时器或重置定时器
                            if (task != null) {
                                task.cancel();
                                //timer.cancel();
                                task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        Message message = new Message();
                                        message.what = 222;
                                        mHandler.sendMessage(message);
                                    }
                                };
                                timer.schedule(task, jidianqi);
                            } else {
                                task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        Message message = new Message();
                                        message.what = 222;
                                        mHandler.sendMessage(message);
                                    }
                                };
                                timer.schedule(task, jidianqi);
                            }
                            soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                        } else {
                            //  Log.d("MianBanJiActivity3", "ddd4");
                            soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
                            //
                            //faceView.setTC(BitmapUtil.rotateBitmap(msrBitmap, SettingVar.msrBitmapRotation), subject.getName(), subject.getDepartmentName());
                        }

                        break;
                    }
                    case 222: {//关门
                        DengUT.getInstance(configBean).closeDool();
                        showUIResult("","",0);
                        break;
                    }
                    case 333:

                        DengUT.getInstance(configBean).openLOED();
                        DengUT.getInstance(configBean).openWrite();

                        break;
                    case 444:

                        DengUT.getInstance(configBean).closeLOED();
                        DengUT.getInstance(configBean).closeWrite();

                        break;
                    case 999:
                        DengUT.getInstance(configBean).openLOED();
                        String icdata = (String) msg.obj;

                        Log.d("MianBanJiActivity3", "icdata"+icdata);
                        if (icdata==null)
                            break;
                        if (isReadCard){//是否读取卡的信息给后台
                            isReadCard=false;
                            MyApplication.card=icdata;
                           break ;
                        }
                      //  if (configBean.getConfigModel()==2){//2是刷脸加刷卡都可以
                            try {
                                //  Log.d("MianBanJiActivity3", icdata.toUpperCase());
                                List<Subject> subjectList= subjectBox.query().equal(Subject_.idcard,icdata.toUpperCase()).build().find();
                                if (subjectList.size()>1){
                                    StringBuilder builder=new StringBuilder();
                                    for (Subject subject:subjectList){
                                        builder.append(subject.getName());
                                        builder.append(",");
                                    }
                                    showToase("此ID卡被 "+builder.toString()+" 多人绑定.",TastyToast.ERROR);

                                }else if (subjectList.size()==1){
                                    long bitmapId=System.currentTimeMillis();
                                   // String riqi=DateUtils.timeNYR(bitmapId+"");
                                    DengUT.getInstance(configBean).openDool();
                                    // soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                                    //启动定时器或重置定时器
                                    if (task != null) {
                                        task.cancel();
                                        //timer.cancel();
                                        task = new TimerTask() {
                                            @Override
                                            public void run() {
                                                Message message = new Message();
                                                message.what = 222;
                                                if (mHandler!=null)
                                                    mHandler.sendMessage(message);
                                            }
                                        };
                                        timer.schedule(task, jidianqi);
                                    } else {
                                        task = new TimerTask() {
                                            @Override
                                            public void run() {
                                                Message message = new Message();
                                                message.what = 222;
                                                if (mHandler!=null)
                                                    mHandler.sendMessage(message);
                                            }
                                        };
                                        timer.schedule(task, jidianqi);
                                    }
                                    //显示结果
                                    showUIResult(subjectList.get(0).getName(),"验证成功!",1);

                                    DengUT.isOPEN = true;
                                    //保存一份刷卡记录
                                    DaKaBean daKaBean=new DaKaBean();
                                    daKaBean.setId(bitmapId);
                                    daKaBean.setPath(null);
                                    daKaBean.setPersonId(subjectList.get(0).getSid());
                                    daKaBean.setTime(bitmapId);
                                    daKaBean.setType(2);
                                    daKaBean.setPeopleType(subjectList.get(0).getPeopleType());
                                    daKaBeanBox.put(daKaBean);
                                    //mq发送一份
                                } else {
                                    showToase("找不到此卡信息",TastyToast.ERROR);
                                    soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                showToase("刷卡异常"+e.getMessage(),TastyToast.ERROR);
                            }
//                        }else if (configBean.getConfigModel()==3){//刷脸加刷卡双重认证
//                            if (faceID==null){
//                                soundPool.play(musicId.get(6), 1, 1, 0, 0, 1);//请刷脸
//                                cardID=icdata;
//                            }else {
//                                try {
//                                    //  Log.d("MianBanJiActivity3", icdata.toUpperCase());
//                                    List<Subject> subjectList= subjectBox.query().equal(Subject_.sid,faceID).build().find();
//                                    if (subjectList.size()>1){
//                                        StringBuilder builder=new StringBuilder();
//                                        for (Subject subject:subjectList){
//                                            builder.append(subject.getName());
//                                            builder.append(",");
//                                        }
//                                        showToase(builder.toString()+"人员信息重复,请重新设置",TastyToast.ERROR);
//                                        faceID=null;
//                                        cardID=null;
//
//                                    }else if (subjectList.size()==1){
//                                        if (subjectList.get(0).getIdcardNum().equals(icdata)){
//                                            long bitmapId=System.currentTimeMillis();
//                                            String riqi=DateUtils.timeNYR(bitmapId+"");
//                                            DengUT.getInstance(configBean).openDool();
//                                            //  soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
//                                            //启动定时器或重置定时器
//                                            if (task != null) {
//                                                task.cancel();
//                                                //timer.cancel();
//                                                task = new TimerTask() {
//                                                    @Override
//                                                    public void run() {
//                                                        Message message = new Message();
//                                                        message.what = 222;
//                                                        if (mHandler!=null)
//                                                            mHandler.sendMessage(message);
//                                                    }
//                                                };
//                                                timer.schedule(task, jidianqi);
//                                            } else {
//                                                task = new TimerTask() {
//                                                    @Override
//                                                    public void run() {
//                                                        Message message = new Message();
//                                                        message.what = 222;
//                                                        if (mHandler!=null)
//                                                            mHandler.sendMessage(message);
//                                                    }
//                                                };
//                                                timer.schedule(task, jidianqi);
//                                            }
//                                            showUIResult(4,subjectList.get(0).getName(),"验证成功！");
//                                            link_shangchuanshualian(subjectList.get(0).getTeZhengMa(),null,"card",bitmapId,riqi);
//                                            faceID=null;
//                                            cardID=null;
//                                            DengUT.isOPEN=true;
//                                        }else {
//                                            showUIResult(3,"卡号不匹配","");
//                                            showToase("卡号与人员信息不匹配,验证失败",TastyToast.ERROR);
//                                            soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//                                            faceID=null;
//                                            cardID=null;
//                                            DengUT.isOPEN=true;
//                                        }
//                                    } else {
//                                        showUIResult(3,"找不到卡号信息","");
//                                        showToase("找不到卡号信息,请确认已经入库",TastyToast.ERROR);
//                                        soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//                                        faceID=null;
//                                        cardID=null;
//                                        DengUT.isOPEN=true;
//                                    }
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                    showToase("刷卡异常"+e.getMessage(),TastyToast.ERROR);
//                                    faceID=null;
//                                    cardID=null;
//                                }
//                            }

                      //  }else {
                      //      showToase("未开启刷卡模式",TastyToast.ERROR);
                     //   }
                        break;

                }
                return false;
            }
        });


        faceWork=new FaceWork();
        faceAndCardWork = new FaceAndCardWork();
        icCardWork=new IcCardWork();
        faceOrCardWork=new FaceOrCardWork(mHandler);


       // init_NFC();

        //开启摄像头
        manager.open(getWindowManager(), SettingVar.cameraId, cameraWidth, cameraHeight);//前置是1

//        if (configBean.isHuoTi()) {
//            if (SettingVar.cameraId == 1) {
//                manager2.open(getWindowManager(), 0, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
//            } else {
//                manager2.open(getWindowManager(), 1, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
//            }
//        }

        if (jiqiType==0 || jiqiType==1){
            try {
                mFuncs = new Function(this, mHandler);
                loc_readerHandle = mFuncs.lc_init_ex(1, "/dev/ttyS1".toCharArray(), Integer.parseInt("9600"));
                if(loc_readerHandle == -1)
                {
                    Toast.makeText(getApplicationContext(), "连接读卡器失败", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int aa= mFuncs.lc_beep(loc_readerHandle, 2);
                    Log.d("MianBanJiActivity3", loc_readerHandle+"   "+ aa);
                    mReadThread2 = new ReadThread2();
                    mReadThread2.start();
                }
            }catch (NoClassDefFoundError error){
                Log.d("MianBanJiActivity3", error.getMessage()+"");
            }
        }


        guanPing();//关屏
        DengUT.getInstance(configBean).openLOED();


        if (jiqiType==2) {
            try {
                SerialPort mSerialPort = MyApplication.myApplication.getSerialPort();
                //mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (Exception e) {
                Log.d("MianBanJiActivity", e.getMessage() + "dddddddd");
            }

            Sensor defaultSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sm.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_NORMAL);
            if (mTimerTask == null) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (juli > 0 && juli<10000) {
                            isPM2 = true;
                            //有人
                            if (isPM) {
                                isPM = false;
                                pm = 0;
                                Message message = new Message();
                                message.what = 333;
                                mHandler.sendMessage(message);
                            }
                        } else {
                            isPM = true;
                            if (isPM2) {
                                pm++;
                                if (pm == 8) {
                                    Message message = new Message();
                                    message.what = 444;
                                    mHandler.sendMessage(message);
                                    isPM2 = false;
                                    pm = 0;

                                    if (DengUT.isOPENRed) {
                                        DengUT.isOPENRed = false;
                                        DengUT.getInstance(configBean).closeRed();
                                    }
                                    if (DengUT.isOPENGreen) {
                                        DengUT.isOPENGreen = false;
                                        DengUT.getInstance(configBean).closeGreen();
                                    }
                                    if (DengUT.isOPEN) {
                                        DengUT.isOPEN = false;
                                        DengUT.getInstance(configBean).closeWrite();
                                    }
                                }
                            }
                        }
                    }
                };
            }
            if (mTimer == null) {
                mTimer = new Timer();
            }
            mTimer.schedule(mTimerTask, 0, 1000);
        }

        if (jiqiType==0||jiqiType==1){
            if (lztek!=null){
                mReadThread3 = new ReadThread3();//微波雷达
                mReadThread3.start();
            }
        }

    }



    private void showToase(String s,int type){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast tastyToast = TastyToast.makeText(MianBanJiActivity4.this, s+"", TastyToast.LENGTH_LONG,type );
                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                tastyToast.show();
            }
        });
    }


    @OnClick(R.id.root_layout)
    public void onViewClicked() {
        if (configBean.isShowShiPingLiu()){
            MiMaDialog3 miMaDialog=new MiMaDialog3(MianBanJiActivity4.this, configBean.getMima2());
            WindowManager.LayoutParams params= miMaDialog.getWindow().getAttributes();
            params.width=dw;
            params.height=dh+60;
            miMaDialog.getWindow().setGravity(Gravity.CENTER);
            miMaDialog.getWindow().setAttributes(params);
            miMaDialog.show();
        }
    }



    private class ReadThread extends Thread {
        boolean isIterrupt;
        @Override
        public void run() {
            super.run();
            while (!isIterrupt) {
                int size;
                try {
                    final byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                       //  Log.d("ReadThread", "buffer.length:" + byteToString(buffer));
                       //  Log.d("ReadThread", new String(buffer));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                readdd(buffer);
                            }
                        });
                    }
                    SystemClock.sleep(400);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        @Override
        public void interrupt() {
            isIterrupt = true;
            super.interrupt();
        }
    }

    private class ReadThread2 extends Thread {
        boolean isIterrupt;
        @Override
        public void run() {
            super.run();
            while (!isIterrupt) {
                int st = 0;
                byte[] rData = new byte[128];
                int[] rlen = new int[2];
                if (loc_readerHandle!=-1){
                    //  Log.d("MianBanJiActivity3", "ffffffff:"+loc_readerHandle);
                    st = mFuncs.lc_getAutoReturnedData(loc_readerHandle, rData, rlen);
                    //  Log.d("MianBanJiActivity3", "hh哈哈:"+st);
                    if (st == 0)
                    {
                    //    Log.d("ReadThread2", "rData:" + );
                        String sdfds = new String(rData);
                        sdfds = sdfds.substring(0, 10);
                        Log.d("ReadThread2", sdfds);
//                        StringBuilder showStr= new StringBuilder();
//                        int len=rlen[0];
//                        for(int i= 0; i<len; i++)
//                            showStr.append(byteToHexString(rData[i]));
//                        Log.d("ReadThread2", showStr.toString());
                        Message message = new Message();
                        message.what = 999;
                        message.obj=sdfds.trim();
                        mHandler.sendMessage(message);
                    }
                }
                SystemClock.sleep(400);
            }
        }

        @Override
        public void interrupt() {
            isIterrupt = true;
            super.interrupt();
        }
    }


    private static boolean isR=false;
    private class ReadThread3 extends Thread {
        boolean isIterrupt;
        @Override
        public void run() {
            super.run();
            while (!isIterrupt) {
                if (lztek!=null){
                        SystemClock.sleep(200);
                        final int value = lztek.getGpioValue(configBean.getGpio());
                        // Log.d("MianBanJiActivity3", "value:" + value);
                        if (value==1){//有人
                            isCLOSDLED=true;
                            // Log.d("MianBanJiActivity3", "value:" + value);
                            isR=true;
                            Message message = new Message();
                            message.what = 333;
                            mHandler.sendMessage(message);

                            if (task != null)
                                task.cancel();
                            SystemClock.sleep(2000);

                        }else {//没人
                            if (isR){
                                isR=false;
                                //  Log.d("MianBanJiActivity3", "valuettttt:" + value);
                                //启动定时器或重置定时器
                                if (task != null) {
                                    task.cancel();
                                    //timer.cancel();
                                    task = new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (isCLOSDLED){
                                                Message message = new Message();
                                                message.what = 444;
                                                mHandler.sendMessage(message);
                                            }
                                        }
                                    };
                                    try {
                                        timer.schedule(task, 10000);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else {
                                    task = new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (isCLOSDLED){
                                                Message message = new Message();
                                                message.what = 444;
                                                mHandler.sendMessage(message);
                                            }
                                        }
                                    };
                                    try {
                                        timer.schedule(task, 10000);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                }


            }
        }
        @Override
        public void interrupt() {
            isIterrupt = true;
            super.interrupt();
        }
    }



    //涂鸦转10位10进制数字
    private void readdd(byte[] idid) {

        StringBuilder builder = new StringBuilder();
        String sdfds = byteToString(idid);
        long d=0;
        if (sdfds != null) {
            sdfds = sdfds.substring(6, 14);
            Log.d("ReadThread", sdfds);
            for(int i = 0; i<4; i++) {
                String str = sdfds.substring(sdfds.length()-2 * (i+1), sdfds.length()-2*i);
                builder.append(str);
            }
             d = Long.valueOf(builder.toString(),16);   //d=255
            Log.d("ReadThread", "builder:" + d);
        } else {
            return;
        }
        //306721218
        String str= addO(d+"");
        Message message = new Message();
        message.what = 999;
        message.obj=str;
        mHandler.sendMessage(message);

    }

    private String addO(String ss){
        if (ss.length()<10){
            ss="0"+ss;
            addO(ss);
        }
        return ss;
    }

    @Override
    protected void onResume() {
        Log.d("MianBanJiActivity3", "重新开始");
        super.onResume();
        //开启服务



    }


    @Override
    public void onSensorChanged(SensorEvent event) {
     //   Log.e("距离", "" + event.values[0]);
        juli = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


//    @Override
//    public void onNewIntent(Intent intent) {
//        //  super.onNewIntent(intent);
//        // Log.d("SheZhiActivity2", "intent:" + intent);
//        processIntent(intent);
//





    /* 相机回调函数 */
    @Override
    public void onPictureTaken(CameraPreviewData cameraPreviewData) {


        /* 如果SDK实例还未创建，则跳过 */
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
                    if (detectionResult == null || detectionResult.faceList.length <= 0) {
                       // Log.d("FeedFrameThread", "没人"+DengUT.isOPEN);
                        if (DengUT.isOPEN || DengUT.isOPENRed || DengUT.isOPENGreen) {
                           // Log.d("FeedFrameThread", "没人2");
                            DengUT.isOPEN = false;
                            DengUT.isOPENGreen = false;
                            DengUT.isOPENRed = false;
                            DengUT.isOpenDOR = false;
                            DengUT.getInstance(configBean).closeWrite();
                            showUIResult(1,"","");
                            //启动定时器或重置定时器
                            if (task2 != null) {
                                task2.cancel();
                                //timer.cancel();
                                task2 = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (!isCLOSDLED && jiqiType!=2){
                                            Message message = new Message();
                                            message.what = 444;
                                            mHandler.sendMessage(message);
                                        }
                                    }
                                };
                                timer2.schedule(task2, 5000);
                            } else {
                                task2 = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (!isCLOSDLED && jiqiType!=2){
                                            Message message = new Message();
                                            message.what = 444;
                                            mHandler.sendMessage(message);
                                        }
                                    }
                                };
                                timer2.schedule(task2, 5000);
                            }
                        }
                    }else {
                        final FacePassFace[] bufferFaceList = detectionResult.faceList;
                        FacePassMouthOccAttr attr=bufferFaceList[0].mouthOccAttr;
                        if (attr.is_valid){//有效
                            String kouzhao="";
                            switch (attr.mouth_occ_status){
                                case 0:
                                    kouzhao="未佩戴口罩";
                                    break;
                                case 1:
                                    kouzhao="面具遮挡";
                                    break;
                                case 2:
                                    kouzhao="已佩戴口罩";
                                    break;
                                case 3:
                                    kouzhao="其他遮挡";
                                    break;
                                default:
                                    kouzhao="";
                                    break;
                            }
                            String finalKouzhao = kouzhao;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvFaceTips_Ir.setText(finalKouzhao);
                                }
                            });
                        }

                        /*离线模式，将识别到人脸的，message不为空的result添加到处理队列中*/
                        if (detectionResult.message.length != 0) {
                            //  Log.d("FeedFrameThread", "插入");
                            if (!DengUT.isOPEN) {
                                DengUT.isOPEN = true;
                                DengUT.getInstance(configBean).openWrite();
                                showUIResult(2,"","");
                            }
                            mDetectResultQueue.offer(detectionResult);
                            //   Log.d("ggggg", "1 mDetectResultQueue.size = " + mDetectResultQueue.size());
                        }
                        if (lkl){
                            lkl=false;
                            if (!isCLOSDLED && jiqiType!=2){
                                if (task2 != null)
                                    task2.cancel();
                                Message message = new Message();
                                message.what = 333;
                                mHandler.sendMessage(message);
                            }
                        }else {
                            lkl=true;
                        }
                    }

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
                                bitmap = Glide.with(MianBanJiActivity4.this).asBitmap()
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
                                        Subject subject2 = subjectBox.query().equal(Subject_.sid, commandsBean.getId()).build().findUnique();
                                        if (subject2 != null) {
                                            paAccessControl.deleteFace(subject2.getTeZhengMa().getBytes());
                                            subjectBox.remove(subject2);
                                        }
                                        Subject subject = new Subject();
                                        subject.setTeZhengMa(new String(faceToken));
                                        subject.setSid(commandsBean.getId());
                                        subject.setId(System.currentTimeMillis());
                                        subject.setPeopleType(commandsBean.getPepopleType());//0是员工 1是访客
                                        subject.setName(commandsBean.getName());
                                        subject.setDepartmentName(commandsBean.getDepartmentName());
                                        subject.setWorkNumber(commandsBean.getCardID());
                                        subjectBox.put(subject);
                                        paAccessControl.bindGroup(group_name,faceToken);
                                        Log.d("MyReceiver", "单个员工入库成功" + subject.toString());

                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }
                                } else {

                                }
                            } else {

                            }
                            break;
                        }
                        case 1002://修改
                        {
                            Subject subject=null;
                            try {
                                 subject = subjectBox.query().equal(Subject_.sid, commandsBean.getId()).build().findUnique();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if (subject != null) {
                                FacePassAddFaceResult faceResult = null;
                                Bitmap bitmap = null;
                                try {
                                    if (commandsBean.getImage() != null)
                                        bitmap = Glide.with(MianBanJiActivity4.this).asBitmap()
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
                                            if (commandsBean.getPepopleType() != 0) {
                                                subject.setPeopleType(commandsBean.getPepopleType());
                                            }
                                            subject.setTeZhengMa(new String(faceToken));
                                            subjectBox.put(subject);

                                        } catch (Exception e) {
                                            e.printStackTrace();

                                        }

                                    } else {

                                    }
                                } else {//没图片只修改其他值
                                    String name = commandsBean.getName();
                                    String bumen = commandsBean.getDepartmentName();
                                    int pepopleType = commandsBean.getPepopleType();
                                    if (name != null)
                                        subject.setName(name);
                                    if (bumen != null) {
                                        subject.setDepartmentName(bumen);
                                    }
                                    if (pepopleType != 0) {
                                        subject.setPeopleType(pepopleType);
                                    }
                                    subjectBox.put(subject);

                                }
                            } else {

                            }
                        }
                        break;
                        case 1003://删除
                        {
                            Subject subject=null;
                            try {
                                subject = subjectBox.query().equal(Subject_.sid, commandsBean.getId()).build().findUnique();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if (subject != null) {
                                try {
                                    paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                               subjectBox.remove(subject);

                            }else {

                            }
                        }
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
//        if (mNfcAdapter != null) {
//            stopNFC_Listener();
//        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (sm != null)
            sm.unregisterListener(this);

    }


    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SharedPreferences preferences = getSharedPreferences(SettingVar.SharedPrefrence, Context.MODE_PRIVATE);
        SettingVar.isSettingAvailable = preferences.getBoolean("isSettingAvailable", SettingVar.isSettingAvailable);
        SettingVar.cameraId = preferences.getInt("cameraId", SettingVar.cameraId);
        SettingVar.faceRotation = preferences.getInt("faceRotation", SettingVar.faceRotation);
        SettingVar.cameraPreviewRotation = preferences.getInt("cameraPreviewRotation", SettingVar.cameraPreviewRotation);
        cameraFacingFront = preferences.getBoolean("cameraFacingFront", cameraFacingFront);
        SettingVar.cameraPreviewRotation2 = preferences.getInt("cameraPreviewRotation2", SettingVar.cameraPreviewRotation2);
        SettingVar.faceRotation2 = preferences.getInt("faceRotation2", SettingVar.faceRotation2);
        SettingVar.msrBitmapRotation = preferences.getInt("msrBitmapRotation", SettingVar.msrBitmapRotation);

        setContentView(R.layout.activity_mianbanji3);
        ButterKnife.bind(this);
        //faceView=findViewById(R.id.faceview);
        ImageView shezhi = findViewById(R.id.shezhi);
        shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MiMaDialog4 miMaDialog = new MiMaDialog4(MianBanJiActivity4.this, configBean.getMima());
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
        heightPixels = displayMetrics.heightPixels;
        widthPixels = displayMetrics.widthPixels;
        SettingVar.mHeight = heightPixels;
        SettingVar.mWidth = widthPixels;
        /* 初始化界面 */
        manager = new CameraManager();
        cameraView = (CameraPreview) findViewById(R.id.preview);
        manager.setPreviewDisplay(cameraView);
        /* 注册相机回调函数 */
        manager.setListener(this);

      //  manager2 = new CameraManager2();
      //  cameraView2 = findViewById(R.id.preview2);
      //  manager2.setPreviewDisplay(cameraView2);
        /* 注册相机回调函数 */
      //  manager2.setListener(this);

        tvName_Ir = findViewById(R.id.tvName_Ir);//名字
        tvName_Ir2 = findViewById(R.id.tvName_Ir2);//名字
        tvTime_Ir = findViewById(R.id.tvTime_Ir);//时间
        tvTime_Ir2 = findViewById(R.id.tvTime_Ir2);//时间
        tvFaceTips_Ir = findViewById(R.id.tvFaceTips_Ir);//识别信息提示
        tvFaceTips_Ir2 = findViewById(R.id.tvFaceTips_Ir2);//识别信息提示
        layout_loadbg_Ir = findViewById(R.id.layout_loadbg_Ir);//头像区域的显示的底图背景
        layout_loadbg_Ir2 = findViewById(R.id.layout_loadbg_Ir2);//头像区域的显示的底图背景
        layout_true_gif_Ir = findViewById(R.id.layout_true_gif_Ir);
        layout_error_gif_Ir = findViewById(R.id.layout_error_gif_Ir);
        iv_true_gif_in_Ir = findViewById(R.id.iv_true_gif_in_Ir);
        iv_true_gif_out_Ir = findViewById(R.id.iv_true_gif_out_Ir);
        iv_error_gif_in_Ir = findViewById(R.id.iv_error_gif_in_Ir);
        iv_error_gif_out_Ir = findViewById(R.id.iv_error_gif_out_Ir);
        tvTitle_Ir = findViewById(R.id.tvTitle_Ir);

        //region 动画
        gifClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anim_face_clockwise);
        gifAntiClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anim_face_anti_clockwise);
        lir_gif = new LinearInterpolator();//设置为匀速旋转
        gifClockwise.setInterpolator(lir_gif);
        gifAntiClockwise.setInterpolator(lir_gif);

        iv_true_gif_out_Ir.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        iv_error_gif_out_Ir.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        iv_true_gif_out_Ir.startAnimation(gifClockwise);
        iv_error_gif_out_Ir.startAnimation(gifClockwise);
        AssetManager mgr = getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/hua.ttf");
        tvTitle_Ir.setTypeface(tf);
        if (configBean.getCompanyName() == null) {
            tvTitle_Ir.setText("请设置公司名称");
        } else {
            tvTitle_Ir.setText(configBean.getCompanyName());
        }

        showUIResult(1,"","");

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

                            if (FacePassRecognitionResultType.RECOG_OK == result.facePassRecognitionResultType) {
                                //识别的
                                Subject subject = subjectBox.query().equal(Subject_.teZhengMa, new String(result.faceToken)).build().findUnique();
                                   if (subject!=null){
                                       for (int i = 0; i < detectionResult.faceList.length; i++) {
                                           FacePassImage images = detectionResult.images[i];
                                           if (images.trackId == result.trackId) {
                                               final Bitmap fileBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
                                               switch (configBean.getConfigModel()){//1是只刷脸，2只刷卡，3刷脸加刷卡，4，刷脸或者刷卡//现在先只做模式4
                                                   case 1:{
                                                      faceWork.work(subject,fileBitmap);
                                                       break;
                                                   }
                                                   case 2:{
                                                       icCardWork.work();
                                                       break;
                                                   }
                                                   case 3:{
                                                       faceAndCardWork.work();
                                                       break;
                                                   }
                                                   case 4:{
                                                       if (!DengUT.isOPENGreen) {
                                                           DengUT.isOPENGreen = true;
                                                           DengUT.getInstance(configBean).openGreen();
                                                       }
                                                       showUIResult(4,subject.getName(),"验证成功");
                                                       faceOrCardWork.work(subject,fileBitmap);
                                                       DengUT.isOPEN=true;
                                                       break;
                                                   }
                                               }

                                               break;
                                           }
                                       }
//                                       if (subject.getEntryTime()==0 || subject.getEntryTime()>=System.currentTimeMillis()) {//没过期
//                                           if (configBean.getConfigModel()==3){//双重认证
//                                               if (cardID==null){
//                                                   soundPool.play(musicId.get(5), 1, 1, 0, 0, 1);//请刷卡
//                                                   faceID=subject.getSid();
//                                                   DengUT.isOPEN = true;
//                                               }else {//认证
//                                                   if (cardID.equals(subject.getIdcardNum())) {//卡跟id相等
//                                                       Message message2 = Message.obtain();
//                                                       message2.what = 111;
//                                                       message2.obj = subject;
//                                                       mHandler.sendMessage(message2);
//                                                       if (!DengUT.isOPENGreen) {
//                                                           DengUT.isOPENGreen = true;
//                                                           DengUT.getInstance(configBean).openGreen();
//                                                       }
//
//                                                       showUIResult(4,subject.getName(),"验证成功!");
//                                                       faceID=null;
//                                                       cardID=null;
//                                                       DengUT.isOPEN = true;
//
//                                                   }else {
//                                                       showUIResult(3,subject.getName(),"验证失败!");
//                                                       DengUT.isOPEN = true;
//                                                       showToase("卡号与人员信息不匹配,验证失败",TastyToast.ERROR);
//                                                       soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//                                                       faceID=null;
//                                                       cardID=null;
//                                                   }
//                                               }
//                                           }else {//不双重认证
//                                               Message message2 = Message.obtain();
//                                               message2.what = 111;
//                                               message2.obj = subject;
//                                               mHandler.sendMessage(message2);
//                                               if (!DengUT.isOPENGreen) {
//                                                   DengUT.isOPENGreen = true;
//                                                   DengUT.getInstance(configBean).openGreen();
//                                               }
//                                               for (int i = 0; i < detectionResult.faceList.length; i++) {
//                                                   FacePassImage images = detectionResult.images[i];
//                                                   if (images.trackId == result.trackId) {
//                                                       final Bitmap fileBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
//                                                       //  String paths = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ruitongzipmbj";
//                                                       //  boolean tt = nv21ToBitmap.saveBitmap(fileBitmap, paths, time + ".png");
//                                                       long bitmapId=System.currentTimeMillis();
//                                                       String riqi=DateUtils.timeNYR(bitmapId+"");
//                                                       BitmapUtil.saveBitmapToSD(fileBitmap,MyApplication.SDPATH2+ File.separator+riqi,bitmapId+".png");
//                                                       link_shangchuanshualian(subject.getSid(), fileBitmap,  "face",bitmapId,riqi);
//                                                       break;
//                                                   }
//                                               }
//                                               showUIResult(4,subject.getName(),"验证成功!");
//                                               faceID=null;
//                                               cardID=null;
//                                               DengUT.isOPEN = true;
//                                           }
//                                       }else {//时间过期删除
//                                           List<FaceIDBean> faceIDBeans=faceIDBeanBox.query().equal(FaceIDBean_.subjectId, subject.getSid()).build().find();
//                                           for (FaceIDBean f:faceIDBeans){
//                                               paAccessControl.deleteFace(f.getTeZhengMa().getBytes());
//                                           }
//                                           subjectBox.remove(subject);
//                                           faceID=null;
//                                           cardID=null;
//                                           DengUT.isOPEN = true;
//                                       }


                                   } else {
                                       EventBus.getDefault().post("没有该人员信息");
                                       //faceID=null;
                                      // cardID=null;
                                       DengUT.isOPEN = true;
                                   }


                            } else {
                             //   Log.d("RecognizeThread", "未识别");
                                //未识别的
                                // 防止concurrentHashMap 数据过多 ,超过一定数据 删除没用的
                                if (concurrentHashMap.size() > 30) {
                                    concurrentHashMap.clear();
                                }
                                if (concurrentHashMap.get(result.trackId) == null) {
                                    //找不到新增
                                    concurrentHashMap.put(result.trackId, 1);
                                } else {
                                    //找到了 把value 加1
                                    concurrentHashMap.put(result.trackId, (concurrentHashMap.get(result.trackId)) + 1);
                                }
                                //判断次数超过3次
                                if (concurrentHashMap.get(result.trackId) == cishu) {

                                    Subject subject1 = new Subject();
                                    // subject1.setW(bitmap.getWidth());
                                    // subject1.setH(bitmap.getHeight());
                                    //图片在bitmabToBytes方法里面做了循转
                                    // subject1.setTxBytes(BitmapUtil.bitmabToBytes(bitmap));
                                    subject1.setName("陌生人");
                                    subject1.setSid(null);
                                    subject1.setDepartmentName("暂无进入权限!");
                                    // linkedBlockingQueue.offer(subject1);
                                    Message message2 = Message.obtain();
                                    message2.what = 111;
                                    message2.obj = subject1;
                                    mHandler.sendMessage(message2);
                                    if (!DengUT.isOPENRed) {
                                        DengUT.isOPENRed = true;
                                        DengUT.getInstance(configBean).openRed();
                                    }

                                  //  long time=System.currentTimeMillis();
                                    for (int i = 0; i < detectionResult.faceList.length; i++) {//保存陌生人记录
                                        FacePassImage images = detectionResult.images[i];
                                        if (images.trackId == result.trackId) {
                                            final Bitmap fileBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
                                            long bitmapId=System.currentTimeMillis();
                                            String riqi= DateUtils.timeNYR(bitmapId+"");
                                            BitmapUtil.saveBitmapToSD(fileBitmap, MyApplication.SDPATH4+ File.separator+riqi,bitmapId+".png");
                                            //本地保存一份
                                            DaKaBean daKaBean=new DaKaBean();
                                            daKaBean.setId(bitmapId);
                                            daKaBean.setPath("http://" + FileUtil.getLocalHostIp() + ":8090"  + "/app/getFaceBitmap3?time=" +riqi+"&id="+bitmapId);
                                            daKaBean.setTime(bitmapId);
                                            daKaBean.setType(1);
                                            daKaBean.setPeopleType(-1);
                                            daKaBeanBox.put(daKaBean);
                                            break;
                                        }
                                    }
                                    showUIResult(3,"陌生人","");
                                    DengUT.isOPEN = true;
                                    //   msrBitmap = nv21ToBitmap.nv21ToBitmap(result.feedback.rgbImage.image, result.feedback.rgbImage.width, result.feedback.rgbImage.height);
                                    //   Log.d("RecognizeThread", "入库"+tID);
                                }
                            }

                        }
                    }
                } catch (InterruptedException | FacePassException e) {
                    e.printStackTrace();
                    DengUT.isOPEN = true;
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
        if (mServerManager!=null)
        mServerManager.stopServer();

        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("MianBanJiActivity3", "onRestart");
        if (mServerManager!=null)
            mServerManager.startServer();
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
        if (mServerManager!=null)
        mServerManager.unRegister();

        if (mReadThread != null) {
            mReadThread.isIterrupt=true;
            mReadThread.interrupt();
        }
        if (mReadThread2 != null) {
            mReadThread2.isIterrupt=true;
            mReadThread2.interrupt();
        }
        if (mReadThread3 != null) {
            mReadThread3.isIterrupt=true;
            mReadThread3.interrupt();
        }
        if (linkedBlockingQueue != null) {
            linkedBlockingQueue.clear();
        }
        if (mFeedFrameQueue != null) {
            mFeedFrameQueue.clear();
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
        if (manager != null) {
            manager.release();
        }

        if (task != null)
            task.cancel();
        timer.cancel();

        if (task2 != null)
            task2.cancel();
        timer2.cancel();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DengUT.getInstance(configBean).closeWrite();
                DengUT.getInstance(configBean).closeGreen();
                DengUT.getInstance(configBean).closeRed();
            }
        }).start();

        Log.d("MianBanJiActivity3", "onDestroy");
        super.onDestroy();
        MyApplication.myApplication.removeActivity(this);
    }




    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {
        if (event.equals("kaimen")) {
            DengUT.getInstance(configBean).openDool();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(jidianqi);
                    DengUT.getInstance(configBean).closeDool();
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
            configBean = MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class);
            if (configBean.getCompanyName() == null) {
                tvTitle_Ir.setText("请设置公司名称");
            } else {
                tvTitle_Ir.setText(configBean.getCompanyName());
            }
            if (configBean.getJidianqi()!=0){
                jidianqi= configBean.getJidianqi();
            }
            if (configBean.getRetryCount()!=0){
                cishu= configBean.getRetryCount();
            }
        }

        if (event.equals("mFacePassHandler")) {
            paAccessControl = MyApplication.myApplication.getFacePassHandler();
            return;
        }
        if (event.equals("youwang")){
            if (mServerManager==null){
                mServerManager = new ServerManager(MianBanJiActivity4.this);
                mServerManager.register();
            }
            if ((mServerManager!=null))
                mServerManager.startServer();

            String address = FileUtil.getLocalHostIp()+":"+configBean.getPort();
            logo.setText("本机后端地址 http://"+address);

            return;
        }
        if (event.equals("openCard")){//读卡返回给后台
            isReadCard=true;
            return;
        }

        Toast tastyToast = TastyToast.makeText(MianBanJiActivity4.this, event, TastyToast.LENGTH_LONG, TastyToast.INFO);
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
                    String xiaoshiss = DateUtils.timeMinute(System.currentTimeMillis() + "");
                    if (xiaoshiss.split(":")[0].equals("03") && xiaoshiss.split(":")[1].equals("40")) {
                        //过期删除
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                deleteFaceByTime();
                            }
                        }).start();
                    }
                    //1分钟一次指令获取

                    if (configBean.getHoutaiDiZhi() != null && !configBean.getHoutaiDiZhi().equals("")) {
                        if (isGET){
                            isGET=false;
                            try {
                                link_get_zhiling();
                            }catch (Exception e){
                                e.printStackTrace();
                                isGET=true;
                            }
                        }
                    }
                    link_xintiao();

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

    //心跳
    private void link_xintiao() {
        if (configBean.getXintiaoDIZhi() == null || configBean.getXintiaoDIZhi().equals("")) {
            return;
        }
        RequestBody body = null;
        body = new FormBody.Builder()
                .add("deviceKey", JHM)
                .add("time", System.currentTimeMillis()+"")
                .add("ip", FileUtil.getLocalHostIp())
                .add("version", AppUtils.getVersionName(MyApplication.myApplication)+"")
                .build();
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(configBean.getXintiaoDIZhi());
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "心跳请求失败", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                        tastyToast.show();
//                    }
//                });
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();

                    Log.d("AllConnects", "心跳" + ss);

                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");

                }
            }
        });
    }

    //过期删除
    private void deleteFaceByTime(){
       // @SuppressLint("SimpleDateFormat")
        // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //刷脸和刷卡记录过期删除
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -2);    //得到前二个月
        long start = c.getTimeInMillis();
        Log.d("MianBanJiActivity3", start+"");
        // Log.d("MianBanJiActivity3", DateUtils.timeNYR(start+""));
        LazyList<DaKaBean> subjectList= daKaBeanBox.query().less(DaKaBean_.time,start).build().findLazy();
        for (DaKaBean bean:subjectList){
            Log.d("MianBanJiActivity3", bean.toString());
            try {
                String[] times=bean.getPath().split("=");//找到日期
                String s= times[1].replace("&id","");
                if (bean.getPeopleType()==-1){
                    //陌生人记录过期删除
                    Log.d("MianBanJiActivity3", "删除文件夹4:"+s+":"+ FileUtil.delete(MyApplication.SDPATH4+File.separator+s));
                }else {
                    Log.d("MianBanJiActivity3", "删除文件夹2:"+s+":"+ FileUtil.delete(MyApplication.SDPATH2+File.separator+s));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        daKaBeanBox.remove(subjectList);



    }


    //获取指令
    private void link_get_zhiling() {
        if (configBean.getHoutaiDiZhi() == null || configBean.getHoutaiDiZhi().equals("")) {
            return;
        }
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .get()
                .url(configBean.getHoutaiDiZhi() + "/app/getCommands?" + "serialnumber=" + JHM);
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
                    isGET=true;
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
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
                        for (ZhiLingBean.ResultBean resultBean : commandsBean.getResult()) {
                            linkedBlockingQueue.put(resultBean);
                        }
                      //  if (linkedBlockingQueue.size()==0){

                     //   }
                        isGET=true;
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


//    //上传记录
//    private void link_shangchuanshualian(String id, Bitmap bitmap, String pepopleType,long bitmapId,String riqi) {
//        if (!id.equals("STRANGERBABY")){
//            soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
//        }
//
//        if (configBean.getHoutaiDiZhi() == null || configBean.getHoutaiDiZhi().equals("")) {
//            DaKaBean daKaBean=new DaKaBean();
//            daKaBean.setId(bitmapId);
//            daKaBean.setPath("http://" + FileUtil.getIPAddress(getApplicationContext()) + ":" + configBean.getPort() + "/getFaceBitmap2?time="+riqi+"&id="+bitmapId);
//            daKaBean.setPersonId(id);
//            daKaBean.setTime(bitmapId);
//            daKaBean.setType(pepopleType);
//            daKaBean.setState(1);
//            daKaBeanBox.put(daKaBean);
//            return;
//        }
//        RequestBody body = null;
//        if (pepopleType.equals("card")){
//            body = new FormBody.Builder()
//                    .add("personId", id + "")
//                    .add("deviceKey", JHM)
//                    .add("time", bitmapId + "")
//                    .add("type", pepopleType)
//                    .add("path", "")
//                    .add("imgBase64", "")
//                    .add("data", "")
//                    .build();
//        }else {
//            Bitmap bb = BitmapUtil.rotateBitmap(bitmap, SettingVar.msrBitmapRotation);
//            body = new FormBody.Builder()
//                    .add("personId", id + "")
//                    .add("deviceKey", JHM)
//                    .add("time", bitmapId + "")
//                    .add("type", pepopleType)
//                    .add("path", "http://" + FileUtil.getIPAddress(getApplicationContext()) + ":" + configBean.getPort() + "/getFaceBitmap2?time=" + riqi + "&id=" + bitmapId)
//                    .add("imgBase64", Bitmap2StrByBase64(bb))
//                    .add("data", "")
//                    .build();
//        }
//
//        Request.Builder requestBuilder=null;
//        try {
//            requestBuilder = new Request.Builder()
//                    .header("Content-Type", "application/json")
//                    .post(body)
//                    .url(configBean.getHoutaiDiZhi());
//        }catch (Exception e){
//            e.printStackTrace();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast tastyToast = TastyToast.makeText(MianBanJiActivity4.this, "上传识别记录地址错误", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                    tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                    tastyToast.show();
//                }
//            });
//            DaKaBean daKaBean=new DaKaBean();
//            daKaBean.setId(bitmapId);
//            daKaBean.setPath("http://" + FileUtil.getIPAddress(getApplicationContext()) + ":" + configBean.getPort() + "/getFaceBitmap2?time=" +riqi+"&id="+bitmapId);
//            daKaBean.setPersonId(id);
//            daKaBean.setTime(bitmapId);
//            daKaBean.setType(pepopleType);
//            daKaBean.setState(1);
//            daKaBeanBox.put(daKaBean);
//
//            return;
//        }
//        // step 3：创建 Call 对象
//
//        Call call = okHttpClient.newCall(requestBuilder.build());
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "请求失败" + e.getMessage());
//                DaKaBean daKaBean=new DaKaBean();
//                daKaBean.setId(bitmapId);
//                daKaBean.setPath("http://" + FileUtil.getIPAddress(getApplicationContext()) + ":" + configBean.getPort() + "/getFaceBitmap2?time=" +riqi+"&id="+bitmapId);
//                daKaBean.setPersonId(id);
//                daKaBean.setTime(bitmapId);
//                daKaBean.setType(pepopleType);
//                daKaBean.setState(1);
//                daKaBeanBox.put(daKaBean);
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity4.this, "上传识别记录失败", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                        tastyToast.show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//
//
//                    DaKaBean daKaBean=new DaKaBean();
//                    daKaBean.setId(bitmapId);
//                    daKaBean.setPath("http://" + FileUtil.getIPAddress(getApplicationContext()) + ":" + configBean.getPort() + "/getFaceBitmap2?time=" +riqi+"&id="+bitmapId);
//                    daKaBean.setPersonId(id);
//                    daKaBean.setTime(bitmapId);
//                    daKaBean.setType(pepopleType);
//                    daKaBean.setState(1);
//                    daKaBeanBox.put(daKaBean);
//
//                    Log.d("AllConnects", "上传识别记录" + ss);
//
//                } catch (Exception e) {
//                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
//                }
//            }
//        });
//    }
//
//    //上传记录
//    private void link_shuaka(String id,String name) {
//        if (configBean.getHoutaiDiZhi() == null || configBean.getHoutaiDiZhi().equals("")) {
//            return;
//        }
//        RequestBody body = null;
//        body = new FormBody.Builder()
//                .add("id", id)
//                .add("name", name)
//                .add("serialnumber", JHM)
//                .build();
//        Request.Builder requestBuilder = new Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(body)
//                .url(configBean.getHoutaiDiZhi() + "/app/updateIDcardTake");
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "请求失败" + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity4.this, "上传识别记录失败", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                        tastyToast.show();
//                    }
//                });
//            }
//
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//
//                    Log.d("AllConnects", "上传识别记录" + ss);
//
//                } catch (Exception e) {
//                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
//
//                }
//            }
//        });
//    }
//
//    //数据同步
//    private void link_infoSync() {
//        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        JSONArray array = new JSONArray();
//        LazyList<HuiFuBean> huiFuBeanList = huiFuBeanBox.query().build().findLazy();
//        if (huiFuBeanList.size()==0)
//            return;
//        List<HuiFuBean> huiFuBeans=new ArrayList<>();
//        for (HuiFuBean h:huiFuBeanList){
//            huiFuBeans.add(h);
//        }
//            for (HuiFuBean bean : huiFuBeans) {
//                JSONObject object = new JSONObject();
//                try {
//                    object.put("pepopleId", bean.getPepopleId());
//                    object.put("pepopleType", bean.getPepopleType());
//                    object.put("type", bean.getType());
//                    object.put("msg", bean.getMsg());
//                    object.put("shortId", bean.getShortId());
//                    object.put("serialnumber", JHM);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                array.put(object);
//            }
//
//        Log.d("MianBanJiActivity3", "数据同步：" + array.toString());
//        RequestBody body = RequestBody.create(array.toString(), JSON);
//        Request.Builder requestBuilder = new Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(body)
//                .url(configBean.getHoutaiDiZhi() + "/app/infoSync");
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "数据同步请求失败" + e.getMessage());
//              //  isLink = false;
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//                    Log.d("AllConnects", "数据同步:" + ss);
//                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
//
//                    for (HuiFuBean bean : huiFuBeans) {
//                        huiFuBeanBox.remove(bean);
//                    }
//                } catch (Exception e) {
//                    Log.d("WebsocketPushMsg", e.getMessage() + "数据同步");
//                }
//            }
//        });
//    }




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
        try {
            HwitManager.HwitSetHideSystemBar(MianBanJiActivity4.this);
            HwitManager.HwitSetDisableSlideShowSysBar(1);
        }catch (NoClassDefFoundError error){
            error.printStackTrace();
        }
    }



//    private void init_NFC() {
//
//        NfcManager mNfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
//        mNfcAdapter = mNfcManager.getDefaultAdapter();
//        if (mNfcAdapter == null) {
//            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "设备不支持NFC", TastyToast.LENGTH_LONG, TastyToast.INFO);
//            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//            tastyToast.show();
//        } else if (!mNfcAdapter.isEnabled()) {
//            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请先去设置里面打开NFC开关", TastyToast.LENGTH_LONG, TastyToast.INFO);
//            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//            tastyToast.show();
//        } else if ((mNfcAdapter != null) && (mNfcAdapter.isEnabled())) {
//
//        }
//        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
//
//
//        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
//        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
//
//        if (mNfcAdapter != null) {
//            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
//            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(this.getIntent().getAction())) {
//                processIntent(this.getIntent());
//            }
//        }
//
//
//    }

//    private void stopNFC_Listener() {
//        mNfcAdapter.disableForegroundDispatch(this);
//    }

//    public void processIntent(Intent intent) {
//        //  String data = null;
//        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        // String[] techList = tag.getTechList();
//        // Log.d("Mian", "tag.describeContents():" + tag.describeContents());
//        byte[] ID;
//        //  data = tag.toString();
//        if (tag == null)
//            return;
//        ID = tag.getId();
////        data += "\n\nUID:\n" + byteToString(ID);
////        data += "\nData format:";
////        for (String tech : techList) {
////            data += "\n" + tech;
////        }
////         Log.d("MianBanJiActivity3", byteToString(ID));
//        String sdfds = byteToString(ID);
//        if (sdfds != null) {
//            Log.d("MianBanJiActivity3", sdfds);
//            sdfds = sdfds.toUpperCase();
//            List<IDCardBean> idCardBeanList = idCardBeanBox.query().equal(IDCardBean_.idCard, sdfds).build().find();
//            if (idCardBeanList.size() > 0) {
//                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证成功!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
//                tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                tastyToast.show();
//                soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
//                DengUT.getInstance(configBean).openDool();
//                IDCardBean cardBean=idCardBeanList.get(0);
//                link_shuaka(sdfds,cardBean.getName());
//                //启动定时器或重置定时器
//                if (task != null) {
//                    task.cancel();
//                    task = new TimerTask() {
//                        @Override
//                        public void run() {
//                            Message message = new Message();
//                            message.what = 222;
//                            mHandler.sendMessage(message);
//                        }
//                    };
//                    timer.schedule(task, 6000);
//                } else {
//                    task = new TimerTask() {
//                        @Override
//                        public void run() {
//                            Message message = new Message();
//                            message.what = 222;
//                            mHandler.sendMessage(message);
//                        }
//                    };
//                    timer.schedule(task, 6000);
//                }
//
//                IDCardTakeBean takeBean=new IDCardTakeBean();
//                takeBean.setIdCard(sdfds);
//                takeBean.setName(cardBean.getName());
//                takeBean.setTime(System.currentTimeMillis());
//                idCardTakeBeanBox.put(takeBean);
//
//            } else {
//                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证失败!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
//                tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                tastyToast.show();
//                soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//            }
//        }
//
//
//    }

    /**
     * 将byte数组转化为字符串
     *
     * @param src
     * @return
     */
    public static String byteToString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            // System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }


    public static class NetWorkStateReceiver extends BroadcastReceiver {
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
                }
                for (Network network : networks) {
                    //获取ConnectivityManager对象对应的NetworkInfo对象
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                    if (networkInfo!=null && networkInfo.isConnected()) {
                        //连接上
                        Log.d("MianBanJiActivity3", "有网2");
                        EventBus.getDefault().post("youwang");
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



    /**
     * 显示结果UI
     *
     * @param state 状态 1 初始状态  2 识别中,出现提示语  3 识别失败  4 识别成功
     */
    protected void showUIResult(final int state, final String name, final String detectFaceTime) {
        if (state==STATE){
            return;
        }else {
            STATE=state;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              //  Log.d("MianBanJiActivity3", "state:" + state);
                switch (state) {
                    case 1: {//初始状态
                        layout_true_gif_Ir.setVisibility(View.INVISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.INVISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.INVISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.INVISIBLE);//识别结果大框
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.GONE);//时间
                        tvFaceTips_Ir.setVisibility(View.GONE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("");
                        tvFaceTips_Ir.setText("");
                        break;
                    }
                    case 2: {//识别中,出现提示语
                        layout_true_gif_Ir.setVisibility(View.VISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.VISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.VISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.true_bg);//切换背景
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.GONE);//时间
                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("");
                        tvFaceTips_Ir.setText("识别中,请稍后...");
                        break;
                    }
                    case 3: {//识别失败
                        layout_true_gif_Ir.setVisibility(View.INVISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.VISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.INVISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.INVISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.VISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.VISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.error_bg);//切换背景
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.VISIBLE);//时间
                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("无权限通过,请重试");
                        break;
                    }
                    case 4: {//识别成功
                        layout_true_gif_Ir.setVisibility(View.VISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.VISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.VISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.true_bg);//切换背景
                        tvName_Ir.setVisibility(View.VISIBLE);//姓名
                        tvTime_Ir.setVisibility(View.VISIBLE);//时间
                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
                        tvName_Ir.setText(name);
                        tvTime_Ir.setText(detectFaceTime);
                        break;
                    }
                }
            }
        });
    }


    /**
     * 显示结果UI
     *
     * @param
     */
    protected void showUIResult(String name, final String detectFaceTime,int tt) {
        if (tt==0){
            layout_loadbg_Ir2.setVisibility(View.INVISIBLE);//识别结果大框
            tvName_Ir2.setVisibility(View.INVISIBLE);//姓名
            tvTime_Ir2.setVisibility(View.INVISIBLE);//时间
            tvFaceTips_Ir2.setVisibility(View.INVISIBLE);//识别提示
            tvName_Ir2.setText(name);
            tvTime_Ir2.setText(detectFaceTime);
        }else {
            layout_loadbg_Ir2.setVisibility(View.VISIBLE);//识别结果大框
            tvName_Ir2.setVisibility(View.VISIBLE);//姓名
            tvTime_Ir2.setVisibility(View.VISIBLE);//时间
            tvFaceTips_Ir2.setVisibility(View.VISIBLE);//识别提示
            tvName_Ir2.setText(name);
            tvTime_Ir2.setText(detectFaceTime);
        }

    }

    private void closePing(){
        //启动定时器或重置定时器
        if (task != null) {
            task.cancel();
            //timer.cancel();
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    //关屏
                    message.what = 444;
                    mHandler.sendMessage(message);
                }
            };
            timer.schedule(task, jidianqi);
        } else {
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    //关屏
                    message.what = 444;
                    mHandler.sendMessage(message);

                }
            };
            timer.schedule(task, jidianqi);
        }
    }

    /**
     * Activity截获按键事件.发给ScanGunKeyEventHelper
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //  Log.d("Main4Activity", event.getDevice().toString());
        mScanGunKeyEventHelper.analysisKeyEvent(event);
        return true;
        //  return super.dispatchKeyEvent(event);
    }


    //usb读卡器输出
    @Override
    public void onScanSuccess(String barcode) {
        //   Log.d("MianBanJiActivity3", barcode+"dddddd");

        if (barcode!=null && !barcode.equals("")){
            Message message = new Message();
            message.what = 999;
            message.obj=barcode.trim();
            mHandler.sendMessage(message);
        }

    }

}
