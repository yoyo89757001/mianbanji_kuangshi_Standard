package megvii.testfacepass.pa.html.controller;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.SystemClock;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;




import com.alibaba.fastjson.JSON;


import com.lztek.toolkit.Lztek;
import com.tencent.mmkv.MMKV;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


import io.objectbox.Box;
import io.objectbox.query.LazyList;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import mcv.facepass.FacePassException;
import mcv.facepass.types.FacePassAddFaceResult;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.beans.ConfigBean;
import megvii.testfacepass.pa.beans.DaKaBean;
import megvii.testfacepass.pa.beans.DaKaBean_;
import megvii.testfacepass.pa.beans.DataSaveBean;
import megvii.testfacepass.pa.beans.DepartmentBean;
import megvii.testfacepass.pa.beans.DepartmentBean_;
import megvii.testfacepass.pa.beans.Logingbean;
import megvii.testfacepass.pa.beans.PeoplePage;
import megvii.testfacepass.pa.beans.PeopleReques;
import megvii.testfacepass.pa.beans.ResBean;
import megvii.testfacepass.pa.beans.ResultBean;
import megvii.testfacepass.pa.beans.SSHistroy;
import megvii.testfacepass.pa.beans.SouSuob;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.beans.Subject_;
import megvii.testfacepass.pa.beans.UserInfos;
import megvii.testfacepass.pa.beans.WeekDataBean;
import megvii.testfacepass.pa.beans.WeekDataBean_;
import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.DateUtils;
import megvii.testfacepass.pa.utils.DengUT;
import megvii.testfacepass.pa.utils.FileUtil;



@RestController
@RequestMapping(path = "/app")
public class MyService3 {

    private static final String TAG = "MyService3";
    private static WritableFont arial14font = null;
    private static WritableCellFormat arial14format = null;
    private static WritableFont arial10font = null;
    private static WritableCellFormat arial10format = null;
    private static WritableFont arial12font = null;
    private static WritableCellFormat arial12format = null;
    private final static String UTF8_ENCODING = "UTF-8";
    private static Lztek lztek=null;
    static {
        try {
             lztek= Lztek.create(MyApplication.myApplication);
        }catch (NoClassDefFoundError error){
            error.printStackTrace();
        }

    }

    private  final String group_name = "facepasstestx";
    private Box<Subject> subjectBox  = MyApplication.myApplication.getSubjectBox();
    private Box<DaKaBean> daKaBeanBox  = MyApplication.myApplication.getDaKaBeanBox();
    private Box<DepartmentBean> departmentBeanBox  = MyApplication.myApplication.getDepartmentBeanBox();
    private Box<WeekDataBean> weekDataBeanBox  = MyApplication.myApplication.getWeekDataBeanBox();
    //private ConfigBean MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class)= MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class);
   // private  String serialnumber= MyApplication.myApplication.getMMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class)Box().get(123456).getJihuoma();
   // private ConfigBean MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class)= MyApplication.myApplication.getMMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class)Box().get(123456);
  //  private  String pass= MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).getJiaoyanmima();




    @PostMapping("/login")
    String login(@RequestBody Logingbean logingbean){
        if (logingbean.getPassword().equals(MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).getLoginPassword()) && logingbean.getUsername().equals("admin")){
            return com.alibaba.fastjson.JSONObject.toJSONString(new ResultBean(1,"登录成功","","token"+System.currentTimeMillis()));
        }else {
            return JSON.toJSONString(new ResultBean(0,"密码错误","",""));
        }
    }


    //修改密码
    @PostMapping("/xiugaimima")
    String logindsds(@RequestParam(name = "mima1" ) String mima1,@RequestParam(name = "mimanew" ) String mimanew){
        if (mima1.equals(MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).getLoginPassword()) && !mimanew.equals("")){
           ConfigBean bean= MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class);
           bean.setLoginPassword(mimanew);
            MMKV.defaultMMKV().encode("configBean",bean);
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"修改密码成功"));
        }else {
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"旧密码不对，或新密码为空"));
        }
    }

    @GetMapping("/logout")
    String logout(@RequestParam(name = "token" ) String token){
        return JSON.toJSONString(new ResultBean(1,"退出成功","",""));
    }


    //    2.设备序列号获取
//    请求地址：   http://设备IP:8090/getDeviceKey
//    请求方法： POST
//    请求说明：
    @GetMapping(path ="/getInfo")
    String getuserInfo(@RequestParam(name = "token" ) String token){
        Log.d("gggggggg", token);
        return JSON.toJSONString(new ResultBean(1,"获取个人信息成功",JSON.toJSONString(new UserInfos("admin","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"))));
    }

    //    2.重启
//    请求地址：   http://设备IP:8090/getDeviceKey
//    请求方法： POST
//    请求说明：
    @GetMapping(path ="/chongqi")
    String getuserIdddnfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                DengUT.reboot();
            }
        }).start();
        return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"重启成功"));
    }


    //    2.设备序列号获取
//    请求地址：   http://设备IP:8090/getDeviceKey
//    请求方法： POST
//    请求说明：
    @GetMapping(path ="/getDeviceKey")
     String getDeviceKey(){
       String ss= MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).getJihuoma();
       if (ss==null|| ss.equals("")){
             return requsBean(-1,true,"","获取序列号失败");
       }else {
           return JSON.toJSONString(ss);
       }
    }


    @GetMapping(path = "/getConfig")
    String getConfig(){
        return com.alibaba.fastjson.JSONObject.toJSONString(MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class));
    }



