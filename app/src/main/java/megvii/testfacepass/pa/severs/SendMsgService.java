package megvii.testfacepass.pa.severs;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;




public class SendMsgService extends Service {

    public static final String TAG = "viclee";



    @Override
    public void onCreate() {
        Log.i(TAG, "ProcessTestService onCreate");
//        FacePassHandler.initSDK(getApplicationContext());
//        FacePassUtil2 util = new FacePassUtil2();
//
//        util.init(getApplicationContext(), SettingVar.faceRotation, MMKV.defaultMMKV().decodeParcelable("saveBean", BaoCunBean.class));


    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
