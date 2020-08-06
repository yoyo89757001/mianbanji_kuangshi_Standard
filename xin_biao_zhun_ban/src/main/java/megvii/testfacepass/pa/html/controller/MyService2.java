package megvii.testfacepass.pa.html.controller;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
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
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import io.objectbox.Box;
import io.objectbox.query.LazyList;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassAddFaceResult;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.beans.ConfigBean;
import megvii.testfacepass.pa.beans.ConﬁgsBean;
import megvii.testfacepass.pa.beans.DaKaBean;
import megvii.testfacepass.pa.beans.DaKaBean_;
import megvii.testfacepass.pa.beans.FaceIDBean;
import megvii.testfacepass.pa.beans.FaceIDBean_;
import megvii.testfacepass.pa.beans.IdsBean;
import megvii.testfacepass.pa.beans.Logingbean;
import megvii.testfacepass.pa.beans.PersonsBean;
import megvii.testfacepass.pa.beans.ResBean;
import megvii.testfacepass.pa.beans.ResultBean;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.beans.Subject_;
import megvii.testfacepass.pa.beans.UserInfos;
import megvii.testfacepass.pa.beans.WiﬁMsgBean;
import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.DateUtils;
import megvii.testfacepass.pa.utils.DengUT;
import megvii.testfacepass.pa.utils.FileUtil;
import megvii.testfacepass.pa.utils.GsonUtil;
import top.zibin.luban.Luban;




@RestController
@RequestMapping(path = "/app")
public class MyService2 {

    private static final String TAG = "MyService";
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




    @PostMapping("/setPassWord")
    String setPassWord(@RequestParam(name = "oldPass") String oldPass ,
                       @RequestParam(name = "newPass") String newPass){
                if (oldPass==null || newPass==null || oldPass.trim().equals("") || newPass.trim().equals("")){
                    return requsBean(400,true,"","参数验证失败");
                }else {
                    if (pass==null){
                        //第一次设置
                        if (newPass.equals(oldPass)){
                            MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setJiaoyanmima(newPass.trim());
                            MMKV.defaultMMKV().encode("configBean",MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class));
                            pass=newPass.trim();
                            return requsBean(1,true,oldPass);
                        }else {
                            return requsBean(400,true,"","密码不一致");
                        }
                    }else {
                        //修改密码
                        if (pass.equals(oldPass)){
                            MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setJiaoyanmima(newPass.trim());
                            MMKV.defaultMMKV().encode("configBean",MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class));
                            pass=newPass.trim();
                            return requsBean(1,true,newPass);
                        }else {
                            return requsBean(400,true,"","旧密码错误");
                        }
                    }
                }

    }

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