//3.设备配置
//    请求地址：  http://设备IP:8090/setConfig
//    请求方法： POST
    @PostMapping("/setConfig")
    String setConfig(@RequestParam(name = "mqPort",required = false) String mqPort,@RequestParam(name = "isLive",required = false) boolean isLive,
                     @RequestParam(name = "mima",required = false) String mima,@RequestParam(name = "cameraId",required = false) String cameraId,
                     @RequestParam(name = "faceRotation",required = false) String faceRotation,@RequestParam(name = "faceRotation2",required = false) String faceRotation2,
                     @RequestParam(name = "cameraPreviewRotation",required = false) String cameraPreviewRotation,@RequestParam(name = "cameraPreviewRotation2",required = false) String cameraPreviewRotation2,
                     @RequestParam(name = "msrBitmapRotation",required = false) String msrBitmapRotation,@RequestParam(name = "shibieFaZhi",required = false) String shibieFaZhi,
                     @RequestParam(name = "companyName",required = false) String companyName,@RequestParam(name = "shibieFaceSize",required = false) String shibieFaceSize,
                     @RequestParam(name = "ruKuMoHuDu",required = false) String ruKuMoHuDu,@RequestParam(name = "ruKuFaceSize",required = false) String ruKuFaceSize,
                     @RequestParam(name = "dangqianChengShi2",required = false) String dangqianChengShi2,@RequestParam(name = "gpio",required = false) String gpio,
                     @RequestParam(name = "isOpenCard",required = false) boolean isOpenCard){
            try {
                ConfigBean configBean=MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class);
                boolean ii =false;

                if (mqPort!=null && !mqPort.equals("")){
                    configBean.setMqProt(Integer.parseInt(mqPort));
                }
                //Log.d(TAG, "isLive:" + isLive);
                if (configBean.isHuoTi()!=isLive){
                    ii=true;
                    configBean.setHuoTi(isLive);
                }
                Log.d("hgftrteda", "isOpenCard:" + isOpenCard);
                configBean.setOpenCard(isOpenCard);
                if (mima!=null && !mima.equals("")){
                    configBean.setMima(Integer.parseInt(mima));
                }
                if (cameraId!=null && !cameraId.equals("")){
                    configBean.setCameraId(Integer.parseInt(cameraId));
                    ii=true;
                }
                if (faceRotation!=null && !faceRotation.equals("")){
                    configBean.setFaceRotation(Integer.parseInt(faceRotation));
                    ii=true;
                }
                if (faceRotation2!=null && !faceRotation2.equals("")){
                    configBean.setFaceRotation2(Integer.parseInt(faceRotation2));
                    ii=true;
                }
                if (cameraPreviewRotation!=null && !cameraPreviewRotation.equals("")){
                    configBean.setCameraPreviewRotation(Integer.parseInt(cameraPreviewRotation));
                    ii=true;
                }
                if (cameraPreviewRotation2!=null && !cameraPreviewRotation2.equals("")){
                    configBean.setCameraPreviewRotation2(Integer.parseInt(cameraPreviewRotation2));
                    ii=true;
                }
                if (msrBitmapRotation!=null && !msrBitmapRotation.equals("")){
                    configBean.setMsrBitmapRotation(Integer.parseInt(msrBitmapRotation));
                }
                if (shibieFaZhi!=null && !shibieFaZhi.equals("")){
                    configBean.setShibieFaZhi(Float.parseFloat(shibieFaZhi));
                    ii=true;
                }
                if (shibieFaceSize!=null && !shibieFaceSize.equals("")){
                    configBean.setShibieFaceSize(Integer.parseInt(shibieFaceSize));
                    ii=true;
                }
                if (ruKuMoHuDu!=null && !ruKuMoHuDu.equals("")){
                    configBean.setRuKuMoHuDu(Float.parseFloat(ruKuMoHuDu));
                    ii=true;
                }
                if (ruKuFaceSize!=null && !ruKuFaceSize.equals("")){
                    configBean.setRuKuFaceSize(Integer.parseInt(ruKuFaceSize));
                    ii=true;
                }
                if (gpio!=null && !gpio.equals("")){
                    configBean.setGpio(Integer.parseInt(gpio));
                }
                if (dangqianChengShi2!=null && !dangqianChengShi2.equals("")){
                    ii=true;
                    configBean.setDangqianChengShi2(dangqianChengShi2);
                }
                if (companyName!=null && !companyName.equals("")){
                    configBean.setCompanyName(companyName);
                }
                MMKV.defaultMMKV().encode("configBean",configBean);
                //发送广播更新配置  还没实现
                if (ii){
                    EventBus.getDefault().post("configs2");
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"设置成功,APP重启中"));
                }else {
                    EventBus.getDefault().post("configs");
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"设置成功"));
                }
            }catch (Exception e){
                return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"设置异常"+e.getMessage()));
            }
    }




//    5.设置设备时间
//    请求地址：  http://设备IP:8090/setTime
//    请求方法： POST
    @PostMapping("/setTime")
        String setTime(@RequestParam(name = "pass") String pass ,
                          @RequestParam(name = "timestamp") String timestamp){
            if (timestamp!=null && !timestamp.equals("")){
                try {
                    if (lztek==null)
                        return  requsBean(400,true,"","设备没有该方法");
                    lztek.setSystemTime(Long.parseLong(timestamp));
                    return requsBean(1,true,"","设置成功");
                }catch (Exception e){
                    return requsBean(-1,true,e.getMessage()+"","设置失败");
                }
            }else {
                return requsBean(400,true,"","参数验证失败");
            }
        }



