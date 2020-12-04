package megvii.testfacepass.pa.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import com.srp.AuthApi.AuthApi;
import com.srp.AuthApi.AuthApplyResponse;
import com.srp.AuthApi.AuthCallback;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import megvii.testfacepass.pa.beans.BangDingBean;
import megvii.testfacepass.pa.beans.BaoCunBean;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import sun.misc.BASE64Decoder;


public class FaceInit {
    private Context context;
    private Activity activity;
    private BaoCunBean baoCunBean;
    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .writeTimeout(22000, TimeUnit.MILLISECONDS)
            .connectTimeout(22000, TimeUnit.MILLISECONDS)
            .readTimeout(22000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
            //         .retryOnConnectionFailure(true)
            .build();

    private SharedPreferences mSharedPreferences;

    private static final int GET_AUTH_OK = 0;
    private static final String CERT_PATH = "/Download/CBG_Android_Face_Reco---30-Trial-two-stage.cert";
    private static final String ACTIVE_PATH = "/Download/CBG_Android_Face_Reco---90-Trial--1-active.txt";
    private AuthApi obj;


    static {
        System.loadLibrary("ruitongnative");
    }


    public FaceInit(Context context, Activity activity) {
        this.context =context;
        this.activity=activity;
        obj = new AuthApi();
        mSharedPreferences = context.getSharedPreferences("SP", Context.MODE_PRIVATE);

        try {
            singleCertification("","","","");
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(e.getMessage()+"授权失败");
        }

    }




    private  void initFacePassSDK(String s1, String s2, String s3, String id, int type, String url) {
      //  FileUtil.writeCERT(context);
        try {
            singleCertification(s1,s2,id,url);
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(e.getMessage()+"授权失败");
        }
    }


    private  void singleCertification(String cert,String active, final String id, final String url2) throws IOException {
      //  String cert = readExternal(CERT_PATH).trim();
         cert="\"{\"\"serial\"\":\"\"m00420764abde7861a9499f135641501301d9\"\",\"\"key\"\":\"\"d773e499d7987dee4a3b9fe607853b364bf09504e148787d4c51e762bb029e165137f5a1b2533768ad64cde31cfc20fc0f818ac120de0cdfba564b6c052244e392c424d99ab6dd6028cc3f6a8ce8d21b7344bc1d6d4bf9e470dca6120fc3ced81b90c7899cc761b4daf30b181fb69881559f0c7689d37c8f575483ea5b353787502c2528f43b8f04181237dc9a88ddc97e4cb1ad4e56d77726a7efba9b57f07da19531c47609de8c017edac474a31e6b066f3fbc99fd01589257592ad5223ddb\"\",\"\"counting_logic\"\":\"\"\"\"}\"\n";
        active="n5d10446eb34881deed7df35c50a60dd6000708";
        if (TextUtils.isEmpty(cert)|| TextUtils.isEmpty(active)){
            EventBus.getDefault().post("授权文件为空");
            return;
        }
        Log.d("FaceInit", "cert:"+cert);
        Log.d("FaceInit","active:"+active );
        obj.authDevice(context, cert, active, new AuthApi.AuthDeviceCallBack() {
            @Override
            public void GetAuthDeviceResult(AuthApplyResponse authApplyResponse) {
                if(authApplyResponse.errorCode == GET_AUTH_OK){
                    //Log.d("FaceInit", "激活成功");
                    MMKV.defaultMMKV().encode("token",true);
                    //link_uplodeBD(id,url2);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,"激活成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Log.d("激活失败", authApplyResponse.errorMessage);
                    Log.d("FaceInit", "i:" + authApplyResponse.errorCode);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,"激活失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private String readExternal(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            filename = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + filename;
            File file = new File(filename);
            if (!file.exists()){
                return "";
            }
            FileInputStream inputStream = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            while(len > 0){
                sb.append(new String(buffer,0,len));
                len = inputStream.read(buffer);
            }
            inputStream.close();
        }

        return sb.toString();
    }


    public void init(String registration, BaoCunBean baoCunBean){
        this.baoCunBean=baoCunBean;
        link_uplod(registration,baoCunBean.getTouxiangzhuji());

    }

    //绑定
    private void link_uplod(String zhucema, final String url){
        //	final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    //    Log.d("FaceInit", "dsadsadasf");
        //RequestBody requestBody = RequestBody.create(JSON, json);
        RequestBody body = new FormBody.Builder()
                .add(init(System.currentTimeMillis()),zhucema)
                .add("machineCode", baoCunBean.getTuisongDiZhi()+"")
                .add("packagesName", AppUtils.getPackageName(context)+"")
                .add("machineToken",baoCunBean.getXgToken()+"")
                .build();
        Request.Builder requestBuilder = new Request.Builder()
//				.header("Content-Type", "application/json")
//				.header("user-agent","Koala Admin")
                //.post(requestBody)
                //.get()
                .post(body)
                .url(url+recess2(System.currentTimeMillis()));

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败"+e.getMessage());
                EventBus.getDefault().post("网络请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) {
              //  Log.d("AllConnects", "请求成功"+call.request().toString());
                //获得返回体
                String ss=null;
                try{
                    ResponseBody body = response.body();
                     ss=body.string().trim();
                    Log.d("AllConnects", "注册码"+ss);
                    final JsonObject jsonObject= parse(ss).getAsJsonObject();
                    String idid=jsonObject.get("id").getAsString();
                    String urlurl=jsonObject.get("url").getAsString();
                    link_ferrce(idid,url+urlurl,url);

                }catch (Exception e){
                    try {
                        JsonObject jsonObject= parse(ss).getAsJsonObject();
                        String idid=jsonObject.get("message").getAsString();
                        EventBus.getDefault().post(idid);
                    }catch (Exception eee){
                        EventBus.getDefault().post(e.getMessage()+"");
                    }
                  //  Log.d("WebsocketPushMsg", e.getMessage()+"ttttt");
                }

            }
        });
    }

    //绑定设备
    private void link_ferrce(String id, final String url, final String url2){
        //	final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

        //RequestBody requestBody = RequestBody.create(JSON, json);
        RequestBody body = new FormBody.Builder()
                .add("id",id)
                .build();
        Request.Builder requestBuilder = new Request.Builder()
//				.header("Content-Type", "application/json")
//				.header("user-agent","Koala Admin")
                //.post(requestBody)
                //.get()
                .post(body)
                .url(url);

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
              //  Log.d("AllConnects", "请求失败"+e.getMessage());
                EventBus.getDefault().post("网络请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
              //  Log.d("AllConnects", "请求成功"+call.request().toString());
                //获得返回体
                try{
                    ResponseBody body = response.body();
                    String ss=body.string().trim();
                   // Log.d("AllConnects", "绑定："+ss);
                    JsonObject jsonObject= parse(ss).getAsJsonObject();
                    Gson gson=new Gson();
                    final BangDingBean dingBean=gson.fromJson(jsonObject,BangDingBean.class);
                    String passd=recess(dingBean.getAccredit()+dingBean.getSdkPwd());

                    byte[] b1=new BASE64Decoder().decodeBuffer(passd);
                    byte[] b2=dingBean.getKeyiv().getBytes();
                    byte[] b3=new BASE64Decoder().decodeBuffer(dingBean.getVerifyIp());
                    byte[] b4=new BASE64Decoder().decodeBuffer(dingBean.getApiKey());
                    byte[] b5=new BASE64Decoder().decodeBuffer(dingBean.getApiSecret());

                    String s1s= null;
                    String s2s=null;
                    String s3s=null;
                    try {
                        s1s = new String(des3DecodeCBC(b1,b2,b3));
                        s2s = new String(des3DecodeCBC(b1,b2,b4));
                        s3s = new String(des3DecodeCBC(b1,b2,b5));
                    } catch (Exception e) {
                        e.printStackTrace();
                        EventBus.getDefault().post("激活设备失败"+e.getMessage());
//                        Log.d("MyReceiver", e.getMessage()+"gggg");
                    }

//
//                    isExists(dinner(System.currentTimeMillis()));
//                    File file = null;
//                    try {
//                        file = new File(SDPATH + File.separator+dinner(System.currentTimeMillis())+File.separator+recess2(System.currentTimeMillis()));
//                        if (!file.exists()) {
//                            file.createNewFile();
//                        }else {
//                            file.delete();
//                            file.createNewFile();
//                        }
//                        FileOutputStream outputStream=new FileOutputStream(file,true);
//                        byte[] bytesArray = s1s.getBytes();
//                        outputStream.write(bytesArray);
//                        outputStream.write("\r\n".getBytes());// 写入一个换行
//                        outputStream.flush();
//                        byte[] bytesArray2 = s2s.getBytes();
//                        outputStream.write(bytesArray2);
//                        outputStream.write("\r\n".getBytes());// 写入一个换行
//                        outputStream.flush();
//                        byte[] bytesArray3 = s3s.getBytes();
//                        outputStream.write(bytesArray3);
//                        outputStream.write("\r\n".getBytes());// 写入一个换行
//                        outputStream.flush();
//                        outputStream.close();
//                        String a1 = null,a2=null,a3=null;
//                        int i=0;
//                        FileInputStream inputStream = new FileInputStream(file);
//                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                        String str = null;
//                        while((str = bufferedReader.readLine()) != null)
//                        {
//                            i++;
//                            if (i==1){
//                                a1=str;
//                            }else if (i==2){
//                                a2=str;
//                            }else if (i==3){
//                                a3=str;
//                            }
//                        }
//                        inputStream.close();
//                        bufferedReader.close();
                    initFacePassSDK(s1s, s2s, s3s,dingBean.getId(),2,url2);
                    Log.d("FaceInit", s1s+":1");//APIKey
                    Log.d("FaceInit", s2s+":2");//APIsecret
                //    Log.d("FaceInit", s3s+"");//验证IP

                }catch (Exception e){
                  //  Log.d("WebsocketPushMsg", e.getMessage()+"ttttt");
                    EventBus.getDefault().post("激活设备失败"+e.getMessage());
                }

            }
        });
    }



