package megvii.testfacepass.pa.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.SerialPort;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.lztek.toolkit.Lztek;
import com.sdsmdg.tastytoast.TastyToast;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Lib.Reader.MT.Function;
import io.objectbox.Box;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.adapter.UserListAdapter2;
import megvii.testfacepass.pa.beans.BaoCunBean;
import megvii.testfacepass.pa.utils.GsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class ReadCardActivity extends AppCompatActivity {
    private ReadThread mReadThread;
    private Button tijiao;
    private List<String> subjectList=new ArrayList<>();
    private UserListAdapter2 userListAdapter2;
    private ListView listView;
    private Function mFuncs = null;
    private int loc_readerHandle=-1;
    private Handler mHandler;
    private BaoCunBean baoCunBean=null;
    private Box<BaoCunBean> baoCunBeanBox=MyApplication.myApplication.getBaoCunBeanBox();
    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .writeTimeout(20000, TimeUnit.MILLISECONDS)
            .connectTimeout(20000, TimeUnit.MILLISECONDS)
            .readTimeout(20000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
            //        .retryOnConnectionFailure(true)
            .build();
    private int jiqiType=-1;
    private InputStream mInputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_card);
        listView=findViewById(R.id.recyle);
        ImageView imageView=findViewById(R.id.fanhui);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       baoCunBean= baoCunBeanBox.get(123456L);

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
        }

        if (jiqiType==2){
            try {
                SerialPort mSerialPort = MyApplication.myApplication.getSerialPort();
                //mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (Exception e) {
                Log.d("MianBanJiActivity", e.getMessage() + "dddddddd");
            }
        }