//5.设备重启
//    请求地址：   http://设备IP:8090/restartDevice
//    请求方法： POST
//    请求数据：
    @GetMapping(path = "/restartDevice")
    String restartDevice(){
//            if (lztek==null)
//                return  requsBean(400,true,"","设备没有该方法");
//            lztek.hardReboot();
             DengUT.reboot();
            return requsBean(1,true,"","设置成功");
    }


    //6.设备重置
    //    请求地址：   http://设备IP:8090/device/reset
    //    请求方法： POST
    //    请求数据：
    @PostMapping("/device/reset")
    String reset(@RequestParam(name = "pass") String pass ){
           // MyApplication.myApplication.getFacePassHandler().deleteFace()
            subjectBox.removeAll();
            return requsBean(1,true,"","重置成功");
    }


//    8.识别回调配置
//    请求地址：   http://设备IP:8090/setIdentifyCallBack
//    请求方法： POST
    @PostMapping("/setIdentifyCallBack")
    String setIdentifyCallBack(@RequestParam(name = "pass") String pass,
                               @RequestParam(name = "url") String url){
        if (url!=null && !url.equals("")){
            if (isValidUrl(url)){//是url
                MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setHoutaiDiZhi(url);
                MMKV.defaultMMKV().encode("configBean",MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class));
                EventBus.getDefault().post("kaimen");
                return requsBean(1,true,"","设置成功");
            }else {
                return requsBean(400,true,"","参数验证失败");
            }
        }else {
            return requsBean(400,true,"","参数验证失败");
        }
    }


    private boolean isValidUrl(String url){
        return !TextUtils.isEmpty(url) && url.matches(Patterns.WEB_URL.pattern());
    }

    //    8.考勤配置
//    请求地址：   http://设备IP:8090/setIdentifyCallBack
//    请求方法： POST
    @PostMapping(path = "/data/save")
    String setIdentifyCallBdddsack(@RequestBody DataSaveBean dataSaveBean){
       // MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setHoutaiDiZhi(url);
        if (dataSaveBean!=null){
            if (dataSaveBean.getIsArrange().size()>0){//删掉所有重新录入
                weekDataBeanBox.removeAll();
                for (String s : dataSaveBean.getIsArrange()) {
                    weekDataBeanBox.put(new WeekDataBean(s, DateUtils.data(s)));
                }
            }
            //保存其他的配置
            ConfigBean configBean=MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class);
            configBean.setWeekDate(dataSaveBean.getXinqi());
            if (dataSaveBean.getRadio().equals("1")){
                configBean.setKqOneFour(0);
            }else {
                configBean.setKqOneFour(1);
            }
            configBean.setWook1(dataSaveBean.getStartTime1());
            configBean.setOffDuty1(dataSaveBean.getEndTime1());
            configBean.setWook2(dataSaveBean.getStartTime2());
            configBean.setOffDuty2(dataSaveBean.getEndTime2());
            configBean.setChidao(dataSaveBean.getMinute1_1());
            configBean.setZaotui(dataSaveBean.getMinute1_2());
            configBean.setJiaban(dataSaveBean.getMinute1_3());
            configBean.setQueqing1(dataSaveBean.getMinute2_1());
            configBean.setQueqing2(dataSaveBean.getMinute2_2());
            MMKV.defaultMMKV().encode("configBean",configBean);
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"保存设置成功"));
        }else {
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"保存数据失败"));
        }
    }



    //获取图片
    @GetMapping(path = "/getFaceBitmap")
    public void getFaceBitmap(HttpResponse response, @QueryParam(name = "id",required = true) String id){
        File file=new File(MyApplication.SDPATH+File.separator+id+".png");
        if (file.exists()){
            FileBody body = new FileBody(file);
            response.addHeader("Content-Disposition", "attachment;filename="+id+".png");
            response.setBody(body);
        }else {
            StringBody body = new StringBody("文件不存在");
            response.setBody(body);
        }
    }

    //获取图片
    @GetMapping(path = "/getFaceBitmap2")
    public void getFaceBitmap2(HttpResponse response, @QueryParam(name = "time",required = true) String time, @QueryParam(name = "id",required = true) String id){
        File file=new File(MyApplication.SDPATH2+File.separator+time+File.separator+id+".png");
        if (file.exists()){
            FileBody body = new FileBody(file);
            response.addHeader("Content-Disposition", "attachment;filename="+id+".png");
            response.setBody(body);
        }else {
            StringBody body = new StringBody("文件不存在");
            response.setBody(body);
        }
    }

    //获取陌生人图片
    @GetMapping(path = "/getFaceBitmap3")
    public void getFaceBitmap3(HttpResponse response, @QueryParam(name = "time",required = true) String time, @QueryParam(name = "id",required = true) String id){
        File file=new File(MyApplication.SDPATH4+File.separator+time+File.separator+id+".png");
        if (file.exists()){
            FileBody body = new FileBody(file);
            response.addHeader("Content-Disposition", "attachment;filename="+id+".png");
            response.setBody(body);
        }else {
            StringBody body = new StringBody("文件不存在");
            response.setBody(body);
        }
    }


   //     10.人员创建