//4.修改Logo
//    请求地址：  http://设备IP:8090/changeLogo
//    请求方法： POST
    @PostMapping("/changeLogo")
    String changeLogo(@RequestParam(name = "pass") String pass ,
                     @RequestParam(name = "imgBase64") String imgBase64){
        if (pass!=null && pass.equals(this.pass)){
            if (imgBase64!=null && !imgBase64.equals("")){
                MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setLogo(imgBase64);
                MMKV.defaultMMKV().encode("configBean",MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class));
                EventBus.getDefault().post("configs");
                return requsBean(1,true,"","设置成功");
            }else {
                return requsBean(400,true,"","参数验证失败");
            }
        }else {
            return requsBean(401,true,"","签名校验失败");
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

//    有线网络配置
//    请求地址：  http://设备IP:8090/setNetInfo
//    请求方法： POST

    @PostMapping("/setNetInfo")
    String setNetInfo(@RequestParam(name = "pass") String pass ,
                   @RequestParam(name = "isDHCPMod") String isDHCPMod,
                      @RequestParam(name = "ip",required = false) String ip,
                      @RequestParam(name = "gateway",required = false) String gateway,
                      @RequestParam(name = "subnetMask",required = false) String subnetMask,
                      @RequestParam(name = "DNS",required = false) String DNS){
        if (pass!=null && pass.equals(this.pass)){
            if (isDHCPMod!=null && !isDHCPMod.equals("0")){
                try {
                    if (lztek==null)
                        return  requsBean(400,true,"","设备没有该方法");
                    lztek.setEthEnable(true);
                    if (isDHCPMod.equals("1")){ //自动获取ip
                        lztek.setEthDhcpMode();
                        return requsBean(1,true,"","设置成功");
                    }else if (isDHCPMod.equals("2")){
                        if (ip!=null && gateway!=null && subnetMask!=null && DNS!=null){
                            lztek.setEthIpAddress(ip,subnetMask,gateway,DNS);
                            return requsBean(1,true,"","设置成功");
                        }else {
                            return requsBean(400,true,"","参数验证失败");
                        }
                    }else {
                        return requsBean(400,true,"","参数验证失败");
                    }
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


//6.Wi-Fi配置
//    请求地址：  http://设备IP:8090/setWifi
//    请求方法： POST
    @PostMapping("/setWifi")
    String setWifi(@RequestParam(name = "pass") String pass ,
                   @RequestParam(name = "wiﬁMsg") String wiﬁMsg){
        if (pass!=null && pass.equals(this.pass)){
            if (wiﬁMsg!=null && !wiﬁMsg.equals("")){
                try {
                    JsonObject jsonObject= GsonUtil.parse(wiﬁMsg).getAsJsonObject();
                    Gson gson=new Gson();
                    WiﬁMsgBean wiﬁMsgBean=gson.fromJson(jsonObject, WiﬁMsgBean.class);

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

//    7.设备开门
//    请求地址：   http://设备IP:8090/device/openDoorControl
//    请求方法： POST
    @PostMapping("/openDoorControl")
    String openDoorControl(@RequestParam(name = "pass") String pass ){

        EventBus.getDefault().post("kaimen");
        return requsBean(1,true,"","请求成功");

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

//9.心跳地址配置
//    请求地址：   http://设备IP:8090/setDeviceHeartBeat
//    请求方法： POST
    @PostMapping("/setDeviceHeartBeat")
    String setDeviceHeartBeat(@RequestParam(name = "pass") String pass,
                               @RequestParam(name = "url") String url){
        if (pass!=null && pass.equals(this.pass)){
            ;if (url!=null && !url.equals("")){
                if (isValidUrl(url)){//是url
                    MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).setXintiaoDIZhi(url);
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



    //获取图片
    @GetMapping(path = "/getFaceBitmap")
    public void getFaceBitmap(HttpResponse response, @QueryParam(name = "id",required = true) String id){
        File file=new File(MyApplication.SDPATH3+File.separator+id+".png");
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

//    10.人员创建
//    请求地址：   http://设备IP:8090/person/create
//    请求方法： POST
    @PostMapping("/person/create")
    String create(@RequestParam(name = "person") String person){
            if (person!=null && !person.equals("")){
                try {
                    JsonObject jsonObject= GsonUtil.parse(person).getAsJsonObject();
                    Gson gson=new Gson();
                    PersonsBean personsBean=gson.fromJson(jsonObject, PersonsBean.class);
                    if (null==personsBean.getId() || null==personsBean.getName())
                        return requsBean(400,true,"id为空","参数验证失败");
                     Subject subjects=  subjectBox.query().equal(Subject_.sid,personsBean.getId()).build().findUnique();
                    if (subjects!=null){
                        return requsBean(400,true,personsBean.getId(),"该人员已经存在");
                    }
                    Subject subject=new Subject();
                    subject.setSid(personsBean.getId());
                    subject.setName(personsBean.getName());
                   // subject.setIdcardNum(personsBean.getIdcardNum().toUpperCase());
                   // subject.setEntryTime(personsBean.getExpireTime());
                    subjectBox.put(subject);
                    return requsBean(1,true,personsBean.getId(),"创建成功");
                }catch (Exception e){
                    return requsBean(-1,true,e.getMessage()+"","参数异常");
                }

            }else {
                return requsBean(400,true,"","参数验证失败");
            }

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

//12.人员删除（批量）
//    请求地址：   http://设备IP:8090/person/delete
//    请求方法： POST
    @PostMapping("/person/delete")
    String delete(@RequestParam(name = "id") String id){
            if (id!=null && !id.equals("")){
                try {
                    StringBuilder stringBuffer1=new StringBuilder();
                    StringBuilder stringBuffer2=new StringBuilder();
                   FacePassHandler facePassHandler=MyApplication.myApplication.getFacePassHandler();
                    if (id.equals("-1")){
                       LazyList<Subject> subjectLazyList= subjectBox.query().build().findLazy();
                       for (Subject subject:subjectLazyList){
                           if (subject.getFaceIds1()!=null)
                           facePassHandler.deleteFace(subject.getFaceIds1().getBytes());
                           if (subject.getFaceIds2()!=null)
                           facePassHandler.deleteFace(subject.getFaceIds2().getBytes());
                           if (subject.getFaceIds3()!=null)
                           facePassHandler.deleteFace(subject.getFaceIds3().getBytes());
                           stringBuffer1.append(subject.getSid());
                           stringBuffer1.append(",");
                       }
                       subjectBox.removeAll();
                       return requsBean(1, true, new IdsBean(stringBuffer1.toString(),stringBuffer2.toString()), "删除成功");
                    }else {
                        String[] strings=id.split(",");
                        for (String string : strings) {
                            stringBuffer1.append(string);
                            stringBuffer1.append(",");
                            List<Subject> sus = subjectBox.query().equal(Subject_.teZhengMa, string).build().find();
                            for (Subject ss : sus) {
                                try {
                                    subjectBox.remove(ss);
                                    if (ss.getFaceIds1()!=null)
                                        facePassHandler.deleteFace(ss.getFaceIds1().getBytes());
                                    if (ss.getFaceIds2()!=null)
                                        facePassHandler.deleteFace(ss.getFaceIds2().getBytes());
                                    if (ss.getFaceIds3()!=null)
                                        facePassHandler.deleteFace(ss.getFaceIds3().getBytes());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    stringBuffer2.append(ss);
                                }
                            }
                        }
                        return requsBean(1, true, new IdsBean(stringBuffer1.toString(),stringBuffer2.toString()), "删除成功");
                    }
                }catch (Exception e){
                    return requsBean(-1,true,e.getMessage()+"","参数异常");
                }
            }else {
                return requsBean(400,true,"","参数验证失败");
            }
    }


//    13.人员分页查询
//    请求地址：   http://设备IP:8090/person/findByPage
//    请求方法： post
    @PostMapping("/person/findByPage")
    String findByPage(@RequestParam(name = "size") String length,
                      @RequestParam(name = "page") String index){
            if (length!=null){
                try {
                    int ind=Integer.parseInt(index);
                    int len=Integer.parseInt(length);
                    JSONArray jsonArray=new JSONArray();
                   List<Subject> subjectList= subjectBox.query().build().find(ind,len);
                    for (Subject subject:subjectList){
//                        PersonsBean personsBean=new PersonsBean();
//                        personsBean.setId(subject.getTeZhengMa());
//                        personsBean.setName(subject.getName());
//                        personsBean.setIdcardNum(subject.getIdcardNum());
//                        personsBean.setExpireTime(subject.getEntryTime());
//                        Log.d(TAG, JSON.toJSONString(personsBean));
                        JSONObject object=new JSONObject();
                        object.put("id",subject.getSid()+"");//sid是id
                        object.put("name",subject.getName()+"");
                       // object.put("idcardNum",subject.getIdcardNum()+"");
                       // object.put("expireTime", DateUtils.time(subject.getEntryTime()+""));
                        jsonArray.put(object);
                    }
                    JSONObject object=new JSONObject();
                    object.put("total",subjectBox.query().build().findLazy().size());
                    object.put("data",jsonArray);
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
@PostMapping("/person/find")
String find(@RequestParam(name = "id") String id){
        if (id!=null){
            try {
                Subject subjectList= subjectBox.query().equal(Subject_.teZhengMa,id).build().findUnique();
                if (subjectList!=null){
                    PersonsBean personsBean=new PersonsBean();
                    personsBean.setId(subjectList.getTeZhengMa());
                    personsBean.setName(subjectList.getName());
                   // personsBean.setIdcardNum(subjectList.getIdcardNum());
                   // personsBean.setExpireTime(subjectList.getEntryTime());
                    return requsBean(1,true,personsBean,"获取成功");
                }else {
                    return requsBean(1,true,"","未找到该人员信息");
                }
            }catch (Exception e){
                return requsBean(-1,true,e.getMessage()+"","参数异常");
            }
        }else {
            return requsBean(400,true,"","参数验证失败");
        }

}


//19.人员有效期设置
//    请求地址：   http://设备IP:8090/person/permissionsCreate
//    请求方法： POST
    @PostMapping("/person/permissionsCreate")
    String permissionsCreate(@RequestParam(name = "personId") String personId,
                             @RequestParam(name = "time") String time){
            if (personId!=null && !personId.equals("") && time!=null && !time.equals("")){
                try {
                    long ti = Long.parseLong(time);
                    Subject subject= subjectBox.query().equal(Subject_.teZhengMa,personId).build().findUnique();
                   if (subject!=null){
                       // subject.setEntryTime(ti);
                       subjectBox.put(subject);
                       return requsBean(1,true,"","设置成功");
                    }else {
                       return requsBean(408,true,"","未找到该人员信息");
                   }
                }catch (Exception e){
                    return requsBean(-1,true,e.getMessage()+"","参数异常");
                }

            }else {
                return requsBean(400,true,"","参数验证失败");
            }

    }

////    20.照片注册（base64）
////    请求地址：  http://设备IP:8090/face/create
////    请求方法： POST
//    @PostMapping("/face/create")
//    String facecreate(@RequestParam(name = "personId") String personId,
//                             @RequestParam(name = "faceId") String faceId,
//                            @RequestParam(name = "imgBase64") String imgBase64){
//            if (personId!=null && !personId.equals("") && imgBase64!=null && !imgBase64.equals("") && faceId!=null && !faceId.equals("")){
//                try {
//                    Subject subject= subjectBox.query().equal(Subject_.sid,personId).build().findUnique();
//                    if (subject==null)
//                        return requsBean(-1, true, "", "未找到该人员");
//
//                    List<FaceIDBean> personIds=faceIDBeanBox.query().equal(FaceIDBean_.subjectId,personId).build().find();
//                    if (personIds.size()>3){//一个人超过3张
//                        return requsBean(-1, true, "", "注册失败,该人员超过三张注册照片,请先删除其中一张");
//                    }
//                    FacePassAddFaceResult detectResult = null;
//                    Bitmap bitmap= BitmapUtil.base64ToBitmap(imgBase64);
//                    BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3, "aaabbb.png");
//                    File file=  Luban.with(MyApplication.myApplication).load(MyApplication.SDPATH3+File.separator + "aaabbb.png")
//                            .ignoreBy(500)
//                            .setTargetDir(MyApplication.SDPATH3+File.separator)
//                            .get(MyApplication.SDPATH3+File.separator + "aaabbb.png");
//                    //没有保存图片 上面只是为了做图片压缩
//                    try {
//                        detectResult = MyApplication.myApplication.getFacePassHandler().addFace(BitmapFactory.decodeFile(file.getAbsolutePath()));
//                    } catch (FacePassException e) {
//                        e.printStackTrace();
//                    }
//                    if (detectResult != null && detectResult.result==0) {
//                        byte [] faceToken=detectResult.faceToken;
//                        //先查询有没有
//                        try {
//                           FaceIDBean faceIDBean=faceIDBeanBox.query().equal(FaceIDBean_.faceBitmapId,faceId).build().findUnique();
//                           if (faceIDBean==null){
//                               FaceIDBean fff = new FaceIDBean();
//                               fff.setTeZhengMa(new String(faceToken));
//                               fff.setSubjectId(personId);
//                               fff.setFaceBitmapId(faceId);
//                               MyApplication.myApplication.getFacePassHandler().bindGroup(group_name,faceToken);
//                               faceIDBeanBox.put(fff);
//                           }else {
//                               MyApplication.myApplication.getFacePassHandler().deleteFace(faceIDBean.getTeZhengMa().getBytes());
//                               faceIDBean.setTeZhengMa(new String(faceToken));
//                               MyApplication.myApplication.getFacePassHandler().bindGroup(group_name,faceToken);
//                               faceIDBeanBox.put(faceIDBean);
//                           }
//                            return requsBean(1, true, "", "注册成功");
//                        } catch (Exception e) {
//
//                            return requsBean(-1, true, e.getMessage() + "", "参数异常");
//                        }
//                    }else {
//                        return requsBean(-1, true, "", "照片不符合入库标准");
//                    }
//                } catch (Exception e) {
//                    return requsBean(-1, true, e.getMessage() + "", "参数异常");
//                }
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//    }


////    23.照片删除
////    请求地址：  http://设备IP:8090/face/delete
////    请求方法： POST
//@PostMapping("/face/delete")
//String facedelete(@RequestParam(name = "faceId") String faceId){
//        if (faceId!=null && !faceId.equals("")){
//            FaceIDBean subject=null;
//            try {
//                subject= faceIDBeanBox.query().equal(FaceIDBean_.faceBitmapId,faceId).build().findUnique();
//                if (subject!=null){
//                  MyApplication.myApplication.getFacePassHandler().deleteFace(subject.getTeZhengMa().getBytes());
//                    return requsBean(1,true,"","删除成功");
//                }else {
//                    return requsBean(408,true,"","删除失败，未找到该faceId");
//                }
//            }catch (Exception e){
//                return requsBean(-1,true,e.getMessage()+"","参数异常");
//            }
//
//        }else {
//            return requsBean(400,true,"","参数验证失败");
//        }
//}

//24.清空人员照片
//    请求地址：   http://设备IP:8090/face/deletePerson
//    请求方法： POST

//    @PostMapping("/face/deletePerson")
//    String facedeletePerson(@RequestParam(name = "personId") String personId){
//            if (personId!=null && !personId.equals("")){
//                try {
//                 List<FaceIDBean> subject = faceIDBeanBox.query().equal(FaceIDBean_.subjectId,personId).build().find();
//                    if (subject.size()==0){
//                        return requsBean(408,true,"","删除失败，未找到该personId");
//                    }else {
//                        for (FaceIDBean f:subject){
//                            faceIDBeanBox.remove(f);
//                        }
//                        return requsBean(1,true,"","删除成功");
//                    }
//                }catch (Exception e){
//                    return requsBean(-1,true,e.getMessage()+"","参数异常");
//                }
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//    }

//    21.照片注册（url）
//    请求地址：   http://设备IP:8090/face/createByUrl
//    请求方法： POST

//@PostMapping("/face/createByUrl")
//String createByUrl(@RequestParam(name = "personId") String personId,
//                  @RequestParam(name = "faceId") String faceId,
//                  @RequestParam(name = "imgUrl") String imgBase64){
//        if (personId!=null && !personId.equals("") && imgBase64!=null && !imgBase64.equals("") && faceId!=null && !faceId.equals("")){
//            try {
//                Bitmap bitmap=null;
//                try {
//                    bitmap = Glide.with(MyApplication.myApplication).asBitmap()
//                            .load(imgBase64)
//                            // .sizeMultiplier(0.5f)
//                            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                            .get();
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
//                if (bitmap==null)
//                    return requsBean(408, true, "", "下载图片失败");
//                List<FaceIDBean> personIds=faceIDBeanBox.query().equal(FaceIDBean_.subjectId,personId).build().find();
//                if (personIds.size()>3){//一个人超过3张
//                    return requsBean(-1, true, "", "注册失败,该人员超过三张注册照片,请先删除其中一张");
//                }
//                FacePassAddFaceResult detectResult = null;
//                BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3, "aaabbb.png");
//                File file=  Luban.with(MyApplication.myApplication).load(MyApplication.SDPATH3+File.separator + "aaabbb.png")
//                        .ignoreBy(500)
//                        .setTargetDir(MyApplication.SDPATH3+File.separator)
//                        .get(MyApplication.SDPATH3+File.separator + "aaabbb.png");
//                try {
//                    detectResult = MyApplication.myApplication.getFacePassHandler().addFace(BitmapFactory.decodeFile(file.getAbsolutePath()));
//                } catch (FacePassException e) {
//                    e.printStackTrace();
//                }
//                if (detectResult != null && detectResult.result==0) {
//                    byte [] faceToken=detectResult.faceToken;
//                    //先查询有没有
//                    try {
//                        FaceIDBean faceIDBean=faceIDBeanBox.query().equal(FaceIDBean_.faceBitmapId,faceId).build().findUnique();
//                        if (faceIDBean==null){
//                            FaceIDBean fff = new FaceIDBean();
//                            fff.setTeZhengMa(new String(faceToken));
//                            fff.setSubjectId(personId);
//                            fff.setFaceBitmapId(faceId);
//                            MyApplication.myApplication.getFacePassHandler().bindGroup(group_name,faceToken);
//                            faceIDBeanBox.put(fff);
//                        }else {
//                            MyApplication.myApplication.getFacePassHandler().deleteFace(faceIDBean.getTeZhengMa().getBytes());
//                            faceIDBean.setTeZhengMa(new String(faceToken));
//                            MyApplication.myApplication.getFacePassHandler().bindGroup(group_name,faceToken);
//                            faceIDBeanBox.put(faceIDBean);
//                        }
//                        return requsBean(1, true, "", "注册成功");
//                    } catch (Exception e) {
//                        return requsBean(-1, true, e.getMessage() + "", "参数异常");
//                    }
//                }else {
//                    return requsBean(-1, true, "", "照片不符合入库标准");
//                }
//            } catch (Exception e) {
//                return requsBean(-1, true, e.getMessage() + "", "参数异常");
//            }
//        }else {
//            return requsBean(400,true,"","参数验证失败");
//        }
//}

//22.照片查询
//    请求地址：  http://设备IP:8090/face/find
//    请求方法： POST
@PostMapping("/face/find")
Object facefind(@RequestParam(name = "personId") String person){
        if (person!=null && !person.equals("")){
            try {
                Subject subject= subjectBox.query().equal(Subject_.teZhengMa,person).build().findUnique();
                if (subject!=null){
                    JSONArray array=new JSONArray();

                    if (subject.getFaceIds1()!=null){
                        JSONObject object=new JSONObject();
                        object.put("faceId",subject.getFaceIds1());
                        object.put("path","http://" + FileUtil.getIPAddress(MyApplication.myApplication) + ":" + MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).getPort() + "/getFaceBitmap?id=" + subject.getFaceIds1());
                        object.put("personId",subject.getTeZhengMa());
                        array.put(object);
                    }
                    if (subject.getFaceIds2()!=null){
                        JSONObject object=new JSONObject();
                        object.put("faceId",subject.getFaceIds2());
                        object.put("path","http://" + FileUtil.getIPAddress(MyApplication.myApplication) + ":" + MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).getPort() + "/getFaceBitmap?id=" + subject.getFaceIds2());
                        object.put("personId",subject.getTeZhengMa());
                        array.put(object);
                    }
                    if (subject.getFaceIds3()!=null){
                        JSONObject object=new JSONObject();
                        object.put("faceId",subject.getFaceIds3());
                        object.put("path","http://" + FileUtil.getIPAddress(MyApplication.myApplication) + ":" + MMKV.defaultMMKV().decodeParcelable("configBean",ConfigBean.class).getPort() + "/getFaceBitmap?id=" + subject.getFaceIds3());
                        object.put("personId",subject.getTeZhengMa());
                        array.put(object);
                    }
                    JSONObject object=new JSONObject();
                    object.put("result",1);
                    object.put("success",1);
                    object.put("data",array);
                    object.put("msg","查询成功");
                    return object.toString();
                   // return requsBean(1,true,array.toString(),"查询成功");
                }else {
                    return requsBean(408,true,"","未找到该人员信息");
                }
            }catch (Exception e){
                return requsBean(-1,true,e.getMessage()+"","参数异常");
            }

        }else {
            return requsBean(400,true,"","参数验证失败");
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

//26.照片更新（base64）
//    v1.2.1.8+版本以上支持
//    请求地址：  http://设备IP:8090/face/update
//    请求方法： POST






    private String requsBean(int result,boolean success,Object data){
        return JSON.toJSONString(new ResBean(result,success,data));
    }
    private String requsBean(int result,boolean success,Object data,String msg){
        return JSON.toJSONString(new ResBean(result,success,data,msg));
    }

}
