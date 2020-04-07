package megvii.testfacepass.pa.utils;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;

import org.greenrobot.eventbus.EventBus;

import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassConfig;
import mcv.facepass.types.FacePassModel;
import mcv.facepass.types.FacePassPose;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.beans.BaoCunBean;


public class FacePassUtil {

    /* SDK 实例对象 */
    private Context context;
  //  private int TIMEOUT=30*1000;

    private FacePassHandler mFacePassHandler;  /* 人脸识别Group */
    private  final String group_name = "facepasstestx";

    public  void init(final Activity activity , final Context context, final int cameraRotation, final BaoCunBean baoCunBean){
        this.context=context;

            new Thread() {
                @Override
                public void run() {
                    while (!activity.isFinishing()) {
                        if (FacePassHandler.isAvailable()) {
                            FacePassConfig config;
                            try {
                                /* 填入所需要的配置 */
                                config = new FacePassConfig();
                                config.poseBlurModel = FacePassModel.initModel(MyApplication.ampplication.getAssets(), "attr.pose_blur.align.av200.190630.bin");

                                config.livenessModel = FacePassModel.initModel(MyApplication.ampplication.getAssets(), "liveness.3288CPU.rgb.int8.C.bin");

                                //也可以使用GPU活体模型，GPU活体模型分两个，用于GPU加速的模型和CACHE，当使用CPU活体模型时，请传null，当使用GPU活体模型时，必须传入加速cache模型
//                            config.livenessModel = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.3288GPU.rgb.C.bin");
//                            config.livenessGPUCache = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.GPU.AlgoPolicy.C.cache");

                                config.searchModel = FacePassModel.initModel(MyApplication.ampplication.getAssets(), "feat2.arm.F.v1.0.1core.bin");
                                config.detectModel = FacePassModel.initModel(MyApplication.ampplication.getAssets(), "detector.arm.C.bin");
                                config.detectRectModel = FacePassModel.initModel(MyApplication.ampplication.getAssets(), "detector_rect.arm.C.bin");
                                config.landmarkModel = FacePassModel.initModel(MyApplication.ampplication.getAssets(), "pf.lmk.float32.1015.bin");

                                config.mouthOccAttributeModel = FacePassModel.initModel(MyApplication.ampplication.getAssets(), "attribute.mouth.occ.gray.12M.190930.bin");
                                //config.smileModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.smile.mgf29.0.1.1.181229.bin");
                                //config.ageGenderModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.age_gender.surveillance.nnie.av200.0.1.0.190630.bin");
                                //config.occlusionFilterModel = FacePassModel.initModel(getApplicationContext().getAssets(), "occlusion.all_attr_configurable.occ.190816.bin");
                                //如果不需要表情和年龄性别功能，smileModel和ageGenderModel可以为null
                                //config.smileModel = null;
                                //config.ageGenderModel = null;

                                //config.occlusionFilterEnabled = true;
                                config.mouthOccAttributeEnabled = true;
                                config.rgbIrLivenessEnabled = false;
                                config.smileEnabled = false;
                                config.rotation = cameraRotation;
                                config.searchThreshold =  baoCunBean.getShibieFaZhi();
                                config.livenessThreshold = baoCunBean.getHuoTiFZ();
                                config.livenessEnabled = baoCunBean.isHuoTi();
                                boolean ageGenderEnabledGlobal = (config.ageGenderModel != null);
                                config.faceMinThreshold = baoCunBean.getShibieFaceSize();
                                config.poseThreshold = new FacePassPose(30f, 30f, 30f);
                                config.blurThreshold = 0.5f;
                                config.lowBrightnessThreshold = 70f;
                                config.highBrightnessThreshold = 210f;
                                config.brightnessSTDThreshold = 80f;
                                config.retryCount = baoCunBean.getMoshengrenPanDing();
                                config.maxFaceEnabled = true;
                                config.fileRootPath = MyApplication.SDPATH2;
                                /* 创建SDK实例 */
                                mFacePassHandler = new FacePassHandler(config);
                                MyApplication.myApplication.setFacePassHandler(mFacePassHandler);

                                checkGroup(activity,context);
                              //  float searchThreshold2 = 75f;
                              //  float livenessThreshold2 = 48f;
                             //   boolean livenessEnabled2 = true;
                                int faceMinThreshold2 = baoCunBean.getRuKuFaceSize();
                                float blurThreshold2 = 0.3f;
                                float lowBrightnessThreshold2 = 70f;
                                float highBrightnessThreshold2 = 210f;
                                float brightnessSTDThreshold2 = 80f;
                                FacePassConfig config1=new FacePassConfig(faceMinThreshold2,22f,22f,22f,blurThreshold2,
                                        lowBrightnessThreshold2,highBrightnessThreshold2,brightnessSTDThreshold2);
                                boolean is=   mFacePassHandler.setAddFaceConfig(config1);

                                Log.d("YanShiActivity", " 设置入库质量配置"+is );

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast tastyToast= TastyToast.makeText(context,"识别模块初始化成功",TastyToast.LENGTH_LONG,TastyToast.INFO);
                                        tastyToast.setGravity(Gravity.CENTER,0,0);
                                        tastyToast.show();
                                        MyApplication.myApplication.setFacePassHandler(mFacePassHandler);
                                        EventBus.getDefault().post("mFacePassHandler");
                                    }
                                });



                            } catch (FacePassException e) {
                                e.printStackTrace();

                                return;
                            }
                            return;
                        }
                        try {
                            /* 如果SDK初始化未完成则需等待 */
                            sleep(500);
                            Log.d("FacePassUtil", "激活中。。。");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }


    private  void checkGroup(Activity activity, final Context context) {
        if (mFacePassHandler == null) {
            return;
        }
        String[] localGroups = mFacePassHandler.getLocalGroups();

        if (localGroups == null || localGroups.length == 0) {
            try {
               boolean a= mFacePassHandler.createLocalGroup(group_name);
                Log.d("FacePassUtil", "创建组:" + a);
            } catch (FacePassException e) {
                e.printStackTrace();
            }
        }else {
            Log.d("FacePassUtil", "组名:"+localGroups[0]);
        }
    }

//    private void chaxuncuowu(){
//        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
//                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
//                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
////				.cookieJar(new CookiesManager())
//                //.retryOnConnectionFailure(true)
//                .build();
//
//        RequestBody body = new FormBody.Builder()
//                .add("machineCode)", FileUtil.getSerialNumber(context) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(context))
//                .build();
//        Request.Builder requestBuilder = new Request.Builder()
//                //.header("Content-Type", "application/json")
//                .post(body)
//                .url(baoCunBean.getHoutaiDiZhi() + "/app/findFailurePush");
//
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "请求失败" + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    //没了删除，所有在添加前要删掉所有
//
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//                    Log.d("AllConnects", "查询错误推送" + ss);
//
//                } catch (Exception e) {
//
//                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
//                }
//            }
//        });
//    }

}