//    请求地址：   http://设备IP:8090/person/create
//    请求方法： POST
    @PostMapping("/person/create2")
    String createPeople(@RequestParam(name = "name") String name, @RequestParam("file") MultipartFile file,
                        @RequestParam(name = "department",required = false) String department,@RequestParam(name = "sex",required = false) String sex,
                        @RequestParam(name = "peopleType",required = false) String peopleType,@RequestParam(name = "birthday",required = false) String birthday,
                        @RequestParam(name = "startTime",required = false) String startTime,@RequestParam(name = "endTime",required = false) String endTime,
                        @RequestParam(name = "remarks",required = false) String remarks,@RequestParam(name = "phone",required = false) String phone,
                        @RequestParam(name = "icCard",required = false) String icCard,@RequestParam(name = "orient",required = false) String orient){//orient
            try {
                if (MyApplication.myApplication.getFacePassHandler()==null){
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"机器算法未初始化"));
                }
               // Log.d(TAG, name+"peopleType:"+peopleType+"startTime:"+startTime+"birthday"+birthday);
                Log.d(TAG, "file.getSize():" + file.getSize());
                long peopleId=System.currentTimeMillis();
                Bitmap bitmap=readInputStreamToBitmap(file.getStream(),file.getSize());
                FacePassAddFaceResult detectResult = null;
                if (orient!=null && !orient.equals("undefined") && !orient.equals("")){
                    if (orient.equals("6")){//ios相册竖向拍照 传过来是转了90度的，旋转修正
                        bitmap=BitmapUtil.rotateBitmap(bitmap,90);
                    }
                    if (orient.equals("3")){//ios相册横向拍照 传过来是转了180度的，旋转修正
                        bitmap=BitmapUtil.rotateBitmap(bitmap,180);
                    }
                }
                BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH, peopleId+".png");

//                File fileTemp=  Luban.with(MyApplication.myApplication).load(MyApplication.SDPATH3+File.separator + "aaabbb.png")
//                        .ignoreBy(500)
//                        .setTargetDir(MyApplication.SDPATH3+File.separator)
//                        .get(MyApplication.SDPATH3+File.separator + "aaabbb.png");


                try {
                    detectResult = MyApplication.myApplication.getFacePassHandler().addFace(bitmap);
                } catch (FacePassException e) {
                    e.printStackTrace();
                }
                if (detectResult != null && detectResult.result==0) {
                    byte[] faceToken = detectResult.faceToken;
                    Subject subject=new Subject();
                    subject.setSid(peopleId+"");
                    subject.setName(name);
                    subject.setSex(sex);
                    subject.setIcCard(icCard);
                    subject.setPhone(phone);
                    subject.setCreatTime(System.currentTimeMillis());
                    subject.setDepartment(department);
                    subject.setRemarks(remarks);
                    subject.setPhotoId(peopleId);
                    subject.setPhoto("http://" + FileUtil.getLocalHostIp() + ":8090"  + "/app/getFaceBitmap?id="+peopleId);
                    try {
                        if (peopleType!=null && !peopleType.equals("")){
                            subject.setPeopleType(Integer.parseInt(peopleType));
                        }
                        if (birthday!=null && !birthday.equals("") && !birthday.equals("NaN")){
                            subject.setBirthday(Long.parseLong(birthday));
                        }
                        if (startTime!=null && !startTime.equals("") && !startTime.equals("NaN")){
                            subject.setStartTime(Long.parseLong(startTime));
                        }
                       if (endTime!=null && !endTime.equals("") && !endTime.equals("NaN")){
                           subject.setEndTime(Long.parseLong(endTime));
                       }
                    }catch (Exception e){
                        Log.d(TAG, e.getMessage()+"时间转换异常");
                    }
                    subject.setTeZhengMa(new String(faceToken));
                    MyApplication.myApplication.getFacePassHandler().bindGroup(group_name,faceToken);
                    subjectBox.put(subject);
                    Log.d(TAG, "人员创建成功 "+subject.toString());
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"创建成功"));
                }else {
//                    File fff=new File(MyApplication.SDPATH+File.separator+peopleId+".png");
//                    if (fff.exists())
//                    Log.d(TAG, "fff.delete():" + fff.delete());
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"图片不符合入库要求"));
                }
            }catch (Exception e){
                Log.d(TAG, e.getMessage()+"创建异常");
                return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"创建异常"));
            }
    }


        private Bitmap readInputStreamToBitmap(InputStream ins, long fileSize) {
        if (ins == null) {
            return null;
        }
        byte[] b;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int size = -1;
            int len = 0;// 已经接收长度
            size = ins.read(buffer);
            while (size != -1) {
                len = len + size;//
                bos.write(buffer, 0, size);
                if (fileSize == len) {// 接收完毕
                    break;
                }
                size = ins.read(buffer);
            }
            b = bos.toByteArray();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

//11.人员更新
//    请求地址：   http://设备IP:8090/person/update
//    请求方法： POST
//     10.人员创建
//    请求地址：   http://设备IP:8090/person/create
//    请求方法： POST
@PostMapping("/person/updata2")
String createPeoplewww(@RequestParam(name = "name") String name, @RequestParam(name = "file",required = false) MultipartFile file,
                    @RequestParam(name = "department",required = false) String department,@RequestParam(name = "sex",required = false) String sex,
                    @RequestParam(name = "peopleType",required = false) String peopleType,@RequestParam(name = "birthday",required = false) String birthday,
                    @RequestParam(name = "startTime",required = false) String startTime,@RequestParam(name = "endTime",required = false) String endTime,
                    @RequestParam(name = "remarks",required = false) String remarks,@RequestParam(name = "phone",required = false) String phone,
                    @RequestParam(name = "icCard",required = false) String icCard,@RequestParam(name = "sid") String sid,@RequestParam(name = "orient",required = false) String orient){
    try {
        if (MyApplication.myApplication.getFacePassHandler()==null){
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"机器算法未初始化"));
        }
       // Log.d(TAG, department+" return com.alibaba.fast");
        // Log.d(TAG, "更新时传过来的file:" + file);
        Subject subject=subjectBox.query().equal(Subject_.sid,sid).build().findUnique();
        if (subject==null){
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"更新失败,未找到该人员信息"));
        }
        if (file!=null){
            Bitmap bitmap=readInputStreamToBitmap(file.getStream(),file.getSize());
            FacePassAddFaceResult detectResult = null;
            long photoID=System.currentTimeMillis();
            if (orient!=null && !orient.equals("undefined") && !orient.equals("")){
                if (orient.equals("6")){//ios相册竖向拍照 传过来是转了90度的，旋转修正
                    bitmap=BitmapUtil.rotateBitmap(bitmap,90);
                }
                if (orient.equals("3")){//ios相册横向拍照 传过来是转了180度的，旋转修正
                    bitmap=BitmapUtil.rotateBitmap(bitmap,180);
                }
            }
            BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH, photoID+".png");
