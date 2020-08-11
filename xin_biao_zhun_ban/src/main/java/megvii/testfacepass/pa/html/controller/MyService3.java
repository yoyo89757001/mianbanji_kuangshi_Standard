package megvii.testfacepass.pa.html.controller;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.SerialPort;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

import Lib.Reader.MT.Function;
import io.objectbox.Box;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import mcv.facepass.FacePassException;
import mcv.facepass.types.FacePassAddFaceResult;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.beans.ConfigBean;
import megvii.testfacepass.pa.beans.ConﬁgsBean;
import megvii.testfacepass.pa.beans.DaKaBean;
import megvii.testfacepass.pa.beans.DaKaBean_;
import megvii.testfacepass.pa.beans.Logingbean;
import megvii.testfacepass.pa.beans.PeoplePage;
import megvii.testfacepass.pa.beans.PeopleReques;
import megvii.testfacepass.pa.beans.PersonsBean;
import megvii.testfacepass.pa.beans.ResBean;
import megvii.testfacepass.pa.beans.ResultBean;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.beans.Subject_;
import megvii.testfacepass.pa.beans.UserInfos;
import megvii.testfacepass.pa.ui.MianBanJiActivity4;
import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.DengUT;
import megvii.testfacepass.pa.utils.FileUtil;
import megvii.testfacepass.pa.utils.GsonUtil;


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
 //   private Box<FaceIDBean> faceIDBeanBox  = MyApplication.myApplication.getFaceIDBeanBox();
   // private Box<IDCardTakeBean> idCardTakeBeanBox  = MyApplication.myApplication.getIdCardTakeBeanBox();
    //private ConfigBean MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class)= MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class);
   // private  String serialnumber= MyApplication.myApplication.getMMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class)Box().get(123456).getJihuoma();
   // private ConfigBean MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class)= MyApplication.myApplication.getMMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class)Box().get(123456);
    private  String pass= MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).getJiaoyanmima();





    @PostMapping("/login")
    String login(@RequestBody Logingbean logingbean){
        if (logingbean.getPassword().equals(MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).getLoginPassword()) && logingbean.getUsername().equals("admin")){
            return com.alibaba.fastjson.JSONObject.toJSONString(new ResultBean(1,"登录成功","","token"+System.currentTimeMillis()));
        }else {
            return JSON.toJSONString(new ResultBean(0,"密码错误","",""));
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




//3.设备配置
//    请求地址：  http://设备IP:8090/setConfig
//    请求方法： POST
    @PostMapping("/setConfig")
    String setConfig(@RequestParam(name = "conﬁg") String conﬁg){

            try {
                if (conﬁg==null || conﬁg.equals("")){//为空
                    return requsBean(400,true,"","参数验证失败");
                }else {
                    JsonObject jsonObject= GsonUtil.parse(conﬁg).getAsJsonObject();
                    Gson gson=new Gson();
                    ConﬁgsBean conﬁgsBean=gson.fromJson(jsonObject, ConﬁgsBean.class);
                    if (conﬁgsBean.getLivenessEnabled()==1){
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setHuoTi(false);
                    }
                    if (conﬁgsBean.getLivenessEnabled()==0){
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setHuoTi(true);
                    }
                    if (conﬁgsBean.getRetryCount()!=0){
                     MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setRetryCount(conﬁgsBean.getRetryCount());
                    }
                   if (conﬁgsBean.getSearchThreshold()!=0){
                       MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setShibieFaZhi(conﬁgsBean.getSearchThreshold());
                   }
                   if (null==conﬁgsBean.getCompanyName()){//公司名称
                       Log.d(TAG, "kong");
                   }else {
                       MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setCompanyName(conﬁgsBean.getCompanyName());
                   }
                    if (conﬁgsBean.getIsOpenDoor()==1){
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setShowShiPingLiu(true);
                    }
                    if (conﬁgsBean.getIsOpenDoor()==0){
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setShowShiPingLiu(false);
                    }
                    if (conﬁgsBean.getRelayInterval()!=0){
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setJidianqi(conﬁgsBean.getRelayInterval());
                    }
                    if (conﬁgsBean.getConfigModel()!=0){
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setConfigModel(conﬁgsBean.getConfigModel());
                    }
                    if (conﬁgsBean.getLivenessThreshold()!=0){//活体阈值
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setHuoTiFZ(conﬁgsBean.getLivenessThreshold());
                    }
                    if (conﬁgsBean.getFaceMinThreshold()!=0){//识别最小人脸
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setShibieFaceSize(conﬁgsBean.getFaceMinThreshold());
                    }
                    if (conﬁgsBean.getLowBrightnessThreshold()!=0){//最小亮度
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setLowBrightnessThreshold(conﬁgsBean.getLowBrightnessThreshold());
                    }
                    if (conﬁgsBean.getHighBrightnessThreshold()!=0){//最小亮度
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setHighBrightnessThreshold(conﬁgsBean.getHighBrightnessThreshold());
                    }
                    if (conﬁgsBean.getBrightnessSTDThreshold()!=0){//最小亮度
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setBrightnessSTDThreshold(conﬁgsBean.getBrightnessSTDThreshold());
                    }
                    if (conﬁgsBean.getAddFaceMinThreshold()!=0){//入库最小人脸
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setRuKuFaceSize(conﬁgsBean.getAddFaceMinThreshold());
                    }
                    if (conﬁgsBean.getAddFaceBlurThreshold()!=0){//入库模糊度
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setRuKuMoHuDu(conﬁgsBean.getAddFaceBlurThreshold());
                    }
                    if (conﬁgsBean.getPwd1()!=0){//密码1
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setMima(conﬁgsBean.getPwd1());
                    }
                    if (conﬁgsBean.getPwd2()!=0){//密码2
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setMima2(conﬁgsBean.getPwd2());
                    }
                    if (conﬁgsBean.getHeartbeatIntervalTime()!=0){//间隔
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setHeartbeatIntervalTime(conﬁgsBean.getHeartbeatIntervalTime());
                    }
                    if (conﬁgsBean.getTaskIntervalTime()!=0){//间隔
                        MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setTaskIntervalTime(conﬁgsBean.getTaskIntervalTime());
                    }


                    MMKV.defaultMMKV().encode("configBean",MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class));
                    //发送广播更新配置  还没实现
                    EventBus.getDefault().post("configs");
                    return requsBean(1,true,"","设置成功");
                }

            }catch (Exception e){
                return requsBean(400,true,e.getMessage()+"","参数异常");
            }
    }


