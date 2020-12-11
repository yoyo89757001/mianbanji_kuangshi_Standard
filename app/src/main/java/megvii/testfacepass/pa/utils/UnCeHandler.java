package megvii.testfacepass.pa.utils;

import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.hwit.HwitManager;
import com.lztek.toolkit.Lztek;

import megvii.testfacepass.pa.MyApplication;

import megvii.testfacepass.pa.ui.BaseActivity;


public class UnCeHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    public static final String TAG = "CatchExcep";
    MyApplication application;
    Context context;

    public UnCeHandler(MyApplication application,Context context){
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.application = application;
        this.context=context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if(!handleException(ex) && mDefaultHandler != null){
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }else{
            //kaiPing();
            Intent intent = new Intent(context.getApplicationContext(), BaseActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    context.getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            //退出程序
            AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
                    restartIntent); // 1秒钟后重启应用
            application.finishActivity();
        }
    }


    private void kaiPing() {
        Intent intent = new Intent();
        intent.setAction("LYD_SHOW_NAVIGATION_BAR");
        intent.putExtra("type", 1);
        context.sendBroadcast(intent);
        context.sendBroadcast(new Intent("com.android.internal.policy.impl.showNavigationBar"));
        context.sendBroadcast(new Intent("com.android.systemui.statusbar.phone.statusopen"));
        try {
            Lztek lztek=Lztek.create(MyApplication.ampplication);
            lztek.navigationBarSlideShow(true);
        }catch (NoClassDefFoundError e){
            e.printStackTrace();
        }
        try {
            HwitManager.HwitSetShowSystemBar(context);
            HwitManager.HwitSetDisableSlideShowSysBar(0);
        }catch (NoClassDefFoundError error){
            error.printStackTrace();
        }

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
//        new Thread(){
//            @Override
//            public void run() {
//                Looper.prepare();
//                Toast.makeText(application.getApplicationContext(), "很抱歉,程序出现异常,即将退出.",
//                        Toast.LENGTH_SHORT).show();
//                Looper.loop();
//            }
//        }.start();
        return true;
    }
}