//              File file=  Luban.with(MyApplication.myApplication).load(MyApplication.SDPATH3+File.separator + "aaabbb.png")
//                        .ignoreBy(500)
//                        .setTargetDir(MyApplication.SDPATH3+File.separator)
//                        .get(MyApplication.SDPATH3+File.separator + "aaabbb.png");
            try {
                detectResult = MyApplication.myApplication.getFacePassHandler().addFace(bitmap);
            } catch (FacePassException e) {
                e.printStackTrace();
            }
            if (detectResult != null && detectResult.result==0) {
                //删除旧的照片
                File f=new File(MyApplication.SDPATH+File.separator+subject.getPhotoId()+".png");
                if (f.exists()){
                    Log.d(TAG, "删除底库文件:" + f.delete());
                }
                byte[] faceToken = detectResult.faceToken;
                subject.setName(name);
                subject.setSex(sex);
                subject.setPhoto("http://" + FileUtil.getLocalHostIp() + ":8090"  + "/app/getFaceBitmap?id="+photoID);
                subject.setPhotoId(photoID);//设置新的照片id
                if (icCard!=null && !icCard.equals(""))
                    subject.setIcCard(icCard);
                if (phone!=null && !phone.equals(""))
                    subject.setPhone(phone);
                if (department!=null && !department.equals("")){
                    subject.setDepartment(department);
                }
                if (remarks!=null && !remarks.equals("")){
                    subject.setRemarks(remarks);
                }
                try {
                    if (peopleType!=null && !peopleType.equals("")){
                        subject.setPeopleType(Integer.parseInt(peopleType));
                    }
                    if (birthday!=null && !birthday.equals("") && !birthday.equals("NaN")){
                        subject.setBirthday(Long.parseLong(birthday));
                    }
                    if (startTime!=null && !startTime.equals("") && !startTime.equals("NaN")){
                        subject.setStartTime(Long.parseLong(startTime));
                    }
                    if (endTime!=null && !endTime.equals("") && !endTime.equals("NaN")){
                        subject.setEndTime(Long.parseLong(endTime));
                    }
                }catch (Exception e){
                    Log.d(TAG, e.getMessage()+"时间转换异常");
                }
                MyApplication.myApplication.getFacePassHandler().deleteFace(subject.getTeZhengMa().getBytes());
                subject.setTeZhengMa(new String(faceToken));
                MyApplication.myApplication.getFacePassHandler().bindGroup(group_name,faceToken);
                subjectBox.put(subject);
                Log.d(TAG, "人员修改成功 "+subject.toString());
                return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"修改成功"));
            }else {
                File fff=new File(MyApplication.SDPATH+File.separator+photoID+".png");
                if (fff.exists())
                    Log.d(TAG, "fff.delete():" + fff.delete());
                return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"图片不符合入库要求"));
            }
        }else {
            subject.setName(name);
            subject.setSex(sex);
            if (icCard!=null && !icCard.equals(""))
                subject.setIcCard(icCard);
            if (phone!=null && !phone.equals(""))
                subject.setPhone(phone);
            if (department!=null && !department.equals("")){
                subject.setDepartment(department);
            }
            if (remarks!=null && !remarks.equals("")){
                subject.setRemarks(remarks);
            }
            try {
                if (peopleType!=null && !peopleType.equals("")){
                    subject.setPeopleType(Integer.parseInt(peopleType));
                }
                if (birthday!=null && !birthday.equals("") && !birthday.equals("NaN")){
                    subject.setBirthday(Long.parseLong(birthday));
                }
                if (startTime!=null && !startTime.equals("") && !startTime.equals("NaN")){
                    subject.setStartTime(Long.parseLong(startTime));
                }
                if (endTime!=null && !endTime.equals("") && !endTime.equals("NaN")){
                    subject.setEndTime(Long.parseLong(endTime));
                }
            }catch (Exception e){
                Log.d(TAG, e.getMessage()+"时间转换异常");
            }
            subjectBox.put(subject);
            Log.d(TAG, "人员修改成功 "+subject.toString());
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"修改成功"));
        }


    }catch (Exception e){
        Log.d(TAG, e.getMessage()+"创建异常");
        return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"创建异常"));
    }
}



    //12.人员删除（单个）
//    请求地址：   http://设备IP:8090/person/delete
//    请求方法： get
    @GetMapping(path = "/person/deleteone")
    String deleteone(@RequestParam(name = "id") String id){
        if (id!=null && !id.equals("")){
            try {
                Log.d(TAG, id+"要删除的ID");
                if (MyApplication.myApplication.getFacePassHandler()==null){
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"算法未初始化"));
                }
                String [] ids=id.split(",");
                int size=ids.length;
                if (size>0){
                    for (String s : ids) {
                        Subject subject=subjectBox.query().equal(Subject_.sid,s).build().findUnique();
                        if (subject!=null){
                            MyApplication.myApplication.getFacePassHandler().deleteFace(subject.getTeZhengMa().getBytes());
                            File f=new File(MyApplication.SDPATH+File.separator+subject.getPhotoId()+".png");
                            if (f.exists()){
                                Log.d(TAG, "删除底库文件:" + f.delete());
                            }
                            subjectBox.remove(subject);
                        }
                    }
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"删除成功"));
                }else {
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"删除失败,参数错误"));
                }
            }catch (Exception e){
                return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"删除异常"+e.getMessage()));
            }
        }else {
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"参数验证失败"));
        }
    }