//    5.设置设备时间
//    请求地址：  http://设备IP:8090/setTime
//    请求方法： POST
    @PostMapping("/setTime")
        String setTime(@RequestParam(name = "pass") String pass ,
                          @RequestParam(name = "timestamp") String timestamp){
            if (pass!=null && pass.equals(this.pass)){
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
            }else {
                return requsBean(401,true,"","签名校验失败");
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
        if (pass!=null && pass.equals(this.pass)){
            ;if (url!=null && !url.equals("")){
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
        }else {
            return requsBean(401,true,"","签名校验失败");
        }
    }

    private boolean isValidUrl(String url){
        return !TextUtils.isEmpty(url) && url.matches(Patterns.WEB_URL.pattern());
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
                        @RequestParam(name = "icCard",required = false) String icCard){
            try {
                if (MyApplication.myApplication.getFacePassHandler()==null){
                    return com.alibaba.fastjson.JSONObject.toJSONString(new PeopleReques(0,"机器算法未初始化"));
                }
                Log.d(TAG, name+"peopleType:"+peopleType+"startTime:"+startTime+"birthday"+birthday);
               // Log.d(TAG, "file.getSize():" + file.getSize());
                long peopleId=System.currentTimeMillis();
                Bitmap bitmap=readInputStreamToBitmap(file.getStream(),file.getSize());
                FacePassAddFaceResult detectResult = null;
                BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH, peopleId+".png");
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
                    byte[] faceToken = detectResult.faceToken;
                    Subject subject=new Subject();
                    subject.setSid(peopleId+"");
                    subject.setName(name);
                    subject.setSex(sex);
                    subject.setIdcard(icCard);
                    subject.setPhone(phone);
                    subject.setCreatTime(System.currentTimeMillis());
                    subject.setDepartmentName(department);
                    subject.setRemark(remarks);
                    subject.setPhoto("http://" + FileUtil.getLocalHostIp() + ":8090"  + "/app/getFaceBitmap?id="+peopleId);
                    try {
                        if (peopleType!=null && !peopleType.equals("")){
                            subject.setPeopleType(Integer.parseInt(peopleType));
                        }
                        if (birthday!=null && !birthday.equals("") && !birthday.equals("NaN")){
                            subject.setBirthday(birthday);
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
    @PostMapping("/person/update")
    String update(@RequestParam(name = "person") String person){
            if (person!=null && !person.equals("")){
                try {
                    JsonObject jsonObject= GsonUtil.parse(person).getAsJsonObject();
                    Gson gson=new Gson();
                    PersonsBean personsBean=gson.fromJson(jsonObject, PersonsBean.class);
                    if (null==personsBean.getId() || null==personsBean.getName())
                        return requsBean(400,true,"id为空","参数验证失败");
                    Subject subject=  subjectBox.query().equal(Subject_.sid,personsBean.getId()).build().findUnique();
                    if (subject==null){
                        return requsBean(400,true,personsBean.getId(),"该人员不存在");
                    }
                    subject.setSid(personsBean.getId());
                    subject.setName(personsBean.getName());
                   // subject.setIdcardNum(personsBean.getIdcardNum().toUpperCase());
                   // subject.setEntryTime(personsBean.getExpireTime());
                    subjectBox.put(subject);
                    return requsBean(1,true,personsBean.getId(),"更新成功");
                }catch (Exception e){
                    return requsBean(-1,true,e.getMessage()+"","参数异常");
                }

            }else {
                return requsBean(400,true,"","参数验证失败");
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
                            File f=new File(MyApplication.SDPATH+File.separator+s+".png");
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
                   Log.d(TAG, "subjectList.size():" + subjectList.size());
                    for (Subject subject:subjectList){
//                        PersonsBean personsBean=new PersonsBean();
//                        personsBean.setId(subject.getTeZhengMa());
//                        personsBean.setName(subject.getName());
//                        personsBean.setIdcardNum(subject.getIdcardNum());
//                        personsBean.setExpireTime(subject.getEntryTime());
//                        Log.d(TAG, JSON.toJSONString(personsBean));
                        JSONObject object=new JSONObject();
                        object.put("id",subject.getSid());//sid是id
                        object.put("name",subject.getName());
                        object.put("photo",subject.getPhoto());
                        object.put("phone",subject.getPhone());
                        object.put("icCard",subject.getIdcard());
                        object.put("sex",subject.getSex());
                        object.put("department",subject.getDepartmentName());
                        if (subject.getBirthday()!=null && !subject.getBirthday().equals("")){
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



//25.刷脸记录查询
//    请求地址：  http://设备IP:8090/findRecords
//    请求方法： POST
@PostMapping("/findRecords")
String findRecords(@RequestParam(name = "personId") String personId,
                  @RequestParam(name = "length") String length,
                   @RequestParam(name = "index") String index,
                   @RequestParam(name = "startTime") String startTime,
                   @RequestParam(name = "endTime") String endTime,
                   @RequestParam(name = "type") String type){
            try {
                int ind=Integer.parseInt(index);
                int len=Integer.parseInt(length);
                JSONArray jsonArray=new JSONArray();
                long min=0,max=0;
                min=Long.parseLong(startTime);
                max=Long.parseLong(endTime);
                List<DaKaBean> subjectList=null;
                if (personId.equals("-1")){
                    if (type.equals("all")){
                        subjectList= daKaBeanBox.query().between(DaKaBean_.time,min,max).build().find(ind,len);
                    }else {
                        subjectList= daKaBeanBox.query().equal(DaKaBean_.type,type).between(DaKaBean_.time,min,max).build().find(ind,len);
                    }
                }else {
                    if (type.equals("all")){
                        subjectList= daKaBeanBox.query().equal(DaKaBean_.personId,personId).between(DaKaBean_.time,min,max).build().find(ind,len);
                    }else {
                        subjectList= daKaBeanBox.query().equal(DaKaBean_.personId,personId).equal(DaKaBean_.type,type).between(DaKaBean_.time,min,max).build().find(ind,len);
                    }
                }
                for (DaKaBean subject:subjectList){
//                        PersonsBean personsBean=new PersonsBean();
//                        personsBean.setId(subject.getTeZhengMa());
//                        personsBean.setName(subject.getName());
//                        personsBean.setIdcardNum(subject.getIdcardNum());
//                        personsBean.setExpireTime(subject.getEntryTime());
//                        Log.d(TAG, JSON.toJSONString(personsBean));
                    JSONObject object=new JSONObject();
                    object.put("id",subject.getId());
                    object.put("path",subject.getPath());
                    object.put("personId",subject.getPersonId());
                   // object.put("state",subject.getState());
                    object.put("time",subject.getTime());
                    object.put("type",subject.getType());
                    jsonArray.put(object);
                }
                JSONObject object=new JSONObject();
                object.put("result",1);
                object.put("success",1);
                object.put("data",jsonArray);
                object.put("msg","查询成功");
                return object.toString();
                //  return requsBean(1,true,jsonArray.toString(),"获取成功");
            }catch (Exception e){
                return requsBean(-1,true,e.getMessage()+"","参数异常");
            }

}


    //25.刷脸记录查询
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


    private String requsBean(int result,boolean success,Object data,String msg){
        return JSON.toJSONString(new ResBean(result,success,data,msg));
    }

}
