package megvii.testfacepass.pa.ui;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import java.io.File;

import io.objectbox.Box;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.beans.DaKaBean;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.DateUtils;
import megvii.testfacepass.pa.utils.DengUT;
import megvii.testfacepass.pa.utils.FileUtil;

public class FaceOrCardWork {//刷脸或者刷卡都可以
    private Box<DaKaBean> daKaBeanBox=null;
    private Handler mHandler=null;

    public FaceOrCardWork(Handler handler) {
        mHandler=handler;
        daKaBeanBox=MyApplication.myApplication.getDaKaBeanBox();
    }


    public void work(Subject subject, Bitmap fileBitmap){
        switch (subject.getPeopleType()){
            case 1:{//员工

                long bitmapId=System.currentTimeMillis();
                String riqi= DateUtils.timeNYR(bitmapId+"");
                BitmapUtil.saveBitmapToSD(fileBitmap, MyApplication.SDPATH2+ File.separator+riqi,bitmapId+".png");

                //本地保存一份
                DaKaBean daKaBean=new DaKaBean();
                daKaBean.setId(bitmapId);
                daKaBean.setPath("http://" + FileUtil.getLocalHostIp() + ":8090"  + "/app/getFaceBitmap2?time=" +riqi+"&id="+bitmapId);
                daKaBean.setPersonId(subject.getSid());
                daKaBean.setTime(bitmapId);
                daKaBean.setType(1);
                daKaBean.setPeopleType(subject.getPeopleType());
                daKaBeanBox.put(daKaBean);
                //发送一份到mq


                Message message2 = Message.obtain();
                message2.what = 111;
                message2.obj = subject;
                mHandler.sendMessage(message2);


                break;
            }
            case 2:{//访客
                if (System.currentTimeMillis()>=subject.getStartTime() && System.currentTimeMillis()<=subject.getEndTime()){
                    //在有效时间范围内


                }


                break;
            }
        }

    }

}