//    13.人员分页查询
//    请求地址：   http://设备IP:8090/person/findByPage
//    请求方法： post
    @PostMapping("/person/findByPage")
    String findByPage(@RequestBody PeoplePage peoplePage){
            if (peoplePage!=null){
                Log.d(TAG, peoplePage.toString());
                try {
                    JSONArray jsonArray=new JSONArray();
                   List<Subject> subjectList= subjectBox.query()
                           .equal(Subject_.peopleType,peoplePage.getPeopleType())
                           .orderDesc(Subject_.creatTime)//降序 按时间排序
                           .build()
                           .find(peoplePage.getPage()*peoplePage.getSize(),peoplePage.getSize());
                 //  Log.d(TAG, "subjectList.size():" + subjectList.size());
                    for (Subject subject:subjectList){
//                        PersonsBean personsBean=new PersonsBean();
//                        personsBean.setId(subject.getTeZhengMa());
//                        personsBean.setName(subject.getName());
//                        personsBean.setIdcardNum(subject.getIdcardNum());
//                        personsBean.setExpireTime(subject.getEntryTime());
//                        Log.d(TAG, JSON.toJSONString(personsBean));
                        JSONObject object=new JSONObject();
                        object.put("sid",subject.getSid());//sid是id
                        object.put("name",subject.getName());
                        object.put("photo",subject.getPhoto());
                        object.put("phone",subject.getPhone());
                        object.put("icCard",subject.getIcCard());
                        object.put("sex",subject.getSex());
                        object.put("remarks",subject.getRemarks());
                        object.put("department",subject.getDepartment());
                        object.put("photoID",subject.getPhotoId());
                        if (subject.getBirthday()!=0){
                            object.put("birthday",subject.getBirthday());
                        }
                        if (subject.getStartTime()!=0){
                            object.put("startTime",subject.getStartTime());
                        }
                        if (subject.getEndTime()!=0 ){
                            object.put("endTime",subject.getEndTime());
                        }
                        jsonArray.put(object);
                    }
                    JSONObject object=new JSONObject();
                    object.put("total",subjectBox.query().equal(Subject_.peopleType,peoplePage.getPeopleType()).build().findLazy().size());
                    object.put("requestData",jsonArray);
                    object.put("msg","查询成功");
                    return object.toString();

                  //  return requsBean(1,true,jsonArray.toString(),"获取成功");
                }catch (Exception e){
                    return requsBean(-1,true,e.getMessage()+"","参数异常");
                }
            }else {
                return requsBean(400,true,"","参数验证失败");
            }

    }


    //    13.考勤日期获取
//    请求地址：   http://设备IP:8090/person/findByPage
//    请求方法： post
    @GetMapping(path = "/data/findData")
    String findByPagessss(){
            try {
                JSONArray jsonArray=new JSONArray();
                List<WeekDataBean> subjectList= weekDataBeanBox.query()
                        .order(WeekDataBean_.time)//降序 按时间排序
                        .build()
                        .find();
                //  Log.d(TAG, "subjectList.size():" + subjectList.size());
                for (WeekDataBean subject:subjectList){
                    JSONObject object=new JSONObject();
                    object.put("id",subject.getId());//sid是id
                    object.put("data",subject.getData());
                    object.put("time",subject.getTime());
                    jsonArray.put(object);
                }
                JSONObject object=new JSONObject();
                object.put("total",weekDataBeanBox.query().build().findLazy().size());
                object.put("requestData",jsonArray);
                object.put("msg","查询成功");
                return object.toString();
            }catch (Exception e){
                return requsBean(-1,true,e.getMessage()+"","参数异常");
            }
    }

//    18.人员信息查询
//    请求地址：  http://设备IP:8090/person/find
//    请求方法： POST
@GetMapping(path = "/person/find")
String find(@RequestParam(name = "id") String id){
        if (id!=null){
            try {
                Subject subjectList= subjectBox.query().equal(Subject_.sid,id).build().findUnique();
                if (subjectList!=null){
                    return com.alibaba.fastjson.JSONObject.toJSONString(subjectList);
                }else {
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"没有该人员信息"));
                }
            }catch (Exception e){
                return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"查询异常"+e.getMessage()));
            }
        }else {
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"参数异常"));
        }
}

    //    18.人员信息搜索
//    请求地址：  http://设备IP:8090/person/find
//    请求方法： POST
    @PostMapping(path = "/person/finds")
    String finds(@RequestBody SouSuob souSuob){
        if (souSuob!=null){
            try {
                List<Subject> subjectList= subjectBox.query().equal(Subject_.peopleType,souSuob.getType()).contains(Subject_.name,souSuob.getName()).build().find();
                if (subjectList.size()>0){
                    return com.alibaba.fastjson.JSONObject.toJSONString(subjectList);
                }else {
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"没有该人员信息"));
                }
            }catch (Exception e){
                return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"搜索异常"+e.getMessage()));
            }
        }else {
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"参数异常"));
        }
    }