    public native static String init(long l1);
    public native static String dinner(long l2);
    public native static String recess(String info);
    public native static String recess2(long info);

    public static JsonElement parse(String json) {

        JsonParser mJsonParser = new JsonParser();
        return mJsonParser.parse(json);
    }

    public static JsonObject optJsonObject(JsonElement e) {
        return optJsonObject(e, null);
    }

    public static JsonObject optJsonObject(JsonElement e, JsonObject defaultOne) {
        if (null == e || e.isJsonNull()) {
            return defaultOne;
        }

        return e.getAsJsonObject();
    }

    public static JsonArray optJsonArray(JsonElement e) {
        return optJsonArray(e, null);
    }

    public static JsonArray optJsonArray(JsonElement e, JsonArray defaultOne) {
        if (null == e || e.isJsonNull()) {
            return defaultOne;
        }

        return e.getAsJsonArray();
    }

    public static String optString(JsonElement e) {
        return optString(e, null);
    }

    public static String optString(JsonElement e, String defaultOne) {
        if (null == e || e.isJsonNull()) {
            return defaultOne;
        }

        return e.getAsString();
    }

    public static int optInt(JsonElement e) {
        return optInt(e, -1);
    }

    public static int optInt(JsonElement e, int defaultOne) {
        if (null == e || e.isJsonNull()) {
            return defaultOne;
        }

        return e.getAsInt();
    }