//        sousuokuang= findViewById(R.id.sousuo);
//        sousuokuang.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d("ReadCardActivity", s.toString()+"L");
//                String ss=s.toString().trim();
//                if (!ss.equals("")){
//                    Log.d("ReadCardActivity", "不为空");
//                  List<Subject> subjects= subjectBox.query().contains(Subject_.name,ss).build().find();
//                 subjectList.clear();
//                 subjectList.addAll(subjects);
//                 userListAdapter2.notifyDataSetChanged();
//                }else {
//                    subjectList.clear();
//                    userListAdapter2.notifyDataSetChanged();
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

        tijiao=findViewById(R.id.tijiao);
        tijiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ReadCardActivity", "提交");
                link_infoSync();
            }
        });

        userListAdapter2=new UserListAdapter2(subjectList,this);
        userListAdapter2.setOnItemDeleteButtonClickListener(new UserListAdapter2.ItemDeleteButtonClickListener() {
            @Override
            public void OnItemDeleteButtonClickListener(int position) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(ReadCardActivity.this);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subjectList.remove(position);
                        userListAdapter2.notifyDataSetChanged();
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setMessage("你确定要删除吗？");
                builder.setTitle("温馨提示");
                builder.show();
            }
        });
        listView.setAdapter(userListAdapter2);


        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NotNull Message msg) {
                switch (msg.what) {
                    case 222:
                      String ka= (String) msg.obj;
                      boolean isa=false;
                      if (ka!=null){
                          for (String s:subjectList){
                              if (s.equals(ka)){
                                  Toast tastyToast = TastyToast.makeText(ReadCardActivity.this, "ID卡重复,请换张卡", TastyToast.LENGTH_SHORT, TastyToast.INFO);
                                  tastyToast.setGravity(Gravity.CENTER, 0, 0);
                                  tastyToast.show();
                                  isa=true;
                                  break;
                              }
                          }
                          if (!isa){
                              subjectList.add(ka);
                              userListAdapter2.notifyDataSetChanged();
                          }
                      }
                      break;
                }
                return false;
            }
        });

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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true){
                                int st = 0;
                                byte[] rData = new byte[128];
                                int[] rlen = new int[2];
                                if (loc_readerHandle!=-1){
                                    //Log.d("MianBanJiActivity3", "ffffffff:"+loc_readerHandle);
                                    st = mFuncs.lc_getAutoReturnedData(loc_readerHandle, rData, rlen);
                                    //  Log.d("MianBanJiActivity3", "hh哈哈:"+st);
                                    if (st == 0)
                                    {
                                        String sdfds = new String(rData);
                                        sdfds = sdfds.substring(0, 10);
                                        Log.d("ReadThread2", sdfds);
            //                        StringBuilder showStr= new StringBuilder();
            //                        int len=rlen[0];
            //                        for(int i= 0; i<len; i++)
            //                            showStr.append(byteToHexString(rData[i]));
            //                        Log.d("MianBanJiActivity3", showStr.toString());
                                        Message message = new Message();
                                        message.what = 222;
                                        message.obj=sdfds.trim();
                                        mHandler.sendMessage(message);
                                        SystemClock.sleep(100);
                                    }
                                }

                            }

                        }
                    }).start();



                }
            }catch (NoClassDefFoundError error){
                Log.d("MianBanJiActivity3", error.getMessage()+"");
            }
        }

        Toast tastyToast = TastyToast.makeText(ReadCardActivity.this, "请刷卡！", TastyToast.LENGTH_LONG, TastyToast.INFO);
        tastyToast.setGravity(Gravity.CENTER, 0, 0);
        tastyToast.show();

    }



    private void readdd(byte[] idid) {
        StringBuilder builder = new StringBuilder();
        String sdfds = byteToString(idid);
        long d=0;
        if (sdfds != null) {
            sdfds = sdfds.substring(6, 14);
            Log.d("ReadThread", sdfds);
            if(sdfds.length() == 8) {
                for(int i = 0; i<4; i++) {
                    String str = sdfds.substring(sdfds.length()-2 * (i+1), sdfds.length()-2*i);
                    builder.append(str);
                }
            }
            d = Long.valueOf(builder.toString(),16);   //d=255
            Log.d("ReadThread", "builder:" + d);
        } else {
            return;
        }
      //  sdfds = sdfds.toUpperCase();
      //  Log.d("MianBanJiActivity3", sdfds);

        String str= addO(d+"");
        Message message = new Message();
        message.what = 222;
        message.obj=str;
        mHandler.sendMessage(message);

    }

    private String addO(String ss){
        if (ss.length()>0 && ss.length()<10){
            ss="0"+ss;
            addO(ss);
        }
        return ss;
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    final byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        // Log.d("ReadThread", "buffer.length:" + byteToString(buffer));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                readdd(buffer);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }



    private String  byteToHexString(byte mByte)
    {
        String hexStr;

        hexStr = Integer.toHexString(mByte & 0xff);
        if(hexStr.length() == 1)
            hexStr = '0'+ hexStr;

        return hexStr;
    }


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

private ZLoadingDialog zLoadingDialog=null;
    //数据同步
    private void link_infoSync() {

        zLoadingDialog = new ZLoadingDialog(ReadCardActivity.this);
        zLoadingDialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                .setLoadingColor(Color.parseColor("#0d2cf9"))//颜色
                .setHintText("提交中...")
                .setHintTextSize(18) // 设置字体大小 dp
                .setHintTextColor(Color.WHITE)  // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(Color.parseColor("#CC111111")) // 设置背景色，默认白色
                .show();

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONArray array = new JSONArray();
        for (String bean : subjectList) {
            JSONObject object = new JSONObject();
            try {
                object.put("icCard", bean);
                object.put("serialnumber", baoCunBean.getJihuoma()+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(object);
        }


        Log.d("MianBanJiActivity3", "传卡号：" + array.toString());
        RequestBody body = RequestBody.create(array.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi()+ "/app/addIcCard");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "传卡号请求失败" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (zLoadingDialog!=null){
                            zLoadingDialog.dismiss();
                        }
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast tastyToast = TastyToast.makeText(ReadCardActivity.this, "请求失败,请检查网络", TastyToast.LENGTH_LONG, TastyToast.INFO);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (zLoadingDialog!=null){
                                zLoadingDialog.dismiss();
                            }
                        }
                    });
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "传卡号:" + ss);
                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                  //  Gson gson=new Gson();
                    if (jsonObject.get("code").getAsInt()==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast tastyToast = TastyToast.makeText(ReadCardActivity.this, ""+jsonObject.get("message").getAsString(), TastyToast.LENGTH_LONG, TastyToast.INFO);
                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                                tastyToast.show();
                                subjectList.clear();
                                userListAdapter2.notifyDataSetChanged();
                            }
                        });

                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast tastyToast = TastyToast.makeText(ReadCardActivity.this, "保存失败", TastyToast.LENGTH_LONG, TastyToast.INFO);
                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                                tastyToast.show();
                            }
                        });
                    }

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast tastyToast = TastyToast.makeText(ReadCardActivity.this, "数据异常", TastyToast.LENGTH_LONG, TastyToast.INFO);
                            tastyToast.setGravity(Gravity.CENTER, 0, 0);
                            tastyToast.show();
                        }
                    });
                    Log.d("WebsocketPushMsg", e.getMessage() + "传卡号");
                }
            }
        });
    }

}