//25.刷脸记录查询
//    请求地址：  http://设备IP:8090/findRecords
//    请求方法： POST
    @PostMapping(path = "/findRecords")
    String findRecords(@RequestBody SSHistroy ssHistroy){
                try {
                    if (ssHistroy==null){
                        return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"参数异常"));
                    }
                    Log.d(TAG, ssHistroy.toString());
                    if (ssHistroy.getName()!=null && !ssHistroy.getName().equals("") && ssHistroy.getStartTime()!=0 && ssHistroy.getEndTime()!=0){
                       List<DaKaBean> daKaBeanList= daKaBeanBox.query().contains(DaKaBean_.name,ssHistroy.getName())
                                .between(DaKaBean_.time,ssHistroy.getStartTime(),ssHistroy.getEndTime())
                                .equal(DaKaBean_.type,ssHistroy.getType())
                                .orderDesc(DaKaBean_.time)//降序 按时间排序
                                .build()
                                .find(ssHistroy.getPage()*ssHistroy.getSize(),ssHistroy.getSize());
                        JSONArray jsonArray=new JSONArray();
                        for (DaKaBean daKaBean : daKaBeanList) {
                            JSONObject object=new JSONObject();
                            object.put("name",daKaBean.getName());
                            object.put("path",daKaBean.getPath());
                            object.put("department",daKaBean.getDepartment());
                            object.put("icCard",daKaBean.getIcCard());
                            object.put("peopleType",daKaBean.getPeopleType());
                            object.put("time",daKaBean.getTime());
                            jsonArray.put(object);
                        }
                        JSONObject object=new JSONObject();
                        object.put("total",daKaBeanBox.query().equal(DaKaBean_.type,ssHistroy.getType()).build().findLazy().size());
                        object.put("requestData",jsonArray);
                        object.put("msg","查询成功");
                        return object.toString();
                    }else if (ssHistroy.getName()!=null && !ssHistroy.getName().equals("")){
                        List<DaKaBean> daKaBeanList= daKaBeanBox.query().contains(DaKaBean_.name,ssHistroy.getName())
                                .equal(DaKaBean_.type,ssHistroy.getType())
                                .orderDesc(DaKaBean_.time)//降序 按时间排序
                                .build()
                                .find(ssHistroy.getPage()*ssHistroy.getSize(),ssHistroy.getSize());
                        JSONArray jsonArray=new JSONArray();
                        for (DaKaBean daKaBean : daKaBeanList) {
                            JSONObject object=new JSONObject();
                            object.put("name",daKaBean.getName());
                            object.put("path",daKaBean.getPath());
                            object.put("department",daKaBean.getDepartment());
                            object.put("icCard",daKaBean.getIcCard());
                            object.put("peopleType",daKaBean.getPeopleType());
                            object.put("time",daKaBean.getTime());
                            jsonArray.put(object);
                        }
                        JSONObject object=new JSONObject();
                        object.put("total",daKaBeanBox.query().equal(DaKaBean_.type,ssHistroy.getType()).build().findLazy().size());
                        object.put("requestData",jsonArray);
                        object.put("msg","查询成功");
                        return object.toString();
                    } else if (ssHistroy.getStartTime()!=0 && ssHistroy.getEndTime()!=0){
                        List<DaKaBean> daKaBeanList= daKaBeanBox.query()
                                .between(DaKaBean_.time,ssHistroy.getStartTime(),ssHistroy.getEndTime())
                                .equal(DaKaBean_.type,ssHistroy.getType())
                                .orderDesc(DaKaBean_.time)//降序 按时间排序
                                .build()
                                .find(ssHistroy.getPage()*ssHistroy.getSize(),ssHistroy.getSize());
                        JSONArray jsonArray=new JSONArray();
                        for (DaKaBean daKaBean : daKaBeanList) {
                            JSONObject object=new JSONObject();
                            object.put("name",daKaBean.getName());
                            object.put("path",daKaBean.getPath());
                            object.put("department",daKaBean.getDepartment());
                            object.put("icCard",daKaBean.getIcCard());
                            object.put("peopleType",daKaBean.getPeopleType());
                            object.put("time",daKaBean.getTime());
                            jsonArray.put(object);
                        }
                        JSONObject object=new JSONObject();
                        object.put("total",daKaBeanBox.query().equal(DaKaBean_.type,ssHistroy.getType()).build().findLazy().size());
                        object.put("requestData",jsonArray);
                        object.put("msg","查询成功");
                        return object.toString();
                    }else {
                        List<DaKaBean> daKaBeanList= daKaBeanBox.query()
                                .equal(DaKaBean_.type,ssHistroy.getType())
                                .orderDesc(DaKaBean_.time)//降序 按时间排序
                                .build()
                                .find(ssHistroy.getPage()*ssHistroy.getSize(),ssHistroy.getSize());
                        JSONArray jsonArray=new JSONArray();
                        for (DaKaBean daKaBean : daKaBeanList) {
                            JSONObject object=new JSONObject();
                            object.put("name",daKaBean.getName());
                            object.put("path",daKaBean.getPath());
                            object.put("department",daKaBean.getDepartment());
                            object.put("icCard",daKaBean.getIcCard());
                            object.put("peopleType",daKaBean.getPeopleType());
                            object.put("time",daKaBean.getTime());
                            jsonArray.put(object);
                        }
                        JSONObject object=new JSONObject();
                        object.put("total",daKaBeanBox.query().equal(DaKaBean_.type,ssHistroy.getType()).build().findLazy().size());
                        object.put("requestData",jsonArray);
                        object.put("msg","查询成功");
                        return object.toString();
                    }

                }catch (Exception e){
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"参数异常"+e.getMessage()));
                }

    }


    //25.绑卡