    public static byte[] des3DecodeCBC(byte[] key, byte[] keyiv, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        return cipher.doFinal(data);
    }

    public static String getSerialNumber(Context context){

        String serial = null;

        try {

            serial = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return serial;

    }

    /**
     * 获取手机IMSI号
     */
    public static String getIMSI(){


        return "355" +
                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10;
    }

    public static boolean isExists(String path) {
        if (null == path) {
            return false;
        }
        String name;

        name = SDPATH + File.separator + path;

        File file = new File(name);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.exists();
    }
    private static final String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath();


    //提交绑定状态
    private  void link_uplodeBD(String id, String url){

        //	final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

        //RequestBody requestBody = RequestBody.create(JSON, json);
        RequestBody body = new FormBody.Builder()
                .add("id",id)
                .build();
        Request.Builder requestBuilder = new Request.Builder()
//				.header("Content-Type", "application/json")
//				.header("user-agent","Koala Admin")
                //.post(requestBody)
                //.get()
                .post(body)
                .url(url+"/app/destroyMachine");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
              //  Log.d("AllConnects", "请求失败"+e.getMessage());
                EventBus.getDefault().post("网络请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
              //  Log.d("AllConnects", "请求成功"+call.request().toString());
                //获得返回体
                try{
                    ResponseBody body = response.body();
                    String ss=body.string().trim();
                    Log.d("AllConnects", "上传绑定成功状态"+ss);
                   JsonObject jsonObject= parse(ss).getAsJsonObject();
                   EventBus.getDefault().post(jsonObject.get("message").getAsString());
//					Gson gson=new Gson();
//					final HuiYiInFoBean renShu=gson.fromJson(jsonObject,HuiYiInFoBean.class);

                }catch (Exception e){
                    EventBus.getDefault().post("绑定设备异常"+e.getMessage());
                //    Log.d("WebsocketPushMsg", e.getMessage()+"ttttt");
                }

            }
        });
    }

}