//    请求地址：  http://设备IP:8090/findRecords
//    请求方法： POST
    @GetMapping(path = "/icCard/openCard")
    String openICCard(){
        EventBus.getDefault().post("openCard");
        int i=0;
        while (true){
            if (MyApplication.card==null){
                SystemClock.sleep(500);
                i++;
                if (i>18){
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"读卡超时"));
                }
            }else {
                String c=MyApplication.card;
                MyApplication.card=null;
                List<Subject> subjects=subjectBox.query().equal(Subject_.icCard,c).build().find();
                if (subjects.size()>0){
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"此卡已经被其他人绑定"));
                }
                JSONObject object=new JSONObject();
                try {
                    object.put("code",1);
                    object.put("card",c);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object.toString();
            }
        }
    }


    @PostMapping("/person/createDepartment")
    String createDepartment(@RequestParam(name = "name") String name){
        DepartmentBean bean=new DepartmentBean();
        bean.setName(name);
        bean.setSid(System.currentTimeMillis()+"");
        departmentBeanBox.put(bean);
        return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"创建成功"));
    }

    //12.部门删除
//    请求地址：   http://设备IP:8090/person/delete
//    请求方法： get
    @GetMapping(path = "/person/deleteDepartment")
    String deleteoneDe(@RequestParam(name = "id") String id){
        if (id!=null && !id.equals("")){
            try {
                Log.d(TAG, id+"要删除的ID");
                String [] ids=id.split(",");
                int size=ids.length;
                if (size>0){
                    for (String s : ids) {
                        DepartmentBean subject=departmentBeanBox.query().equal(DepartmentBean_.sid,s).build().findUnique();
                        if (subject!=null){
                            departmentBeanBox.remove(subject);
                        }
                    }
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"删除成功"));
                }else {
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"删除失败,参数错误"));
                }
            }catch (Exception e){
                return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"删除异常"+e.getMessage()));
            }
        }else {
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"参数验证失败"));
        }
    }

    @PostMapping("/person/updataDepartment")
    String reatePeoplewww(@RequestParam(name = "name") String name,@RequestParam(name = "sid") String sid){
        Log.d(TAG, sid);
        DepartmentBean departmentBean=departmentBeanBox.query().equal(DepartmentBean_.sid,sid).build().findUnique();
        if (departmentBean!=null && name!=null && !name.equals("")){
            departmentBean.setName(name);
            departmentBeanBox.put(departmentBean);
            LazyList<Subject> subjectLazyList=subjectBox.query().equal(Subject_.department,name).build().findLazy();
            for (Subject subject : subjectLazyList) {
                subject.setDepartment(name);
                subjectBox.put(subject);
            }
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(1,"修改成功"));
        }else {
            return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"修改失败"));
        }
    }

   ///    13.部门分页查询
//    请求地址：   http://设备IP:8090/person/findByPage
//    请求方法： post
    @PostMapping("/department/findByPage")
    String findByPage22(@RequestBody PeoplePage peoplePage){
        if (peoplePage!=null){
            Log.d(TAG, peoplePage.toString());
            try {
                JSONArray jsonArray=new JSONArray();
                List<DepartmentBean> subjectList= departmentBeanBox.query()
                        .orderDesc(DepartmentBean_.creatTime)//降序 按时间排序
                        .build()
                        .find(peoplePage.getPage()*peoplePage.getSize(),peoplePage.getSize());
                  Log.d(TAG, "DepartmentBean.size():" + subjectList.size());
                for (DepartmentBean subject:subjectList){
                    JSONObject object=new JSONObject();
                    object.put("sid",subject.getSid());//sid是id
                    object.put("name",subject.getName());
                    object.put("num",subjectBox.query().equal(Subject_.department,subject.getName()).build().findLazy().size());
                    jsonArray.put(object);
                }
                JSONObject object=new JSONObject();
                object.put("total",departmentBeanBox.query().build().findLazy().size());
                object.put("requestData",jsonArray);
                object.put("msg","查询成功");
                return object.toString();

            }catch (Exception e){
                return requsBean(-1,true,e.getMessage()+"","参数异常");
            }
        }else {
            return requsBean(400,true,"","参数验证失败");
        }

    }


    @GetMapping(path = "/person/getDashboard")
    String getDashboard(){
        try {
            JSONObject jsonObject=new JSONObject();
            LazyList<DepartmentBean> departmentBeanLazyList= departmentBeanBox.query().build().findLazy();
            JSONArray jsonArrayString=new JSONArray();
            JSONArray jsonArrayInt=new JSONArray();
            for (DepartmentBean bean : departmentBeanLazyList) {
                jsonArrayString.put(bean.getName());
                jsonArrayInt.put(subjectBox.query().equal(Subject_.department,bean.getName()).build().findLazy().size());
            }

            jsonObject.put("total",subjectBox.query().build().findLazy().size());
            jsonObject.put("sex1",subjectBox.query().equal(Subject_.sex,"1").build().findLazy().size());
            jsonObject.put("sex2",subjectBox.query().equal(Subject_.sex,"2").build().findLazy().size());
            jsonObject.put("yg",subjectBox.query().equal(Subject_.peopleType,1).build().findLazy().size());
            jsonObject.put("fk",subjectBox.query().equal(Subject_.peopleType,2).build().findLazy().size());
            jsonObject.put("xDataString",jsonArrayString);
            jsonObject.put("xDataInt",jsonArrayInt);
            jsonObject.put("msg","查询成功");
            return jsonObject.toString();
        }catch (Exception e){
            e.printStackTrace();
            return requsBean(-1,true,e.getMessage()+"","参数异常");
        }
    }


    private String requsBean(int result,boolean success,Object data,String msg){
        return JSON.toJSONString(new ResBean(result,success,data,msg));
    }

}
